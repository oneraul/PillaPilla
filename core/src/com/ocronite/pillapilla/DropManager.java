package com.ocronite.pillapilla;

import com.badlogic.gdx.math.MathUtils;

class DropManager {

    private final Item[] allItemsEver;
    private final Item[] availableItems; /** complete list of available items for the game */
    private final float totalDropRatioSum; /** the sum of the individual dropRates of the available items */

    private final float dropRate; /** global rate of item drops */
    private final float max_drops; /** max amount of active drops on game */

    DropManager(float dropRate, int max_drops, Item... availableItems) {
        allItemsEver = Item.values();

        this.availableItems = availableItems;
        this.dropRate = dropRate;
        this.max_drops = max_drops;

        float tmpSum = 0;
        for(Item item : availableItems) tmpSum += item.dropRatio;
        totalDropRatioSum = tmpSum;
    }

    /** returns a random AVAILABLE item, according to each one's drop rate */
    private Item dropAvailableItem() {
        float value = (MathUtils.random() * totalDropRatioSum)-0.001f;
        float sum = 0;
        for(Item item : availableItems) {
            sum += item.dropRatio;
            if(value < sum) return item;
        }

        throw new IllegalArgumentException("Reached probability " + value + " of total posible " + totalDropRatioSum
                                            + ". The probability is out of bounds.");
    }

    /** returns a random item, according to each one's drop rate */
    private Item dropRandomItem() {
        return allItemsEver[MathUtils.random(allItemsEver.length-1)];
    }

    /** returns a random item and excludes the ones provided in the vararg */
    Item dropRandomItemExclude(Item... itemsToExclude) {
        if(availableItems.length == 0) throw new IllegalArgumentException("No hay items posibles que dropear");

        Item item = null;
        while(item == null) {
            item = this.dropRandomItem();
            for(Item excluded : itemsToExclude) {
                if(item == excluded) item = null;
            }
        }

        return item;
    }

    /** called by the server. drops (or not) the items and removes extra ones */
    void tick(GameServer server) {
        if(availableItems.length == 0) return;

        if(MathUtils.randomBoolean(dropRate)) {
            int intentos = 0;
            while (intentos < 8) {
                int x = MathUtils.random(server.partida.map.length - 1);
                int y = MathUtils.random(server.partida.map[x].length - 1);
                if (server.partida.map[x][y] == Tile.floor) {
                    Network.SpawnDropPacket p = new Network.SpawnDropPacket();
                    p.itemID = this.dropAvailableItem().id();
                    p.x = (x + MathUtils.random()) * Tile.size;
                    p.y = (y + MathUtils.random()) * Tile.size;
                    server.sendToAllTCP(p);

                    // limit number of active drops
                    if(server.partida.drops.size > max_drops) {
                        Network.DropPickedPacket q = new Network.DropPickedPacket();
                        q.index = 0;
                        server.sendToAllTCP(q);
                    }
                    break;
                }
                intentos++;
            }
        }
    }

    enum DropRate {
        Ninguno(0), Pocos(0.01f), Bastantes(0.035f), Demasiados(0.09f);

        final float value;

        DropRate(float value) {
            this.value = value;
        }
    }
}