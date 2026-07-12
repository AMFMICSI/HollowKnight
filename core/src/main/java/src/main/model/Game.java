package src.main.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.data.KeyBindings;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardian;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardianLaser;
import src.main.model.entity.enemy.groundEnemy.huskHornhead.HuskHornhead;
import src.main.model.entity.enemy.flyingEnemy.FlyingEnemy;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunter;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalProjectile;
import src.main.model.entity.enemy.boss.falseKnight.FalseKnight;
import src.main.model.entity.enemy.boss.falseKnight.FalseKnightState;
import src.main.model.entity.enemy.groundEnemy.GroundEnemy;
import src.main.model.entity.enemy.groundEnemy.crawlid.Crawlid;
import src.main.model.entity.npc.zote.Zote;
import src.main.model.entity.particle.ButterflyParticle;
import src.main.model.enviroment.MapLoader;
import src.main.model.enviroment.SolidBlock;
import src.main.model.entity.charm.CharmType;
import src.main.model.entity.knight.Knight;
import src.main.model.entity.spell.SpellManager;
import src.main.model.entity.spell.SpellType;
import src.main.model.enviroment.Spike;
import src.main.model.enviroment.CrackedWall;
import src.main.model.physics.CollisionSystem;
import src.main.model.physics.PhysicsSystem;
import src.main.view.config.TranslationManager;
import src.main.view.manager.AchievementManager;
import src.main.view.manager.UiManager;
import src.main.model.data.SaveData;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") // کپسوله کردن هشدارهای متدهای فریمورک و پرفورمنس
public class Game {
    public record EndGameData(int deathCount, int totalKilled, float playTime) {}

    private static final float SAFE_POINT_INTERACT_RANGE = 40f;

    private final Knight knight;
    private final KeyBindings keyBindings = new KeyBindings();
    private final MapLoader mapLoader;
    private List<Enemy> enemies;
    private final Zote zote;
    private boolean dialogueActive;
    private String currentDialogueText;
    private boolean dialogueAdvanceRequested;
    private Rectangle bossArena;
    private boolean inBossFight = false;
    private float cameraShakeTimer = 0;
    private float cameraShakeIntensity = 0;
    private boolean gatesActivated = false;
    private FalseKnight falseKnight;
    private final List<SolidBlock> bossGateBlocks = new ArrayList<>();
    private String pendingToast = null;
    private final SpellManager spellManager;
    private final List<Vector2> safePoints;
    private final List<CrackedWall> crackedWalls;
    private final Vector2 spawnHiddenRoom;
    private final Vector2 respawnAfterHiddenRoom;
    private final AchievementManager achievementManager = UiManager.achievements;
    private int saveSlot = -1;
    private float playTime = 0;
    private int deathCount = 0;
    private int totalEnemiesKilled = 0;
    private boolean gameCompleted = false;
    private EndGameData pendingEndGameData = null;
    private String currentArea = null;
    private boolean paused = false;
    private final List<ButterflyParticle> butterflies = new ArrayList<>();
    private final List<ButterflyParticle> ambientButterflies = new ArrayList<>();
    private final List<Vector2> butterflySpawnPoints;

    public Knight getKnight() { return knight; }
    public KeyBindings getKeyBindings() { return keyBindings; }
    public MapLoader getMapLoader() { return mapLoader; }
    public List<Enemy> getEnemies() { return enemies; }
    public Zote getZote() { return zote; }
    public boolean isDialogueActive() { return dialogueActive; }
    public boolean isPaused() { return paused; }
    public void setPaused(boolean p) { this.paused = p; }
    public String getCurrentDialogueText() { return currentDialogueText; }
    public void requestDialogueAdvance() { dialogueAdvanceRequested = true; }
    public boolean consumeDialogueAdvance() {
        boolean v = dialogueAdvanceRequested;
        dialogueAdvanceRequested = false;
        return v;
    }

    public Game() {
        this(-1, null);
    }

    public Game(int slot, SaveData data) {
        mapLoader = new MapLoader();
        knight = new Knight(mapLoader.getSpawnPoint().x, mapLoader.getSpawnPoint().y, keyBindings);
        spawnEnemies();
        findBossArena();

        Vector2 zs = mapLoader.getZoteSpawnPoint();
        zote = new Zote(zs.x, zs.y);
        safePoints = mapLoader.getSafePoints();
        crackedWalls = mapLoader.getCrackedWalls();
        spawnHiddenRoom = mapLoader.getSpawnHiddenRoom();
        respawnAfterHiddenRoom = mapLoader.getRespawnAfterHiddenRoom();
        butterflySpawnPoints = mapLoader.getButterflySpawnPoints();
        for (Vector2 sp : butterflySpawnPoints)
            ambientButterflies.add(new ButterflyParticle(sp.x, sp.y, true));

        spellManager = new SpellManager(knight, enemies, mapLoader.getSolidBlocks());
        spellManager.setOnKill(enemy -> {
            totalEnemiesKilled++;
            achievementManager.onEnemyKilled(enemy.getClass().getSimpleName());
        });

        if (slot >= 0 && data != null) {
            saveSlot = slot;
            data.applyTo(knight, achievementManager);
        }
    }

    private void spawnEnemies() {
        enemies = new ArrayList<>();
        for (MapLoader.EnemySpawnInfo info : mapLoader.getEnemySpawnInfos()) {
            Enemy e = switch (info.enemyType) {
                case "Crawlid" -> new Crawlid(
                    info.position.x, info.position.y, info.zone, knight::getPosition);
                case "CrystalHunter" -> new CrystalHunter(
                    info.position.x, info.position.y, info.zone, knight::getPosition);
                case "HuskHornhead" -> new HuskHornhead(
                    info.position.x, info.position.y, knight::getPosition);
                case "CrystalGuardian" -> new CrystalGuardian(
                    info.position.x, info.position.y, info.zone, new CrystalGuardian.KnightRef() {
                    public Vector2 getPosition() { return knight.getPosition(); }
                    public Rectangle getBoundingBox() { return knight.getBoundingBox(); }
                });
                case "FalseKnight" -> {
                    FalseKnight fk = new FalseKnight(
                        info.position.x, info.position.y, knight::getPosition);
                    falseKnight = fk;
                    yield fk;
                }
                default -> throw new RuntimeException("Unknown enemy: " + info.enemyType);
            };
            enemies.add(e);
        }
        for (Enemy e : enemies) e.setSolidBlocks(mapLoader.getSolidBlocks());
    }

    private void findBossArena() {
        bossArena = null;
        for (MapLoader.EnemySpawnInfo info : mapLoader.getEnemySpawnInfos()) {
            if ("FalseKnight".equals(info.enemyType) && info.zone != null) {
                bossArena = info.zone;
                break;
            }
        }
        if (falseKnight != null && bossArena != null) {
            falseKnight.setZone(bossArena);
        }
    }

    public void update(float delta) {
        if (paused) return;
        playTime += delta;

        updateBossArena();
        updateCurrentArea();
        for (Vector2 sp : safePoints) {
            if (knight.getPosition().dst(sp) < SAFE_POINT_INTERACT_RANGE
                && Gdx.input.isKeyJustPressed(keyBindings.get("INTERACT"))) {
                knight.setSpawnPoint(sp.x, sp.y);
                break;
            }
        }
        updateKnight(delta);

        if (knight.consumeJustRespawned()) {
            deathCount++;
            if (inBossFight) releaseBossFight();
        }

        updateCombat(); // اصلاح پارامتر بلااستفاده
        checkCrackedWallTeleport();
        updateEnemies(delta);
        SpellType castType = knight.consumePendingCastResult();
        if (castType != null) triggerCameraShake(2f, 0.2f);
        spellManager.firePending(castType);
        spellManager.updateProjectiles(delta);
        spellManager.updateAoes(delta);
        if (knight.consumePendingSoulToast()) {
            pendingToast = TranslationManager.get("toast.no_soul");
        }
        updateProjectiles(delta);

        if (knight.consumeJustDamaged()) {
            triggerCameraShake(3f, 0.3f);
        }

        respawnEnemies();
        checkPogoAchievement();
        knight.updateAnimationState();
        updateZote(delta);
        updateButterflies(delta);

        if (cameraShakeTimer > 0) cameraShakeTimer -= delta;
    }

    private void updateKnight(float delta) {
        if (knight.isDead()) {
            knight.update(delta);
            return;
        }
        knight.update(delta);
        if (knight.isNoclipMode()) {
            Vector2 pos = knight.getPosition();
            pos.x += knight.getVelocityX() * delta;
            pos.y += knight.getVelocityY() * delta;
            knight.getBoundingBox().setPosition(pos.x, pos.y);
        } else {
            CollisionSystem.resolve(knight, mapLoader.getSolidBlocks(),
                mapLoader.getSpikes(), mapLoader.getClimbableWalls(), crackedWalls, delta);
        }
    }

    private void updateBossArena() {
        if (bossArena == null) return;

        if (!inBossFight && bossArena.contains(knight.getPosition().x, knight.getPosition().y)) {
            inBossFight = true;
            if (falseKnight != null) falseKnight.setActive(true);
        }

        if (inBossFight && !gatesActivated) {
            for (Rectangle gate : mapLoader.getBossGates()) {
                SolidBlock block = new SolidBlock(gate.x, gate.y, gate.width, gate.height);
                mapLoader.getSolidBlocks().add(block);
                bossGateBlocks.add(block);
            }
            if (bossArena != null) {
                for (Rectangle gate : mapLoader.getBossGates()) {
                    if (knight.getBoundingBox().overlaps(gate)) {
                        knight.getPosition().x = bossArena.x + bossArena.width / 2f
                            - knight.getBoundingBox().width / 2f;
                        knight.getBoundingBox().setPosition(knight.getPosition());
                        break;
                    }
                }
            }
            gatesActivated = true;
        }

        if (inBossFight && falseKnight != null && falseKnight.isDead()
            && falseKnight.isDeadAnimationDone()) {
            releaseBossFight();
            if (!gameCompleted) {
                gameCompleted = true;
                achievementManager.onBossDefeated();
                achievementManager.onGameCompleted(playTime);
                pendingEndGameData = new EndGameData(deathCount, totalEnemiesKilled, playTime);
            }
        }
    }

    private void updateButterflies(float delta) {
        for (int i = 0; i < ambientButterflies.size(); i++) {
            ButterflyParticle b = ambientButterflies.get(i);
            b.update(delta);
            if (b.isDead()) {
                Vector2 sp = butterflySpawnPoints.get(i);
                ambientButterflies.set(i, new ButterflyParticle(sp.x, sp.y, true));
            }
        }
        for (ButterflyParticle b : butterflies) b.update(delta);
        butterflies.removeIf(ButterflyParticle::isDead);
    }

    public void spawnButterfly(float x, float y) {
        butterflies.add(new ButterflyParticle(x, y));
    }

    public List<ButterflyParticle> getButterflies() {
        List<ButterflyParticle> all = new ArrayList<>(ambientButterflies);
        all.addAll(butterflies);
        return all;
    }

    private void updateZote(float delta) {
        zote.update(delta);
        zote.updateProximity(knight.getPosition(), delta);
    }

    private void updateCombat() { // اصلاح متد با حذف پارامتر اضافی دمیج
        if (!knight.isAttacking() || knight.isHitRegistered()) return;

        Rectangle hitbox = buildAttackHitbox();
        checkEnemyHits(hitbox);
        if (!knight.isHitRegistered()) checkZoteHit(hitbox);
        if (!knight.isHitRegistered()) checkCrackedWallHit(hitbox);
        if (knight.isPogoAttack() && !knight.isHitRegistered()) checkSpikeHit(hitbox);
    }

    private Rectangle buildAttackHitbox() {
        if (knight.isPogoAttack()) {
            return new Rectangle(
                knight.getBoundingBox().x + 2,
                knight.getBoundingBox().y - 28,
                knight.getBoundingBox().width - 4,
                28);
        }
        float hx = knight.isFacingRight()
            ? knight.getBoundingBox().x + knight.getBoundingBox().width
            : knight.getBoundingBox().x - 30;
        return new Rectangle(hx, knight.getBoundingBox().y + 10, 30, 30);
    }

    private void checkEnemyHits(Rectangle hitbox) {
        for (Enemy enemy : enemies) {
            if (enemy.isDead() || enemy.isDeadAnimationDone()) continue;

            if (enemy instanceof FalseKnight fk && fk.isStunned()) {
                if (hitbox.overlaps(fk.getStunHitbox())) {
                    registerHit(enemy);
                    fk.takeDamage(knight.getAttackDamage());
                    break;
                }
                continue;
            }

            if (hitbox.overlaps(enemy.getBoundingBox())) {
                registerHit(enemy);
                enemy.takeDamage(knight.getAttackDamage());
                if (enemy.isDead()) {
                    totalEnemiesKilled++;
                    achievementManager.onEnemyKilled(enemy.getClass().getSimpleName());
                }
                break;
            }
        }
    }

    private void registerHit(Enemy enemy) {
        knight.setHitRegistered(true);
        knight.addSoul(knight.getSoulPerHit());
        applyHeavyBlowKnockback(enemy);
        if (knight.isPogoAttack()) knight.doPogoBounce();
    }

    private void checkZoteHit(Rectangle hitbox) {
        if (hitbox.overlaps(zote.getBoundingBox())) {
            knight.setHitRegistered(true);
            zote.takeDamage();
            zote.setFacingRight(knight.getPosition().x >= zote.getPosition().x);
        }
    }

    private void checkCrackedWallHit(Rectangle hitbox) {
        for (CrackedWall wall : crackedWalls) {
            if (!wall.isIntact()) continue;
            if (hitbox.overlaps(wall.getBounds())) {
                knight.setHitRegistered(true);
                wall.registerHit();
                if (!wall.isIntact()) teleportForWall(wall);
                break;
            }
        }
    }

    private void checkSpikeHit(Rectangle hitbox) {
        for (Spike spike : mapLoader.getSpikes()) {
            if (hitbox.overlaps(spike.getBounds())) {
                knight.setHitRegistered(true);
                knight.doPogoBounce();
                break;
            }
        }
    }

    private void teleportForWall(CrackedWall wall) {
        Rectangle wb = wall.getBounds();
        if (wb.x > 2000) {
            knight.getPosition().set(spawnHiddenRoom.x, spawnHiddenRoom.y);
            knight.getBoundingBox().setPosition(spawnHiddenRoom.x, spawnHiddenRoom.y);
            if (!knight.isCharmEquipped(CharmType.VOID_HEART)) {
                knight.equipCharm(CharmType.VOID_HEART);
                pendingToast = TranslationManager.get("toast.void_heart");
            }
        } else {
            knight.getPosition().set(respawnAfterHiddenRoom.x, respawnAfterHiddenRoom.y);
            knight.getBoundingBox().setPosition(respawnAfterHiddenRoom.x, respawnAfterHiddenRoom.y);
        }
        knight.setVelocityX(0);
        knight.setVelocityY(0);
    }

    private void checkCrackedWallTeleport() {
        for (CrackedWall wall : crackedWalls) {
            if (wall.isIntact()) continue;
            if (knight.getBoundingBox().overlaps(wall.getBounds())) {
                teleportForWall(wall);
                break;
            }
        }
    }

    private void applyHeavyBlowKnockback(Enemy enemy) {
        if (!enemy.isDead() && knight.isCharmEquipped(CharmType.HEAVY_BLOW)) {
            enemy.setVelocityX(enemy.getVelocityX() * 2f);
            enemy.setVelocityY(enemy.getVelocityY() * 2f);
        }
    }

    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                updateDeadEnemy(enemy, delta);
                continue;
            }
            updateAliveEnemy(enemy, delta);

            if (enemy.getBoundingBox().overlaps(knight.getBoundingBox()) && !enemy.isDead()) {
                if (knight.trySharpShadowHit(enemy)) {
                    enemy.takeDamage(1);
                    knight.addSoul(knight.getSoulPerHit());
                } else {
                    knight.takeDamage();
                }
            }
        }
    }

    private void updateDeadEnemy(Enemy enemy, float delta) {
        enemy.update(delta);
        if (!(enemy instanceof FalseKnight) && !enemy.isDeadAnimationDone()) {
            if (!enemy.isOnGround()) {
                enemy.setVelocityY(enemy.getVelocityY() - PhysicsSystem.GRAVITY * delta);
            }
            if (enemy instanceof FlyingEnemy) {
                enemy.getPosition().x += enemy.getVelocityX() * delta;
                enemy.getPosition().y += enemy.getVelocityY() * delta;
                enemy.getBoundingBox().setPosition(enemy.getPosition());
            } else {
                CollisionSystem.resolve(enemy, mapLoader.getSolidBlocks(), delta);
            }
        }
    }

    private void updateDeltaParam(Enemy enemy, float delta) {
        // متد کمکی برای پر کردن آپدیت‌های درونی
    }

    private void updateAliveEnemy(Enemy enemy, float delta) {
        float prevVx = enemy.getVelocityX();
        enemy.update(delta);
        if (enemy instanceof FlyingEnemy) {
            enemy.getPosition().x += enemy.getVelocityX() * delta;
            enemy.getPosition().y += enemy.getVelocityY() * delta;
            enemy.getBoundingBox().setPosition(enemy.getPosition());
        } else {
            CollisionSystem.resolve(enemy, mapLoader.getSolidBlocks(), delta);
            if (enemy instanceof GroundEnemy ge)
                ge.onCollisionResolved(prevVx, mapLoader.getSolidBlocks());
        }

        if (enemy instanceof FalseKnight fk) {
            updateFalseKnight(fk); // اصلاح پارامتر بلااستفاده
        }
    }

    private void updateFalseKnight(FalseKnight fk) { // حذف پارامتر بلااستفاده delta
        Rectangle zone = fk.getZone();
        if (zone != null) {
            float px = fk.getPosition().x;
            float py = fk.getPosition().y;
            if (px < zone.x) {
                fk.getPosition().x = zone.x;
                fk.setVelocityX(0);
            } else if (px + fk.getBoundingBox().width > zone.x + zone.width) {
                fk.getPosition().x = zone.x + zone.width - fk.getBoundingBox().width;
                fk.setVelocityX(0);
            }
            if (py < zone.y) {
                fk.getPosition().y = zone.y;
                fk.setVelocityY(0);
            } else if (py + fk.getBoundingBox().height > zone.y + zone.height) {
                fk.getPosition().y = zone.y + zone.height - fk.getBoundingBox().height;
                fk.setVelocityY(0);
            }
            fk.getBoundingBox().setPosition(fk.getPosition());
        }

        if (fk.getAttackHitbox().overlaps(knight.getBoundingBox())) {
            int dmg = fk.isPowerfulHitboxActive() ? 2 : 1;
            knight.takeDamage(dmg);
        }
        if ((fk.getCurrentState() == FalseKnightState.RUN
            || fk.getCurrentState() == FalseKnightState.RUN_ANTIC)
            && fk.getBoundingBox().overlaps(knight.getBoundingBox())) {
            knight.takeDamage();
        }
        if (fk.isShaking()) {
            triggerCameraShake(fk.getShakeIntensity(), fk.getShakeDuration());
        }
    }

    private void updateProjectiles(float delta) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof CrystalHunter ch) {
                updateCrystalProjectiles(delta, ch);
            } else if (enemy instanceof CrystalGuardian cg && cg.getLaser().isActive()) {
                updateCrystalGuardianLasers(delta, cg);
            }
        }
    }

    private void updateCrystalProjectiles(float delta, CrystalHunter ch) {
        for (CrystalProjectile p : ch.getProjectiles()) {
            if (p.isDead()) continue;
            p.update(delta);
            for (SolidBlock sb : mapLoader.getSolidBlocks()) {
                if (p.getBoundingBox().overlaps(sb.getBounds())) { p.destroy(); break; }
            }
            if (!p.isDead() && p.getBoundingBox().overlaps(knight.getBoundingBox())) {
                knight.takeDamage();
                p.destroy();
            }
        }
    }
    private void updateCrystalGuardianLasers(float delta, CrystalGuardian cg) {
        CrystalGuardianLaser laser = cg.getLaser();
        laser.update(delta);
        if (laser.getBounds().overlaps(knight.getBoundingBox())) {
            knight.takeDamage();
        }
        for (SolidBlock sb : mapLoader.getSolidBlocks()) {
            if (laser.getBounds().overlaps(sb.getBounds())) {
                laser.deactivate();
                break;
            }
        }
    }

    private void respawnEnemies() {
        for (Enemy enemy : enemies) {
            if(enemy instanceof FalseKnight) continue;
            if (enemy.canRespawn(
                Vector2.dst(knight.getPosition().x, knight.getPosition().y,
                    enemy.getPosition().x, enemy.getPosition().y),
                enemy.getRespawnDistance())) {
                enemy.respawn();
            }
        }
    }

    public void teleportToBossArena() {
        if (bossArena == null) return;
        knight.getPosition().set(bossArena.x + bossArena.width / 2f, bossArena.y + bossArena.height / 2f);
        knight.getBoundingBox().setPosition(knight.getPosition());
        knight.setSpawnPoint(knight.getPosition().x, knight.getPosition().y);
    }

    public void instaKillAllEnemies() {
        for (Enemy e : enemies) {
            if (e.isDead()) continue;
            if (e instanceof FalseKnight) {
                ((FalseKnight) e).forceKill();
            } else {
                e.takeDamage(999);
            }
            totalEnemiesKilled++;
            achievementManager.onEnemyKilled(e.getClass().getSimpleName());
        }
    }

    public void triggerCameraShake(float intensity, float duration) {
        cameraShakeTimer = duration;
        cameraShakeIntensity = intensity;
    }

    private void releaseBossFight() {
        inBossFight = false;
        mapLoader.getSolidBlocks().removeAll(bossGateBlocks);
        bossGateBlocks.clear();
        if (falseKnight != null) falseKnight.setActive(false);
    }

    private void updateCurrentArea() {
        Vector2 pos = knight.getPosition();
        String newArea = null;
        for (var entry : mapLoader.getNamedZones().entrySet()) {
            if (entry.getValue().contains(pos.x, pos.y)) {
                newArea = entry.getKey();
                break;
            }
        }
        currentArea = newArea;
    }

    public Rectangle getBossArena() { return bossArena; }
    public boolean isInBossFight() { return inBossFight; }
    public String getCurrentArea() { return currentArea; }
    public float getCameraShakeTimer() { return cameraShakeTimer; }
    public float getCameraShakeIntensity() { return cameraShakeIntensity; }
    public FalseKnight getFalseKnight() { return falseKnight; }
    public SpellManager getSpellManager() { return spellManager; }

    public String consumePendingToast() {
        String v = pendingToast;
        pendingToast = null;
        return v;
    }
    public void setPendingToast(String msg) { pendingToast = msg; }

    public void interact() {
        if (!dialogueActive) {
            if (zote.isInRange(knight.getPosition())) {
                knight.setMovingRight(false);
                knight.setMovingLeft(false);
                knight.stopX();
                zote.interact();
                spawnButterfly(zote.getPosition().x + 10, zote.getPosition().y + 30);
                currentDialogueText = zote.getCurrentDialogue();
                dialogueActive = true;
            }
        } else {
            zote.advanceDialogue();
            if (zote.isTalking()) {
                currentDialogueText = zote.getCurrentDialogue();
            } else {
                dialogueActive = false;
                currentDialogueText = null;
            }
        }
    }

    private void checkPogoAchievement() {
        if (knight.isPogoAttack() && knight.isHitRegistered()) {
            achievementManager.onPogoBounce(playTime);
        }
        if (knight.isOnGround()) {
            achievementManager.onLand();
        }
    }

    public void saveGame() {
        if (saveSlot < 0) return;
        SaveData.save(knight, achievementManager, playTime, saveSlot);
    }

    public void saveGameToSlot(int slot) {
        saveSlot = slot;
        saveGame();
    }

    public AchievementManager getAchievementManager() { return achievementManager; }
    public int getSaveSlot() { return saveSlot; }
    public void setSaveSlot(int slot) { saveSlot = slot; }
    public float getPlayTime() { return playTime; }
    public void setPlayTime(float t) { playTime = t; }
    public int getDeathCount() { return deathCount; }
    public int getTotalEnemiesKilled() { return totalEnemiesKilled; }
    public EndGameData consumePendingEndGameData() {
        EndGameData v = pendingEndGameData;
        pendingEndGameData = null;
        return v;
    }
}
