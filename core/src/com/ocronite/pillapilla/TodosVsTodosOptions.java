package com.ocronite.pillapilla;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

class TodosVsTodosOptions extends GameModeOptions {

    private final OptionsButton<TodosVsTodosGameMode.ObjectiveMode> objectiveModeButton;
    private final OptionsButton<Utils.Digits10> resetTargetsTimeButton;
    private final OptionsButton<Utils.Digits10> objectiveButton;

    @Override
    GameMode.Mode id() {
        return GameMode.Mode.TodosVsTodos;
    }

    TodosVsTodosOptions() {
        objectiveButton = new OptionsButton<Utils.Digits10>("", Utils.Digits10.values());
        objectiveModeButton = new OptionsButton<TodosVsTodosGameMode.ObjectiveMode>("", TodosVsTodosGameMode.ObjectiveMode.values());
        resetTargetsTimeButton = new OptionsButton<Utils.Digits10>("", Utils.Digits10.values());

        objectiveButton.setOption(Utils.Digits10.tres);
        resetTargetsTimeButton.setOption(Utils.Digits10.cinco);
    }

    @Override
    void setOptionsUI(Table layout) {
        layout.clear();

        final Table row0 = new Table(PillaPilla.skin());
        row0.add(objectiveButton).padRight(5);
        row0.add(objectiveModeButton);
        layout.add(row0).row();

        final Table row1 = new Table(PillaPilla.skin());
        row1.add("Objectivos nuevos cada ");
        row1.add(resetTargetsTimeButton);
        row1.add("segundos");
        layout.add(row1).padTop(5).row();

        super.setOptionsUI(layout);
    }

    @Override
    Network.GameOptionsPacket networkPacket() {
        Network.TodosVsTodosGameOptionsPacket p = new Network.TodosVsTodosGameOptionsPacket();
        p.gameMode = id().name();
        p.objectiveMode = getObjectiveMode().name();
        p.objective = getObjective().name();
        p.resetTargetsTime = getResetTargetsTime().name();
        p.spawnItemsRate = getItemsRate().name();
        p.availableItemsMode = getItemsMode().name();
        p.map = getMap().name();
        return p;
    }

    @Override
    public String lobbyInfo() {
        return "Todos vs Todos\n"
                + getObjective() + " " + getObjectiveMode().toString() + "\n"
                + "Objetivos cada " + getResetTargetsTime().toString() + "s\n"
                + "Items: " + getItemsRate().toString() + " - " + getItemsMode().toString() + "\n"
                + "Mapa: " + getMap().toString();
    }

    @Override
    public GameMode getGameMode(GameClient client) {
        return new TodosVsTodosGameMode(this, client);
    }

    Utils.Digits10 getObjective() {
        return objectiveButton.getOption();
    }

    void setObjective(String value) {
        objectiveButton.setOption(Utils.Digits10.valueOf(value));
    }

    TodosVsTodosGameMode.ObjectiveMode getObjectiveMode() {
        return objectiveModeButton.getOption();
    }

    void setObjectiveMode(String value) {
        objectiveModeButton.setOption(TodosVsTodosGameMode.ObjectiveMode.valueOf(value));
    }

    Utils.Digits10 getResetTargetsTime() {
        return resetTargetsTimeButton.getOption();
    }

    void setResetTargetsTime(String value) {
        resetTargetsTimeButton.setOption(Utils.Digits10.valueOf(value));
    }
}
