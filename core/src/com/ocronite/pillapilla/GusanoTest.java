package com.ocronite.pillapilla;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GusanoTest extends ApplicationAdapter {
    private ShapeRenderer shaper;
    private DesktopGusanoController controller;
    private SpriteBatch batch;
    private Cabeza bicho, bicho2, bicho3;

    private boolean drawBody = true;
    private boolean drawSprites = true;
    private boolean drawDebug = false;

    private float tintAccumulator;
    private final Color color = new Color(Color.WHITE);
    private final Color newColor = new Color(Color.WHITE);

    @Override
    public void create() {
        shaper = new ShapeRenderer();
        shaper.setColor(Color.RED);
        batch = new SpriteBatch();
        bicho = new Cabeza();
        bicho2 = new Cabeza();
        bicho3 = new Cabeza();
        controller = new DesktopGusanoController();
        Gdx.input.setInputProcessor(controller);

        bicho2.pos.set(bicho.pos).add(100, 0);
        bicho3.pos.set(bicho.pos).sub(100, 0);
    }

    @Override
    public void render() {

        tintAccumulator += Gdx.graphics.getDeltaTime();
        if(tintAccumulator >= 5) {
            tintAccumulator -= 5;

            newColor.r = MathUtils.random();
            newColor.g = MathUtils.random();
            newColor.b = MathUtils.random();
        }

        color.lerp(newColor, 0.05f);

        if(Gdx.input.isKeyJustPressed(Input.Keys.H)) drawBody = !drawBody;
        if(Gdx.input.isKeyJustPressed(Input.Keys.G)) drawSprites = !drawSprites;
        if(Gdx.input.isKeyJustPressed(Input.Keys.J)) drawDebug = !drawDebug;

        controller.update();
        bicho.move(controller.getDir());
        bicho2.move(controller.getDir());
        bicho3.move(controller.getDir());

        Gdx.gl.glClearColor(0.92f, 0.69f, 0.93f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(drawSprites) {
            batch.begin();
            batch.setColor(color);
            if(drawBody) for(Cola c : bicho2.cola) c.draw(batch);
            bicho2.draw(batch);
            if(drawBody) for(Cola c : bicho3.cola) c.draw(batch);
            bicho3.draw(batch);
            batch.setColor(Color.WHITE);
            if(drawBody) for(Cola c : bicho.cola) c.draw(batch);
            bicho.draw(batch);
            batch.end();
        }

        if(drawDebug) {
            shaper.setColor(Color.RED);
            shaper.begin(ShapeRenderer.ShapeType.Line);
            bicho.draw(shaper);
            if(drawBody) for (Cola c : bicho.cola) c.draw(shaper);

            shaper.setColor(Color.BLUE);
            shaper.line(bicho.pos.x, bicho.pos.y, bicho.pos.x+controller.getDir().x*30, bicho.pos.y+controller.getDir().y*30);
            shaper.end();
        }
    }

    @Override
    public void dispose() {
        shaper.dispose();
        bicho.dispose();
        batch.dispose();
    }
}

abstract class Coso {

    static final Vector2 tmp = new Vector2();

    Texture texture;
    Vector2 pos, joint;
    int originX, originY;
    float scale;
    int dst;
    float angle;

    protected void draw(SpriteBatch batch, int angleOffset) {
        batch.draw(texture,
                pos.x-originX, pos.y-originY,                   // pos
                originX, originY,                               // origin
                texture.getWidth(), texture.getHeight(),        // width, height
                scale, scale,                                   // scaleX, scaleY
                angle+angleOffset,                              // rotation
                0, 0, texture.getWidth(), texture.getHeight(),  // srcX, srcY, srcWidth, srcHeight
                false, false);                                  // flipX, flipY
    }

    abstract void draw(SpriteBatch batch);

    void draw(ShapeRenderer shaper) {
        shaper.line(pos, joint);
        shaper.circle(pos.x, pos.y, 5);
        shaper.circle(joint.x, joint.y, 5);
    }
}

class Cabeza extends Coso {
    private final float max_v;
    final Cola[] cola;
    private float v;

    Cabeza() {
        texture = new Texture("test/0.png");
        originX = texture.getWidth()/2;
        originY = texture.getHeight()-721;
        scale = 0.03f;
        max_v = 500f;

        joint = pos = new Vector2();

        cola = new Cola[5];
        cola[0] = new Cola(this, new Texture("test/1.png"), 39, 260);
        cola[1] = new Cola(cola[0], new Texture("test/2.png"), 44, 175);
        cola[2] = new Cola(cola[1], new Texture("test/3.png"), 37, 145);
        cola[3] = new Cola(cola[2], new Texture("test/4.png"), 31, 449);
        cola[4] = new Cola(cola[3], new Texture("test/5.png"), 23, 100);

        pos.set(Gdx.graphics.getWidth()/2, 100);
    }


    private boolean turn180;
    final void move(Vector2 controllerDir) {

        float a = 0.2f;

        v = MathUtils.lerp(v, controllerDir.len(), a);
        if(controllerDir.len() > 0.8f) {

            float newAngle = controllerDir.angle();

            float deltaAngle = angleDifference(angle, newAngle);
            if(deltaAngle > 160 && deltaAngle < 200) turn180 = true;

            angle = MathUtils.lerpAngleDeg(angle, newAngle, a);

            deltaAngle = angleDifference(angle, newAngle);
            if(turn180 && (deltaAngle < 20 || deltaAngle > 340)) turn180 = false;
        }

        //if(turn180) v *= 0.5f;

        tmp.set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)); // dir
        pos.x += tmp.x * v * max_v * Gdx.graphics.getDeltaTime();
        pos.y += tmp.y * v * max_v * Gdx.graphics.getDeltaTime();

        for(Cola cola : this.cola) {
            cola.move();
        }
    }

    private float angleDifference(float a, float b) {
        //transform to [0, 360)
        a %= 360;
        b %= 360;

        float delta = b-a;
        if(delta < 0) delta = -delta;

        return delta;
    }

    @Override
    void draw(SpriteBatch batch) {
        super.draw(batch, -90);
    }

    void dispose() {
        this.texture.dispose();
        for(Cola c : cola) {
            c.texture.dispose();
        }
    }
}

class Cola extends Coso {

    Cola(Coso parent, Texture texture, int originY, int dst) {
        this.texture = texture;
        this.scale = parent.scale; //* 0.8f;

        pos = parent.joint;
        joint = new Vector2();

        this.originX = texture.getWidth()/2;
        this.originY = texture.getHeight()-((int)(originY*1.4f));
        this.dst = dst;
    }

    void move() {
        joint.sub(pos).nor().scl(dst).scl(scale).add(pos);
        angle = tmp.set(joint).sub(pos).angle();
    }

    @Override
    void draw(SpriteBatch batch) {
        super.draw(batch, 90);
    }
}

class DesktopGusanoController extends InputAdapter {
    private boolean W, A, S, D;
    private final Vector2 dir = new Vector2();

    Vector2 getDir() {
        return dir;
    }

    void update() {
        if(W || A || S || D) {
            float a = 1f;

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

            if(dir.len2() > 1) dir.nor();

        } else {
            if(dir.len() > 0.002f) dir.scl(0.8f);
            else dir.setZero();
        }
    }

    @Override
    public boolean keyDown(int keycode) {

        switch(keycode) {
            case Input.Keys.W: W = true; break;
            case Input.Keys.A: A = true; break;
            case Input.Keys.S: S = true; break;
            case Input.Keys.D: D = true; break;
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
