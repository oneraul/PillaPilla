package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;

class TodosVsTodosGameMode extends GameMode {

	private float resetTagsAccumulator; // current remaining time to reset the tags
	private final Array<Pj> saco = new Array<Pj>(); // tmp array used when setting the targets
	
	private final TodosVsTodosObjectiveMode objectiveMode;
	private final float resetTargetsTime;

	TodosVsTodosGameMode(TodosVsTodosOptions options, GameClient client) {
		super(options, client);

        int objective = options.getObjective().value();
        ObjectiveMode objectiveModeEnum = options.getObjectiveMode();
        objectiveMode = objectiveModeEnum.getObjectiveMode(objective);
        resetTagsAccumulator = resetTargetsTime = options.getResetTargetsTime().value();
	}

	enum ObjectiveMode {
        Minutos {
            @Override
            TodosVsTodosObjectiveMode getObjectiveMode(int objective) {
                return new TimeObjectiveMode(objective);
            }
        },
        Vidas {
            @Override
            TodosVsTodosObjectiveMode getObjectiveMode(int objective) {
                return new VidasObjectiveMode(objective);
            }
        };

        abstract TodosVsTodosObjectiveMode getObjectiveMode(int objective);
    }

    @Override
    public void initGame(GameServer server) {
        objectiveMode.initGame(server);

        // send empty targets packet so the first 'round' is to get ready
        Network.ResetTagsPacket p = new Network.ResetTagsPacket();
        p.flee = new int[0];
        p.target = new int[0];
        server.sendToAllTCP(p);
    }
	
    @Override
    public void update(GameServer server) {
        // check and handle game's end
        checkEnd(server);

        // reset tags
        if (resetTagsAccumulator <= 0) {
            resetTags(server);

            // tell the clients
            for (Pj pj : server.partida.pjsArray()) {
				Network.ResetTagsPacket p = new Network.ResetTagsPacket();

				p.target = new int[pj.tagTarget.size];
				for (int i = 0; i < p.target.length; i++) {
					p.target[i] = pj.tagTarget.get(i).index;
				}

				p.flee = new int[pj.fleeFrom.size];
				for (int i = 0; i < p.flee.length; i++) {
					p.flee[i] = pj.fleeFrom.get(i).index;
				}

				server.sendToTCP(pj.index, p);
            }
        }

        // pick up items
        pickUpItems(server);

        // spawn drops
        server.partida.dropManager.tick(server);

        // localUpdate & remove buffs
        updateAndRemoveBuffsAndItems(server);

        // tag pjs
        for (Pj pj : server.partida.pjsArray()) {
            if (!pj.dead) {
            	for(int i = 0; i < pj.tagTarget.size; i++) {
					Pj target = pj.tagTarget.get(i);
                    if(target == null) continue;
					if(!target.isImmmune() && Utils.intersectionCircleCircle(pj.pos.x, pj.pos.y, Pj.size, target.pos.x, target.pos.y, Pj.size)) {
                        pj.kills++;
                		target.vidas--;

                		Network.PjTaggedPacket p;

               			if (target.vidas == 0) {
               			    p = new Network.PjDeadPacket();

                        } else {
                    		p = new Network.PjTaggedPacket();
                            Item.randomTeleport.usedCallback(target, server);
                		}

                		p.pj_index = target.index;
                		p.vidas = target.vidas;
                        p.killer_pj_index = pj.index;
                        p.kills = pj.kills;
                		server.sendToAllTCP(p);
            		}
				}
			}
        }
    }

	@Override
	protected void checkEnd(GameServer server) {
		objectiveMode.checkEnd(server);
	}
	
	private void resetTags(GameServer server) {

        for (Pj pj : server.partida.pjsArray()) {
            pj.tagTarget.clear();
            pj.fleeFrom.clear();
        }

        saco.clear();
        saco.addAll(server.partida.pjsArray());
        for (int j = saco.size - 1; j >= 0; j--) {
            if (saco.get(j).dead) saco.removeIndex(j);
        }

        if (saco.size <= 0) ; // TODO throw exception ? could happen with sudden disconnections
        else if (saco.size == 1) ;
        else if (saco.size == 2) resetTags2pjs();
        else resetTargetMulti();

        for (Pj pj : server.partida.pjsArray()) {
			for (Pj target : pj.tagTarget) {
				target.fleeFrom.add(pj);
			}
        }
    }

    private void resetTags2pjs() {
        Pj a = saco.get(0);
        Pj b = saco.get(1);

        if (MathUtils.randomBoolean()) {
            a.tagTarget.add(b);
        } else {
            b.tagTarget.add(a);
        }
    }

    private void resetTargetMulti() {
        int i = MathUtils.random(saco.size - 1);
        Pj first = saco.get(i);
        saco.removeIndex(i);

        Pj current = first;
        while (saco.size > 0) {
            i = MathUtils.random(saco.size - 1);
            current.tagTarget.add(saco.get(i));
            current = current.tagTarget.peek();
            saco.removeIndex(i);
        }

        current.tagTarget.add(first);
    }

	@Override
    public void drawUI(float UIsizeUnit, ShapeRenderer shaper, SpriteBatch batch, BitmapFont font, Partida partida, Pj pj) {
		
		// reset target timer ui
		shaper.begin(ShapeRenderer.ShapeType.Filled);
        shaper.setColor(Color.WHITE);
        shaper.rect(Gdx.graphics.getWidth() - UIsizeUnit - UIsizeUnit * 0.1f, Gdx.graphics.getHeight() - UIsizeUnit / 2 - UIsizeUnit * 0.1f, UIsizeUnit, UIsizeUnit / 2);
        shaper.setColor(Color.RED);
        shaper.rect(Gdx.graphics.getWidth() - UIsizeUnit + 1 - UIsizeUnit * 0.1f, Gdx.graphics.getHeight() - UIsizeUnit / 2 + 1 - UIsizeUnit * 0.1f, (UIsizeUnit - 2) * (resetTagsAccumulator / resetTargetsTime), UIsizeUnit / 2 - 2);
        shaper.end();

        // reset targets timer number
        batch.begin();
        font.draw(batch, "" + ((int) (resetTagsAccumulator) + 1), Gdx.graphics.getWidth() - UIsizeUnit * 0.6f, Gdx.graphics.getHeight() - UIsizeUnit * 0.3f);
        batch.end();
		
		// ObjectiveMode-specific UI
		objectiveMode.drawUI(UIsizeUnit, shaper, batch, font, partida, pj);
    }

    @Override
    public void clientUpdate(float dt, GameClient client) {
        resetTagsAccumulator -= dt;
        objectiveMode.clientUpdate(dt, client);
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
                            if (o instanceof Network.ResetTagsPacket) {
                                Network.ResetTagsPacket p = (Network.ResetTagsPacket) o;

                                resetTagsAccumulator = resetTargetsTime;

                                Pj myPj = client.partida.pjsMap.get(client.getID());
                                myPj.tagTarget.clear();
                                myPj.fleeFrom.clear();
                                for (int target : p.target) {
                                    myPj.tagTarget.add(client.partida.pjsMap.get(target));
                                }
                                for (int flee : p.flee) {
                                    myPj.fleeFrom.add(client.partida.pjsMap.get(flee));
                                }

                                // set colors
                                for (Pj pj : client.partida.pjsArray()) {
                                    if (pj == myPj) pj.color.set(Color.WHITE);
                                    else if (myPj.tagTarget.contains(pj, true)) pj.color.set(Color.CYAN);
                                    else if (myPj.fleeFrom.contains(pj, true)) pj.color.set(Color.RED);
                                    else pj.color.set(Color.LIGHT_GRAY);
                                }

                            } else if (o instanceof Network.SetVidasPacket) {
                                Network.SetVidasPacket p = (Network.SetVidasPacket) o;
                                Pj pj = client.partida.pjsMap.get(p.pj_index);
                                pj.vidas = p.vidas;

                                if (o instanceof Network.PjTaggedPacket) {
                                    Network.PjTaggedPacket q = (Network.PjTaggedPacket) p;
                                    client.partida.pjsMap.get(q.killer_pj_index).kills = q.kills;

                                    pjTagged(pj);

                                    if (o instanceof Network.PjDeadPacket) {
                                        pj.reset();
                                        pj.dead = true;

                                        if (pj.index == client.getID()) {
                                            // TODO implement local death changes here
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
			}
		};
    }
}
