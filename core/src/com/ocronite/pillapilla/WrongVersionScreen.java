package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import javafx.scene.control.Tab;

public class WrongVersionScreen extends ScreenAdapter {

    private Stage stage;

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        final Table layout = new Table(PillaPilla.skin());
        layout.setFillParent(true);
        stage.addActor(layout);

        final LinkLabel updateLink = new LinkLabel(PillaPilla.language("wrongVersionLink"), "https://ocronite.itch.io/pillapilla", PillaPilla.skin());
        final TextButton exitButton = new TextButton(PillaPilla.language("exit"), PillaPilla.skin());
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(0);
            }
        });

        final Table secondRow = new Table(PillaPilla.skin());
        secondRow.add(updateLink);
        secondRow.add(" " + PillaPilla.language("wrongVersion2"));

        layout.add("ERROR").padBottom(15).row();
        layout.add(PillaPilla.language("wrongVersion1")).row();
        layout.add(secondRow).row();
        layout.add(exitButton).padTop(40);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
