package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class ScoresScreen extends ScreenAdapter {

	private String winner;
	private Stage stage;

	ScoresScreen(String winner) {
		this.winner = winner;
	}

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		Table layout = new Table(PillaPilla.skin());
		layout.setFillParent(true);
		stage.addActor(layout);

        Label label = new Label("Ha ganado " + winner + "!", PillaPilla.skin());
		TextButton exitButton = new TextButton("volver al lobby", PillaPilla.skin());

        layout.add(label).row();
		layout.add(exitButton);

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	PillaPilla.disposeCurrentScreen();
                PillaPilla juego = (PillaPilla)Gdx.app.getApplicationListener();
                juego.setScreen(juego.lobby);
            }
        });
    }

	@Override
	public void render(float dt) {
		Gdx.gl.glClearColor(0.3f, 0.2f, 0.1f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
	}

	@Override
	public void dispose() {
	    System.out.println("dispose scoresScreen");
		stage.dispose();
	}
}
