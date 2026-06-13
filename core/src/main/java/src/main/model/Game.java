package src.main.model;

import src.main.model.data.KeyBindings;
import src.main.model.enviroment.Room;
import src.main.model.knight.Knight;

public class Game {
    public Knight knight = new Knight();
    public KeyBindings keyBindings = new KeyBindings();
    public Room currentRoom = new Room();

    public void update(float delta) {
        knight.update(delta);
    }
}
