package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;

class Sounds {

    private com.badlogic.gdx.audio.Sound death;
    private final Music[] songs;
    private Music playingSong;
    private float musicVolume, sfxVolume;

    Sounds(AssetManager assets) {

        // set volumes
        final Preferences prefs = Gdx.app.getPreferences(PillaPilla.preferencesName);
        setMusicVolume(prefs.getFloat("music"));
        setMusicVolume(prefs.getFloat("sfx"));

        death = assets.get("sfx/death.wav");
        songs = new Music[] {
            assets.get("music/dark_pursuit.mp3"),
            assets.get("music/graffiti.mp3"),
            assets.get("music/hot_rocks.mp3"),
            assets.get("music/inner_city.mp3"),
            assets.get("music/nowhere_to_turn.mp3"),
            assets.get("music/rim_shot.mp3"),
            assets.get("music/urbamatic.mp3"),
        };
    }

    void setMusicVolume(float volume) {
        musicVolume = volume;
    }
    void setSFXvolume(float volume) {
        sfxVolume = volume;
    }

    void play(Sound sound) {
        getSound(sound).play(sfxVolume);
    }

    void playRandomSong() {
        playingSong = songs[MathUtils.random(songs.length-1)];
        playingSong.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                playRandomSong();
            }
        });
        playingSong.setVolume(musicVolume);
        playingSong.play();
    }

    void stopMusic() {
        if(playingSong != null) {
            playingSong.stop();
        }
        playingSong = null;
    }

    enum Sound {
        death
    }

    private com.badlogic.gdx.audio.Sound getSound(Sound sound) {
        switch(sound) {
            case death: return death;
        }

        throw new IllegalArgumentException("There's no sound attached to the id " + sound.toString());
    }
}
