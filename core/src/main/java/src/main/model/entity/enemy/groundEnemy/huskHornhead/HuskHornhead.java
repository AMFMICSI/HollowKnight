package src.main.model.entity.enemy.groundEnemy.huskHornhead;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.enemy.groundEnemy.GroundEnemy;
import src.main.model.enviroment.SolidBlock;
import src.main.model.physics.PhysicsSystem;
import src.main.view.manager.GameAssetManager;

import java.util.List;

public class HuskHornhead extends GroundEnemy {
    private static final float WALK_SPEED = 60f;
    private static final float CHARGE_SPEED = 400f;
    private static final float WALK_DURATION = 3f;
    private static final float REST_DURATION = 1.5f;
    private static final float DETECT_RANGE = 200f;
    private static final float ALERT_DURATION = 0.5f;
    private static final int MAX_HP = 5;

    private float stateTimer;
    private HuskHornheadState currentState = HuskHornheadState.PATROL;
    private final AnimationSet<HuskHornheadAnimationType> animSet;
    private final KnightRef knightRef;

    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    public HuskHornhead(float x, float y, KnightRef knightRef) {
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        this.knightRef = knightRef;
        boundingBox.setSize(28, 40);
        walkSpeed = WALK_SPEED;
        animSet = new AnimationSet<>(GameAssetManager.huskHornheadAnimations, HuskHornheadAnimationType.WALK);
        setFacingRight(true);
        stateTimer = WALK_DURATION;
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) deadAnimationDone = true;
            return;
        }

        if (!isOnGround())
            velocity.y -= PhysicsSystem.GRAVITY * delta;

        stateTimer -= delta;
        switch (currentState) {
            case PATROL:
                velocity.x = isFacingRight() ? walkSpeed : -walkSpeed;
                checkDetection();
                if (currentState != HuskHornheadState.ALERT && stateTimer <= 0) {
                    currentState = HuskHornheadState.REST;
                    stateTimer = REST_DURATION;
                    velocity.x = 0;
                }
                break;
            case REST:
                velocity.x = 0;
                if (stateTimer <= 0) {
                    setFacingRight(!isFacingRight());
                    currentState = HuskHornheadState.PATROL;
                    stateTimer = WALK_DURATION;
                }
                checkDetection();
                break;
            case ALERT:
                velocity.x = 0;
                if (stateTimer <= 0) {
                    currentState = HuskHornheadState.CHARGING;
                    stateTimer = 2f;
                    setFacingRight(knightRef.getPosition().x > position.x);
                    velocity.x = isFacingRight() ? CHARGE_SPEED : -CHARGE_SPEED;
                }
                break;
            case CHARGING:
                velocity.x = isFacingRight() ? CHARGE_SPEED : -CHARGE_SPEED;
                if (stateTimer <= 0 && isOnGround()) {
                    stopCharging();
                }
                break;
        }

        boundingBox.setPosition(position);
    }

    @Override
    public void onCollisionResolved(float prevVx, List<SolidBlock> blocks) {
        if (currentState == HuskHornheadState.PATROL) {
            super.onCollisionResolved(prevVx, blocks);
        } else if (currentState == HuskHornheadState.CHARGING) {
            boolean hitWall = prevVx != 0 && Math.abs(getVelocityX()) < 0.01f;
            boolean cliff = isCliffAhead(blocks);
            if (hitWall || cliff) stopCharging();
        }
    }

    private void stopCharging() {
        currentState = HuskHornheadState.REST;
        stateTimer = REST_DURATION;
        velocity.x = 0;
        setFacingRight(!isFacingRight());
    }

    private boolean hasLineOfSight(Vector2 to) {
        float sx = position.x + boundingBox.width / 2f;
        float sy = position.y + boundingBox.height / 2f;
        if (solidBlocks == null) return true;
        for (SolidBlock block : solidBlocks) {
            if (Intersector.intersectSegmentRectangle(sx, sy, to.x, to.y, block.getBounds()))
                return false;
        }
        return true;
    }

    private void checkDetection() {
        Vector2 knightPos = knightRef.getPosition();
        if (!hasLineOfSight(knightPos)) return;
        float detectX = isFacingRight()
            ? position.x + boundingBox.width
            : position.x - DETECT_RANGE;
        if (knightPos.x > detectX && knightPos.x < detectX + DETECT_RANGE
            && Math.abs(knightPos.y - position.y) < boundingBox.height) {
            currentState = HuskHornheadState.ALERT;
            stateTimer = ALERT_DURATION;
            setFacingRight(knightPos.x > position.x);
        }
    }

    @Override
    public void takeDamage(int amount) {
        if (isDead) return;
        super.takeDamage(amount);
        if (!isDead) {
            velocity.x = isFacingRight() ? -200f : 200f;
            velocity.y = 100f;
            currentState = HuskHornheadState.ALERT;
            stateTimer = 0.3f;
        }
    }

    @Override
    public TextureRegion getFrame(float delta) {
        HuskHornheadAnimationType type;
        if (isDead) type = HuskHornheadAnimationType.DEATH_LAND;
        else switch (currentState) {
            case PATROL:   type = HuskHornheadAnimationType.WALK; break;
            case REST:     type = HuskHornheadAnimationType.IDLE; break;
            case ALERT:    type = HuskHornheadAnimationType.ATTACK_ANTICIPATE; break;
            case CHARGING: type = HuskHornheadAnimationType.ATTACK_LUNGE; break;
            default:       type = HuskHornheadAnimationType.IDLE;
        }
        animSet.setAnimation(type);
        return animSet.getFrame(delta);
    }

    @Override
    public TextureRegion getCorpseFrame() {
        animSet.setAnimation(HuskHornheadAnimationType.DEATH_LAND);
        return animSet.getFrame(0);
    }
}
