package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class CreditsScreen extends ScreenAdapter {

    private Stage stage;

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        final Table layout = new Table(PillaPilla.skin());
        layout.setFillParent(true);
        stage.addActor(layout);

        final Skin skin = PillaPilla.skin();

        final LinkLabel kenneyPlatformerLink = new LinkLabel("Platformer Pack Medieval", "http://kenney.nl/assets/platformer-pack-medieval", skin);
        final LinkLabel kenneyUiLink = new LinkLabel("UI Pack: RPG Expansion", "http://kenney.nl/assets/ui-pack-rpg-expansion", skin);
        final LinkLabel kenneyControlsLink = new LinkLabel("Onscreen Controls", "http://kenney.nl/assets/onscreen-controls", skin);
        final LinkLabel kenneyLicenceLink = new LinkLabel("CC0 1.0", "https://creativecommons.org/publicdomain/zero/1.0/", skin);
        final LinkLabel iconsLink = new LinkLabel("game-icons.net", "http://game-icons.net/", skin);
        final LinkLabel iconsLicenceLink = new LinkLabel("CC BY 3.0", "https://creativecommons.org/licenses/by/3.0/", skin);
        final LinkLabel soundIdeasLink = new LinkLabel("Sound Ideas", "https://www.sound-ideas.com/", skin);
        final LinkLabel freeSFXlink = new LinkLabel("freeSFX", "http://www.freesfx.co.uk/soundeffects/hip-hop_rap/", skin);
        final LinkLabel freeSFXlicenceLink = new LinkLabel("licencia", "http://www.freesfx.co.uk/info/eula/", skin);
        final LinkLabel ocroniteLink = new LinkLabel("Ocronite Studio", "https://ocronite.itch.io/", skin);
        final TextButton exitButton = new TextButton("volver al menu", skin);

        final Table kenneyRow = new Table(skin);
        final Table kenneyA = new Table(skin);
        final Table kenneyB = new Table(skin);
        kenneyA.add("Algunos assets de los packs ");
        kenneyA.add(kenneyPlatformerLink);
        kenneyA.add(", ");
        kenneyB.add(kenneyUiLink);
        kenneyB.add(" y ");
        kenneyB.add(kenneyControlsLink);
        kenneyB.add(" de Kenney, bajo licencia ");
        kenneyB.add(kenneyLicenceLink);

        kenneyRow.add(kenneyA).row();
        kenneyRow.add(kenneyB).padTop(5);

        final Table iconsRow = new Table(skin);
        iconsRow.add("Iconos de ");
        iconsRow.add(iconsLink);
        iconsRow.add(" bajo licencia ");
        iconsRow.add(iconsLicenceLink);

        final Table musicRow = new Table(skin);
        musicRow.add("Música de ");
        musicRow.add(soundIdeasLink);
        musicRow.add(" en ");
        musicRow.add(freeSFXlink);
        musicRow.add(" (");
        musicRow.add(freeSFXlicenceLink);
        musicRow.add(")");

        final Table ocroniteRow = new Table(skin);
        final Table ocroniteB = new Table(skin);

        ocroniteRow.add("Todos los demás assets son contenido original y, por lo tanto,").padBottom(5).row();
        ocroniteRow.add(ocroniteB);
        ocroniteB.add("propiedad intelectual de ");
        ocroniteB.add(ocroniteLink);

        layout.defaults().padTop(15);
        layout.add("Este juego utiliza:").padTop(0).row();
        layout.add(kenneyRow).padTop(30).row();
        layout.add(iconsRow).row();
        layout.add(musicRow).row();
        layout.add(ocroniteRow).padTop(30).row();
        layout.add(exitButton).padTop(30);

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PillaPilla.disposeCurrentScreen();
                ((PillaPilla)(Gdx.app.getApplicationListener())).setScreen(new MainMenuScreen());
            }
        });
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        System.out.println("dispose creditsScreen");
        stage.dispose();
    }
}
