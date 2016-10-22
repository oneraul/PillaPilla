package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

class OptionsScreen extends ScreenAdapter {

    private Stage stage;

    private Label musicLabel, sfxLabel, languageLabel;
    private TextField nameTextfield;
    private TextButton exitButton;

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        final Table layout = new Table(PillaPilla.skin());
        layout.setFillParent(true);
        stage.addActor(layout);

        final Skin skin = PillaPilla.skin();

        musicLabel = new Label("", skin);
        sfxLabel = new Label("", skin);
        languageLabel = new Label("", skin);
        nameTextfield = new TextField("", skin);
        final Slider musicVolumeSlider = new Slider(0, 1, 0.01f, false, skin);
        final Slider sfxVolumeSlider = new Slider(0, 1, 0.01f, false, skin);
        final OptionsButton<Language> languageButton = new OptionsButton<Language>("", Language.values());
        exitButton = new TextButton("", skin);

        layout.add(nameTextfield).colspan(2).row();
        layout.add(musicLabel);
        layout.add(musicVolumeSlider).row();
        layout.add(sfxLabel);
        layout.add(sfxVolumeSlider).row();
        layout.add(languageLabel);
        layout.add(languageButton).row();
        layout.add(exitButton).colspan(2);

        final Preferences prefs = Gdx.app.getPreferences(PillaPilla.preferencesName);
        nameTextfield.setText(prefs.getString("pjName"));
        musicVolumeSlider.setValue(prefs.getFloat("music"));
        sfxVolumeSlider.setValue(prefs.getFloat("sfx"));

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final String pjName = nameTextfield.getText();
                final float musicVolume = musicVolumeSlider.getValue();
                final float sfxVolume = sfxVolumeSlider.getValue();

                new Runnable() {
                    @Override
                    public void run() {
                        prefs.putString("pjName", pjName);
                        prefs.putFloat("music", musicVolume);
                        prefs.putFloat("sfx", sfxVolume);
                        prefs.flush();
                    }
                }.run();

                PillaPilla.disposeCurrentScreen();

                final PillaPilla app = (PillaPilla)Gdx.app.getApplicationListener();
                app.sounds.setMusicVolume(musicVolume);
                app.sounds.setSFXvolume(sfxVolume);
                app.setScreen(new MainMenuScreen());
            }
        });

        languageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final String languageCode = languageButton.getOption().name();
                prefs.putString("language", languageCode);
                new Runnable() {
                    @Override
                    public void run() {
                        prefs.flush();
                    }
                }.run();
                PillaPilla.assets().unload("i18n/MyBundle");
                PillaPilla.assets().load("i18n/MyBundle", I18NBundle.class,
                        new I18NBundleLoader.I18NBundleParameter(new Locale(languageCode)));
                PillaPilla.assets().finishLoading();
                setLanguageLabels();
            }
        });

        final Language language = Language.valueOf(prefs.getString("language"));
        languageButton.setOption(language);
        setLanguageLabels();
    }

    private void setLanguageLabels() {
        musicLabel.setText(PillaPilla.language("music"));
        sfxLabel.setText(PillaPilla.language("sfx"));
        languageLabel.setText(PillaPilla.language("language"));
        nameTextfield.setMessageText(PillaPilla.language("name"));
        exitButton.setText(PillaPilla.language("saveAndExit"));
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        System.out.println("dispose optionsScreen");
        stage.dispose();
    }

    private enum Language {
        en("English"), es("Español");

        String nombreLegible;

        Language(String nombre) {
            this.nombreLegible = nombre;
        }

        @Override
        public String toString() {
            return nombreLegible;
        }
    }
}
