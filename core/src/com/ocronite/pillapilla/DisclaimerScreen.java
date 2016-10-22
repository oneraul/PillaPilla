package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class DisclaimerScreen extends ScreenAdapter {

    private Stage stage;

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        final Table layout = new Table(PillaPilla.skin());
        layout.setFillParent(true);
        stage.addActor(layout);

        final Table disclaimerRow = new Table(PillaPilla.skin());
        disclaimerRow.defaults().padTop(5);
        disclaimerRow.add(PillaPilla.language("disclaimerTitle")).padTop(0).padBottom(15).row();
        disclaimerRow.add(PillaPilla.language("disclaimer1")).row();
        disclaimerRow.add(PillaPilla.language("disclaimer2")).row();
        disclaimerRow.add(PillaPilla.language("disclaimer3")).row();
        disclaimerRow.add(PillaPilla.language("disclaimer4")).row();
        final TextButton button = new TextButton(PillaPilla.language("disclaimerButton"), PillaPilla.skin());

        layout.add(disclaimerRow).row();
        layout.add(button).padTop(40);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                ((PillaPilla)Gdx.app.getApplicationListener())
                    .setScreen(new MainMenuScreen());
            }
        });
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        System.out.println("disclaimer dispose");
        stage.dispose();
    }
}
