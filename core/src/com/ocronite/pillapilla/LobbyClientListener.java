package com.ocronite.pillapilla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

class LobbyClientListener extends Listener {
	private final GameClient client;
	
	LobbyClientListener(GameClient client) {
		this.client = client;
	}

	@Override
	public void disconnected(Connection connection) {
		client.handleDisconnection();
	}
	
	@Override
	public void connected(Connection connection) {
		Network.NewPlayerPacket p = new Network.NewPlayerPacket();
		p.name = client.name;
		connection.sendTCP(p);
	}
	
	@Override
	public void received(Connection connection, Object o) {
		if(o instanceof Network.NewPlayerPacket) {
			Network.NewPlayerPacket p = (Network.NewPlayerPacket) o;
			client.lobbyPlayerNames.put(p.pj_index, p.name);
            client.lobbyPlayerReady.put(p.pj_index, false);

		} else if(o instanceof Network.ReadyPacket) {
		    Network.ReadyPacket p = (Network.ReadyPacket)o;
            client.lobbyPlayerReady.put(p.pj_index, p.ready);

		} else if(o instanceof Network.LoadGameScreenPacket) {
			if(client.partida.gameMode == null)
			    client.partida.gameMode = client.partida.gameModeOptions.getGameMode(client);

			client.setGameListener();
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					((Game)(Gdx.app.getApplicationListener())).setScreen(new LoadingScreen());
				}
			});
			
		} else if(o instanceof Network.GameOptionsPacket) {
			Network.GameOptionsPacket q = (Network.GameOptionsPacket)o;

            // set gameMode
			GameMode.Mode mode = GameMode.Mode.valueOf(q.gameMode);
			if(!(client.partida.gameModeOptions.id() == mode)) {
			    client.partida.gameModeOptions = mode.getOptions();
			}

			// set common stuff
			client.partida.gameModeOptions.setMap(q.map);
            client.partida.gameModeOptions.setItemsRate(q.spawnItemsRate);
			client.partida.gameModeOptions.setItemsMode(q.availableItemsMode);

            // set options
			if(o instanceof Network.TodosVsTodosGameOptionsPacket) {
				Network.TodosVsTodosGameOptionsPacket p = (Network.TodosVsTodosGameOptionsPacket)o;
				TodosVsTodosOptions gameMode = (TodosVsTodosOptions) client.partida.gameModeOptions;
				
				gameMode.setObjectiveMode(p.objectiveMode);
				gameMode.setObjective(p.objective);
				gameMode.setResetTargetsTime(p.resetTargetsTime);
				
			} else if(o instanceof Network.ZombiesGameOptionsPacket) {
				Network.ZombiesGameOptionsPacket p = (Network.ZombiesGameOptionsPacket)o;
				ZombiesOptions gameMode = (ZombiesOptions) client.partida.gameModeOptions;
				
				gameMode.setDuration(p.duration);
			}
			
		} else if(o instanceof Network.PlayerDisconnectedPacket) {
			Network.PlayerDisconnectedPacket p = (Network.PlayerDisconnectedPacket)o;
			client.handlePlayerDisconnected(p.pj_index);

		} else if(o instanceof Network.IpsPacket) {
			Network.IpsPacket p = (Network.IpsPacket)o;
            client.localIp = p.localIp;
            client.publicIp = p.publicIp;
		}
	}
}
