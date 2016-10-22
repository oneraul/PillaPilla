package com.ocronite.pillapilla;

import com.badlogic.gdx.graphics.Color;

enum Tile {
	floor(new Color(0.3f, 0.3f, 0.3f, 1)),
	wall(new Color(0.8f, 0.8f, 0.8f, 1));
	
	static final float size = 48;
	Color color;
	
	Tile(Color color) {
		this.color = color;
	}
}
