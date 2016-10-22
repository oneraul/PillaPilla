package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class LobbySearchScreen extends ScreenAdapter {

    private Stage stage;

    @Override
    public void show() {

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Table layout = new Table(PillaPilla.skin());
        layout.setFillParent(true);
        stage.addActor(layout);

        final TextButton lanButton = new TextButton("LAN", PillaPilla.skin());
        final TextButton ipButton = new TextButton("IP", PillaPilla.skin());
        final TextButton ipConnectButton = new TextButton(PillaPilla.language("connect"), PillaPilla.skin());
        final TextField ipTextfield = new TextField("", PillaPilla.skin());

        ipConnectButton.setVisible(false);
        ipConnectButton.setDisabled(true);
        ipTextfield.setVisible(false);
        ipTextfield.setMessageText("IP");

        final Table buttonsLayout = new Table(PillaPilla.skin());
        buttonsLayout.defaults().width(150);
        buttonsLayout.add(lanButton).padRight(10);
        buttonsLayout.add(ipButton);

        final Table ipLayout = new Table(PillaPilla.skin());
        ipLayout.add(ipTextfield).padRight(5);
        ipLayout.add(ipConnectButton);

        layout.add(buttonsLayout).row();
        layout.add(ipLayout).padTop(15).row();

        lanButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                ((PillaPilla)Gdx.app.getApplicationListener())
                    .setScreen(new LobbyConnectingScreen(null));
            }
        });

        ipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ipTextfield.setVisible(!ipTextfield.isVisible());
                ipConnectButton.setVisible(!ipConnectButton.isVisible());
            }
        });

        ipTextfield.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean validIP = Utils.isValidIP4Address(ipTextfield.getText());
                ipConnectButton.setDisabled(!validIP);
            }
        });

        ipConnectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                ((PillaPilla) Gdx.app.getApplicationListener())
                    .setScreen(new LobbyConnectingScreen(ipTextfield.getText()));
            }
        });
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0.3f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        System.out.println("dispose slobbySearchScreen");
        stage.dispose();
    }
}
