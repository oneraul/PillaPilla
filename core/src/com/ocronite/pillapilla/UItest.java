package com.ocronite.pillapilla;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class UItest extends ApplicationAdapter {

    private Stage stage;
    private Skin skin;
    private AssetManager assets;

    @Override
    public void create() {

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

        assets.finishLoading();

        skin = new Skin();
        skin.addRegions(assets.get("ui/atlas.pack", TextureAtlas.class));
        skin.add("default-font", assets.get("calibri.ttf", BitmapFont.class), BitmapFont.class);
        skin.load(Gdx.files.internal("ui/skin.json"));

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        final Table layout = new Table(skin);
        layout.setFillParent(true);
        stage.addActor(layout);

        final TextButton button = new TextButton("super long sentence on a single button for the win cause why not", skin);
        final TextButton disabledButton = new TextButton("I'm disabled", skin);
        final TextField textfield = new TextField("", skin);
        final Slider slider = new Slider(0, 1, 0.01f, false, skin);
        final LinkLabel linkLabel = new LinkLabel("Google.com", "http://google.com", skin);
        final CheckBox checkBox = new CheckBox("cosomohoso", skin);
        final CheckBox disabledCheckBox = new CheckBox("cosoMUYmohoso", skin);
        final Table scrollPaneInterior = new Table(skin);
        final ScrollPane scrollPane = new ScrollPane(scrollPaneInterior, skin);

        disabledButton.setDisabled(true);
        disabledCheckBox.setDisabled(true);

        for(int i = 0; i < 50; i++) scrollPaneInterior.add("asdasfadfsadfasdfasdfhbasjhfbjasdfbhasbdfjasdhbfafasjhfbhasj").row();
        scrollPane.setFadeScrollBars(false);
        scrollPane.setVariableSizeKnobs(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);

        layout.defaults().padTop(10);
        layout.add("pene").padTop(0).row();
        layout.add(button).row();
        layout.add(disabledButton).row();
        layout.add(textfield).row();
        layout.add(slider).row();
        layout.add(linkLabel).row();
        layout.add(checkBox).row();
        layout.add(disabledCheckBox).row();
        layout.add(scrollPane).height(50).width(100).row();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
