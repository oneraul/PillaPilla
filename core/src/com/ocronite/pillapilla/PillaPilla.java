package com.ocronite.pillapilla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

public class PillaPilla extends Game {

	static final int version = 0;
	static final String preferencesName = "PillaPilla";

	private final AssetManager assets = new AssetManager();
	LobbyScreen lobby;
	Sounds sounds;
    Skin skin;
	I18NBundle languageBundle;

	@Override
	public void create() {
		this.setScreen(new SplashScreen());
	}

	static Skin skin() {
	    return ((PillaPilla)Gdx.app.getApplicationListener()).skin;
    }
	static AssetManager assets() {
		return ((PillaPilla)Gdx.app.getApplicationListener()).assets;
	}
	static String language(String words) {
		return ((PillaPilla)Gdx.app.getApplicationListener()).languageBundle.format(words);
	}
	static void disposeCurrentScreen() {
		final Screen screen = ((Game)Gdx.app.getApplicationListener()).getScreen();
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				screen.dispose();
			}
		});
	}

	@Override
	public void dispose() {
		this.getScreen().dispose();
        assets.dispose();
		System.exit(0);
	}
}
