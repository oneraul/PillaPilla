package com.ocronite.pillapilla;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;

interface TodosVsTodosObjectiveMode {
	void initGame(GameServer server);
	void checkEnd(GameServer server);
	void clientUpdate(float dt, GameClient client);
	void drawUI(float UIsizeUnit, ShapeRenderer shaper, SpriteBatch batch, BitmapFont font, Partida partida, Pj pj);
}

class VidasObjectiveMode implements TodosVsTodosObjectiveMode {
	
	private final int vidasIniciales;
	private final Array<Pj> saco = new Array<Pj>(); // tmp array used when setting the targets
	
	VidasObjectiveMode(int vidas) {
		this.vidasIniciales = vidas;
	}

	@Override
	public void initGame(GameServer server) {
		for(Pj pj : server.partida.pjsArray()) {
			Network.SetVidasPacket p = new Network.SetVidasPacket();
			p.pj_index = pj.index;
			p.vidas = vidasIniciales;
			server.sendToAllTCP(p);

			Item.randomTeleport.usedCallback(pj, server);
		}
	}
	
	@Override
	public void checkEnd(GameServer server) {
		saco.clear();
        saco.addAll(server.partida.pjsArray());
        for (int i = saco.size-1; i >= 0; i--) {
            if (saco.get(i).dead) saco.removeIndex(i);
        }
        if (saco.size == 1) {
            // game finished. show scores
            Network.WinnerPacket p = new Network.WinnerPacket();
            p.pj_name = saco.peek().name;
            server.sendToAllTCP(p);
        }
	}

    @Override
    public void clientUpdate(float dt, GameClient client) {}

    @Override
	public void drawUI(float UIsizeUnit, ShapeRenderer shaper, SpriteBatch batch, BitmapFont font, Partida partida, Pj pj) {
		shaper.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < pj.vidas; i++) {
            shaper.setColor(Color.WHITE);
            shaper.circle(UIsizeUnit * 1.35f + i * UIsizeUnit * 0.35f, Gdx.graphics.getHeight() - UIsizeUnit * 0.85f, UIsizeUnit * 0.15f);
            shaper.setColor(Color.RED);
        	shaper.circle(UIsizeUnit * 1.35f + i * UIsizeUnit * 0.35f, Gdx.graphics.getHeight() - UIsizeUnit * 0.85f, UIsizeUnit * 0.13f);
        }
		shaper.end();
	}
}

class TimeObjectiveMode implements TodosVsTodosObjectiveMode {
	
	private float timer;
	private final int duration; // in seconds
	
	TimeObjectiveMode(int duration) {
		this.duration = duration * 60;
	}
	
	@Override
	public void initGame(GameServer server) {
		for(Pj pj : server.partida.pjsArray()) {
			Item.randomTeleport.usedCallback(pj, server);
		}
	}
	
	@Override
	public void checkEnd(GameServer server) {
		if(timer >= duration) {
			Pj winner = null;
			for(Pj pj : server.partida.pjsArray()) {
				if(winner == null || pj.kills > winner.kills) {
					winner = pj;
				}
			}

			if(winner == null) throw new IllegalArgumentException("There must be a winner!");

			Network.WinnerPacket p = new Network.WinnerPacket();
			p.pj_name = winner.name;
            server.sendToAllTCP(p);
		}
	}

    @Override
    public void clientUpdate(float dt, GameClient client) {
        timer += dt;
    }

    @Override
	public void drawUI(float UIsizeUnit, ShapeRenderer shaper, SpriteBatch batch, BitmapFont font, Partida partida, Pj pj) {
		int remaining = duration - (int)timer;
		int minutos = remaining / 60;
		int segundos = remaining % 60;
		
		batch.begin();
		font.draw(batch, minutos+":"+segundos, UIsizeUnit * 1.35f, Gdx.graphics.getHeight() - UIsizeUnit * 0.85f);
		font.draw(batch, pj.kills+" kills", UIsizeUnit * 1.35f, Gdx.graphics.getHeight() - UIsizeUnit * 0.85f + font.getLineHeight()*2);
		batch.end();
	}
}
