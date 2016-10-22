package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Locale;

class SplashScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private Texture ocroniteLogo;
    private AssetManager assets;

    private float accumulator;
    private float opacity = 1;
    private boolean finishedLoading;
    private boolean offline;
    private boolean versionChecked;
    private boolean validVersionNumber;

    @Override
    public void show() {
        batch = new SpriteBatch();
        ocroniteLogo = new Texture("images/ocronite.png");

        final Preferences prefs = Gdx.app.getPreferences(PillaPilla.preferencesName);

        // First Setup
        if(!prefs.contains("setup")) {
            prefs.putBoolean("setup", true);
            prefs.putFloat("music", 1f);
            prefs.putFloat("sfx", 1f);
            prefs.putString("language", "en");
            prefs.flush();
        }

        assets = PillaPilla.assets();
        InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        final String languageCode = prefs.getString("language");
        assets.load("i18n/MyBundle", I18NBundle.class, new I18NBundleLoader.I18NBundleParameter(new Locale(languageCode)));

        assets.load("images/pj.png", Texture.class);
        assets.load("images/explosion.png", Texture.class);
        assets.load("images/sprint.png", Texture.class);
        assets.load("images/platano.png", Texture.class);
        assets.load("images/pielDePlatano.png", Texture.class);
        assets.load("images/tinta.png", Texture.class);
        assets.load("images/traverseWalls.png", Texture.class);
        assets.load("images/randomTeleport.png", Texture.class);
        assets.load("images/immune.png", Texture.class);
        assets.load("images/invisible.png", Texture.class);
        assets.load("images/reloj.png", Texture.class);
        assets.load("images/wall.png", Texture.class);
        assets.load("images/floor.png", Texture.class);
        assets.load("images/surpriseBox.png", Texture.class);
        assets.load("images/buff.png", Texture.class);
        assets.load("images/tinta_splash.png", Texture.class);
        assets.load("images/borracho.png", Texture.class);

        assets.load("music/dark_pursuit.mp3", Music.class);
        assets.load("music/graffiti.mp3", Music.class);
        assets.load("music/hot_rocks.mp3", Music.class);
        assets.load("music/inner_city.mp3", Music.class);
        assets.load("music/nowhere_to_turn.mp3", Music.class);
        assets.load("music/rim_shot.mp3", Music.class);
        assets.load("music/urbamatic.mp3", Music.class);
        assets.load("sfx/death.wav", Sound.class);

        assets.load("androidUI/stick_in.png", Texture.class);
        assets.load("androidUI/stick_out.png", Texture.class);
        assets.load("androidUI/button.png", Texture.class);

        assets.load("ui/atlas.pack", TextureAtlas.class);

        int fontSize = 16;
        float dpi = Gdx.graphics.getDensity(); // pc = 0.8809244, bq = 2.0
        if(dpi >= 2.5f) fontSize = 32;
        else if(dpi >= 1.5f) fontSize = 24;
        FreetypeFontLoader.FreeTypeFontLoaderParameter param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "ui/calibri.ttf";
        param.fontParameters.size = fontSize;
        assets.load("calibri.ttf", BitmapFont.class, param);


        // check internet connection and version number
        if(!Utils.netIsAvailable()) {
            offline = true;

        } else {
            Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
            httpRequest.setUrl("https://itch.io/api/1/x/wharf/latest?game_id=83086&channel_name=win-linux-osx-alpha");
            Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {

                @Override
                public void handleHttpResponse(final Net.HttpResponse httpResponse) {
                    final String str = httpResponse.getResultAsString();
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            checkVersionNumber(str);
                        }
                    });
                }

                @Override
                public void failed(Throwable t) {
                    System.out.println("HTTP request failed!");
                }

                @Override
                public void cancelled() {
                    System.out.println("HTTP request cancelled!");
                }
            });
        }
    }

    @Override
    public void render(float dt) {

        if(!finishedLoading && assets.update()) finishedLoading = true;

        final float splashDuration = 1;
        accumulator += dt;
        if(accumulator >= splashDuration && finishedLoading
        && (offline || versionChecked)) {

            final float opacityChangeRate = 2;
            opacity -= opacityChangeRate * dt;
            if(opacity <= 0) {
                opacity = 0;

                if(offline || validVersionNumber) goToScreen(new DisclaimerScreen());
                else goToScreen(new WrongVersionScreen());
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(1, 1, 1, opacity);
        batch.draw(ocroniteLogo, Gdx.graphics.getWidth()/2-ocroniteLogo.getWidth()/2, Gdx.graphics.getHeight()/2-ocroniteLogo.getHeight()/2);
        batch.end();
    }

    @Override
    public void dispose() {
        System.out.println("dispose Splash");
        batch.dispose();
        ocroniteLogo.dispose();
    }

    private void checkVersionNumber(String response) {
        versionChecked = true;
        JsonValue json = new JsonReader().parse(response);
        if(json.getInt("latest") == PillaPilla.version) {
            validVersionNumber = true;
        }
    }

    private void goToScreen(ScreenAdapter screen) {
        PillaPilla app = (PillaPilla)Gdx.app.getApplicationListener();
        app.sounds = new Sounds(assets);
        app.languageBundle = assets.get("i18n/MyBundle", I18NBundle.class);

        app.skin = new Skin();
        app.skin.addRegions(assets.get("ui/atlas.pack", TextureAtlas.class));
        app.skin.add("default-font", assets.get("calibri.ttf", BitmapFont.class), BitmapFont.class);
        app.skin.load(Gdx.files.internal("ui/skin.json"));

        PillaPilla.disposeCurrentScreen();
        ((PillaPilla)Gdx.app.getApplicationListener())
            .setScreen(screen);
    }
}
