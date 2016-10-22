package com.ocronite.pillapilla;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

class GameServer extends Server {
	final Partida partida = new Partida();

	String localIp, publicIp;
	boolean ready;
	
	private LobbyServerListener lobbyListener;
	private Listener activeListener;
	
	@Override
	protected Connection newConnection() {
		return new GameConnection();
	}
	
	void setLobbyListener() {
		if(lobbyListener == null) lobbyListener = new LobbyServerListener(this);
		if(activeListener != null) removeListener(activeListener);
		activeListener = lobbyListener;
        lobbyListener.resetReady();
		addListener(activeListener);
	}
	
	void setGameListener() {
		if(activeListener != null) removeListener(activeListener);
		activeListener = new GameServerListener(this);
		addListener(activeListener);
	}
	
	// to avoid duplicated code in the listeners
	void handleDisconnection(Connection connection) {
		Network.PlayerDisconnectedPacket p = new Network.PlayerDisconnectedPacket();
		p.pj_index = connection.getID();
		sendToAllTCP(p);
	}

	void update() {
		partida.gameMode.update(this);
	}
}
