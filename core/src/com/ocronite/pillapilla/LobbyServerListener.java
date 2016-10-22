package com.ocronite.pillapilla;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

class LobbyServerListener extends Listener {
    private final GameServer server;
    private int readyCount;

    LobbyServerListener(GameServer server) {
        this.server = server;
    }

    @Override
    public void connected(Connection connection) {
        Network.IpsPacket p = new Network.IpsPacket();
        p.localIp = server.localIp;
        p.publicIp = server.publicIp;
        connection.sendTCP(p);
    }

    @Override
    public void disconnected(Connection connection) {
        server.handleDisconnection(connection);
        readyCount--;
        readyCheck();
    }

    @Override
    public void received(Connection connection, Object o) {
        GameConnection c = (GameConnection)connection;

        if(o instanceof Network.NewPlayerPacket) {
            Network.NewPlayerPacket p = (Network.NewPlayerPacket)o;
            p.pj_index = c.getID();
            c.name = p.name;

            // send game options
            c.sendTCP(server.partida.gameModeOptions.networkPacket());

            // put the new player up to date
            for(Connection conn : server.getConnections()) {
                Network.NewPlayerPacket q = new Network.NewPlayerPacket();
                q.name = ((GameConnection)conn).name;
                q.pj_index = conn.getID();
                c.sendTCP(q);

                Network.ReadyPacket r = new Network.ReadyPacket();
                r.ready = ((GameConnection)conn).ready;
                r.pj_index = conn.getID();
                c.sendTCP(r);
            }

            // update everybody
            server.sendToAllExceptTCP(c.getID(), p);

            readyCheck();

        } else if(o instanceof Network.ReadyPacket) {
            Network.ReadyPacket p = (Network.ReadyPacket)o;
            p.pj_index = c.getID();
            p.ready = c.ready = !c.ready;
            server.sendToAllTCP(p);

            // set lobby button active/inactive
            if(p.ready) readyCount++;
            else readyCount--;

            readyCheck();
        }
    }

    private void readyCheck() {
        server.ready = (server.getConnections().length != 1) && (readyCount == server.getConnections().length - 1);
    }

    void resetReady() {

        /* FIXME maybe??
                LobbyScreen#show()                      line 44
                    GameServer#setLobbyListener()       line 25
                        ServerListener#resetReady()

                after changing any options in OptionsScreen (or even without any changes), the LobbyScreen calls a reset of the ready count
         */

        readyCount = 0;
        readyCheck();

        for(Connection conn : server.getConnections()) {
            ((GameConnection)conn).ready = false;

            Network.ReadyPacket p = new Network.ReadyPacket();
            p.ready = false;
            p.pj_index = conn.getID();
            server.sendToAllTCP(p);
        }
    }
}
