package src.main.model.entity.enemy.constantEnemy.crystalGuardian;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.enemy.Enemy;
import src.main.view.GameAssetManager;

public class CrystalGuardian extends Enemy {
    private static final int MAX_HP = 8;
    private static final float DETECT_RANGE = 300f;
    private static final float ENRAGED_SPEED = 300f;
    private static final float COOLDOWN_DURATION = 1.5f;
    private static final float DEATH_DURATION = 1.0f;

    private CrystalGuardianState currentState = CrystalGuardianState.IDLE;
    private final AnimationSet<CrystalGuardianAnimationType> animSet;
    private float stateTimer;
    private CrystalGuardianLaser laser;
    private boolean facingRight = true;
    private KnightRef knightRef;
    private float spawnX;

    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    public CrystalGuardian(float x, float y, KnightRef knightRef) {
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        spawnX = x;
        position.set(x, y);
        this.knightRef = knightRef;
        boundingBox.setSize(32, 48);
        animSet = new AnimationSet<>(GameAssetManager.crystalGuardianAnimations, CrystalGuardianAnimationType.IDLE);
        laser = new CrystalGuardianLaser();
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) deadAnimationDone = true;
            return;
        }

        stateTimer -= delta;
        Vector2 knightPos = knightRef.getPosition();
        float dist = Math.abs(knightPos.x - position.x);
        boolean seePlayer = dist < DETECT_RANGE
            && ((facingRight && knightPos.x > position.x) || (!facingRight && knightPos.x < position.x));

        switch (currentState) {
            case IDLE:
                animSet.setAnimation(CrystalGuardianAnimationType.IDLE);
                velocity.x = 0;
                if (seePlayer) {
                    currentState = CrystalGuardianState.SHOOT;
                    stateTimer = 0.5f;
                    animSet.resetAnimation();
                }
                break;

            case SHOOT:
                animSet.setAnimation(CrystalGuardianAnimationType.SHOOT);
                velocity.x = 0;
                if (stateTimer <= 0 && animSet.getStateTime() >= 0.3f) {
                    laser.fire(
                        facingRight ? position.x + boundingBox.width : position.x - 600f,
                        position.y + boundingBox.height / 2f,
                        facingRight
                    );
                    currentState = CrystalGuardianState.ENRAGED;
                    stateTimer = 2f;
                }
                break;

            case ENRAGED:
                animSet.setAnimation(CrystalGuardianAnimationType.RUN);
                float dir = knightPos.x > position.x ? 1 : -1;
                velocity.x = dir * ENRAGED_SPEED;
                facingRight = dir > 0;
                if (stateTimer <= 0) {
                    currentState = CrystalGuardianState.COOLDOWN;
                    stateTimer = COOLDOWN_DURATION;
                    velocity.x = 0;
                }
                break;

            case COOLDOWN:
                animSet.setAnimation(CrystalGuardianAnimationType.IDLE);
                velocity.x = 0;
                if (stateTimer <= 0) {
                    currentState = CrystalGuardianState.IDLE;
                }
                break;
        }

        facingRight = knightPos.x > position.x;
        boundingBox.setPosition(position);
        laser.update(delta);
    }

    @Override
    public void takeDamage(int amount) {
        if (isDead) return;
        hp -= amount;
        if (hp <= 0) {
            isDead = true;
            deathTimer = DEATH_DURATION;
            velocity.x = 0;
            velocity.y = 0;
        } else {
            // knockback
            velocity.x = knightRef.getPosition().x > position.x ? -100f : 100f;
            velocity.y = 50f;
        }
    }

    @Override
    public TextureRegion getFrame(float delta) {
        if (isDead) {
            animSet.setAnimation(CrystalGuardianAnimationType.DEATH_LAND);
        }
        return animSet.getFrame(delta);
    }

    public CrystalGuardianLaser getLaser() { return laser; }
    public boolean isDeadAnimationDone() { return deadAnimationDone; }

    @Override
    public TextureRegion getCorpseFrame() {
        animSet.setAnimation(CrystalGuardianAnimationType.DEATH_LAND);
        return animSet.getFrame(0);
    }
}
