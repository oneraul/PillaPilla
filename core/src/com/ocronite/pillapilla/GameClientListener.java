package com.ocronite.pillapilla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

abstract class GameClientListener extends Listener {
    protected final GameClient client;

    GameClientListener(GameClient client) {
        this.client = client;
    }

    @Override
    public void disconnected(Connection connection) {
        client.handleDisconnection();
    }

    protected final boolean defaultStuff(Object o) {
        if (o instanceof Network.PositionPacket) {
            Network.PositionPacket p = (Network.PositionPacket) o;
            Pj pj = client.partida.pjsMap.get(p.pj_index);

            if (o instanceof Network.TeleportPositionPacket) {
                pj.pos.set(p.x, p.y);
                pj.dir.set(p.dirX, p.dirY);
                pj.newPositionSnapshot.set(pj.pos);
                pj.newDirSnapshot.set(pj.dir);
            }

            pj.oldPositionSnapshot.set(pj.newPositionSnapshot);
            pj.newPositionSnapshot.set(p.x, p.y);
            pj.oldDirSnapshot.set(pj.oldDirSnapshot);
            pj.newDirSnapshot.set(p.dirX, p.dirY);
            pj.interpolationTimer = 0;

        } else if (o instanceof Network.SpawnDropPacket) {
            Network.SpawnDropPacket p = (Network.SpawnDropPacket) o;
            Drop drop = Drop.byID(p.x, p.y, p.itemID);
            client.partida.drops.add(drop);

        } else if (o instanceof Network.DropPickedPacket) {
            Network.DropPickedPacket p = (Network.DropPickedPacket) o;
            client.partida.drops.removeIndex(p.index);

        } else if (o instanceof Network.ItemGainedPacket) {
            Network.ItemGainedPacket p = (Network.ItemGainedPacket) o;

            Pj pj = client.partida.pjsMap.get(p.pj_index);
            if (pj.item == null) {
                Item newItem = Item.byID(p.itemID);
                pj.itemCaducidad = newItem.caducidad;
                pj.item = newItem;
            }

        } else if (o instanceof Network.ItemLostPacket) {
            Network.ItemLostPacket p = (Network.ItemLostPacket) o;

            Pj pj = client.partida.pjsMap.get(p.pj_index);
            pj.item = null;

        } else if (o instanceof Network.AddBuffPacket) {
            Network.AddBuffPacket p = (Network.AddBuffPacket) o;

            Pj pj = client.partida.pjsMap.get(p.pj_index);
            if (pj != null) {
                Buff buff = Buff.byID(p.buff_index, pj);

                if(o instanceof Network.AddGlobalBuffPacket) {
                    client.partida.globalBuffs.add(buff);
                } else {
                    pj.buffs.add(buff);
                }
            }

        } else if (o instanceof Network.RemoveBuffPacket) {
            Network.RemoveBuffPacket p = (Network.RemoveBuffPacket) o;

            if(o instanceof Network.RemoveGlobalBuffPacket) {
                client.partida.globalBuffs.removeIndex(p.buff_index);

            } else {
                Pj pj = client.partida.pjsMap.get(p.pj_index);
                pj.buffs.get(p.buff_index).end();
                pj.buffs.removeIndex(p.buff_index);
            }

        } else if (o instanceof Network.PlayerDisconnectedPacket) {
            Network.PlayerDisconnectedPacket p = (Network.PlayerDisconnectedPacket) o;
            client.handlePlayerDisconnected(p.pj_index);

        } else if (o instanceof Network.StartGamePacket) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    ((Game)(Gdx.app.getApplicationListener())).setScreen(new GameScreen());
                }
            });

        } else if (o instanceof Network.WinnerPacket) {
            final Network.WinnerPacket p = (Network.WinnerPacket)o;
            client.setLobbyListener();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    client.partida.gameMode = null;
                    PillaPilla app = (PillaPilla)Gdx.app.getApplicationListener();
                    app.sounds.stopMusic();
                    app.setScreen(new ScoresScreen(p.pj_name));
                }
            });

        } else if(o instanceof Network.GoBackToLobbyPacket) {
            client.setLobbyListener();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    client.partida.gameMode = null;
                    PillaPilla app = (PillaPilla)Gdx.app.getApplicationListener();
                    app.sounds.stopMusic();
                    app.setScreen(app.lobby);
                }
            });
        }

        else return false;
        return true;
    }

    protected final void pjTagged(Pj pj) {
        ((PillaPilla)(Gdx.app.getApplicationListener())).sounds.play(Sounds.Sound.death);
        Gdx.input.vibrate(50);
        ScreenShaker.shake();

        pj.setDyingDrawState();
    }
}
