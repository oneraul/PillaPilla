package com.ocronite.pillapilla;

import com.badlogic.gdx.Input;

class DesktopController extends Controller {

    DesktopController(GameClient client) {
        super(client);
    }

    private boolean W, A, S, D;

    @Override
    void pause() {
        W = A = S = D = false;
    }

    @Override
    void update() {
        if(W || A || S || D) {
            float a = 0.05f;

            if(W && S) {
                dir.y = 0;
            } else if(W) {
                if(dir.y < 0) dir.y = 0;
                dir.y += a;
            } else if(S) {
                if(dir.y > 0) dir.y = 0;
                dir.y -= a;
            }

            if(A && D) {
                dir.x = 0;
            } else if(A) {
                if(dir.x > 0) dir.x = 0;
                dir.x -= a;
            } else if(D) {
                if(dir.x < 0) dir.x = 0;
                dir.x += a;
            }

            if (dir.len2() > 1) dir.nor();

        } else {
            dir.clamp(0.0001f, dir.len()*0.8f);
        }

        pj.dir.set(dir);
    }

    @Override
    public boolean keyDown(int keycode) {

        switch(keycode) {
            case Input.Keys.W: W = true; break;
            case Input.Keys.A: A = true; break;
            case Input.Keys.S: S = true; break;
            case Input.Keys.D: D = true; break;
            case Input.Keys.SPACE: pj.useItem(client); break;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Input.Keys.W: W = false; break;
            case Input.Keys.A: A = false; break;
            case Input.Keys.S: S = false; break;
            case Input.Keys.D: D = false; break;
        }

        return true;
    }
}
