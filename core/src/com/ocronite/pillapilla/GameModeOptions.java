package com.ocronite.pillapilla;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

abstract class GameModeOptions {
    private final OptionsButton<Partida.Map> mapButton;
    private final OptionsButton<DropManager.DropRate> itemsRateButton;
    private final OptionsButton<AvailableItemsMode> availableItemsButton;
    private final ScrollPane itemsScrollPane;
    private final Array<CheckBox> checkboxes;

    private final Table thisLayout, mapRow, rateRow, modeRow;
    private boolean showModeButton = true, showItemsScrollpane;

    enum AvailableItemsMode {
        Todos {
            @Override
            Item[] getItems(Array<CheckBox> checkboxes) {
                return Item.values();
            }
        },
        Cajas {
            @Override
            Item[] getItems(Array<CheckBox> checkboxes) {
                return new Item[] { Item.surpriseBox };
            }
        },
        Custom {
            @Override
            Item[] getItems(Array<CheckBox> checkboxes) {
                Array<Item> items = new Array<Item>();
                for(CheckBox checkBox : checkboxes) {
                    if(checkBox.isChecked()) {
                        items.add(Item.valueOf(checkBox.getLabel().getText().toString()));
                    }
                }

                Item[] toReturn = new Item[items.size];
                for(int i = 0; i < items.size; i++) toReturn[i] = items.get(i);

                return toReturn;
            }
        };

        abstract Item[] getItems(Array<CheckBox> checkboxes);
    }

    GameModeOptions() {
        thisLayout = new Table(PillaPilla.skin());
        mapRow = new Table(PillaPilla.skin());
        rateRow = new Table(PillaPilla.skin());
        modeRow = new Table(PillaPilla.skin());
        mapButton = new OptionsButton<Partida.Map>("", Partida.Map.values());

        final Table scrollpaneLayout = new Table(PillaPilla.skin());
        itemsScrollPane = new ScrollPane(scrollpaneLayout, PillaPilla.skin());
        itemsScrollPane.setFadeScrollBars(false);
        itemsScrollPane.setVariableSizeKnobs(false);
        itemsScrollPane.setScrollingDisabled(true, false);
        itemsScrollPane.setFlickScroll(false);

        Item[] allItems = Item.values();
        checkboxes = new Array<CheckBox>();
        for(Item item : allItems) {
            final CheckBox checkBox = new CheckBox(item.name(), PillaPilla.skin());
            checkboxes.add(checkBox);
            scrollpaneLayout.add(checkBox).row();
        }

        itemsRateButton = new OptionsButton<DropManager.DropRate>("", DropManager.DropRate.values());
        itemsRateButton.setOption(DropManager.DropRate.Bastantes);
        itemsRateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showModeButton = !(itemsRateButton.getOption() == DropManager.DropRate.Ninguno);
                setLayout();
            }
        });

        availableItemsButton = new OptionsButton<AvailableItemsMode>("", AvailableItemsMode.values());
        availableItemsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showItemsScrollpane = (availableItemsButton.getOption() == AvailableItemsMode.Custom);
                setLayout();
            }
        });

        setLayout();
    }

    void setOptionsUI(Table layout) {
        layout.add(thisLayout);
        setLayout();
    }

    private void setLayout() {
        thisLayout.clearChildren();
        mapRow.clearChildren();
        rateRow.clearChildren();
        modeRow.clearChildren();

        mapRow.add("Mapa: ");
        mapRow.add(mapButton);
        thisLayout.add(mapRow).row();

        rateRow.add("Objetos: ");
        rateRow.add(itemsRateButton).row();
        thisLayout.add(rateRow).row();

        if(showModeButton) {
            modeRow.add("Objetos disponibles: ");
            modeRow.add(availableItemsButton);
            thisLayout.add(modeRow).row();
        }

        if(showModeButton && showItemsScrollpane) {
            thisLayout.add(itemsScrollPane).height(100).expandX().fillX().row();
        }
    }

    abstract Network.GameOptionsPacket networkPacket();
    abstract String lobbyInfo();
    abstract GameMode getGameMode(GameClient client);
    abstract GameMode.Mode id();

    final Partida.Map getMap() {
        return mapButton.getOption();
    }
    final DropManager.DropRate getItemsRate() {
        return itemsRateButton.getOption();
    }
    final AvailableItemsMode getItemsMode() {
        return availableItemsButton.getOption();
    }

    final void setItemsRate(String value) {
        itemsRateButton.setOption(DropManager.DropRate.valueOf(value));
    }
    final void setMap(String value) {
        mapButton.setOption(Partida.Map.valueOf(value));
    }
    final void setItemsMode(String value) {
        availableItemsButton.setOption(AvailableItemsMode.valueOf(value));
    }

    DropManager getDropManager(GameClient client) {
        float dropRate = itemsRateButton.getOption().value;
        int max_drops = (int)(client.partida.map.length * client.partida.map[0].length * dropRate * 10);
        return new DropManager(dropRate, max_drops, availableItemsButton.getOption().getItems(checkboxes));
    }
}
