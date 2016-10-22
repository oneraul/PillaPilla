package com.ocronite.pillapilla;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Listener;

abstract class GameMode {
	protected abstract void update(GameServer server);
	protected abstract void clientUpdate(float dt, GameClient client);
	protected abstract void drawUI(float UIsizeUnit, ShapeRenderer shaper, SpriteBatch batch, BitmapFont font, Partida partida, Pj pj);
	protected abstract Listener getGameClientListener(GameClient client);
	protected abstract void initGame(GameServer server);
	protected abstract void checkEnd(GameServer server);

    GameMode(GameModeOptions options, GameClient client) {

        // reset/init game stuffDrop
        client.partida.drops = new Array<Drop>();
        client.partida.globalBuffs = new Array<Buff>();
        client.partida.pjsMap = new IntMap<Pj>();
        for(IntMap.Entry<String> entry : client.lobbyPlayerNames.entries()) {
            client.partida.pjsMap.put(entry.key, new Pj(entry.key, entry.value));
        }
        client.partida.map = options.getMap().tiles;
        client.partida.dropManager = options.getDropManager(client);
    }
	
	protected void updateAndRemoveBuffsAndItems(GameServer server) {
		for (Pj pj : server.partida.pjsArray()) {
            for (int i = pj.buffs.size-1; i >= 0; i--) {
                if (pj.buffs.get(i).update(Network.tick)) {
                    Network.RemoveBuffPacket p = new Network.RemoveBuffPacket();
                    p.pj_index = pj.index;
                    p.buff_index = i;
                    server.sendToAllTCP(p);
                }
            }

            if(pj.item != null && pj.itemCaducidad <= 0) {
                Network.ItemLostPacket p = new Network.ItemLostPacket();
                p.pj_index = pj.index;
                server.sendToAllTCP(p);
            }
        }

        for(int i = server.partida.globalBuffs.size-1; i >= 0; i--) {
            Buff globalBuff = server.partida.globalBuffs.get(i);
            if(globalBuff.update(Network.tick)) {
                Network.RemoveGlobalBuffPacket p = new Network.RemoveGlobalBuffPacket();
                p.pj_index = -123; // a non-valid pj id
                p.buff_index = i;
                server.sendToAllTCP(p);
            }
        }
	}
	
	protected void pickUpItems(GameServer server) {
        for (Pj pj : server.partida.pjsArray()) {
            for (int i = server.partida.drops.size-1; i >= 0; i--) {
                Drop drop = server.partida.drops.get(i);

                if(Utils.intersectionCircleCircle(pj.pos.x, pj.pos.y, Pj.size, drop.aabb.centerX, drop.aabb.centerY, Drop.outerCircleRadius)) {
                    // remove drop from list
                    Network.DropPickedPacket q = new Network.DropPickedPacket();
                    q.index = i;
                    server.sendToAllTCP(q);

                    // item callback
                    drop.item.pickedupCallback(pj, server);
                }
            }
        }
	}

    enum Mode {
        TodosVsTodos {
            @Override
            GameModeOptions getOptions() {
                return new TodosVsTodosOptions();
            }
        }, Zombies {
            @Override
            GameModeOptions getOptions() {
                return new ZombiesOptions();
            }
        };

        abstract GameModeOptions getOptions();
    }
}
