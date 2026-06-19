package src.main.model;

import com.badlogic.gdx.math.Vector2;
import src.main.model.data.KeyBindings;
//import src.main.model.entity.enemy.HuskHornhead;
import src.main.model.enviroment.MapLoader;
import src.main.model.entity.knight.Knight;
import src.main.model.system.CollisionSystem;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public Knight knight;
    public KeyBindings keyBindings = new KeyBindings();
    public MapLoader mapLoader;
//    public List<HuskHornhead> enemies;

    public Game(){
        mapLoader = new MapLoader("maps/newMap.tmx");
        knight = new Knight(mapLoader.spawnPoint.x, mapLoader.spawnPoint.y);
//        enemies = new ArrayList<>();
//        for (Vector2 spawn : mapLoader.enemySpawnPoints) {
//            HuskHornhead enemy = new HuskHornhead(spawn.x, spawn.y, () -> knight.getPosition());
//            enemies.add(enemy);
//        }
    }
    public void update(float delta) {
        knight.update(delta);
        CollisionSystem.resolve(knight, mapLoader.solidBlocks, delta);
        knight.updateAnimationState();
    }
}
