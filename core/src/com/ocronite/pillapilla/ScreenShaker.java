package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

class ScreenShaker {

    private static float timer, amount = 0.7f;
    private static Camera cam;
    private static boolean shaking = false;
    private static Vector2 lastShake = new Vector2();

    static void setCam(Camera cam) {
        ScreenShaker.cam = cam;
    }

    static void shake() {
        shaking = true;
        timer = 0.3f;
    }

    static void update() {
        if(!shaking) return;

        timer -= Gdx.graphics.getDeltaTime();
        if(timer <= 0) {
            shaking = false;
            return;
        }

        if(lastShake.x == 0) {
            float x = 0, y = 0;
            switch(MathUtils.random(3)) {
                case 0:
                    x -= amount; y += amount;
                    break;

                case 1:
                    x -= amount; y -= amount;
                    break;

                case 2:
                    x += amount; y += amount;
                    break;

                case 3:
                    x += amount; y -= amount;
                    break;
            }
            cam.position.x += x;
            cam.position.y += y;
        }
        else {
            cam.position.x -= lastShake.x;
            cam.position.y -= lastShake.y;
            lastShake.set(0, 0);
        }

        cam.update();
    }
}
