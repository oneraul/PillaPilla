package com.ocronite.pillapilla;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

class Partida {
	Tile[][] map;
	Array<Drop> drops;
	Array<Buff> globalBuffs;
	IntMap<Pj> pjsMap; // los indices son iguales que los los ids de las conexiones. empiezan en 1
    DropManager dropManager;

	GameModeOptions gameModeOptions;
	GameMode gameMode;

	// convenience method
	Array<Pj> pjsArray() {
		return pjsMap.values().toArray();
	}

    enum Map {

        /* structure:
		{
			{0,0	0,1		0,2		0,3}
			{1,0	1,1		1,2		1,3}
			{2,0	2,1		2,2,	2,3}
			...
		}
		 */

        map0(
                new Tile[][] {
                        new Tile[]{Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[]{Tile.floor, Tile.wall, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.wall, Tile.wall, Tile.floor, Tile.floor},
                        new Tile[]{Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.floor},
                        new Tile[]{Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.wall, Tile.floor, Tile.wall, Tile.floor, Tile.floor},
                        new Tile[]{Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.floor},
                        new Tile[]{Tile.wall, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.wall, Tile.floor, Tile.floor},
                        new Tile[]{Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[]{Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.wall, Tile.wall, Tile.floor},
                        new Tile[]{Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[]{Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.floor},
                        new Tile[]{Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor},
                        new Tile[]{Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.floor},
                        new Tile[]{Tile.floor, Tile.wall, Tile.wall, Tile.wall, Tile.floor, Tile.wall, Tile.floor, Tile.wall, Tile.wall, Tile.wall, Tile.floor},
                        new Tile[]{Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.wall, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor}
                }
        ),
        map1(
                new Tile[][] {
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor}
                }
        ),

        pasillo(
                new Tile[][] {
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                        new Tile[] {Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor, Tile.floor},
                }
        );

        final Tile[][] tiles;

        Map(Tile[][] tiles) {
            this.tiles = tiles;
        }
    }
}
