package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import src.main.controller.GameController;
import src.main.model.Game;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.enemy.boss.falseKnight.FalseKnight;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunter;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalProjectile;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardian;
import src.main.model.entity.npc.zote.Zote;
import src.main.model.entity.spell.VengefulProjectile;
import src.main.model.entity.spell.HowlingWraithsAoe;
import src.main.model.enviroment.ClimbableWall;
import src.main.model.enviroment.SolidBlock;
import src.main.model.enviroment.Spike;
import src.main.view.GameAssetManager;
import src.main.view.GameMusic;
import src.main.view.GameSettings;
import src.main.view.HudRenderer;
import src.main.view.actors.modal.DialogueBox;

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

    private DialogueBox dialogueBox;

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

        gameViewport = new ExtendViewport(mapW/5f, mapH/10f, camera);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new GameController(game, game.getKeyBindings()));
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void renderDebug() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        for (SolidBlock sb : game.getMapLoader().getSolidBlocks()) {
            shapeRenderer.rect(sb.getBounds().x, sb.getBounds().y,
                sb.getBounds().width, sb.getBounds().height);
        }
        shapeRenderer.setColor(Color.MAGENTA);
        for (Spike spike : game.getMapLoader().getSpikes()) {
            shapeRenderer.rect(spike.getBounds().x, spike.getBounds().y,
                spike.getBounds().width, spike.getBounds().height);
        }
        shapeRenderer.setColor(Color.ORANGE);
        for (ClimbableWall w : game.getMapLoader().getClimbableWalls()) {
            shapeRenderer.rect(w.getBounds().x, w.getBounds().y,
                w.getBounds().width, w.getBounds().height);
        }
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(game.getKnight().getBoundingBox().x, game.getKnight().getBoundingBox().y,
            game.getKnight().getBoundingBox().width, game.getKnight().getBoundingBox().height);
        shapeRenderer.setColor(Color.YELLOW);
        for (Enemy enemy : game.getEnemies()) {
            shapeRenderer.rect(enemy.getBoundingBox().x, enemy.getBoundingBox().y,
                enemy.getBoundingBox().width, enemy.getBoundingBox().height);
        }
        shapeRenderer.setColor(Color.PURPLE);
        for (Rectangle zone : game.getMapLoader().getZones()) {
            shapeRenderer.rect(zone.x, zone.y, zone.width, zone.height);
        }
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(game.getZote().getBoundingBox().x, game.getZote().getBoundingBox().y,
            game.getZote().getBoundingBox().width, game.getZote().getBoundingBox().height);
        if (game.isInBossFight()) {
            FalseKnight fk = game.getFalseKnight();
            if (fk != null) {
                shapeRenderer.setColor(Color.CYAN);
                shapeRenderer.rect(fk.getAttackHitbox().x, fk.getAttackHitbox().y,
                    fk.getAttackHitbox().width, fk.getAttackHitbox().height);
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(fk.getStunHitbox().x, fk.getStunHitbox().y,
                    fk.getStunHitbox().width, fk.getStunHitbox().height);
            }
        }
        shapeRenderer.end();

        batch.begin();
        BitmapFont font = skin.getFont("default");
        font.draw(batch, "Knight: " + game.getKnight().getCurrentState().name(),
            game.getKnight().getBoundingBox().x,
            game.getKnight().getBoundingBox().y + game.getKnight().getBoundingBox().height + 20);
        font.draw(batch, "HP:" + game.getKnight().getHp() + "/" + game.getKnight().getMaxHp()
            + " Soul:" + game.getKnight().getSoul(),
            game.getKnight().getBoundingBox().x,
            game.getKnight().getBoundingBox().y - 10);
        for (Enemy e : game.getEnemies()) {
            String label = e.getClass().getSimpleName() + " HP:" + e.getHp();
            if (e instanceof FalseKnight fk) {
                label += " " + fk.getCurrentState().name();
            }
            font.draw(batch, label,
                e.getBoundingBox().x,
                e.getBoundingBox().y + e.getBoundingBox().height + 15);
        }
        batch.end();
    }

    @Override
    public void render(float delta) {
        gameViewport.apply();
        accumulator += delta;
        while (accumulator >= STEP) {
            game.update(STEP);
            accumulator -= STEP;
        }
        float targetX = game.getKnight().getPosition().x;
        float targetY = game.getKnight().getPosition().y + 30;

        // Boss arena camera clamp
        if (game.isInBossFight() && game.getBossArena() != null) {
            Rectangle arena = game.getBossArena();
            float halfW = camera.viewportWidth / 2f;
            float halfH = camera.viewportHeight / 2f;
            if (arena.width <= camera.viewportWidth) {
                targetX = arena.x + arena.width / 2f;
            } else {
                targetX = Math.min(Math.max(targetX, arena.x + halfW), arena.x + arena.width - halfW);
            }
            if (arena.height <= camera.viewportHeight) {
                targetY = arena.y + arena.height / 2f;
            } else {
                targetY = Math.min(Math.max(targetY, arena.y + halfH), arena.y + arena.height - halfH);
            }
        }

        // Camera shake
        if (game.getCameraShakeTimer() > 0) {
            float intensity = game.getCameraShakeIntensity();
            targetX += (float) (Math.random() - 0.5f) * intensity * 2;
            targetY += (float) (Math.random() - 0.5f) * intensity * 2;
        }

        camera.position.set(targetX, targetY, 0);
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
            if (enemy instanceof CrystalHunter ch) {
                for (CrystalProjectile p : ch.getProjectiles())
                    p.draw(batch, delta);
            }
            if (enemy instanceof CrystalGuardian cg)
                cg.getLaser().draw(batch, GameAssetManager.laserRegion);
        }
        game.getZote().draw(batch, delta);
        if (game.getZote().isInRange(game.getKnight().getPosition()) && !game.isDialogueActive()) {
            skin.getFont("default").draw(batch, "[E] Talk",
                game.getZote().getPosition().x - 20,
                game.getZote().getPosition().y + 40);
        }
        batch.end();

        hudRenderer.render(batch, game.getKnight().getHp(), game.getKnight().getMaxHp(),
            game.getKnight().getSoul(), game.getKnight().getMaxSoul());

        String toast = game.consumePendingToast();
        if (toast != null) {
            openToast(toast);
        }

        if (game.consumeDialogueAdvance()) {
            if (dialogueBox != null && !dialogueBox.isAnimationComplete()) {
                dialogueBox.skipAnimation();
            } else {
                game.interact();
            }
        }

        if (game.isDialogueActive()) {
            if (dialogueBox == null) {
                dialogueBox = new DialogueBox(skin);
                dialogueBox.show(stage, game.getCurrentDialogueText());
            } else {
                dialogueBox.setText(game.getCurrentDialogueText());
            }
        } else {
            if (dialogueBox != null) {
                dialogueBox.remove();
                dialogueBox = null;
            }
        }

        mapRenderer.render(new int[]{2});

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        if (game.getKnight().hasSharpShadow() && game.getKnight().isDashing()) {
            Animation<TextureRegion> dashAnim = GameAssetManager.dashEffectAnim;
            if (dashAnim != null) {
                TextureRegion frame = dashAnim.getKeyFrame(game.getKnight().getDashTimer());
                float s = 0.6f;
                float w = frame.getRegionWidth() * s;
                float h = frame.getRegionHeight() * s;
                float kx = game.getKnight().getPosition().x;
                float ky = game.getKnight().getPosition().y;
                batch.draw(frame, kx, ky - 8, w, h);
            }
        }
        for (VengefulProjectile p : game.getSpellProjectiles())
            p.draw(batch, delta);
        for (HowlingWraithsAoe aoe : game.getSpellAoes())
            aoe.draw(batch, delta);
        batch.end();

        if (GameSettings.getInstance().isDebugMode())
            renderDebug();

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
        super.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        batch.dispose();
        game.getMapLoader().dispose();
        hudRenderer.dispose();
    }
}
