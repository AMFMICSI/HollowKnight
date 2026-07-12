package src.main.model.entity.spell;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.enemy.Enemy;
import src.main.model.enviroment.SolidBlock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VengefulProjectile {
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private Rectangle boundingBox = new Rectangle();
    private boolean dead = false;
    private boolean facingRight;
    private boolean shadow;
    private Set<Enemy> hitEnemies = new HashSet<>();
    private float animTimer = 0;
    private static final float SPEED = 400f;

    public VengefulProjectile(float x, float y, boolean facingRight, boolean shadow) {
        this.facingRight = facingRight;
        this.shadow = shadow;
        position.set(x, y);
        velocity.set(facingRight ? SPEED : -SPEED, 0);
        boundingBox.setSize(16, 16);
        boundingBox.setPosition(position);
    }

    public boolean checkWallCollision(float delta, List<SolidBlock> blocks) {
        for (SolidBlock sb : blocks) {
            if (boundingBox.overlaps(sb.getBounds())) {
                dead = true;
                return true;
            }
        }
        float nextX = position.x + velocity.x * delta;
        Rectangle nextBounds = new Rectangle(nextX, position.y,
            boundingBox.width, boundingBox.height);
        for (SolidBlock sb : blocks) {
            if (nextBounds.overlaps(sb.getBounds())) {
                dead = true;
                return true;
            }
        }
        return false;
    }

    public void update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        boundingBox.setPosition(position);
        animTimer += delta;
    }

    public boolean tryHit(Enemy enemy) {
        if (hitEnemies.contains(enemy)) return false;
        hitEnemies.add(enemy);
        return true;
    }

    public boolean isDead() { return dead; }
    public void destroy() { dead = true; }
    public Rectangle getBoundingBox() { return boundingBox; }
    public boolean isShadow() { return shadow; }
    public float getAnimTimer() { return animTimer; }
    public boolean isFacingRight() { return facingRight; }
    public Vector2 getPosition() { return position; }
}
