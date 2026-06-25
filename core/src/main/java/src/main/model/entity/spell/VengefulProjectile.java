package src.main.model.entity.spell;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.enemy.Enemy;
import src.main.model.enviroment.SolidBlock;
import src.main.view.GameAssetManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VengefulProjectile {
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private Rectangle boundingBox = new Rectangle();
    private boolean dead = false;
    private boolean facingRight;
    private Set<Enemy> hitEnemies = new HashSet<>();
    private float animTimer = 0;
    private static final float SPEED = 400f;

    public VengefulProjectile(float x, float y, boolean facingRight) {
        this.facingRight = facingRight;
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

    public void draw(SpriteBatch batch, float delta) {
        if (dead) return;
        Animation<TextureRegion> anim = GameAssetManager.vengefulProjectileAnim;
        if (anim != null) {
            TextureRegion frame = anim.getKeyFrame(animTimer);
            float s = 0.5f;
            float w = frame.getRegionWidth() * s;
            float h = frame.getRegionHeight() * s;
            if (facingRight)
                batch.draw(frame, position.x, position.y, w, h);
            else
                batch.draw(frame, position.x + w, position.y, -w, h);
        }
    }

    public boolean isDead() { return dead; }
    public void destroy() { dead = true; }
    public Rectangle getBoundingBox() { return boundingBox; }
}
