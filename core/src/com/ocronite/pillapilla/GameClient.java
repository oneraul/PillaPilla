package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

class GameClient extends Client {
	private LobbyClientListener lobbyListener;
	private Listener activeListener;

	String localIp, publicIp;

	final String name;
	final IntMap<String> lobbyPlayerNames = new IntMap<String>();
    final IntMap<Boolean> lobbyPlayerReady = new IntMap<Boolean>();
	final Partida partida;

	GameClient(String name, Partida partida) {
		this.name = name;
		this.partida = partida;
	}
	
	void setLobbyListener() {
		if(lobbyListener == null) lobbyListener = new LobbyClientListener(this);
		if(activeListener != null) removeListener(activeListener);
		activeListener = lobbyListener;
		addListener(activeListener);
	}

	void setGameListener() {
		if(activeListener != null) removeListener(activeListener);
		activeListener = partida.gameMode.getGameClientListener(this);
		addListener(activeListener);
	}
	
	// to avoid duplicated code in the listeners
	void handleDisconnection() {
		final PillaPilla juego = (PillaPilla)Gdx.app.getApplicationListener();
		juego.sounds.stopMusic();
		juego.lobby = null;
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				juego.setScreen(new MainMenuScreen());
			}
		});
	}
	
	// to avoid duplicated code in the listeners
	void handlePlayerDisconnected(int pj_index) {
		lobbyPlayerNames.remove(pj_index);
        lobbyPlayerReady.remove(pj_index);
		if(partida.pjsMap != null) partida.pjsMap.remove(pj_index);
	}
}
