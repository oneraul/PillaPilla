package com.ocronite.pillapilla;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MaskShaderTest extends ApplicationAdapter {

    private SpriteBatch batch;
    private ShaderProgram shader;
    private Texture a, b, mask;
    private float threshold;
    private int mode;

    private AssetManager assets;
    private Stage stage;
    private Skin skin;

    @Override
    public void create() {

        assets = new AssetManager();

        assets = new AssetManager();
        InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        assets.load("ui/atlas.pack", TextureAtlas.class);

        int fontSize = 16;
        float dpi = Gdx.graphics.getDensity(); // pc = 0.8809244, bq = 2.0
        if(dpi >= 2.5f) fontSize = 32;
        else if(dpi >= 1.5f) fontSize = 24;
        FreetypeFontLoader.FreeTypeFontLoaderParameter param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "ui/calibri.ttf";
        param.fontParameters.size = fontSize;
        assets.load("calibri.ttf", BitmapFont.class, param);

        assets.load("test/20160904_204222.jpg", Texture.class);
        assets.load("test/a.png", Texture.class);
        assets.load("test/mask.png", Texture.class);
        assets.finishLoading();

        a = assets.get("test/20160904_204222.jpg");
        b = assets.get("test/a.png");
        mask = assets.get("test/mask.png");

        b.bind(1);
        mask.bind(2);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        String vert = Gdx.files.internal("test/shader.vert").readString();
        String frag = Gdx.files.internal("test/shader.frag").readString();
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vert, frag);
        if(!shader.isCompiled())
            throw new GdxRuntimeException(shader.getLog());
        if(shader.getLog().length() != 0)
            System.out.println(shader.getLog());

        shader.begin();
        shader.setUniformi("u_otherTexture", 1);
        shader.setUniformi("u_mask", 2);
        shader.end();

        batch = new SpriteBatch();
        batch.setShader(shader);

        skin = new Skin();
        skin.addRegions(assets.get("ui/atlas.pack", TextureAtlas.class));
        skin.add("default-font", assets.get("calibri.ttf", BitmapFont.class), BitmapFont.class);
        skin.load(Gdx.files.internal("ui/skin.json"));

        stage = new Stage();
        final Table layout = new Table(skin);
        layout.setFillParent(true);
        layout.right();
        stage.addActor(layout);
        Gdx.input.setInputProcessor(stage);

        final Slider slider = new Slider(0, 1, 0.01f, false, skin);
        layout.add(slider).row();
        slider.setVisible(false);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                threshold = slider.getValue();
            }
        });

        final TextButton button = new TextButton("", skin);
        layout.add(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mode = mode == 0 ? 1 : 0;
                slider.setVisible(!slider.isVisible());
            }
        });
    }

    @Override
    public void render() {
        stage.act();

        Gdx.gl.glClearColor(0.1f, 0.2f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        shader.setUniformf("u_threshold", threshold);
        shader.setUniformi("u_mode", mode);
        batch.draw(a, 0, 0);
        batch.end();

        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shader.dispose();

        stage.dispose();
        skin.dispose();

        assets.dispose();
    }
}
