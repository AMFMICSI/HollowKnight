package src.main.model.entity.enemy.constantEnemy.crystalGuardian;

import com.badlogic.gdx.math.Rectangle;

public class CrystalGuardianLaser {
    private float startX, startY;
    private float width, height;
    private boolean active;
    private float lifeTimer;
    private static final float LASER_DURATION = 0.6f;
    private static final float LASER_LENGTH = 600f;
    private static final float LASER_HEIGHT = 24f;
    private Rectangle bounds;
    private boolean facingRight;
    private float impactTimer;
    private static final float IMPACT_DURATION = 0.4f;

    public CrystalGuardianLaser() {
        bounds = new Rectangle();
    }

    public void fire(float startX, float startY, boolean facingRight) {
        this.startX = startX;
        this.startY = startY;
        this.facingRight = facingRight;
        width = LASER_LENGTH;
        height = LASER_HEIGHT;
        active = true;
        lifeTimer = LASER_DURATION;
        impactTimer = 0;
        updateBounds();
    }

    private void updateBounds() {
        if (facingRight) {
            bounds.set(startX, startY, width, height);
        } else {
            bounds.set(startX - width, startY, width, height);
        }
    }

    public void update(float delta) {
        if (!active) return;
        lifeTimer -= delta;
        impactTimer += delta;
        if (lifeTimer <= 0) active = false;
    }

    public boolean isActive() { return active; }
    public Rectangle getBounds() { return bounds; }
    public void deactivate() { active = false; }
    public float getLength() { return width; }
    public float getWidth() { return height; }
    public float getStartX() { return startX; }
    public float getStartY() { return startY; }
    public float getAnimTime() { return impactTimer; }
    public boolean isFacingRight() { return facingRight; }
}
