package src.main.model.entity.enemy.flyingEnemy;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.enemy.Enemy;

public abstract class FlyingEnemy extends Enemy {
    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    protected KnightRef knightRef;
    protected float chaseSpeed;
    protected boolean diedInAir;
    protected boolean hasDetectedPlayer;

    public void setZone(Rectangle zone) {
        this.zone = zone;
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) deadAnimationDone = true;
            return;
        }

        if (!hasDetectedPlayer) {
            if (zone == null) {
                hasDetectedPlayer = true;
                onPlayerDetected();
            } else {
                velocity.set(0, 0);
                float clampedX = Math.max(zone.x, Math.min(position.x, zone.x + zone.width - boundingBox.width));
                float clampedY = Math.max(zone.y, Math.min(position.y, zone.y + zone.height - boundingBox.height));
                position.set(clampedX, clampedY);
                Vector2 knightPos = knightRef.getPosition();
                if (zone.contains(knightPos)) {
                    hasDetectedPlayer = true;
                    onPlayerDetected();
                }
            }
        } else {
            Vector2 knightPos = knightRef.getPosition();
            float dist = position.dst(knightPos);
            updateChase(delta, knightPos, dist);
        }

        boundingBox.setPosition(position);
    }

    protected void onPlayerDetected() {}

    protected void updateChase(float delta, Vector2 knightPos, float dist) {
        moveToward(knightPos, chaseSpeed);
    }

    protected void moveToward(Vector2 target, float speed) {
        float dx = target.x - position.x;
        float dy = target.y - position.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 1f) {
            velocity.set(dx / dist * speed, dy / dist * speed);
            setFacingRight(dx > 0);
        } else {
            velocity.set(0, 0);
        }
    }

    @Override
    public void respawn() {
        super.respawn();
        hasDetectedPlayer = false;
    }

    @Override
    public void takeDamage(int amount) {
        if (!isDead) diedInAir = true;
        super.takeDamage(amount);
    }
}
