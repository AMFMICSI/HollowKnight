package src.main.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.data.KeyBindings;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardian;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardianLaser;
import src.main.model.entity.enemy.constantEnemy.huskHornhead.HuskHornhead;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunter;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalProjectile;
import src.main.model.entity.enemy.groundEnemy.crawlid.Crawlid;
import src.main.model.entity.enemy.groundEnemy.crawlid.CrawlidState;
import src.main.model.enviroment.MapLoader;
import src.main.model.enviroment.SolidBlock;
import src.main.model.entity.knight.Knight;
import src.main.model.physics.CollisionSystem;
import src.main.view.Phats;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private static final int SOUL_PER_HIT = 11;

    private Knight knight;
    private KeyBindings keyBindings = new KeyBindings();
    private MapLoader mapLoader;
    private List<Enemy> enemies;

    public Knight getKnight() { return knight; }
    public KeyBindings getKeyBindings() { return keyBindings; }
    public MapLoader getMapLoader() { return mapLoader; }
    public List<Enemy> getEnemies() { return enemies; }

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
                    info.position.x, info.position.y, () -> knight.getPosition());
                default -> throw new RuntimeException("Unknown enemy: " + info.enemyType);
            };
            enemies.add(e);
        }
    }

    public void update(float delta) {
        delta = Math.min(delta, 0.033f);
        updateKnight(delta);
        updateCombat(delta);
        updateEnemies(delta);
        updateProjectiles(delta);
        respawnEnemies(delta);
        knight.updateAnimationState();
    }

    private void updateKnight(float delta) {
        knight.update(delta);
        CollisionSystem.resolve(knight, mapLoader.getSolidBlocks(), delta);
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
                if (hitbox.overlaps(enemy.getBoundingBox())) {
                    knight.setHitRegistered(true);
                    enemy.takeDamage(1);
                    knight.addSoul(SOUL_PER_HIT);
                    if (knight.isPogoAttack()) knight.doPogoBounce();
                    break;
                }
            }

            if (knight.isPogoAttack() && !knight.isHitRegistered()) {
                for (SolidBlock sb : mapLoader.getSolidBlocks()) {
                    if (sb.isDeadly() && hitbox.overlaps(sb.getBounds())) {
                        knight.setHitRegistered(true);
                        knight.doPogoBounce();
                        break;
                    }
                }
            }
        }

        for (SolidBlock sb : mapLoader.getSolidBlocks()) {
            if (sb.isDeadly() && knight.getBoundingBox().overlaps(sb.getBounds())) {
                knight.takeDamage();
                break;
            }
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
            if (enemy instanceof Crawlid c
                && prevVx != 0
                && Math.abs(enemy.getVelocityX()) < 0.01f
                && c.getCurrentState() != CrawlidState.TURNING)
                c.turnAround();
            if (enemy.getBoundingBox().overlaps(knight.getBoundingBox()) && !enemy.isDead())
                knight.takeDamage();
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

    private void respawnEnemies(float delta) {
        float respawnDist = 600f;
        for (Enemy enemy : enemies) {
            if (enemy.canRespawn(
                Vector2.dst(knight.getPosition().x, knight.getPosition().y,
                    enemy.getPosition().x, enemy.getPosition().y),
                respawnDist)) {
                enemy.respawn();
        }
    }
}
}
