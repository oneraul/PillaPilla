package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

class LoadingScreen extends ScreenAdapter {

    private Stage stage;
    private Label label;
    private float accumulator;
    private int dots;

    private String loading;

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Table layout = new Table(PillaPilla.skin());
        layout.setFillParent(true);
        stage.addActor(layout);

        loading = PillaPilla.language("loading");
        layout.add(label = new Label(loading, PillaPilla.skin()));
    }

    @Override
    public void render(float dt) {

        // animate the ...
        accumulator += dt;
        if(accumulator >= 1f/3) {
            accumulator -= 1f/3;

            dots++;
            if(dots > 3) dots = 0;

            String str = loading;
            for(int i = 0; i < dots; i++) {
                str += '.';
            }

            label.setText(str);
        }

        Gdx.gl.glClearColor(0.2f, 0.3f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        System.out.println("dispose loadingScreen");
        stage.dispose();
    }
}
