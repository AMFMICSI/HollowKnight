package src.main.model.entity.enemy.boss.falseKnight;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.enemy.Enemy;
import src.main.model.physics.PhysicsSystem;
import src.main.view.GameAssetManager;

public class FalseKnight extends Enemy {

    private static final int MAX_HP = 10;
    private static final float WALK_SPEED = 40f;
    private static final float PHASE2_SPEED_MULT = 1.5f;
    private static final float PHASE2_FREQ_MULT = 1.5f;

    private static final float CLOSE_RANGE = 150f;
    private static final float FAR_RANGE = 300f;
    private static final float DECISION_INTERVAL = 2f;

    private static final float LEAP_VX = 250f;
    private static final float LEAP_VY = 500f;
    private static final float DEF_LEAP_VX = 200f;
    private static final float DEF_LEAP_VY = 350f;
    private static final float POWERFUL_VX = 200f;
    private static final float POWERFUL_VY = 550f;
    private static final float CHARGE_SPEED = 250f;

    private static final float STUN_DURATION = 4.0f;
    private static final float STUN_RECOVER_DELAY = 3.0f;
    private static final float DAMAGE_WINDOW = 0.5f;

    private final AnimationSet<FalseKnightAnimationType> animSet;
    private final KnightRef knightRef;
    private FalseKnightState currentState = FalseKnightState.IDLE;
    private float stateTimer;
    private float decisionTimer;
    private String lastMove = "";

    private boolean isPhase2 = false;
    private boolean isStunned = false;
    private boolean pendingStun = false;
    private float stunTimer;
    private float stunRecoverDelay = -1f;

    private Rectangle attackHitbox = new Rectangle();
    private Rectangle stunHitbox = new Rectangle();
    private boolean isPowerfulHitboxActive = false;
    private boolean isPowerfulLanding = false;

    private boolean shaking;
    private float shakeIntensity;
    private float shakeDuration;

    private float damageTakenTimer;
    private boolean active;

    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    public FalseKnight(float x, float y, KnightRef knightRef) {
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        boundingBox.setSize(100, 140);
        this.knightRef = knightRef;
        animSet = new AnimationSet<>(GameAssetManager.falseKnightAnimations, FalseKnightAnimationType.IDLE);
        decisionTimer = 1.0f;
        setFacingRight(true);
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            updateDeathAnimation(delta);
            return;
        }

        if (!active) {
            if (!isOnGround()) velocity.y -= PhysicsSystem.GRAVITY * delta;
            animSet.setAnimation(FalseKnightAnimationType.IDLE);
            boundingBox.setPosition(position);
            return;
        }

        float freq = isPhase2 ? PHASE2_FREQ_MULT : 1f;
        float animSpeed = isPhase2 ? PHASE2_SPEED_MULT : 1f;
        animSet.advanceStateTime(delta * animSpeed);

        attackHitbox.setSize(0, 0);
        isPowerfulHitboxActive = false;

        stateTimer -= delta;
        decisionTimer -= delta;
        stunTimer -= delta;
        if (damageTakenTimer > 0) damageTakenTimer -= delta;

        Vector2 knightPos = knightRef.getPosition();
        float dx = knightPos.x - position.x;
        float dst = position.dst(knightPos);

        if (!isStunned && currentState == FalseKnightState.IDLE) {
            setFacingRight(dx > 0);
        }

        checkStunTrigger();
        if (pendingStun && canStunNow()) {
            pendingStun = false;
            enterStun();
        }

        if (decisionTimer <= 0 && (currentState == FalseKnightState.IDLE
            || currentState == FalseKnightState.RUN)) {
            decideMove(dst, dx);
        }

        if (!isOnGround()) velocity.y -= PhysicsSystem.GRAVITY * delta;

        switch (currentState) {
            case IDLE:
                animSet.setAnimation(FalseKnightAnimationType.IDLE);
                velocity.set(0, 0);
                break;

            case RUN:
                animSet.setAnimation(FalseKnightAnimationType.RUN);
                if ("CHARGE".equals(lastMove)) {
                    float s = isPhase2 ? CHARGE_SPEED * PHASE2_SPEED_MULT : CHARGE_SPEED;
                    velocity.x = isFacingRight() ? s : -s;
                } else {
                    float s = isPhase2 ? WALK_SPEED * PHASE2_SPEED_MULT : WALK_SPEED;
                    velocity.x = isFacingRight() ? s : -s;
                }
                break;

            case RUN_ANTIC:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.RUN_ANTIC);
                if (animFinished()) changeState(FalseKnightState.RUN);
                break;

            case ATTACK_ANTIC:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.ATTACK_ANTIC);
                if (animFinished()) changeState(FalseKnightState.ATTACK);
                break;

            case ATTACK:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.ATTACK);
                float t = animSet.getStateTime();
                if (t > 0.15f && t < 0.3f) {
                    attackHitbox.set(
                        position.x + (isFacingRight() ? 30 : -80),
                        position.y + 20, 80, 90);
                }
                if (t > 0.15f && t < 0.17f) {
                    startShake(4f, 0.3f);
                }
                if (animFinished()) changeState(FalseKnightState.ATTACK_RECOVER);
                break;

            case ATTACK_RECOVER:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.ATTACK_RECOVER);
                if (animFinished()) changeState(FalseKnightState.IDLE);
                break;

            case JUMP:
                animSet.setAnimation(FalseKnightAnimationType.JUMP);
                if (isOnGround()) changeState(FalseKnightState.LAND);
                break;

            case JUMP_ATTACK:
                animSet.setAnimation(FalseKnightAnimationType.JUMP_ATTACK);
                if (isOnGround()) {
                    if (isPowerfulLanding) {
                        attackHitbox.set(position.x - 30, position.y, 160, 100);
                        isPowerfulHitboxActive = true;
                        startShake(6f, 0.5f);
                        isPowerfulLanding = false;
                    }
                    changeState(FalseKnightState.LAND);
                }
                break;

            case LAND:
                animSet.setAnimation(FalseKnightAnimationType.LAND);
                velocity.set(0, 0);
                if (animSet.getStateTime() > 0 && animSet.getStateTime() < 0.05f) {
                    startShake(3f, 0.2f);
                }
                if (stateTimer <= 0 || animFinished()) changeState(FalseKnightState.IDLE);
                break;

            case TURN:
                animSet.setAnimation(FalseKnightAnimationType.TURN);
                if (animFinished()) changeState(FalseKnightState.IDLE);
                break;

            case DEATH_FALL:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.DEATH_FALL);
                stunHitbox.set(position.x + 30, position.y + 10, 40, 30);
                if (stunTimer <= 0) {
                    changeState(FalseKnightState.STUN_RECOVER);
                } else if (animFinished()) {
                    changeState(FalseKnightState.BODY);
                }
                break;

            case BODY:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.BODY);
                stunHitbox.set(position.x + 30, position.y + 10, 40, 30);
                if (stunTimer <= 0) {
                    if (stunRecoverDelay < 0) stunRecoverDelay = STUN_RECOVER_DELAY;
                    stunRecoverDelay -= delta;
                    if (stunRecoverDelay <= 0) {
                        changeState(FalseKnightState.STUN_RECOVER);
                    }
                }
                break;

            case STUN_RECOVER:
                velocity.set(0, 0);
                animSet.setAnimation(FalseKnightAnimationType.STUN_RECOVER);
                stunHitbox.set(position.x + 30, position.y + 10, 40, 30);
                if (animFinished() || stateTimer <= 0) {
                    isPhase2 = true;
                    isStunned = false;
                    hp = MAX_HP;
                    stunHitbox.setSize(0, 0);
                    changeState(FalseKnightState.IDLE);
                }
                break;

            case DEATH_HIT:
            case DEATH_LAND:
                break;
        }

        boundingBox.setPosition(position);
    }

    private void updateDeathAnimation(float delta) {
        animSet.advanceStateTime(delta);
        switch (currentState) {
            case DEATH_FALL:
                if (animFinished()) changeState(FalseKnightState.DEATH_HIT);
                break;
            case DEATH_HIT:
                if (animFinished()) changeState(FalseKnightState.DEATH_LAND);
                break;
            case DEATH_LAND:
                if (animFinished()) deadAnimationDone = true;
                break;
            default:
                break;
        }
        if (!isOnGround()) velocity.y -= PhysicsSystem.GRAVITY * delta;
        boundingBox.setPosition(position);
    }

    private void checkStunTrigger() {
        if (!isStunned && !isPhase2 && hp <= MAX_HP / 2
            && currentState != FalseKnightState.STUN_RECOVER) {
            if (canStunNow()) {
                pendingStun = false;
                enterStun();
            } else {
                pendingStun = true;
            }
        }
    }

    private void startShake(float intensity, float duration) {
        shaking = true;
        shakeIntensity = intensity;
        shakeDuration = duration;
    }

    private void decideMove(float dst, float dx) {
        setFacingRight(dx > 0);
        float r = (float) Math.random();

        if (damageTakenTimer > 0 && !lastMove.equals("DEFENSIVE_LEAP")) {
            startDefensiveLeap(dx); return;
        }

        if (dst < CLOSE_RANGE) {
            if (r < 0.6f && !lastMove.equals("SLAM")) {
                startMaceSlam(); return;
            }
            if (r < 0.8f && !lastMove.equals("DEFENSIVE_LEAP")) {
                startDefensiveLeap(dx); return;
            }
        } else if (dst < FAR_RANGE) {
            if (r < 0.3f && !lastMove.equals("SLAM")) {
                startMaceSlam(); return;
            }
            if (r < 0.55f && !lastMove.equals("CHARGE")) {
                startCharge(dx); return;
            }
            if (r < 0.8f && !lastMove.equals("OFFENSIVE_LEAP")) {
                startOffensiveLeap(dx); return;
            }
            if (isPhase2 && !lastMove.equals("POWERFUL_SLAM") && r < 0.95f) {
                startPowerfulSlam(dx); return;
            }
        } else {
            if (r < 0.45f && !lastMove.equals("CHARGE")) {
                startCharge(dx); return;
            }
            if (r < 0.8f && !lastMove.equals("OFFENSIVE_LEAP")) {
                startOffensiveLeap(dx); return;
            }
        }

        startWalk(dx);
    }

    private void startMaceSlam() {
        lastMove = "SLAM";
        changeState(FalseKnightState.ATTACK_ANTIC);
    }

    private void startCharge(float dx) {
        lastMove = "CHARGE";
        changeState(FalseKnightState.RUN_ANTIC);
    }

    private void startOffensiveLeap(float dx) {
        lastMove = "OFFENSIVE_LEAP";
        velocity.set(dx > 0 ? LEAP_VX : -LEAP_VX, LEAP_VY);
        setOnGround(false);
        changeState(FalseKnightState.JUMP);
    }

    private void startDefensiveLeap(float dx) {
        lastMove = "DEFENSIVE_LEAP";
        velocity.set(dx > 0 ? -DEF_LEAP_VX : DEF_LEAP_VX, DEF_LEAP_VY);
        setOnGround(false);
        changeState(FalseKnightState.JUMP);
    }

    private void startPowerfulSlam(float dx) {
        lastMove = "POWERFUL_SLAM";
        velocity.set(dx > 0 ? POWERFUL_VX : -POWERFUL_VX, POWERFUL_VY);
        setOnGround(false);
        isPowerfulLanding = true;
        changeState(FalseKnightState.JUMP_ATTACK);
    }

    private void startWalk(float dx) {
        lastMove = "WALK";
        changeState(FalseKnightState.RUN);
    }

    private boolean canStunNow() {
        return currentState == FalseKnightState.IDLE
            || currentState == FalseKnightState.ATTACK_RECOVER
            || currentState == FalseKnightState.LAND;
    }

    private void enterStun() {
        isStunned = true;
        lastMove = "";
        velocity.set(0, 0);
        stunTimer = STUN_DURATION;
        stunRecoverDelay = -1f;
        changeState(FalseKnightState.DEATH_FALL);
    }

    private void changeState(FalseKnightState newState) {
        currentState = newState;
        float freq = isPhase2 ? PHASE2_FREQ_MULT : 1f;
        stateTimer = switch (newState) {
            case ATTACK_RECOVER -> (5f * 0.12f) / freq;
            case RUN -> DECISION_INTERVAL / freq;
            case LAND -> (5f * 0.1f) / freq;
            case STUN_RECOVER -> (6f * 0.1f) / freq;
            default -> 0;
        };
        if (newState == FalseKnightState.IDLE || newState == FalseKnightState.RUN) {
            decisionTimer = (DECISION_INTERVAL + (float) Math.random()) / freq;
        }
        animSet.resetAnimation();
    }

    private boolean animFinished() {
        return animSet.getAnimationDuration() <= 0
            || animSet.getStateTime() >= animSet.getAnimationDuration();
    }

    @Override
    public TextureRegion getFrame(float delta) {
        animSet.setAnimation(getCurrentAnimType());
        return animSet.getCurrentFrame();
    }

    private FalseKnightAnimationType getCurrentAnimType() {
        if (isDead) {
            return switch (currentState) {
                case DEATH_FALL -> FalseKnightAnimationType.DEATH_FALL;
                case DEATH_HIT -> FalseKnightAnimationType.DEATH_HIT;
                case DEATH_LAND -> FalseKnightAnimationType.DEATH_LAND;
                default -> FalseKnightAnimationType.DEATH_FALL;
            };
        }
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
            case BODY -> FalseKnightAnimationType.BODY;
            case DEATH_FALL -> FalseKnightAnimationType.DEATH_FALL;
            case DEATH_HIT -> FalseKnightAnimationType.DEATH_HIT;
            case DEATH_LAND -> FalseKnightAnimationType.DEATH_LAND;
        };
    }

    @Override
    public TextureRegion getCorpseFrame() {
        Animation<TextureRegion> anim = GameAssetManager.falseKnightAnimations
            .get(FalseKnightAnimationType.DEATH_LAND);
        return anim.getKeyFrames()[anim.getKeyFrames().length - 1];
    }

    @Override
    public void takeDamage(int amount) {
        if (isDead) return;
        damageTakenTimer = DAMAGE_WINDOW;

        int threshold = MAX_HP / 2;

        if (isStunned) {
            hp -= amount;
            if (hp <= 0) {
                hp = 1;
                stunTimer = 0;
                stunRecoverDelay = 0;
            }
            return;
        }

        if (!isPhase2 && hp >= threshold && hp - amount <= threshold) {
            hp = threshold;
            return;
        }

        hp -= amount;
        if (hp <= 0) {
            isDead = true;
            velocity.set(0, 0);
            changeState(FalseKnightState.DEATH_FALL);
        }
    }

    public float getDamageTakenTimer() { return damageTakenTimer; }
    public void setActive(boolean active) { this.active = active; }

    public FalseKnightState getCurrentState() { return currentState; }
    public Rectangle getAttackHitbox() { return attackHitbox; }
    public Rectangle getStunHitbox() { return stunHitbox; }
    public boolean isPhase2() { return isPhase2; }
    public boolean isStunned() { return isStunned; }
    public boolean isPowerfulHitboxActive() { return isPowerfulHitboxActive; }
    public boolean isShaking() { boolean v = shaking; shaking = false; return v; }
    public float getShakeIntensity() { return shakeIntensity; }
    public float getShakeDuration() { return shakeDuration; }
}
