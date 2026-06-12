package src.main.model;

public class Game {
    public Knight knight = new Knight();

    public void update(float delta) {
        knight.update(delta);
    }
}
