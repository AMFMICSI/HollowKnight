package src.main.model.entity.enemy.constantEnemy.huskHornhead;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.behavior.ChargeMovement;
import src.main.model.entity.behavior.PatrolMovement;
import src.main.model.entity.enemy.Enemy;
import src.main.model.physics.PhysicsSystem;
import src.main.view.GameAssetManager;
import java.util.Map;

public class HuskHornhead extends Enemy {
    private static final float WALK_SPEED = 60f;
    private static final float CHARGE_SPEED = 400f;
    private static final float WALK_DURATION = 3f;
    private static final float REST_DURATION = 1.5f;
    private static final float DETECT_RANGE = 200f;
    private static final float DEATH_DURATION = 1.0f;
    private static final int MAX_HP = 5;
    private static final float ALERT_DURATION = 0.5f;

    private float stateTimer = 0;
    private HuskHornheadState currentState = HuskHornheadState.PATROL;

    private final Map<HuskHornheadAnimationType, Animation<TextureRegion>> animations;
    private HuskHornheadAnimationType currentAnimType = HuskHornheadAnimationType.WALK;

    private KnightRef knightRef;
    private PatrolMovement patrol;
    private ChargeMovement charge;

    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    public HuskHornhead(float x, float y, KnightRef knightRef) {
        hp = maxHp = MAX_HP;
        position.set(x, y);
        this.knightRef = knightRef;
        boundingBox.setSize(28, 40);
        zone.setSize(DETECT_RANGE, 40);
        animations = GameAssetManager.huskHornheadAnimations;
        patrol = new PatrolMovement(WALK_SPEED, WALK_DURATION);
        charge = new ChargeMovement(CHARGE_SPEED);
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) deadAnimationDone = true;
            animTime += delta;
            return;
        }

        if (!isOnGround)
            velocity.y -= PhysicsSystem.GRAVITY * delta;

        stateTimer -= delta;
        switch (currentState) {
            case PATROL:
                updatePatrol(delta);
                break;
            case REST:
                updateRest(delta);
                break;
            case ALERT:
                updateAlert(delta);
                break;
            case CHARGING:
                updateCharging(delta);
                break;
        }

        boundingBox.setPosition(position);
        animTime += delta;
    }

    private void updatePatrol(float delta) {
        patrol.update(this, delta);
        if (stateTimer <= 0) {
            currentState = HuskHornheadState.REST;
            stateTimer = REST_DURATION;
            velocity.x = 0;
        }
    }

    private void updateRest(float delta) {
        velocity.x = 0;
        if (stateTimer <= 0) {
            facingRight = !facingRight;
            currentState = HuskHornheadState.PATROL;
            patrol.reset();
            stateTimer = WALK_DURATION;
        }
        checkDetection();
    }

    private void updateAlert(float delta) {
        velocity.x = 0;
        if (stateTimer <= 0) {
            currentState = HuskHornheadState.CHARGING;
            charge.reset();
            stateTimer = 2f;
            facingRight = knightRef.getPosition().x > position.x;
            velocity.x = facingRight ? CHARGE_SPEED : -CHARGE_SPEED;
        }
    }

    private void updateCharging(float delta) {
        charge.update(this, delta);
        if (stateTimer <= 0 || (Math.abs(velocity.x) < 10f && isOnGround)) {
            stopCharging();
        }
    }

    private void checkDetection() {
        Vector2 knightPos = knightRef.getPosition();
        zone.setPosition(
            facingRight ? position.x + boundingBox.width : position.x - zone.width,
            position.y
        );
        if (zone.contains(knightPos)) {
            currentState = HuskHornheadState.ALERT;
            stateTimer = ALERT_DURATION;
            facingRight = knightPos.x > position.x;
        }
    }

    private void stopCharging() {
        currentState = HuskHornheadState.REST;
        stateTimer = REST_DURATION;
        velocity.x = 0;
        facingRight = !facingRight;
    }

    public void takeDamage(int amount) {
        if (isDead) return;
        hp -= amount;
        if (hp <= 0) {
            isDead = true;
            deathTimer = DEATH_DURATION;
            velocity.x = 0;
            velocity.y = 0;
        } else {
            velocity.x = facingRight ? -200f : 200f;
            velocity.y = 100f;
            currentState = HuskHornheadState.ALERT;
            stateTimer = 0.3f;
        }
    }

    public boolean isDeadAnimationDone() { return deadAnimationDone; }
    public boolean isDead() { return isDead; }

    public HuskHornheadAnimationType getCurrentAnimType() {
        if (isDead) return HuskHornheadAnimationType.DEATH_LAND;
        switch (currentState) {
            case PATROL:   return HuskHornheadAnimationType.WALK;
            case REST:     return HuskHornheadAnimationType.IDLE;
            case ALERT:    return HuskHornheadAnimationType.ATTACK_ANTICIPATE;
            case CHARGING: return HuskHornheadAnimationType.ATTACK_LUNGE;
            default:       return HuskHornheadAnimationType.IDLE;
        }
    }

    @Override
    public TextureRegion getFrame(float delta) {
        HuskHornheadAnimationType newType = getCurrentAnimType();
        if (currentAnimType != newType) {
            currentAnimType = newType;
            animTime = 0;
        }
        return animations.get(currentAnimType).getKeyFrame(animTime);
    }
}
