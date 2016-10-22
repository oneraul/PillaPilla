package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class LobbyScreen extends ScreenAdapter {

    private Stage stage;
    private final Table pjsNameTable;
    private final Table pjsReadyTable;
    private final Label lobbyInfoLabel;
    private TextButton beginButton;

	GameClient client;
	GameServer server;

	LobbyScreen(GameServer server, GameClient client) {
	    this.server = server;
        this.client = client;

        pjsNameTable = new Table(PillaPilla.skin());
        pjsReadyTable = new Table(PillaPilla.skin());
        lobbyInfoLabel = new Label("", PillaPilla.skin());
    }
	
	@Override
	public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Table layout = new Table(PillaPilla.skin());
        layout.setFillParent(true);
        stage.addActor(layout);

        final Skin skin = PillaPilla.skin();

        final TextButton exitButton = new TextButton(PillaPilla.language("exit"), skin);
        beginButton = new TextButton(PillaPilla.language("start"), skin);
        final TextButton optionsButton = new TextButton(PillaPilla.language("options"), skin);
        final TextButton readyButton = new TextButton(PillaPilla.language("ready"), skin);

        final LinkLabel publicIp = new LinkLabel(PillaPilla.language("publicIP") + ": " + client.publicIp, client.publicIp, skin);
        final LinkLabel localIp = new LinkLabel(PillaPilla.language("localIP") + ": " + client.localIp, client.localIp, skin);

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                client.close();
                if (server != null) server.close();

                PillaPilla juego = (PillaPilla) Gdx.app.getApplicationListener();
                juego.lobby = null;
                juego.setScreen(new MainMenuScreen());
            }
        });

        beginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                server.setGameListener();
                server.partida.gameMode = server.partida.gameModeOptions.getGameMode(client);
                server.sendToAllTCP(new Network.LoadGameScreenPacket());
                server.partida.gameMode.initGame(server);
                server.sendToAllTCP(new Network.StartGamePacket());
            }
        });

        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((PillaPilla)Gdx.app.getApplicationListener())
                        .setScreen(new GameOptionsScreen(server.partida.gameModeOptions));
            }
        });

        readyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                client.sendTCP(new Network.ReadyPacket());
            }
        });

        publicIp.setListener(new LinkLabel.LinkLabelListener() {
            @Override
            public void clicked(String url) {
                Gdx.app.getClipboard().setContents(publicIp.getUrl().toString());
            }
        });

        localIp.setListener(new LinkLabel.LinkLabelListener() {
            @Override
            public void clicked(String url) {
                Gdx.app.getClipboard().setContents(localIp.getUrl().toString());
            }
        });

        // diferencias entre server y clientes
        client.setLobbyListener();
        if(server != null) {
            server.setLobbyListener();
            readyButton.setDisabled(true);

        } else {
            optionsButton.setDisabled(true);
            beginButton.setDisabled(true);
        }


        // layout

        final Table A = new Table(skin);
        final Table B = new Table(skin);
        final Table pjsLayout = new Table(skin);
        final Table buttonsLayout = new Table(skin);
        final Table ipsLayout = new Table(skin);

        layout.add(A).pad(50).padBottom(0).expand().fill().row();
        layout.add(B).pad(50).padTop(10).expandX().fillX().row();

        A.add(pjsLayout).expand().fill();
        A.add(buttonsLayout).width(200).expandY().fillY();

        pjsLayout.add(pjsReadyTable).top().width(100);
        pjsLayout.add(pjsNameTable).top().expand().fill();

        pjsNameTable.top().left();
        pjsNameTable.defaults().padTop(10).expandX().fillX();
        pjsReadyTable.defaults().padTop(10).expandX().fillX();

        buttonsLayout.top().defaults().expandX().fillX().padBottom(10).top();
        buttonsLayout.add(exitButton).row();
        buttonsLayout.add(beginButton).row();
        buttonsLayout.add(optionsButton).row();
        buttonsLayout.add(readyButton).row();

        B.add(lobbyInfoLabel).expandX().bottom().left();
        B.add(ipsLayout).bottom().right();

        ipsLayout.add(publicIp).row();
        ipsLayout.add(localIp).padTop(10).row();
	}

	@Override
	public void render(float dt) {

        // FIXME no es muy elegante refrescarlo cada frame pero...
	    setPjsLayout();
        setLobbyInfo();
        //setReadyButton();

		Gdx.gl.glClearColor(0.1f, 0.2f, 0.3f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
	}

    private void setPjsLayout() {

        pjsNameTable.clear();
        pjsReadyTable.clear();

        for(Object n : client.lobbyPlayerNames.values().toArray()) {
            String name = (String)n;

            Label nameLabel = new Label(name, PillaPilla.skin());
            pjsNameTable.add(nameLabel).row();

            if(name.equals(client.name)) nameLabel.setColor(Color.RED);
        }

        for(Object n : client.lobbyPlayerReady.values().toArray()) {
            boolean ready = (Boolean)n;

            if(ready) pjsReadyTable.add(PillaPilla.language("ready")).row();
            else pjsReadyTable.add("").row();
        }
    }

    private void setLobbyInfo() {
        lobbyInfoLabel.setText(client.partida.gameModeOptions.lobbyInfo());
    }

    private void setReadyButton() {
        if(server != null) {
            if(server.ready && beginButton.isDisabled()) beginButton.setDisabled(false);
            else if(!server.ready && !beginButton.isDisabled()) beginButton.setDisabled(true);
        }
    }

    @Override
    public void dispose() {
        System.out.println("dispose lobbyScreen");
        stage.dispose();
    }
}
