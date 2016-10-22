package com.ocronite.pillapilla;

class Drop {
    static final float size = 0.6f * Tile.size;
    static final float outerCircleRadius = size * (float)Math.sqrt(2)/2; // mitad de la diagonal del cuadrado
    AABB aabb;
    Item item;

    private Drop(float x, float y, Item item) {
        this.item = item;
        this.aabb = new AABB(x, y, size/2, size/2);
    }

    static Drop byID(float x, float y, int id) {
        return new Drop(x, y, Item.byID(id));
    }
}
