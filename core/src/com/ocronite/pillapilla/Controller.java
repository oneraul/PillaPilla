package com.ocronite.pillapilla;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

abstract class Controller extends InputAdapter  {
	protected final Vector2 dir = new Vector2();
	protected final GameClient client;
	protected final Pj pj;

	protected Controller(GameClient client) {
		this.client = client;
		this.pj = client.partida.pjsMap.get(client.getID());
	}

	void drawUI(SpriteBatch batch, BitmapFont font) {}
	void pause() {}
	abstract void update();
}
