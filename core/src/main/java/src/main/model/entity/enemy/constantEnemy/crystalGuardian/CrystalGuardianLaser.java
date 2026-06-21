package src.main.model.entity.enemy.constantEnemy.crystalGuardian;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CrystalGuardianLaser {
    private Vector2 position;
    private float width, height;
    private boolean active;
    private float lifeTimer;
    private static final float LASER_DURATION = 0.6f;
    private static final float LASER_WIDTH = 600f;
    private static final float LASER_HEIGHT = 8f;
    private Rectangle bounds;

    public CrystalGuardianLaser() {
        position = new Vector2();
        bounds = new Rectangle();
    }

    public void fire(float startX, float startY, boolean facingRight) {
        position.set(startX, startY);
        width = LASER_WIDTH;
        height = LASER_HEIGHT;
        active = true;
        lifeTimer = LASER_DURATION;
        bounds.set(position.x, position.y, width, height);
    }

    public void update(float delta) {
        if (!active) return;
        lifeTimer -= delta;
        if (lifeTimer <= 0) active = false;
    }

    public void draw(SpriteBatch batch, TextureRegion region) {
        if (!active) return;
        batch.draw(region, position.x, position.y, width, height);
    }

    public boolean isActive() { return active; }
    public Rectangle getBounds() { return bounds; }
    public void deactivate() { active = false; }
}
