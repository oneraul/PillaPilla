package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

class MobileController extends Controller {
    private final AABB stickAabb, buttonAabb;
    private float stickSize, deadZoneRadius;
    private final Texture stickOutTexture, stickInTexture, buttonTexture;
    
    MobileController(GameClient client) {
        super(client);
        stickSize = Gdx.graphics.getHeight() / 5;
        deadZoneRadius = stickSize * 0.27f;
        stickAabb = new AABB(stickSize*1.1f, stickSize*1.1f, stickSize, stickSize);
        buttonAabb = new AABB(Gdx.graphics.getWidth()-stickSize/1.8f-stickSize*0.1f, stickSize, stickSize/1.8f, stickSize/1.8f);

        stickOutTexture = PillaPilla.assets().get("androidUI/stick_out.png", Texture.class);
        stickInTexture = PillaPilla.assets().get("androidUI/stick_in.png", Texture.class);
        buttonTexture = PillaPilla.assets().get("androidUI/button.png", Texture.class);
    }

    @Override
    void drawUI(SpriteBatch batch, BitmapFont font) {
        if(!pj.dead) {
            batch.begin();
            batch.draw(stickOutTexture, stickAabb.centerX-stickAabb.halfWidth, stickAabb.centerY-stickAabb.halfHeight, stickAabb.halfWidth*2, stickAabb.halfHeight*2);
            batch.draw(stickInTexture, stickAabb.centerX-stickAabb.halfWidth/2 + dir.x, stickAabb.centerY-stickAabb.halfHeight/2 + dir.y, stickAabb.halfWidth, stickAabb.halfHeight);
            batch.draw(buttonTexture, buttonAabb.centerX-buttonAabb.halfWidth, buttonAabb.centerY-buttonAabb.halfHeight, buttonAabb.halfWidth*2, buttonAabb.halfHeight*2);
            batch.end();
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight()-screenY;

		if(buttonAabb.containsPoint(screenX, screenY)) {
            pj.useItem(client);
		}

        return true;
    }

    void update() {
		
		if(Gdx.input.isTouched()) {
			
			float screenX = Gdx.input.getX();
            float screenY = Gdx.graphics.getHeight()-Gdx.input.getY();
			
			if(screenX < Gdx.graphics.getWidth()/2) {
				if(!Utils.isPointInCircle(screenX, screenY, stickAabb.centerX, stickAabb.centerY, deadZoneRadius)) {
					dir.set(screenX, screenY)
						.sub(stickAabb.centerX, stickAabb.centerY)
						.clamp(0, stickSize);

				} else lerpDirToZero();
				
			} else lerpDirToZero();
			
		} else lerpDirToZero();

        // localUpdate pj.dir
        pj.dir.set(dir).nor().scl(dir.len()/stickSize);
    }

    private void lerpDirToZero() {
        dir.clamp(0.0001f, dir.len()*0.8f);
    }
}

