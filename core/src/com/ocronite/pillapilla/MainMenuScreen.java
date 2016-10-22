package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class MainMenuScreen extends ScreenAdapter {

    private Stage stage;

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        final Table layout = new Table(PillaPilla.skin());
        layout.setFillParent(true);
        stage.addActor(layout);

        final TextButton hostButton = new TextButton(PillaPilla.language("create"), PillaPilla.skin());
        final TextButton joinButton = new TextButton(PillaPilla.language("join"), PillaPilla.skin());
        final TextButton creditsButton = new TextButton(PillaPilla.language("credits"), PillaPilla.skin());
        final TextButton optionsButton = new TextButton(PillaPilla.language("options"), PillaPilla.skin());

        hostButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                ((PillaPilla)Gdx.app.getApplicationListener())
                    .setScreen(new LobbyConnectingScreen("SERVER"));
            }
        });

        joinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                ((PillaPilla)Gdx.app.getApplicationListener())
                    .setScreen(new LobbySearchScreen());
            }
        });

        creditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                ((PillaPilla)Gdx.app.getApplicationListener())
                        .setScreen(new CreditsScreen());
            }
        });

        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                ((PillaPilla)Gdx.app.getApplicationListener())
                        .setScreen(new OptionsScreen());
            }
        });

        layout.defaults().minWidth(300).padBottom(15);
        layout.add(hostButton).height(75).row();
        layout.add(joinButton).height(75).row();
        layout.add(creditsButton).row();
        layout.add(optionsButton).padBottom(0).row();
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0.1f, 0.3f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        System.out.println("mainmenu dispose");
        stage.dispose();
    }
}
