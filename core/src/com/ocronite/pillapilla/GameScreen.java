package com.ocronite.pillapilla;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class GameScreen extends ScreenAdapter {

	private SpriteBatch batch, batchUI;
	private BitmapFont font;
	private ShapeRenderer shaperUI;
	private Camera cam;
	private final Pj pj;
	private final Partida partida;
	private final Controller controller;
	private float networkUpdateAccumulator;
    private float resetPingAccumulator;

	private GameClient client;
	private GameServer server;

    private Stage stage;
    private boolean escMenu;

	private final Vector3 tmp = new Vector3();
	
	GameScreen() {
		PillaPilla app = (PillaPilla)Gdx.app.getApplicationListener();
		
		server = app.lobby.server;
		client = app.lobby.client;
		partida = client.partida;

		controller = getControllerImplementation();
		this.pj = client.partida.pjsMap.get(client.getID());
	}

	private Controller getControllerImplementation() {
		if(Gdx.app.getType() == Application.ApplicationType.Android) {
			Gdx.input.setCatchBackKey(true);
			return new MobileController(client);
			
		} else if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
			return new DesktopController(client);
		}

		throw new IllegalArgumentException("No usas PC ni Android. Desde donde pollas estas jugando?!");
	}
	
	@Override
	public void show() {

	    stage = new Stage();
		batch = new SpriteBatch();
		batchUI = new SpriteBatch();
		font = PillaPilla.assets().get("calibri.ttf", BitmapFont.class);
		shaperUI = new ShapeRenderer();

		int w = (int)(16*Tile.size);
		int h = w * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		cam = new OrthographicCamera(w, h);

		ScreenShaker.setCam(cam);

		final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new PauseMenu().inputAdapter);
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(multiplexer);

        ((PillaPilla)(Gdx.app.getApplicationListener())).sounds.playRandomSong();

        client.updateReturnTripTime();
	}

	@Override
	public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		logic(dt);
        if(!escMenu) draw();
        stage.draw();
	}

	private void logic(float dt) {

        stage.act();

        // ping
        final float resetPingTick = 10;
        resetPingAccumulator += dt;
        if(resetPingAccumulator > resetPingTick) {
            resetPingAccumulator -= resetPingTick;
            client.updateReturnTripTime();
        }

		// localUpdate
		controller.update();
		pj.localUpdate(dt, partida.map);

		for(Pj pj : client.partida.pjsArray()) {
			if(pj != this.pj && !pj.dead) pj.interpolationUpdate(dt); // pj specific
		}

		partida.gameMode.clientUpdate(dt, client); // gameMode specific
		
		// network localUpdate
		networkUpdateAccumulator += dt;
		if(networkUpdateAccumulator >= Network.tick) {
			networkUpdateAccumulator -= Network.tick;
			
			if(server != null) server.update();
			
			// send position to the server
			if(!pj.dead) {
				Network.PositionPacket p = new Network.PositionPacket();
				p.pj_index = pj.index;
				p.x = pj.pos.x;
				p.y = pj.pos.y;
				p.dirX = pj.dir.x;
				p.dirY = pj.dir.y;
				client.sendTCP(p);
			}
		}

		// camera localUpdate
		if(!pj.dead) {
			tmp.set(pj.pos.x, pj.pos.y, cam.position.z);
			if(tmp.dst2(cam.position) >= Tile.size*5) { // Tile.size*5 == Item.randomTeleport.minDisplacement
				cam.position.lerp(tmp, 0.3f);
			} else {
				cam.position.set(tmp);
			}

		} else {
			int w = (int)(Tile.size*2 + partida.map.length*Tile.size);
			int h = w * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
			((OrthographicCamera)cam).setToOrtho(false, w, h);
			cam.position.x = cam.viewportWidth*0.5f - Tile.size;
			cam.position.y = cam.viewportHeight*0.5f - Tile.size*Gdx.graphics.getHeight()/Gdx.graphics.getWidth();
		}
		ScreenShaker.update();
		cam.update();
	}

	private void draw() {
		batch.setProjectionMatrix(cam.combined);

        batch.begin();
        batch.setColor(Color.WHITE);

        // draw map
        for(int x = 0; x < partida.map.length; x++) {
            for(int y = 0; y < partida.map[x].length; y++) {

                // screenCulling
                if((x+1)*Tile.size > cam.position.x-cam.viewportWidth/2
                        && (x)*Tile.size < cam.position.x+cam.viewportWidth/2
                        && (y+1)*Tile.size > cam.position.y-cam.viewportHeight/2
                        && y*Tile.size < cam.position.y+cam.viewportHeight/2) {
                    Texture texture = PillaPilla.assets().get("images/" + partida.map[x][y].name() + ".png");
                    batch.draw(texture, x * Tile.size, y * Tile.size, Tile.size, Tile.size);
                }
            }
        }

        // draw drops
        for(Drop drop : partida.drops) {
            batch.setColor(drop.item.color);
            Texture texture = PillaPilla.assets().get("images/" + drop.item.name() + ".png");
            batch.draw(texture, drop.aabb.centerX-drop.aabb.halfWidth, drop.aabb.centerY-drop.aabb.halfHeight, Drop.size, Drop.size);
        }

		// draw pjs
        for(Pj pj : partida.pjsArray()) pj.draw(batch, font);

		// draw pjs' buffs
        for(Pj pj : partida.pjsArray()) pj.drawBuffs(batch);

		// draw global buffs
		for(Buff globalBuff : client.partida.globalBuffs) globalBuff.draw(batch);

        batch.end();

		// draw UI ----------
		float UIsizeUnit = Tile.size * 1.5f;

		// Item UI
		shaperUI.begin(ShapeRenderer.ShapeType.Line);
		shaperUI.setColor(Color.WHITE);
		shaperUI.rect(UIsizeUnit * 0.1f, Gdx.graphics.getHeight() - UIsizeUnit * 1.1f, UIsizeUnit, UIsizeUnit);
		shaperUI.end();

        if(pj.item != null) {
            batchUI.begin();
            batchUI.setColor(pj.item.color);
            Texture icon = PillaPilla.assets().get("images/" + pj.item.name() + ".png");
            batchUI.draw(icon, UIsizeUnit * 0.2f, Gdx.graphics.getHeight() - UIsizeUnit * 1f, UIsizeUnit * 0.8f, UIsizeUnit * 0.8f);
            batchUI.setColor(Color.WHITE);
            batchUI.end();
        }

		client.partida.gameMode.drawUI(UIsizeUnit, shaperUI, batchUI, font, partida, pj);
		controller.drawUI(batchUI, font);

        batchUI.begin();
        font.draw(batchUI, client.getReturnTripTime() + "ms", 50, 50);
        batchUI.end();
	}

	@Override
	public void dispose() {
		System.out.println("dispose gameScreen");
		batch.dispose();
		batchUI.dispose();
		shaperUI.dispose();
	}

	private class PauseMenu extends Table {

	    final InputAdapter inputAdapter;

        private PauseMenu() {
            super(PillaPilla.skin());
            final PauseMenu THIS = this;
            this.setFillParent(true);

            final TextButton exitGameButton = new TextButton("salir", PillaPilla.skin());
            final TextButton resumeGameButton = new TextButton("continuar", PillaPilla.skin());

            this.add(exitGameButton).row();
            this.add(resumeGameButton).padTop(15).row();

            exitGameButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                	PillaPilla.disposeCurrentScreen();
                    PillaPilla app = (PillaPilla)Gdx.app.getApplicationListener();
                    if(app.lobby.server != null) {
                        app.lobby.server.setLobbyListener();
                        app.lobby.server.sendToAllTCP(new Network.GoBackToLobbyPacket());
                    } else {
                        app.lobby.client.close();
                        app.lobby = null;
                        app.setScreen(new MainMenuScreen());
                    }
                }
            });

            resumeGameButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    escMenu = false;
                    THIS.remove();
                }
            });

            ////////////////////////

            inputAdapter = new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    if(keycode == Input.Keys.ESCAPE) {
                        escMenu = !escMenu;
                        if(escMenu) {
                            stage.addActor(THIS);
                            controller.pause();
                        }
                        else THIS.remove();
                        return true;

                    } else if(keycode == Input.Keys.BACK) {
                        escMenu = true;
                        stage.addActor(THIS);
                        controller.pause();
                        return true;

                    } else if(escMenu) return true;

                    return false;
                }
            };
        }
    }
}
