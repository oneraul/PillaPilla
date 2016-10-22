package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class GameOptionsScreen extends ScreenAdapter {

	private Stage stage;
    private final Table gameModeOptionsLayout;

	private GameModeOptions gameModeOptions;
	
	GameOptionsScreen(GameModeOptions gameModeOptions) {
        this.gameModeOptions = gameModeOptions;
        gameModeOptionsLayout = new Table(PillaPilla.skin());
	}
	
	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		Table layout = new Table(PillaPilla.skin());
		layout.setFillParent(true);
		stage.addActor(layout);

        final TextButton saveOptionsButton = new TextButton("guardar y volver", PillaPilla.skin());

        final OptionsButton<GameMode.Mode> gameModeButton = new OptionsButton<GameMode.Mode>("", GameMode.Mode.values());
        gameModeButton.setOption(gameModeOptions.id());

        layout.add(saveOptionsButton).width(200).height(50).padBottom(15).colspan(2).row();
        layout.add("Modo de juego: ");
        layout.add(gameModeButton).padBottom(10).row();
        layout.add(gameModeOptionsLayout).colspan(2).row();

        saveOptionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                PillaPilla juego = (PillaPilla)Gdx.app.getApplicationListener();
                juego.lobby.server.sendToAllTCP(gameModeOptions.networkPacket());
                juego.setScreen(juego.lobby);
            }
        });

        gameModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameModeOptions = gameModeButton.getOption().getOptions();
                gameModeOptions.setOptionsUI(gameModeOptionsLayout);

                ((PillaPilla) Gdx.app.getApplicationListener())
                        .lobby.server.partida.gameModeOptions = gameModeOptions;
            }
        });

        gameModeOptions.setOptionsUI(gameModeOptionsLayout);
	}

	@Override
	public void render(float dt) {
		Gdx.gl.glClearColor(0.1f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
	}

	@Override
	public void dispose() {
        System.out.println("dispose gameOptionsScreen");
		stage.dispose();
	}
}
