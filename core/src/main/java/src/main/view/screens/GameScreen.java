package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import src.main.controller.GameController;
import src.main.model.Game;
import src.main.model.enviroment.SolidBlock;
import src.main.model.entity.knight.Knight;
import src.main.view.GameMusic;
import src.main.view.GameSettings;

public class GameScreen extends AbstractScreen {
    private Game game;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private ShapeRenderer shapeRenderer;

    private OrthogonalTiledMapRenderer mapRenderer;

    private Viewport gameViewport;

    @Override
    public void show() {
        super.show();
        GameMusic.MENU.stop();

        game = new Game();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        shapeRenderer = new ShapeRenderer();

        TiledMap map = game.mapLoader.tiledMap;
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("main");
        float mapW = layer.getWidth() * layer.getTileWidth();
        float mapH = layer.getHeight() * layer.getTileHeight();


        gameViewport = new ExtendViewport(mapW/5f, mapH/10f, camera); //
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new GameController(game, game.keyBindings));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        gameViewport.apply();
        game.update(delta);
        camera.position.set(game.knight.getPosition().x, game.knight.getPosition().y + 30, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{0});
        mapRenderer.render(new int[]{1});

        batch.begin();
        Knight knight = game.knight;
        TextureRegion frame = knight.getFrame(delta);
        float x = knight.getPosition().x;
        float y = knight.getPosition().y;

        float scaleFactor = 5f;
        float spriteW = knight.getBoundingBox().width * scaleFactor;
        float spriteH = spriteW * frame.getRegionHeight() / (float) frame.getRegionWidth();

        batch.draw(frame, x + (knight.getBoundingBox().width - spriteW) / 2f,
            y,
             spriteW / 2f, 0,
            spriteW, spriteH,
            knight.facingRight ? -1 : 1, 1, 0);

        batch.end();


        // 4. "back" (index 3) = foreground overlay
        mapRenderer.render(new int[]{2});

        // Debug: SolidBlock ها و boundingBox Knight
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        for (SolidBlock sb : game.mapLoader.solidBlocks) {
            shapeRenderer.rect(sb.bounds.x, sb.bounds.y,
                sb.bounds.width, sb.bounds.height);
        }
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(knight.getBoundingBox().x, knight.getBoundingBox().y,
            knight.getBoundingBox().width, knight.getBoundingBox().height);
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        batch.dispose();
        game.mapLoader.dispose();
    }
}
