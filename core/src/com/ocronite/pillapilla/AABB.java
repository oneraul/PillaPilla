package com.ocronite.pillapilla;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

class AABB {
	float centerX, centerY;
	float halfWidth, halfHeight;
	
	AABB(float centerX, float centerY, float halfWidth, float halfHeight) {
		this.centerX    = centerX;
		this.centerY    = centerY;
		this.halfWidth  = halfWidth;
		this.halfHeight = halfHeight;
	}
	
	boolean containsPoint(float pX, float pY) {
		return !(pX < centerX-halfWidth
			|| pX > centerX+halfWidth
			|| pY < centerY-halfHeight
			|| pY > centerY+halfHeight);
	}

	void draw(ShapeRenderer shaper) {
		shaper.rect(centerX-halfWidth, centerY-halfHeight, halfWidth*2, halfHeight*2);
	}
}
