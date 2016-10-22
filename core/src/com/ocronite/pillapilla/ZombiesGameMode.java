package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.badlogic.gdx.graphics.Color;

class ZombiesGameMode extends GameMode {

	private final Array<Pj> saco;
	private float endGameAcumulator;
    private final float gameDuration;

    private Pj exorcist;
    private float exorcistTargetAccumulator;

	/**
        pj.vidas ==  2 -> exorcista (GOLD)
		pj.vidas ==  1 -> vivo (CYAN)
		pj.vidas == -1 -> zombie (RED)
                        -> target del exorcita (VIOLET)
	 */
	
	ZombiesGameMode(ZombiesOptions options, GameClient client) {
	    super(options, client);
        gameDuration = options.getDuration().value() * 60;
        saco = new Array<Pj>();
	}

	@Override
	public void initGame(GameServer server) {
		for(Pj pj : server.partida.pjsArray()) {
			Item.randomTeleport.usedCallback(pj, server);

			Network.SetVidasPacket p = new Network.SetVidasPacket();
			p.pj_index = pj.index;
			p.vidas = 1;
			server.sendToAllTCP(p);
		}

		// set the original zombie and the exorcist
        int originalZombie = server.partida.pjsArray().random().index;
        while(exorcist == null) {
            exorcist = server.partida.pjsArray().random();
            if(exorcist.index == originalZombie) exorcist = null;
        }

		Network.SetVidasPacket p = new Network.SetVidasPacket();
        p.pj_index = originalZombie;
		p.vidas = -1;
		server.sendToAllTCP(p);

        Network.SetVidasPacket q = new Network.SetVidasPacket();
        q.pj_index = exorcist.index;
        q.vidas = 2;
        server.sendToAllTCP(q);
	}

	@Override
    public void update(GameServer server) {
		// pick up items
        pickUpItems(server);

        // spawn drops
		server.partida.dropManager.tick(server);

        // localUpdate & remove buffs
        updateAndRemoveBuffsAndItems(server);

        // tag pjs
       	for (Pj pj : server.partida.pjsArray()) {
            for(int i = pj.tagTarget.size-1; i >= 0; i--) {
                Pj target = pj.tagTarget.get(i);
                if(!target.isImmmune() && Utils.intersectionCircleCircle(pj.pos.x, pj.pos.y, Pj.size, target.pos.x, target.pos.y, Pj.size)) {
                    Network.PjTaggedPacket p = new Network.PjTaggedPacket();
                    p.pj_index = target.index;

                    if(pj.vidas == -1) p.vidas = -1;        // infect new zombie
                    else if(pj.vidas == 2) p.vidas = 1;     // revive zombie

                    server.sendToAllTCP(p);
                }
            }
		}

        // check and handle game's end
        checkEnd(server);

        // update exorcist target
        if(exorcist != null) {
            exorcistTargetAccumulator += Network.tick;
            if (exorcistTargetAccumulator >= 10) {
                exorcistTargetAccumulator -= 10;

                saco.clear();
                saco.addAll(server.partida.pjsArray());
                for (int i = saco.size - 1; i >= 0; i--) {
                    if (saco.get(i).vidas != -1) saco.removeIndex(i);
                }

                Network.SetExorcistTarget p = new Network.SetExorcistTarget();
                p.exorcist_index = exorcist.index;
                p.pj_index = saco.random().index;
                server.sendToAllTCP(p);
            }
        }
	}

	@Override
	protected void checkEnd(GameServer server) {
		saco.clear();
		saco.addAll(server.partida.pjsArray());
		for(int i = saco.size-1; i >= 0; i--) {
			if(saco.get(i).vidas == -1) saco.removeIndex(i);
		}
		if(saco.size == 0) {
			Network.WinnerPacket p = new Network.WinnerPacket();
			p.pj_name = "Zombie";
			server.sendToAllTCP(p);

		} else if(saco.size == server.partida.pjsArray().size
        || endGameAcumulator >= gameDuration) {
            Network.WinnerPacket p = new Network.WinnerPacket();
            p.pj_name = "Vivos";
            server.sendToAllTCP(p);
        }
	}

    @Override
    public void drawUI(float UIsizeUnit, ShapeRenderer shaper, SpriteBatch batch, BitmapFont font, Partida partida, Pj pj) {
		int remaining = (int)(gameDuration - endGameAcumulator);
		int minutos = remaining / 60;
		int segundos = remaining % 60;

		batch.begin();
		font.draw(batch, minutos+":"+segundos, UIsizeUnit * 1.35f, Gdx.graphics.getHeight() - UIsizeUnit * 0.85f);
		batch.end();
    }

	@Override
    public void clientUpdate(float dt, GameClient client) {
		endGameAcumulator += Gdx.graphics.getDeltaTime();
	}

    // --------------------------

    @Override
    public GameClientListener getGameClientListener(GameClient client) {
		return new GameClientListener(client) {
			@Override
			public void received(Connection connection, final Object o) {
			    Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if(!defaultStuff(o)) {

                            if (o instanceof Network.SetVidasPacket) {
                                Network.SetVidasPacket p = (Network.SetVidasPacket) o;

                                Pj pj = client.partida.pjsMap.get(p.pj_index);

                                // exorcista muerto
                                if(pj.vidas == 2) {
                                    exorcist = null;
                                    for(int i = 0; i < pj.tagTarget.size; i++) {
                                        Pj oldTarget = pj.tagTarget.get(i);
                                        oldTarget.fleeFrom.clear();
                                    }
                                }

                                pj.vidas = p.vidas;
                                pj.fleeFrom.clear();
                                pj.tagTarget.clear();

                                // Z -> pos (nuevo zombie)
                                if(pj.vidas == -1) {
                                    for(Pj other : client.partida.pjsArray()) {
                                        if(other != pj) {
                                            if(other.vidas == -1) {
                                                other.tagTarget.removeValue(pj, true);
                                            } else if(other.vidas > 0) {
                                                pj.tagTarget.add(other);
                                                other.fleeFrom.add(pj);
                                            }
                                        }
                                    }

                                    // E -> S (nuevo vivo)
                                } else if(pj.vidas == 1) {
                                    for(Pj other : client.partida.pjsArray()) {
                                        if (other != pj) {
                                            if (other.vidas == -1) {
                                                pj.fleeFrom.add(other);
                                                other.tagTarget.add(pj);
                                            } else if (other.vidas > 0) {
                                                other.fleeFrom.removeValue(pj, true);

                                                if(other.vidas == 2) {
                                                    other.tagTarget.removeValue(pj, true);
                                                }
                                            }
                                        }
                                    }
                                }

                                // set colors
                                if(pj.vidas == -1) pj.color.set(Color.RED);
                                else if(pj.vidas == 1) pj.color.set(Color.CYAN);
                                else if(pj.vidas == 2) pj.color.set(Color.GOLD);

                                // animation, etc
                                if (o instanceof Network.PjTaggedPacket) {
                                    pjTagged(pj);
                                }

                            } else if(o instanceof Network.SetExorcistTarget) {
                                Network.SetExorcistTarget p = (Network.SetExorcistTarget)o;

                                final Pj exorcist = client.partida.pjsMap.get(p.exorcist_index);
                                final Pj target = client.partida.pjsMap.get(p.pj_index);

                                // reset old target
                                for(int i = 0; i < exorcist.tagTarget.size; i++) {
                                    Pj oldTarget = exorcist.tagTarget.get(i);
                                    oldTarget.fleeFrom.removeValue(exorcist, true);
                                    oldTarget.tagTarget.add(exorcist);
                                    oldTarget.color.set(Color.RED);
                                    exorcist.tagTarget.removeValue(oldTarget, true);
                                    exorcist.fleeFrom.add(oldTarget);
                                }

                                // set new target
                                target.tagTarget.removeValue(exorcist, true);
                                target.fleeFrom.add(exorcist);
                                exorcist.tagTarget.add(target);
                                exorcist.fleeFrom.removeValue(target, true);

                                target.color.set(Color.VIOLET);
                            }
                        }


                        printState(client); // TODO remove call and method. only for debug
                    }
                });
			}

			private void printState(GameClient client) {
                for(Pj pj : client.partida.pjsArray()) {
                    System.out.print(pj.index);

                    if(pj.vidas == -1) System.out.print(" (zombie)   ");
                    else if(pj.vidas == 1) System.out.print(" (vivo)     ");
                    else if(pj.vidas == 2) System.out.print(" (exorcista)");

                    if(pj.tagTarget.size > 0) {
                        System.out.print(" persigue a {");
                        for(Pj t : pj.tagTarget) {
                            System.out.print(t.index + ", ");
                        }
                        System.out.print("}");
                    }
                    if(pj.fleeFrom.size > 0) {
                        System.out.print(" huye de {");
                        for(Pj f : pj.fleeFrom) {
                            System.out.print(f.index + ", ");
                        }
                        System.out.print("}");
                    }
                    System.out.println();
                }
                System.out.println("\n");
            }
		};
    }
}
