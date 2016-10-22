package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.io.IOException;
import java.net.InetAddress;

class LobbyConnectingScreen extends LoadingScreen {

    LobbyConnectingScreen(final String serverIP) {
        new Thread() {
            @Override
            public void run() {
                tryToConnect(serverIP);
            }
        }.start();
    }

    private void tryToConnect(String serverIP) {

        final GameServer server;
        if(serverIP != null && serverIP.equals("SERVER")) {
            serverIP = "localhost";

            // server setup
            server = new GameServer();
            Network.registerPackets(server);
            server.setLobbyListener();
            try {
                server.bind(12345, 12345);
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.start();

            server.localIp = Utils.getLocalIp();
            server.publicIp = Utils.getPublicIp();

        } else server = null;

        // client setup
        Partida partida = server == null ? new Partida() : server.partida;
        partida.gameModeOptions = new TodosVsTodosOptions();
        final GameClient client = new GameClient(pjName(), partida);

        // search the server ip
        if(server == null && serverIP == null) {

            InetAddress rawIp = client.discoverHost(12345, 1000);
            if(rawIp != null) {
                serverIP = rawIp.getHostAddress();

            } else {
                // no server found. back to the main menu
                goBackToMainMenu();
                return;
            }
        }

        Network.registerPackets(client);
        client.start();
        client.setLobbyListener();

        try {
            client.connect(5000, serverIP, 12345, 12345);

        } catch (IOException e) {
            // not able to connect
            goBackToMainMenu();
            return;
        }

        // set lobby screen
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                PillaPilla.disposeCurrentScreen();
                PillaPilla app = (PillaPilla)Gdx.app.getApplicationListener();
                app.lobby = new LobbyScreen(server, client);
                app.setScreen(app.lobby);
            }
        });
    }

    private String pjName() {
        final Preferences prefs = Gdx.app.getPreferences(PillaPilla.preferencesName);
        if(prefs.contains("pjName")) {
            String pjName = prefs.getString("pjName");
            if(!pjName.equals("")) return pjName;
        }

        return NameGenerator.generate();
    }

    private void goBackToMainMenu() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                PillaPilla app = (PillaPilla)Gdx.app.getApplicationListener();
                app.lobby = null;
                app.getScreen().dispose();
                app.setScreen(new MainMenuScreen());
            }
        });
    }
}
