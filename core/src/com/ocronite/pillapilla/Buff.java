package com.ocronite.pillapilla;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

abstract class Buff implements Updatable {

    static Buff byID(int id, Pj pj) {

        if(id == Item.sprint.ordinal()) return new Sprint(pj);
        else if(id == Item.platano.ordinal()) return new PlatanoStun(pj);
        else if(id == Item.tinta.ordinal()) return new Tinta(pj);
        else if(id == Item.traverseWalls.ordinal()) return new TraverseWalls(pj);
        else if(id == Item.immune.ordinal()) return new Immune(pj);
        else if(id == Item.invisible.ordinal()) return new Invisible(pj);
        else if(id == Item.reloj.ordinal()) return new Reloj(pj);
        else if(id == Item.borracho.ordinal()) return new Borracho(pj);

        /** a partir de aqui los buffs no tienen un item asignado. Los ids son arbitrarios.
            Empezando en -30 y restando 1. Cuidado con seguir el orden!
         */
        else if(id == InvisibleSilouette.id) return new InvisibleSilouette(pj);

        throw new IllegalArgumentException("No existe ningun Item con valor " + id);
    }
}

abstract class DurationBuff extends Buff {
    private final float duration;
    private float accumulator;

    DurationBuff(float duration) {
        this.duration = duration;
    }

    @Override
    public boolean update(float dt) {
        accumulator += dt;
        return accumulator >= duration;
    }
}

class Sprint extends Buff {
    private static final float deltaV = Pj.baseV;
    private final Pj pj;

    private final float duration;
    private float accumulator;
    private float activeDeltaV;

    Sprint(Pj pj) {
        this.duration = 1.75f;
        this.pj = pj;
        this.activate();
    }

    @Override
    public void activate() {}

    @Override
    public boolean update(float dt) {
        accumulator += dt;

        final float accelerationPhaseDuration = 0.2f;
        if(accumulator <= accelerationPhaseDuration) {
            pj.v -= activeDeltaV;

            activeDeltaV = accumulator/accelerationPhaseDuration * deltaV;
            pj.v += activeDeltaV;
        }

        return accumulator >= duration;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(Item.sprint.color);
        Texture texture = PillaPilla.assets().get("images/buff.png");
        batch.draw(texture, pj.pos.x-texture.getWidth()/2, pj.pos.y-texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void end() {
        pj.v -= activeDeltaV;
    }
}

class PlatanoStun extends DurationBuff {
    private final Pj pj;

    PlatanoStun(Pj pj) {
        super(1);
        this.pj = pj;
        this.activate();
    }

    @Override
    public void activate() {
        pj.setStunned(true);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(Item.platano.color);
        Texture texture = PillaPilla.assets().get("images/buff.png");
        batch.draw(texture, pj.pos.x-texture.getWidth()/2, pj.pos.y-texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void end() {
        pj.setStunned(false);
    }
}

class Tinta extends DurationBuff {
    private final float[] manchas;

    Tinta(Pj pj) {
        super(3);
        manchas = new float[10];
        for(int i = 0; i < 10; i += 2) {
            manchas[i] = pj.pos.x + MathUtils.random(-Pj.size*8, Pj.size*8);
            manchas[i+1] = pj.pos.y + MathUtils.random(-Pj.size*5, Pj.size*5);
        }
    }

    @Override
    public void activate() {}

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(Item.tinta.color);
        Texture texture = PillaPilla.assets().get("images/tinta_splash.png");
        for(int i = 0; i < 10; i += 2) {
            batch.draw(texture, manchas[i]-texture.getWidth(), manchas[i+1]-texture.getHeight(), texture.getWidth()*2, texture.getHeight()*2);
        }
    }

    @Override
    public void end() {}
}

class TraverseWalls extends DurationBuff {
    private final Pj pj;

    TraverseWalls(Pj pj) {
        super(2);
        this.pj = pj;
        this.activate();
    }

    @Override
    public void activate() {
        pj.setIntangible(true);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(Item.traverseWalls.color);
        Texture texture = PillaPilla.assets().get("images/buff.png");
        batch.draw(texture, pj.pos.x-texture.getWidth()/2, pj.pos.y-texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void end() {
        pj.setIntangible(false);
    }
}

class Immune extends DurationBuff {
    private final Pj pj;

    Immune(Pj pj) {
        super(1);
        this.pj = pj;
        this.activate();
    }

    @Override
    public void activate() {
        pj.setImmune(true);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(Item.immune.color);
        Texture texture = PillaPilla.assets().get("images/buff.png");
        batch.draw(texture, pj.pos.x-texture.getWidth()/2, pj.pos.y-texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void end() {
        pj.setImmune(false);
    }
}

class Invisible extends DurationBuff {
    private final Pj pj;

    Invisible(Pj pj) {
        super(3);
        this.pj = pj;
        this.activate();
    }

    @Override
    public void activate() {
        pj.setInvisible(true);
    }

    @Override
    public void draw(SpriteBatch batch) {}

    @Override
    public void end() {
        pj.setInvisible(false);
    }
}

class InvisibleSilouette extends DurationBuff {
    static final int id = -30;
    private final Pj pj;

    InvisibleSilouette(Pj pj) {
        super(3);
        this.pj = pj;
        this.activate();
    }

    @Override
    public void activate() {}

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(Item.invisible.color);
        Texture texture = PillaPilla.assets().get("images/buff.png");
        batch.draw(texture, pj.pos.x-texture.getWidth()/2, pj.pos.y-texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void end() {}
}

class Reloj extends DurationBuff {
    private static final float deltaV = Pj.baseV*0.5f;
    private final Pj pj;

    Reloj(Pj pj) {
        super(1);
        this.pj = pj;
        activate();
    }

    @Override
    public void activate() {
        pj.v -= deltaV;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(Item.reloj.color);
        Texture texture = PillaPilla.assets().get("images/buff.png");
        batch.draw(texture, pj.pos.x-texture.getWidth()/2, pj.pos.y-texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void end() {
        pj.v += deltaV;
    }
}

class Borracho extends DurationBuff {
    private final Pj pj;

    Borracho(Pj pj) {
        super(2);
        this.pj = pj;
        activate();
    }

    @Override
    public void activate() {
        pj.setBorracho(true);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(Item.borracho.color);
        Texture texture = PillaPilla.assets().get("images/buff.png");
        batch.draw(texture, pj.pos.x-texture.getWidth()/2, pj.pos.y-texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void end() {
        pj.setBorracho(false);
    }
}