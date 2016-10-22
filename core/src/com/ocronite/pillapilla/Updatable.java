package com.ocronite.pillapilla;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

interface Updatable {
    void activate();
    boolean update(float dt); /** true = finished, false = needs to continue */
    void draw(SpriteBatch batch);
    void end();
}
