package com.ocronite.pillapilla;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PlaterolaTest extends ApplicationAdapter {

    private final AssetManager assets = new AssetManager();
    private SpriteBatch batch;
    private ShapeRenderer shaper;
    private OrthographicCamera cam;
    private Texture floorTexture, tableTexture, stoolTexture;
    private NinePatch bucket;

    private static final int unit = 64;

    private int lvlWidth = 210, lvlHeight = 110;

    @Override
    public void create() {
        int w = 16*unit;
        int h = w * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        cam = new OrthographicCamera(w, h);

        shaper = new ShapeRenderer();
        batch = new SpriteBatch();
        assets.load("images/floorTile.png", Texture.class);
        assets.load("images/table.png", Texture.class);
        assets.load("images/stool.png", Texture.class);
        assets.load("images/bucket.atlas", TextureAtlas.class);
        assets.finishLoading();

        floorTexture = assets.get("images/floorTile.png", Texture.class);
        floorTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        tableTexture = assets.get("images/table.png", Texture.class);
        stoolTexture = assets.get("images/stool.png", Texture.class);
        bucket = assets.get("images/bucket.atlas", TextureAtlas.class).createPatch("bucket");
    }

    @Override
    public void render() {
        if(Gdx.input.isKeyPressed(Input.Keys.W)) cam.position.y += 10;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) cam.position.x -= 10;
        if(Gdx.input.isKeyPressed(Input.Keys.S)) cam.position.y -= 10;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) cam.position.x += 10;
        cam.update();
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) lvlHeight += 10;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) lvlHeight -= 10;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) lvlWidth += 10;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) lvlWidth -= 10;

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        float tileCount = 30000 / floorTexture.getWidth();
        batch.draw(floorTexture, -15000, -15000,
                floorTexture.getWidth() * tileCount,
                floorTexture.getHeight() * tileCount,
                0, tileCount, tileCount, 0);


        batch.draw(stoolTexture, -stoolTexture.getWidth()/2, 200);
        batch.draw(tableTexture, 0, 0);
        bucket.draw(batch, 100, 100, lvlWidth+156+142, lvlHeight+160+160);
        batch.end();

        shaper.setProjectionMatrix(cam.combined);
        shaper.begin(ShapeRenderer.ShapeType.Line);
        shaper.setColor(Color.RED);
        shaper.rect(100+156, 100+160, lvlWidth, lvlHeight);
        shaper.end();
    }

    @Override
    public void dispose() {
        shaper.dispose();
        batch.dispose();
        assets.dispose();
    }
}
