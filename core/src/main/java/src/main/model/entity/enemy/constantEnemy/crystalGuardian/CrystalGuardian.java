package src.main.model.entity.enemy.constantEnemy.crystalGuardian;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.enemy.Enemy;
import src.main.model.physics.PhysicsSystem;
import src.main.view.manager.GameAssetManager;

public class CrystalGuardian extends Enemy {
    private static final int MAX_HP = 8;
    private static final float ENRAGED_SPEED = 300f;
    private static final float COOLDOWN_DURATION = 1.5f;

    private CrystalGuardianState currentState = CrystalGuardianState.IDLE;
    private final AnimationSet<CrystalGuardianAnimationType> animSet;
    private float stateTimer;
    private final CrystalGuardianLaser laser;
    private final KnightRef knightRef;

    public interface KnightRef {
        Vector2 getPosition();
        Rectangle getBoundingBox();
    }

    public CrystalGuardian(float x, float y, Rectangle zone, KnightRef knightRef) {
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        this.knightRef = knightRef;
        this.zone = zone;
        boundingBox.setSize(32, 48);
        animSet = new AnimationSet<>(GameAssetManager.crystalGuardianAnimations, CrystalGuardianAnimationType.IDLE);
        laser = new CrystalGuardianLaser();
        setFacingRight(true);
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) deadAnimationDone = true;
            return;
        }

        if (!isOnGround()) velocity.y -= PhysicsSystem.GRAVITY * delta;

        stateTimer -= delta;
        Vector2 knightPos = knightRef.getPosition();
        boolean seePlayer = position.dst(knightPos) < 500f;

        updateState(delta, knightPos, seePlayer);

        boundingBox.setPosition(position);
        laser.update(delta);
    }

    private void updateState(float delta, Vector2 knightPos, boolean seePlayer) {
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
                    setFacingRight(knightPos.x > position.x);
                    laser.fire(
                        isFacingRight() ? position.x + boundingBox.width : position.x,
                        position.y + boundingBox.height / 2f - 12f,
                        isFacingRight()
                    );
                    currentState = CrystalGuardianState.ENRAGED;
                    stateTimer = 2f;
                }
                break;

            case ENRAGED:
                animSet.setAnimation(CrystalGuardianAnimationType.RUN);
                velocity.x = isFacingRight() ? ENRAGED_SPEED : -ENRAGED_SPEED;
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
    }

    @Override
    public void takeDamage(int amount) {
        if (isDead) return;
        hp -= amount;
        if (hp <= 0) {
            isDead = true;
            deathTimer = 1.0f;
            velocity.x = 0;
            velocity.y = 0;
        } else {
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

    @Override
    public TextureRegion getCorpseFrame() {
        animSet.setAnimation(CrystalGuardianAnimationType.DEATH_LAND);
        return animSet.getFrame(0);
    }
}
