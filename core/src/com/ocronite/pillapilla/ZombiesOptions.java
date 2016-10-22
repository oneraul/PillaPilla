package com.ocronite.pillapilla;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

class ZombiesOptions extends GameModeOptions {

    private final OptionsButton<Utils.Digits10> durationButton;

    ZombiesOptions() {
        durationButton = new OptionsButton<Utils.Digits10>("", Utils.Digits10.values());
    }

    @Override
    void setOptionsUI(Table layout) {
        layout.clear();
        final Table row = new Table(PillaPilla.skin());
        row.add("Duración: ");
        row.add(durationButton).width(50);
        row.add(" minutos");
        layout.add(row).row();
        super.setOptionsUI(layout);
    }

    @Override
    GameMode.Mode id() {
        return GameMode.Mode.Zombies;
    }

    @Override
    Network.GameOptionsPacket networkPacket() {
        Network.ZombiesGameOptionsPacket p = new Network.ZombiesGameOptionsPacket();
        p.gameMode = id().name();
        p.duration = getDuration().name();
        p.spawnItemsRate = getItemsRate().name();
        p.availableItemsMode = getItemsMode().name();
        p.map = getMap().name();
        return p;
    }

    @Override
    public String lobbyInfo() {
        return "Modo Zombies\n"
                + getDuration().toString() + " minutos\n"
                + "Items: " + getItemsRate().toString() + " - " + getItemsMode().toString() + "\n"
                + "Mapa: " + getMap().toString();
    }

    @Override
    public GameMode getGameMode(GameClient client) {
        return new ZombiesGameMode(this, client);
    }

    Utils.Digits10 getDuration() {
        return durationButton.getOption();
    }

    void setDuration(String value) {
        durationButton.setOption(Utils.Digits10.valueOf(value));
    }
}
