package com.ocronite.pillapilla;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

enum Item {
	surpriseBox(Color.WHITE, 1) {
		@Override
		void pickedupCallback(Pj pj, GameServer server) {
			Item newItem = server.partida.dropManager.dropRandomItemExclude(Item.surpriseBox, Item.pielDePlatano);

            Network.ItemGainedPacket p = new Network.ItemGainedPacket();
            p.itemID = newItem.ordinal();
            p.pj_index = pj.index;
            server.sendToTCP(pj.index, p);
		}
	},
	sprint(Color.RED, 1) {
		@Override
		void usedCallback(Pj pj, GameServer server) {
			Network.AddBuffPacket p = new Network.AddBuffPacket();
			p.pj_index = pj.index;
			p.buff_index = sprint.ordinal();
			server.sendToAllTCP(p);
		}
	},
    platano(Color.YELLOW, 1) {
        private final Vector2 tmpDir = new Vector2();

        @Override
        void usedCallback(Pj pj, GameServer server) {
            tmpDir.set(pj.dir).nor().scl(Tile.size);

            Network.SpawnDropPacket p = new Network.SpawnDropPacket();
            p.itemID = pielDePlatano.id();
            p.x = pj.pos.x - tmpDir.x;
            p.y = pj.pos.y - tmpDir.y;
            server.sendToAllTCP(p);
        }
    },
	pielDePlatano(platano.color, 1) {
		@Override
		void pickedupCallback(Pj pj, GameServer server) {
			if(!pj.isImmmune()) {
				Network.AddBuffPacket p = new Network.AddBuffPacket();
				p.pj_index = pj.index;
				p.buff_index = platano.id();
				server.sendToAllTCP(p);
			}
		}
	},
	tinta(Color.GREEN, 1) {
		@Override
		void usedCallback(Pj pj, GameServer server) {
			Network.AddGlobalBuffPacket p = new Network.AddGlobalBuffPacket();
			p.pj_index = pj.index;
			p.buff_index = tinta.id();
			server.sendToAllTCP(p);
		}
	}, 
	traverseWalls(Color.BLUE, 1) {
		@Override
		void usedCallback(Pj pj, GameServer server) {
			Network.AddBuffPacket p = new Network.AddBuffPacket();
			p.pj_index = pj.index;
			p.buff_index = traverseWalls.id();
			server.sendToAllTCP(p);
		}
	},
	randomTeleport(Color.ORANGE, 1) {
		
		float minDisplacement = Tile.size * 5;
		
		@Override
		void usedCallback(Pj pj, GameServer server) {
			Network.TeleportPositionPacket p = new Network.TeleportPositionPacket();
			p.pj_index = pj.index;
			// to avoid staying too close to the start point 
			// TODO use a random direction + distance instead
			while(true) {
				p.x = (MathUtils.random(server.partida.map.length-1))*Tile.size;
				p.y = (MathUtils.random(server.partida.map[0].length-1))*Tile.size;
				
				if(pj.pos.dst(p.x, p.y) > minDisplacement) break;
			}
			p.dirX = pj.dir.x;
			p.dirY = pj.dir.y;
			server.sendToAllTCP(p);
		}
	},
	immune(Color.PINK, 1) {
		@Override
		void usedCallback(Pj pj, GameServer server) {
			Network.AddBuffPacket p = new Network.AddBuffPacket();
			p.pj_index = pj.index;
			p.buff_index = immune.id();
			server.sendToAllTCP(p);
		}
	},
	invisible(Color.SKY, 1) {
        @Override
        void usedCallback(Pj pj, GameServer server) {
            Network.AddBuffPacket p = new Network.AddBuffPacket();
            p.pj_index = pj.index;
            p.buff_index = invisible.id();
            server.sendToAllExceptTCP(pj.index, p);

            Network.AddBuffPacket q = new Network.AddBuffPacket();
            q.pj_index = pj.index;
            q.buff_index = InvisibleSilouette.id;
            server.sendToTCP(pj.index, q);
        }
    },
    reloj(Color.GRAY, 1) {
        @Override
        void usedCallback(Pj pj, GameServer server) {
            for(Pj other : server.partida.pjsArray()) {
                if(other.index != pj.index) {
                    Network.AddBuffPacket p = new Network.AddBuffPacket();
                    p.pj_index = other.index;
                    p.buff_index = reloj.ordinal();
                    server.sendToAllTCP(p);
                }
            }
		}
    },
    borracho(Color.VIOLET, 1) {
        @Override
        void usedCallback(Pj pj, GameServer server) {
            Network.AddBuffPacket p = new Network.AddBuffPacket();
            p.pj_index = pj.index;
            p.buff_index = borracho.ordinal();
            server.sendToAllTCP(p);
        }
    };
	
	final Color color;
    final float dropRatio;
    final float caducidad = 5;
	
	Item(Color color, float dropRatio) {
		this.color = color;
        this.dropRatio = dropRatio;
	}

	void usedCallback(Pj pj, GameServer server) {}

    void pickedupCallback(Pj pj, GameServer server) {
        Network.ItemGainedPacket p = new Network.ItemGainedPacket();
        p.itemID = this.ordinal();
        p.pj_index = pj.index;
        server.sendToAllTCP(p);
    }
	
	int id() { return this.ordinal(); }
	
	static Item byID(int id) {
        if(id < Item.values().length) return values()[id];
		else throw new IllegalArgumentException("No existe ningun Item con valor " + id);
	}
}
