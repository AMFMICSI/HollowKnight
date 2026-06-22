package src.main.model.entity.enemy.boss.falseKnight;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.enemy.Enemy;
import src.main.view.GameAssetManager;

public class FalseKnight extends Enemy {
    private static final int MAX_HP = 60;
    private static final float WALK_SPEED = 40f;
    private static final float DEATH_DURATION = 2.0f;

    private final AnimationSet<FalseKnightAnimationType> animSet;
    private FalseKnightState currentState = FalseKnightState.IDLE;
    private float stateTimer;

    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    private final KnightRef knightRef;

    public FalseKnight(float x, float y, KnightRef knightRef) {
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        boundingBox.setSize(100, 140);
        this.knightRef = knightRef;
        animSet = new AnimationSet<>(GameAssetManager.falseKnightAnimations, FalseKnightAnimationType.IDLE);
        setFacingRight(true);
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
        float dist = position.dst(knightPos);
        float dx = knightPos.x - position.x;

        switch (currentState) {
            case IDLE:
                animSet.setAnimation(FalseKnightAnimationType.IDLE);
                velocity.set(0, 0);
                break;

            case RUN:
                animSet.setAnimation(FalseKnightAnimationType.RUN);
                velocity.set(dx > 0 ? WALK_SPEED : -WALK_SPEED, velocity.y);
                break;

            case ATTACK_ANTIC:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.ATTACK_ANTIC);
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(FalseKnightState.ATTACK);
                }
                break;

            case ATTACK:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.ATTACK);
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(FalseKnightState.ATTACK_RECOVER);
                }
                break;

            case ATTACK_RECOVER:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.ATTACK_RECOVER);
                if (stateTimer <= 0) {
                    changeState(FalseKnightState.IDLE);
                }
                break;

            case JUMP:
                animSet.setAnimation(FalseKnightAnimationType.JUMP);
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(FalseKnightState.LAND);
                }
                break;

            case JUMP_ATTACK:
                animSet.setAnimation(FalseKnightAnimationType.JUMP_ATTACK);
                break;

            case LAND:
                animSet.setAnimation(FalseKnightAnimationType.LAND);
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(FalseKnightState.IDLE);
                }
                break;

            case TURN:
                animSet.setAnimation(FalseKnightAnimationType.TURN);
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(FalseKnightState.IDLE);
                }
                break;

            case STUN_RECOVER:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.STUN_RECOVER);
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(FalseKnightState.IDLE);
                }
                break;

            case RUN_ANTIC:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.RUN_ANTIC);
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(FalseKnightState.RUN);
                }
                break;

            case DEATH_FALL:
            case DEATH_HIT:
            case DEATH_LAND:
                break;
        }

        boundingBox.setPosition(position);
    }

    private void changeState(FalseKnightState newState) {
        currentState = newState;
        stateTimer = switch (newState) {
            case ATTACK_RECOVER -> 0.5f;
            default -> 0;
        };
        animSet.resetAnimation();
    }

    private FalseKnightAnimationType getCurrentAnimType() {
        if (isDead) return FalseKnightAnimationType.DEATH_LAND;
        return switch (currentState) {
            case IDLE -> FalseKnightAnimationType.IDLE;
            case RUN -> FalseKnightAnimationType.RUN;
            case RUN_ANTIC -> FalseKnightAnimationType.RUN_ANTIC;
            case ATTACK_ANTIC -> FalseKnightAnimationType.ATTACK_ANTIC;
            case ATTACK -> FalseKnightAnimationType.ATTACK;
            case ATTACK_RECOVER -> FalseKnightAnimationType.ATTACK_RECOVER;
            case JUMP -> FalseKnightAnimationType.JUMP;
            case JUMP_ATTACK -> FalseKnightAnimationType.JUMP_ATTACK;
            case LAND -> FalseKnightAnimationType.LAND;
            case TURN -> FalseKnightAnimationType.TURN;
            case STUN_RECOVER -> FalseKnightAnimationType.STUN_RECOVER;
            case DEATH_FALL -> FalseKnightAnimationType.DEATH_FALL;
            case DEATH_HIT -> FalseKnightAnimationType.DEATH_HIT;
            case DEATH_LAND -> FalseKnightAnimationType.DEATH_LAND;
        };
    }

    @Override
    public TextureRegion getFrame(float delta) {
        animSet.setAnimation(getCurrentAnimType());
        return animSet.getFrame(delta);
    }

    @Override
    public TextureRegion getCorpseFrame() {
        return GameAssetManager.falseKnightAnimations.get(FalseKnightAnimationType.DEATH_LAND).getKeyFrame(0);
    }

    public FalseKnightState getCurrentState() { return currentState; }
}
