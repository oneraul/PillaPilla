package com.ocronite.pillapilla;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

class GameServerListener extends Listener {
    private final GameServer server;

    GameServerListener(GameServer server) {
        this.server = server;
    }

    @Override
    public void connected(Connection connection) {
        connection.close();
    }

    @Override
    public void disconnected(Connection connection) {
        server.handleDisconnection(connection);
    }

    @Override
    public void received(Connection connection, Object o) {
        GameConnection c = (GameConnection)connection;

        if(o instanceof Network.PositionPacket) {
            Network.PositionPacket p = (Network.PositionPacket)o;
            server.sendToAllExceptTCP(c.getID(), p);

        } else if(o instanceof Network.ItemUsedPacket) {

            Pj pj = server.partida.pjsMap.get(c.getID());
            if(pj.item != null) {
                pj.item.usedCallback(pj, server);

                Network.ItemLostPacket p = new Network.ItemLostPacket();
                p.pj_index = pj.index;
                server.sendToAllTCP(p);
            }
        }
    }
}