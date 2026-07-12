package src.main.model.entity.enemy.boss.falseKnight;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimStateTracker;
import src.main.model.entity.enemy.Enemy;
import src.main.model.physics.PhysicsSystem;

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

    private final AnimStateTracker<FalseKnightAnimationType> animState =
        new AnimStateTracker<>(FalseKnightAnimationType.IDLE);
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
            updateInactive(delta);
            return;
        }

        float freq = isPhase2 ? PHASE2_FREQ_MULT : 1f;
        float animSpeed = isPhase2 ? PHASE2_SPEED_MULT : 1f;
        animState.advanceTime(delta * animSpeed);

        updateTimers(delta);

        Vector2 knightPos = knightRef.getPosition();
        float dx = knightPos.x - position.x;
        float dst = position.dst(knightPos);

        updateFacingAndStun(dx, dst);

        if (!isOnGround()) velocity.y -= PhysicsSystem.GRAVITY * delta;

        applyStateBehaviour(delta);

        boundingBox.setPosition(position);
    }

    private void updateInactive(float delta) {
        if (!isOnGround()) velocity.y -= PhysicsSystem.GRAVITY * delta;
        animState.setAnimation(FalseKnightAnimationType.IDLE);
        boundingBox.setPosition(position);
    }

    private void updateTimers(float delta) {
        attackHitbox.setSize(0, 0);
        isPowerfulHitboxActive = false;

        stateTimer -= delta;
        decisionTimer -= delta;
        stunTimer -= delta;
        if (damageTakenTimer > 0) damageTakenTimer -= delta;
    }

    private void updateFacingAndStun(float dx, float dst) {
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
    }

    private void applyStateBehaviour(float delta) {
        switch (currentState) {
            case IDLE:
                handleIdleState();
                break;
            case RUN:
                handleRunState();
                break;
            case RUN_ANTIC:
                handleRunAnticState();
                break;
            case ATTACK_ANTIC:
                handleAttackAnticState();
                break;
            case ATTACK:
                handleAttackState();
                break;
            case ATTACK_RECOVER:
                handleAttackRecoverState();
                break;
            case JUMP:
                handleJumpState();
                break;
            case JUMP_ATTACK:
                handleJumpAttackState();
                break;
            case LAND:
                handleLandState();
                break;
            case TURN:
                handleTurnState();
                break;
            case DEATH_FALL:
                handleDeathFallState();
                break;
            case BODY:
                handleBodyState(delta);
                break;
            case STUN_RECOVER:
                handleStunRecoverState();
                break;
            case DEATH_HIT:
            case DEATH_LAND:
                break;
        }
    }

    private void handleIdleState() {
        animState.setAnimation(FalseKnightAnimationType.IDLE);
        velocity.set(0, 0);
    }

    private void handleRunState() {
animState.setAnimation(FalseKnightAnimationType.RUN);
            if ("CHARGE".equals(lastMove)) {
            float s = isPhase2 ? CHARGE_SPEED * PHASE2_SPEED_MULT : CHARGE_SPEED;
            velocity.x = isFacingRight() ? s : -s;
        } else {
            float s = isPhase2 ? WALK_SPEED * PHASE2_SPEED_MULT : WALK_SPEED;
            velocity.x = isFacingRight() ? s : -s;
        }
    }

    private void handleRunAnticState() {
        velocity.set(0, 0);
        animState.setAnimation(FalseKnightAnimationType.RUN_ANTIC);
        if (animFinished()) changeState(FalseKnightState.RUN);
    }

    private void handleAttackAnticState() {
        velocity.set(0, 0);
        animState.setAnimation(FalseKnightAnimationType.ATTACK_ANTIC);
        if (animFinished()) changeState(FalseKnightState.ATTACK);
    }

    private void handleAttackState() {
        velocity.set(0, 0);
        animState.setAnimation(FalseKnightAnimationType.ATTACK);
        float t = animState.getStateTime();
        if (t > 0.15f && t < 0.3f) {
            attackHitbox.set(
                position.x + (isFacingRight() ? 30 : -80),
                position.y + 20, 80, 90);
        }
        if (t > 0.15f && t < 0.17f) {
            startShake(4f, 0.3f);
        }
        if (animFinished()) changeState(FalseKnightState.ATTACK_RECOVER);
    }

    private void handleAttackRecoverState() {
        velocity.set(0, 0);
        animState.setAnimation(FalseKnightAnimationType.ATTACK_RECOVER);
        if (animFinished()) changeState(FalseKnightState.IDLE);
    }

    private void handleJumpState() {
        animState.setAnimation(FalseKnightAnimationType.JUMP);
        if (isOnGround()) changeState(FalseKnightState.LAND);
    }

    private void handleJumpAttackState() {
        animState.setAnimation(FalseKnightAnimationType.JUMP_ATTACK);
        if (isOnGround()) {
            if (isPowerfulLanding) {
                attackHitbox.set(position.x - 30, position.y, 160, 100);
                isPowerfulHitboxActive = true;
                startShake(6f, 0.5f);
                isPowerfulLanding = false;
            }
            changeState(FalseKnightState.LAND);
        }
    }

    private void handleLandState() {
        animState.setAnimation(FalseKnightAnimationType.LAND);
        velocity.set(0, 0);
        if (animState.getStateTime() > 0 && animState.getStateTime() < 0.05f) {
            startShake(3f, 0.2f);
        }
        if (stateTimer <= 0 || animFinished()) changeState(FalseKnightState.IDLE);
    }

    private void handleTurnState() {
        animState.setAnimation(FalseKnightAnimationType.TURN);
        if (animFinished()) changeState(FalseKnightState.IDLE);
    }

    private void handleDeathFallState() {
        velocity.set(0, 0);
        animState.setAnimation(FalseKnightAnimationType.DEATH_FALL);
        stunHitbox.set(position.x + 30, position.y + 10, 40, 30);
        if (stunTimer <= 0) {
            changeState(FalseKnightState.STUN_RECOVER);
        } else if (animFinished()) {
            changeState(FalseKnightState.BODY);
        }
    }

    private void handleBodyState(float delta) {
        velocity.set(0, 0);
        animState.setAnimation(FalseKnightAnimationType.BODY);
        stunHitbox.set(position.x + 30, position.y + 10, 40, 30);
        if (stunTimer <= 0) {
            if (stunRecoverDelay < 0) stunRecoverDelay = STUN_RECOVER_DELAY;
            stunRecoverDelay -= delta;
            if (stunRecoverDelay <= 0) {
                changeState(FalseKnightState.STUN_RECOVER);
            }
        }
    }

    private void handleStunRecoverState() {
        velocity.set(0, 0);
        animState.setAnimation(FalseKnightAnimationType.STUN_RECOVER);
        stunHitbox.set(position.x + 30, position.y + 10, 40, 30);
        if (animFinished() || stateTimer <= 0) {
            isPhase2 = true;
            isStunned = false;
            hp = MAX_HP;
            stunHitbox.setSize(0, 0);
            changeState(FalseKnightState.IDLE);
        }
    }

    private void updateDeathAnimation(float delta) {
        animState.advanceTime(delta);
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
        isPowerfulLanding = true;
        changeState(FalseKnightState.JUMP_ATTACK);
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
        animState.reset();
    }

    private boolean animFinished() {
        return animState.isFinished();
    }

    public FalseKnightAnimationType getAnimType() {
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
    public float getStateTime() { return animState.getStateTime(); }

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

    public void forceKill() {
        if (isDead) return;
        hp = 0;
        isDead = true;
        velocity.set(0, 0);
        changeState(FalseKnightState.DEATH_FALL);
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
