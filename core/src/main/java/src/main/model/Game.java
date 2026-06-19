package src.main.model;

import com.badlogic.gdx.math.Vector2;
import src.main.model.data.KeyBindings;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.enemy.constantEnemy.huskHornhead.HuskHornhead;
import src.main.model.entity.enemy.groundEnemy.crawlid.Crawlid;
import src.main.model.entity.enemy.groundEnemy.crawlid.CrawlidState;
import src.main.model.enviroment.MapLoader;
import src.main.model.entity.knight.Knight;
import src.main.model.physics.CollisionSystem;
import src.main.view.Phats;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public Knight knight;
    public KeyBindings keyBindings = new KeyBindings();
    public MapLoader mapLoader;
    public List<Enemy> enemies;

    public Game(){
        mapLoader = new MapLoader();
        knight = new Knight(mapLoader.spawnPoint.x, mapLoader.spawnPoint.y);
        enemies = new ArrayList<>();
        for (MapLoader.EnemySpawnInfo info : mapLoader.enemySpawnInfos) {
            Enemy e = switch (info.enemyType) {
                case "Crawlid" -> new Crawlid(
                    info.position.x, info.position.y, info.zone, () -> knight.getPosition());
                case "HuskHornhead" -> new HuskHornhead(
                    info.position.x, info.position.y, () -> knight.getPosition());
                default -> throw new RuntimeException("Unknown enemy: " + info.enemyType);
            };
            enemies.add(e);
        }
    }
    public void update(float delta) {
        knight.update(delta);
        CollisionSystem.resolve(knight, mapLoader.solidBlocks, delta);

        for (Enemy enemy : enemies) {
            if (enemy.isDeadAnimationDone()) continue;
            float prevVx = enemy.getVelocityX();
            enemy.update(delta);
            CollisionSystem.resolve(enemy, mapLoader.solidBlocks, delta);
            // برخورد به دیوار (برای Crawlid)
            if (enemy instanceof Crawlid c && prevVx != 0 && Math.abs(enemy.getVelocityX()) < 0.01f && c.getCurrentState() != CrawlidState.TURNING)
                c.turnAround();
            // کنتکت دمیج
            if (enemy.getBoundingBox().overlaps(knight.getBoundingBox()) && !enemy.isDead())
                knight.takeDamage();
        }
        knight.updateAnimationState();
    }
}
