package src.main.model.entity.enemy.flyingEnemy.crystalHunter;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CrystalProjectile {
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private Rectangle boundingBox = new Rectangle();
    private boolean dead = false;

    public CrystalProjectile(float x, float y, float targetX, float targetY) {
        position.set(x, y);
        Vector2 dir = new Vector2(targetX - x, targetY - y).nor();
        velocity.set(dir.x * 150f, dir.y * 150f);
        boundingBox.setSize(10, 10);
    }

    public void update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        boundingBox.setPosition(position);
    }

    public boolean isDead() { return dead; }
    public void destroy() { dead = true; }

    public Vector2 getPosition() { return position; }
    public Rectangle getBoundingBox() { return boundingBox; }
}
