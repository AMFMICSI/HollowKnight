package src.main.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import src.main.model.data.KeyBindings;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardian;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardianLaser;
import src.main.model.entity.enemy.groundEnemy.huskHornhead.HuskHornhead;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunter;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalProjectile;
import src.main.model.entity.enemy.boss.falseKnight.FalseKnight;
import src.main.model.entity.enemy.boss.falseKnight.FalseKnightState;
import src.main.model.entity.enemy.groundEnemy.GroundEnemy;
import src.main.model.entity.enemy.groundEnemy.crawlid.Crawlid;
import src.main.model.entity.npc.zote.Zote;
import src.main.model.enviroment.MapLoader;
import src.main.model.enviroment.SolidBlock;
import src.main.model.entity.charm.CharmType;
import src.main.model.entity.knight.Knight;
import src.main.model.entity.spell.SpellType;
import src.main.model.entity.spell.VengefulProjectile;
import src.main.model.entity.spell.HowlingWraithsAoe;
import src.main.model.enviroment.Spike;
import src.main.model.physics.CollisionSystem;
import src.main.view.GameAssetManager;
import src.main.view.actors.modal.DialogueBox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private static final int SOUL_PER_HIT = 11;
    private static final float RESPAWN_DISTANCE = 600f;

    private Knight knight;
    private KeyBindings keyBindings = new KeyBindings();
    private MapLoader mapLoader;
    private List<Enemy> enemies;
    private Zote zote;
    private boolean dialogueActive;
    private String currentDialogueText;
    private boolean dialogueAdvanceRequested;
    private Rectangle bossArena;
    private boolean inBossFight = false;
    private float cameraShakeTimer = 0;
    private float cameraShakeIntensity = 0;
    private boolean gatesActivated = false;
    private FalseKnight falseKnight;
    private List<SolidBlock> bossGateBlocks = new ArrayList<>();
    private List<VengefulProjectile> spellProjectiles = new ArrayList<>();
    private List<HowlingWraithsAoe> spellAoes = new ArrayList<>();
    private String pendingToast = null;

    public Knight getKnight() { return knight; }
    public KeyBindings getKeyBindings() { return keyBindings; }
    public MapLoader getMapLoader() { return mapLoader; }
    public List<Enemy> getEnemies() { return enemies; }
    public Zote getZote() { return zote; }
    public boolean isDialogueActive() { return dialogueActive; }
    public String getCurrentDialogueText() { return currentDialogueText; }
    public void requestDialogueAdvance() { dialogueAdvanceRequested = true; }
    public boolean consumeDialogueAdvance() {
        boolean v = dialogueAdvanceRequested;
        dialogueAdvanceRequested = false;
        return v;
    }

    public Game() {
        mapLoader = new MapLoader();
        knight = new Knight(mapLoader.getSpawnPoint().x, mapLoader.getSpawnPoint().y);
        enemies = new ArrayList<>();
        for (MapLoader.EnemySpawnInfo info : mapLoader.getEnemySpawnInfos()) {
            Enemy e = switch (info.enemyType) {
                case "Crawlid" -> new Crawlid(
                    info.position.x, info.position.y, info.zone, () -> knight.getPosition());
                case "CrystalHunter" -> new CrystalHunter(
                    info.position.x, info.position.y, info.zone, () -> knight.getPosition());
                case "HuskHornhead" -> new HuskHornhead(
                    info.position.x, info.position.y, () -> knight.getPosition());
                case "CrystalGuardian" -> new CrystalGuardian(
                    info.position.x, info.position.y, info.zone, () -> knight.getPosition());
                case "FalseKnight" -> {
                    FalseKnight fk = new FalseKnight(
                        info.position.x, info.position.y, () -> knight.getPosition());
                    falseKnight = fk;
                    yield fk;
                }
                default -> throw new RuntimeException("Unknown enemy: " + info.enemyType);
            };
            enemies.add(e);
        }
        for (Enemy e : enemies) e.setSolidBlocks(mapLoader.getSolidBlocks());

        // Find boss arena zone (the zone containing the FalseKnight spawn)
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

        Vector2 zs = mapLoader.getZoteSpawnPoint();
        zote = new Zote(zs.x, zs.y);
    }

    public void update(float delta) {
        delta = Math.min(delta, 0.033f);

        updateBossArena();
        updateKnight(delta);

        if (inBossFight && knight.consumeJustRespawned()) {
            releaseBossFight();
        }

        updateCombat(delta);
        updateEnemies(delta);
        firePendingSpell();
        updateSpellProjectiles(delta);
        updateSpellAoes(delta);
        if (knight.consumePendingSoulToast()) {
            pendingToast = "Not enough Soul!";
        }
        updateProjectiles(delta);

        if (knight.consumeJustDamaged()) {
            triggerCameraShake(3f, 0.3f);
        }

        respawnEnemies();
        knight.updateAnimationState();
        updateZote(delta);

        if (cameraShakeTimer > 0) cameraShakeTimer -= delta;
    }

    private void updateKnight(float delta) {
        knight.update(delta);
        CollisionSystem.resolve(knight, mapLoader.getSolidBlocks(),
            mapLoader.getSpikes(), mapLoader.getClimbableWalls(), delta);
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
        }
    }

    private void updateZote(float delta) {
        zote.update(delta);
        zote.updateProximity(knight.getPosition(), delta);
    }

    private void updateCombat(float delta) {
        if (knight.isAttacking() && !knight.isHitRegistered()) {
            Rectangle hitbox;
            if (knight.isPogoAttack()) {
                hitbox = new Rectangle(
                    knight.getBoundingBox().x + 2,
                    knight.getBoundingBox().y - 28,
                    knight.getBoundingBox().width - 4,
                    28);
            } else {
                float hx = knight.isFacingRight()
                    ? knight.getBoundingBox().x + knight.getBoundingBox().width
                    : knight.getBoundingBox().x - 30;
                hitbox = new Rectangle(hx, knight.getBoundingBox().y + 10, 30, 30);
            }

            for (Enemy enemy : enemies) {
                if (enemy.isDead() || enemy.isDeadAnimationDone()) continue;

                if (enemy instanceof FalseKnight fk && fk.isStunned()) {
                    if (hitbox.overlaps(fk.getStunHitbox())) {
                        knight.setHitRegistered(true);
                        fk.takeDamage(knight.getAttackDamage());
                        knight.addSoul(knight.getSoulPerHit());
                        applyHeavyBlowKnockback(enemy);
                        if (knight.isPogoAttack()) knight.doPogoBounce();
                        break;
                    }
                    continue;
                }

                if (hitbox.overlaps(enemy.getBoundingBox())) {
                    knight.setHitRegistered(true);
                    enemy.takeDamage(knight.getAttackDamage());
                    knight.addSoul(knight.getSoulPerHit());
                    applyHeavyBlowKnockback(enemy);
                    if (knight.isPogoAttack()) knight.doPogoBounce();
                    break;
                }
            }

            if (!knight.isHitRegistered() && hitbox.overlaps(zote.getBoundingBox())) {
                knight.setHitRegistered(true);
                zote.takeDamage();
                zote.setFacingRight(knight.getPosition().x >= zote.getPosition().x);
            }

            if (knight.isPogoAttack() && !knight.isHitRegistered()) {
                for (Spike spike : mapLoader.getSpikes()) {
                    if (hitbox.overlaps(spike.getBounds())) {
                        knight.setHitRegistered(true);
                        knight.doPogoBounce();
                        break;
                    }
                }
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
                enemy.update(delta);
                continue;
            }
            float prevVx = enemy.getVelocityX();
            enemy.update(delta);
            CollisionSystem.resolve(enemy, mapLoader.getSolidBlocks(), delta);
            if (enemy instanceof GroundEnemy ge)
                ge.onCollisionResolved(prevVx, mapLoader.getSolidBlocks());

            if (enemy instanceof FalseKnight fk) {
                // Clamp boss within its arena zone
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
                // Camera shake on heavy attacks
                if (fk.isShaking()) {
                    triggerCameraShake(fk.getShakeIntensity(), fk.getShakeDuration());
                }
            }

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

    private void updateProjectiles(float delta) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof CrystalHunter ch) {
                updateCrystalProjectiles(delta, ch);
            }else if(enemy instanceof CrystalGuardian cg && cg.getLaser().isActive()){
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
    private void updateCrystalGuardianLasers(float delta , CrystalGuardian cg){
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
            if (enemy.canRespawn(
                Vector2.dst(knight.getPosition().x, knight.getPosition().y,
                    enemy.getPosition().x, enemy.getPosition().y),
                RESPAWN_DISTANCE)) {
                enemy.respawn();
            }
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

    public Rectangle getBossArena() { return bossArena; }
    public boolean isInBossFight() { return inBossFight; }
    public float getCameraShakeTimer() { return cameraShakeTimer; }
    public float getCameraShakeIntensity() { return cameraShakeIntensity; }
    public List<VengefulProjectile> getSpellProjectiles() { return spellProjectiles; }
    public List<HowlingWraithsAoe> getSpellAoes() { return spellAoes; }
    public FalseKnight getFalseKnight() { return falseKnight; }

    private void firePendingSpell() {
        SpellType type = knight.consumePendingCastResult();
        if (type == null) return;
        switch (type) {
            case VENGEFUL:
                fireVengefulSpirit();
                break;
            case WRAITHS:
                fireHowlingWraiths();
                break;
        }
    }

    private void fireVengefulSpirit() {
        float x = knight.isFacingRight()
            ? knight.getBoundingBox().x + knight.getBoundingBox().width
            : knight.getBoundingBox().x - 16;
        float y = knight.getPosition().y + 20;
        boolean shadow = knight.isCharmEquipped(CharmType.VOID_HEART);
        spellProjectiles.add(new VengefulProjectile(x, y, knight.isFacingRight(), shadow));
    }

    private void fireHowlingWraiths() {
        boolean shadow = knight.isCharmEquipped(CharmType.VOID_HEART);
        spellAoes.add(new HowlingWraithsAoe(
            knight.getBoundingBox().x, knight.getBoundingBox().y,
            knight.getBoundingBox().width, knight.getBoundingBox().height, shadow));
    }

    private void updateSpellProjectiles(float delta) {
        Iterator<VengefulProjectile> it = spellProjectiles.iterator();
        while (it.hasNext()) {
            VengefulProjectile p = it.next();
            if (p.isDead()) { it.remove(); continue; }
            if (p.checkWallCollision(delta, mapLoader.getSolidBlocks())) {
                it.remove();
                continue;
            }
            p.update(delta);
            for (Enemy enemy : enemies) {
                if (enemy.isDead() || enemy.isDeadAnimationDone()) continue;
                if (p.getBoundingBox().overlaps(enemy.getBoundingBox()) && p.tryHit(enemy)) {
                    enemy.takeDamage(knight.getSpellDamage());
                    knight.addSoul(knight.getSoulPerHit());
                }
            }
        }
    }

    private void updateSpellAoes(float delta) {
        Iterator<HowlingWraithsAoe> it = spellAoes.iterator();
        while (it.hasNext()) {
            HowlingWraithsAoe aoe = it.next();
            if (aoe.isDone()) { it.remove(); continue; }
            int prevTick = aoe.getTickCount();
            aoe.update(delta);
            if (aoe.getTickCount() > prevTick) {
                for (Enemy enemy : enemies) {
                    if (enemy.isDead() || enemy.isDeadAnimationDone()) continue;
                    if (aoe.getBounds().overlaps(enemy.getBoundingBox())) {
                        enemy.takeDamage(knight.getSpellDamage());
                        knight.addSoul(knight.getSoulPerHit());
                    }
                }
            }
        }
    }

    public String consumePendingToast() {
        String v = pendingToast;
        pendingToast = null;
        return v;
    }

    public void interact() {
        if (!dialogueActive) {
            if (zote.isInRange(knight.getPosition())) {
                knight.setMovingRight(false);
                knight.setMovingLeft(false);
                knight.stopX();
                zote.interact();
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
}
