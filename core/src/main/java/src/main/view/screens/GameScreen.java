package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import src.main.controller.GameController;
import src.main.model.Game;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunter;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalProjectile;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardian;
import src.main.model.enviroment.SolidBlock;
import src.main.view.GameAssetManager;
import src.main.view.GameMusic;
import src.main.view.GameSettings;
import src.main.view.HudRenderer;

public class GameScreen extends AbstractScreen {
    private Game game;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private ShapeRenderer shapeRenderer;
    private HudRenderer hudRenderer;

    private OrthogonalTiledMapRenderer mapRenderer;

    private Viewport gameViewport;

    private static final float STEP = 1 / 60f;
    private float accumulator;

    @Override
    public void show() {
        super.show();
        GameMusic.MENU.stop();

        game = new Game();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        shapeRenderer = new ShapeRenderer();
        hudRenderer = new HudRenderer();

        TiledMap map = game.getMapLoader().getTiledMap();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("main");
        float mapW = layer.getWidth() * layer.getTileWidth();
        float mapH = layer.getHeight() * layer.getTileHeight();


        gameViewport = new ExtendViewport(mapW/5f, mapH/10f, camera); //
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new GameController(game, game.getKeyBindings()));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        gameViewport.apply();
        accumulator += delta;
        while (accumulator >= STEP) {
            game.update(STEP);
            accumulator -= STEP;
        }
        camera.position.set(game.getKnight().getPosition().x, game.getKnight().getPosition().y + 30, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{0});
        mapRenderer.render(new int[]{1});

        batch.begin();
        game.getKnight().draw(batch, delta);
        for (Enemy enemy : game.getEnemies()) {
            enemy.draw(batch, delta);
        }
        for (Enemy enemy : game.getEnemies()) {
            if (enemy instanceof CrystalHunter ch) {
                for (CrystalProjectile p : ch.getProjectiles()) {
                    p.draw(batch, delta);
                }
            }
        }
        for (Enemy enemy : game.getEnemies()) {
            if (enemy instanceof CrystalGuardian cg) {
                cg.getLaser().draw(batch, GameAssetManager.laserRegion);
            }
        }
        batch.end();

        hudRenderer.render(batch, game.getKnight().getHp(), game.getKnight().getMaxHp(),
            game.getKnight().getSoul(), game.getKnight().getMaxSoul());



        // 4. "back" (index 3) = foreground overlay
        mapRenderer.render(new int[]{2});

        if (GameSettings.getInstance().isDebugMode()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GREEN);
            for (SolidBlock sb : game.getMapLoader().getSolidBlocks()) {
                shapeRenderer.rect(sb.getBounds().x, sb.getBounds().y,
                    sb.getBounds().width, sb.getBounds().height);
            }
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(game.getKnight().getBoundingBox().x, game.getKnight().getBoundingBox().y,
                game.getKnight().getBoundingBox().width, game.getKnight().getBoundingBox().height);

            shapeRenderer.setColor(Color.YELLOW);
            for (Enemy enemy : game.getEnemies()) {
                shapeRenderer.rect(enemy.getBoundingBox().x, enemy.getBoundingBox().y,
                    enemy.getBoundingBox().width, enemy.getBoundingBox().height);
            }
            shapeRenderer.setColor(Color.CYAN);
            for (Enemy enemy : game.getEnemies()) {
                if (enemy instanceof CrystalHunter ch) {
                    for (CrystalProjectile p : ch.getProjectiles()) {
                        if (p.isDead()) continue;
                        shapeRenderer.rect(p.getBoundingBox().x, p.getBoundingBox().y,
                            p.getBoundingBox().width, p.getBoundingBox().height);
                    }
                }
            }
            shapeRenderer.end();
        }

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
        game.getMapLoader().dispose();
        hudRenderer.dispose();
    }
}
