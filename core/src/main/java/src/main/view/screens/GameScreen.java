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
import src.main.model.enviroment.ClimbableWall;
import src.main.model.enviroment.SolidBlock;
import src.main.model.enviroment.Spike;
import src.main.view.AchievementManager;
import src.main.view.GameAssetManager;
import src.main.view.GameMusic;
import src.main.view.GameSettings;
import src.main.view.HudRenderer;
import src.main.view.popup.AchievementPopup;
import src.main.view.actors.modal.DialogueBox;

public class GameScreen extends AbstractScreen {
    private static GameScreen currentInstance;

    private Game game;
    private SpriteBatch batch;
    private AchievementPopup achievementPopup;
    private OrthographicCamera camera;

    private ShapeRenderer shapeRenderer;
    private HudRenderer hudRenderer;

    private OrthogonalTiledMapRenderer mapRenderer;

    private Viewport gameViewport;

    private static final float STEP = 1 / 60f;
    private float accumulator;
    private float mapW, mapH;
    private static final float CAMERA_LERP = 0.06f;

    private DialogueBox dialogueBox;
    private boolean resourcesCreated;
    private String lastArea = null;

    public GameScreen() {
        this.game = null;
    }

    public GameScreen(Game game) {
        this.game = game;
    }

    public static GameScreen getCurrentInstance() { return currentInstance; }

    @Override
    public void show() {
        if (currentInstance != null && currentInstance != this) {
            currentInstance.dispose();
        }
        currentInstance = this;

        super.show();
        GameMusic.MENU.stop();

        if (!resourcesCreated) {
            if (game == null) game = new Game();
            batch = new SpriteBatch();
            camera = new OrthographicCamera();
            shapeRenderer = new ShapeRenderer();
            hudRenderer = new HudRenderer();

            TiledMap map = game.getMapLoader().getTiledMap();
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("main");
            mapW = layer.getWidth() * layer.getTileWidth();
            mapH = layer.getHeight() * layer.getTileHeight();

            gameViewport = new ExtendViewport(mapW/5f, mapH/10f, camera);
            mapRenderer = new OrthogonalTiledMapRenderer(map);
            resourcesCreated = true;
        }

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new GameController(game, game.getKeyBindings()));
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        achievementPopup = new AchievementPopup(stage);
        game.getAchievementManager().addListener(achievementPopup);
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
        updateAreaMusic();
        float targetX = game.getKnight().getPosition().x;
        float targetY = game.getKnight().getPosition().y + 30;

        // Clamp target to boss arena or map bounds
        float halfW = camera.viewportWidth / 2f;
        float halfH = camera.viewportHeight / 2f;
        if (game.isInBossFight() && game.getBossArena() != null) {
            Rectangle arena = game.getBossArena();
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
        } else {
            targetX = Math.min(Math.max(targetX, halfW), mapW - halfW);
            targetY = Math.min(Math.max(targetY, halfH), mapH - halfH);
        }

        // Smooth camera follow (lerp)
        camera.position.x += (targetX - camera.position.x) * CAMERA_LERP;
        camera.position.y += (targetY - camera.position.y) * CAMERA_LERP;

        // Camera shake (applied after lerp so it isn't smoothed out)
        if (game.getCameraShakeTimer() > 0) {
            float intensity = game.getCameraShakeIntensity();
            camera.position.x += (float) (Math.random() - 0.5f) * intensity * 2;
            camera.position.y += (float) (Math.random() - 0.5f) * intensity * 2;
        }

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{0, 1, 2});

        batch.begin();
        game.getKnight().draw(batch, delta);
        for (Enemy enemy : game.getEnemies()) {
            enemy.draw(batch, delta);
            if (enemy instanceof CrystalHunter ch) {
                for (CrystalProjectile p : ch.getProjectiles())
                    p.draw(batch, delta);
            }
            if (enemy instanceof CrystalGuardian cg)
                cg.getLaser().draw(batch, GameAssetManager.laserRegion, GameAssetManager.laserCircleAnim);
        }
        game.getZote().draw(batch, delta);
        if (game.getZote().isInRange(game.getKnight().getPosition()) && !game.isDialogueActive()) {
            skin.getFont("default").draw(batch, "[E] Talk",
                game.getZote().getPosition().x - 20,
                game.getZote().getPosition().y + 40);
        }

        if (game.getKnight().isAttacking()) {
            Animation<TextureRegion> slashAnim;
            float offX = 0, offY = 0;
            if (game.getKnight().isAttackDown() || game.getKnight().isPogoAttack()) {
                slashAnim = GameAssetManager.downSlashEffectAnim;
                offY = -8;
            } else if (game.getKnight().isAttackUp()) {
                slashAnim = GameAssetManager.upSlashEffectAnim;
                offY = 10;
            } else {
                slashAnim = GameAssetManager.slashEffectAnim;
                offX = game.getKnight().isFacingRight() ? 0 : -10;
                offY = 4;
            }
            TextureRegion frame = slashAnim.getKeyFrame(game.getKnight().getAttackElapsed());
            float s = 0.6f;
            float w = frame.getRegionWidth() * s;
            float h = frame.getRegionHeight() * s;
            float flipX = game.getKnight().isFacingRight() ? -1 : 1;
            batch.draw(frame,
                game.getKnight().getPosition().x + offX,
                game.getKnight().getPosition().y + offY,
                w / 2, 0, w, h, flipX, 1, 0);
        }

        batch.end();

        hudRenderer.render(batch, game.getKnight().getHp(), game.getKnight().getMaxHp(),
            game.getKnight().getSoul(), game.getKnight().getMaxSoul());

        String toast = game.consumePendingToast();
        if (toast != null) {
            openToast(toast);
        }

        Game.EndGameData endData = game.consumePendingEndGameData();
        if (endData != null) {
            new src.main.view.actors.modal.EndGameModal(endData).show();
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

        mapRenderer.render(new int[]{3});

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
        for (var p : game.getSpellManager().getProjectiles())
            p.draw(batch, delta);
        for (var aoe : game.getSpellManager().getAoes())
            aoe.draw(batch, delta);
        batch.end();

        if (GameSettings.getInstance().isDebugMode())
            renderDebug();

        stage.act(delta);
        stage.draw();
    }

    private void updateAreaMusic() {
        String area;
        if (game.isInBossFight()) {
            area = "BATTLE";
        } else {
            area = game.getCurrentArea();
            if (area == null) area = "SILENT";
        }
        if (area.equals(lastArea)) return;
        lastArea = area;

        GameMusic.CROSSROADS.stop();
        GameMusic.CRYSTAL_PEAKS.stop();
        GameMusic.BATTLE.stop();

        switch (area) {
            case "Forgotten Crossroads" -> GameMusic.CROSSROADS.play();
            case "Crystal Peaks" -> GameMusic.CRYSTAL_PEAKS.play();
            case "BATTLE" -> GameMusic.BATTLE.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (achievementPopup != null) {
            game.getAchievementManager().removeListener(achievementPopup);
            achievementPopup = null;
        }
        super.hide();
    }

    @Override
    public void dispose() {
        currentInstance = null;
        super.dispose();
        stage = null; // prevent double-dispose in hide()
        if (mapRenderer != null) mapRenderer.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (hudRenderer != null) hudRenderer.dispose();
        if (game != null) game.getMapLoader().dispose();
        resourcesCreated = false;
    }
}
