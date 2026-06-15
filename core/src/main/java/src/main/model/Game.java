package src.main.model;

import src.main.model.data.KeyBindings;
import src.main.model.enviroment.MapLoader;
import src.main.model.entity.knight.Knight;
import src.main.model.system.CollisionSystem;

public class Game {
    public Knight knight;
    public KeyBindings keyBindings = new KeyBindings();
    public MapLoader mapLoader;

    public Game(){
        mapLoader = new MapLoader("maps/newMap.tmx");
        knight = new Knight(mapLoader.spawnPoint.x, mapLoader.spawnPoint.y);
    }
    public void update(float delta) {
        knight.update(delta);
        CollisionSystem.resolve(knight, mapLoader.solidBlocks, delta);
        knight.updateAnimationState();
    }
}
