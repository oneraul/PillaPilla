package com.ocronite.pillapilla;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

class Pj {
	static final float size = 0.25f * Tile.size;
	static final float baseV = 500f;

	final int index;
	final String name;
	final Vector2 pos = new Vector2();
	final Vector2 dir = new Vector2();
	final Array<Buff> buffs = new Array<Buff>();
    final Color color = new Color();
    final Array<Pj> tagTarget = new Array<Pj>();
    final Array<Pj> fleeFrom = new Array<Pj>();
    Item item;
    float itemCaducidad;
	float v;
	int vidas;
	int kills;
	boolean dead;

    private Updatable drawState;

    float interpolationTimer;
    final Vector2 oldPositionSnapshot = new Vector2();
    final Vector2 newPositionSnapshot = new Vector2();
    final Vector2 oldDirSnapshot = new Vector2();
    final Vector2 newDirSnapshot = new Vector2();

    /** estos estados estan representados por reference count en vez de booleans
     *  para evitar conflictos cuando hay varios buffs modificando el mismo estado.
     *  Por conveniencia cada uno tiene un getter en forma booleana.
     *  Los setters son en realidad metodos de increment/decrement. */
    private int stun, intangible, immune, invisible, borracho;

    boolean isStunned() {
        return stun > 0;
    }
    boolean isIntangible() {
        return intangible > 0;
    }
    boolean isImmmune() {
        return immune > 0;
    }
    boolean isInvisible() {
        return invisible > 0;
    }
    boolean isBorracho() {
        return borracho > 0;
    }

    void setStunned(boolean value) {
        if(value) stun++;
        else stun--;
    }
    void setIntangible(boolean value) {
        if(value) intangible++;
        else intangible--;
    }
    void setImmune(boolean value) {
        if(value) immune++;
        else immune--;
    }
    void setInvisible(boolean value) {
        if(value) invisible++;
        else invisible--;
    }
    void setBorracho(boolean value) {
        if(value) borracho++;
        else borracho--;
    }
	
	Pj(int index, String name) {
		this.index = index;
		this.name = name;
		v = baseV;
        drawState = new NormalDrawState();
	}

	void reset() {
		buffs.clear();
		item = null;
		v = baseV;
		dead = false;
        stun = intangible = immune = invisible = borracho = 0;
		tagTarget.clear();
		fleeFrom.clear();
	}

	/** updates called for both your the local pj and the remote pjs */
	private void commonUpdateStuff(float dt) {
        if(drawState.update(dt)) drawState.end();
        itemCaducidad -= dt;
    }

	/** updates the client's own pj */
	void localUpdate(float dt, Tile[][] map) {

        commonUpdateStuff(dt);

		if(!dead && !isStunned()) {

		    boolean dirDirtyFlag = false;
            if(isBorracho()) dir.scl(-1);

			// x axis
			pos.x += dir.x * v * dt;
		
			// never exit the map limits
			if(pos.x >= map.length*Tile.size) pos.x = map.length*Tile.size-1;
			else if(pos.x < 0) pos.x = 0;

			if(!isIntangible()) {
				// collision detection & response
				int x = (int)(pos.x/Tile.size);
				int y = (int)(pos.y/Tile.size);

				if(x >= 0 && x < map.length && y >= 0 && y < map[x].length) {
					Tile tile = map[x][y];
					if(tile == Tile.wall) {

					    if(dir.len2() == 0) {
					        dirDirtyFlag = true;
                            dir.x = MathUtils.random();
                            dir.y = MathUtils.random();
                        }

						pos.x = x+0.5f - 0.501f*Math.signum(dir.x);
						pos.x *= Tile.size;
					}
				}

                if(dirDirtyFlag) dir.setZero();
			}
			
			// y axis
			pos.y += dir.y * v * dt;
			
			// never exit the map limits
			if(pos.y >= map[0].length*Tile.size) pos.y = map[0].length*Tile.size-1;
			else if(pos.y < 0) pos.y = 0;

			if(!isIntangible()) {
				// collision detection & response
				int x = (int)(pos.x/Tile.size);
				int y = (int)(pos.y/Tile.size);

				if(x >= 0 && x < map.length && y >= 0 && y < map[x].length) {
					Tile tile = map[x][y];
					if(tile == Tile.wall) {

                        if(dir.len2() == 0) {
                            dirDirtyFlag = true;
                            dir.x = MathUtils.random();
                            dir.y = MathUtils.random();
                        }

                        pos.y = y+0.5f - 0.501f*Math.signum(dir.y);
						pos.y *= Tile.size;
					}
				}

                if(dirDirtyFlag) dir.setZero();
			}
		}
	}

	/** updates the other players' pjs */
	void interpolationUpdate(float dt) {

        commonUpdateStuff(dt);

	    // movement interpolation
		interpolationTimer = MathUtils.clamp(interpolationTimer+dt, 0, 1);
		float factor = interpolationTimer / Network.tick;
		pos.x = oldPositionSnapshot.x * (1f-factor) + newPositionSnapshot.x * factor;
		pos.y = oldPositionSnapshot.y * (1f-factor) + newPositionSnapshot.y * factor;
		dir.x = oldDirSnapshot.x * (1f-factor) + newDirSnapshot.x * factor;
		dir.y = oldDirSnapshot.y * (1f-factor) + newDirSnapshot.y * factor;
	}

	void useItem(GameClient client) {
        if(item != null) client.sendTCP(new Network.ItemUsedPacket());
    }

    void draw(SpriteBatch batch, BitmapFont font) {
        this.drawState.draw(batch);
        if(!dead && !isInvisible()) font.draw(batch, name, pos.x, pos.y+30);
    }

    void drawBuffs(SpriteBatch batch) {
        if(!dead && !isInvisible()) {
            // TODO sort them
            for(Buff buff : buffs) {
                buff.draw(batch);
            }
        }
    }

    void setDyingDrawState() {
        drawState = new DyingDrawState();
    }

    private class NormalDrawState implements Updatable {

        private float frameAccumulator;
        private final float frameDuration = 0.12f;
        private int frame;
        private final int max_frame = 1;
        private final Texture pjTexture;

        NormalDrawState() {
            pjTexture = PillaPilla.assets().get("images/pj.png");
        }

        @Override
        public void activate() {}

        @Override
        public boolean update(float dt) {

            // animate
            if(!isStunned()) {
                if (dir.len2() > 0.1f) {
                    frameAccumulator += dt * v / baseV;
                    if (frameAccumulator >= frameDuration) {
                        frameAccumulator -= frameDuration;

                        frame++;
                        if (frame > max_frame) frame = 0;
                    }
                }
            }

            return dead;
        }

        @Override
        public void draw(SpriteBatch batch) {
            if(!isInvisible()) {
                int rotation = (int)dir.angle();
                batch.setColor(color);
                batch.draw(pjTexture, pos.x-14, pos.y-14, 14, 14, 28, 28, 2, 2, rotation, frame * 28, 0, 28, 28, false, false);
                batch.setColor(Color.WHITE);
            }
        }

        @Override
        public void end() {}
    }

    private class DyingDrawState implements Updatable {

        private static final float duration = 0.3f;
        private final Color tmpColor = new Color();
        private float accumulator;
        private final Texture explosionTexture;

        DyingDrawState() {
            explosionTexture = PillaPilla.assets().get("images/explosion.png");
        }

        @Override
        public void activate() {
            setImmune(true);
        }

        @Override
        public boolean update(float dt) {
            accumulator += dt;
            return accumulator >= duration;
        }

        @Override
        public void draw(SpriteBatch batch) {
            float f = accumulator/duration;
            float w = explosionTexture.getWidth() * f;
            float h = explosionTexture.getHeight() * f;
            batch.setColor(tmpColor.set(color).lerp(Color.WHITE, f));

            batch.draw(explosionTexture, pos.x-w/2, pos.y-h/2, w, h);
        }

        @Override
        public void end() {
            setImmune(false);
            drawState = new NormalDrawState();
        }
    }
}
