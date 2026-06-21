package src.main.model.entity.knight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.Entity;
import src.main.model.physics.PhysicsSystem;
import src.main.view.GameAssetManager;

public class Knight extends Entity {
    private static final float MOVE_SPEED = 200f;
    private static final float JUMP_VELOCITY = 500f;
    private static final float DASH_SPEED = 500f;
    private static final float DASH_DURATION = 0.2f;
    private static final float ATTACK_DURATION = 0.3f;
    private static final float POGO_BOUNCE = JUMP_VELOCITY * 0.7f;
    private static final float INVINCIBLE_DURATION = 1.0f;
    private static final int MAX_HP = 5;
    private static final int MAX_SOUL = 99;
    private static final int SOUL_PER_HIT = 11;
    private static final int SOUL_PER_HEAL = 33;
    private static final float FOCUS_DURATION = 1.5f;

    private final AnimationSet animationSet;
    private KnightState currentState = KnightState.IDLE;

    // Movement
    private boolean jumpKeyHeld = false;
    private float dashTimer = 0;
    private boolean isDashing = false;
    private int jumpCount = 0;

    // Attack
    private float attackTimer = 0;
    private boolean isPogoAttack = false;
    private boolean hitRegistered = false;

    // HP
    private int hp = MAX_HP;
    private float invincibleTimer = 0;
    private float spawnX, spawnY;

    // Soul
    private int soul = 0;

    // Focus
    private boolean isFocusing = false;
    private float focusTimer = 0;

    public Knight(float x, float y) {
        animationSet = new AnimationSet<>(GameAssetManager.knightAnimations, KnightAnimationType.IDLE);
        position.set(x, y);
        spawnX = x;
        spawnY = y;
        boundingBox.setSize(24, 52);
    }

    @Override
    public void update(float delta) {
        if (invincibleTimer > 0) invincibleTimer -= delta;

        if (isDashing) {
            dashTimer -= delta;
            if (dashTimer <= 0) isDashing = false;
        }

        if (attackTimer > 0) {
            attackTimer -= delta;
            if (attackTimer <= 0) {
                isPogoAttack = false;
                hitRegistered = false;
            }
        }

        // Focus
        if (isFocusing) {
            focusTimer += delta;
            velocity.x = 0;
            velocity.y = 0;
            if (!isOnGround() || hp >= MAX_HP) {
                cancelFocus();
            }
            if (focusTimer >= FOCUS_DURATION) {
                completeFocus();
            }
        }

        // Variable jump height
        if (!jumpKeyHeld && velocity.y > 0)
            velocity.y *= 0.85f;

        if (!isDashing && !isFocusing) {
            if (isMovingLeft() && !isMovingRight()) velocity.x = -MOVE_SPEED;
            else if (isMovingRight() && !isMovingLeft()) velocity.x = MOVE_SPEED;
            else velocity.x = 0;
        }

        velocity.y -= PhysicsSystem.GRAVITY * delta;

        updateAnimationState();
        boundingBox.setPosition(position.x, position.y);
    }

    public void updateAnimationState() {
        if (isDashing) {
            currentState = KnightState.DASHING;
        } else if (isFocusing) {
            currentState = KnightState.FOCUSING;
        } else if (attackTimer > 0) {
            currentState = KnightState.ATTACKING;
        } else if (!isOnGround()) {
            if (jumpCount == 2) currentState = KnightState.DOUBLE_JUMPING;
            else if (velocity.y > 0) currentState = KnightState.JUMPING;
            else currentState = KnightState.FALLING;
        } else if (isMovingLeft() || isMovingRight()) {
            currentState = KnightState.RUNNING;
        } else {
            currentState = KnightState.IDLE;
        }

        KnightAnimationType animType;
        switch (currentState) {
            case RUNNING:   animType = KnightAnimationType.RUN; break;
            case JUMPING:   animType = KnightAnimationType.AIRBORNE; break;
            case DOUBLE_JUMPING: animType = KnightAnimationType.DOUBLE_JUMP; break;
            case FALLING:   animType = KnightAnimationType.FALL; break;
            case ATTACKING: animType = KnightAnimationType.SLASH; break;
            case DASHING:   animType = KnightAnimationType.DASH; break;
            case FOCUSING:
                if (focusTimer < 0.3f) animType = KnightAnimationType.FOCUS_START;
                else if (focusTimer > FOCUS_DURATION - 0.2f) animType = KnightAnimationType.FOCUS_GET;
                else animType = KnightAnimationType.FOCUS;
                break;
            default:        animType = KnightAnimationType.IDLE;
        }
        animationSet.setAnimation(animType);
    }

    // --- MOVEMENT ---
    public void jump() {
        if (isOnGround()) {
            velocity.y = JUMP_VELOCITY;
            setOnGround(false);
            jumpCount = 1;
            jumpKeyHeld = true;
        } else if (jumpCount < 2) {
            velocity.y = JUMP_VELOCITY * 0.9f;
            jumpCount = 2;
            jumpKeyHeld = true;
        }
    }

    public void jumpReleased() {
        jumpKeyHeld = false;
        if (velocity.y > 0) velocity.y *= 0.4f;
    }

    public void dash() {
        if (!isDashing) {
            isDashing = true;
            dashTimer = DASH_DURATION;
            velocity.x = isFacingRight() ? DASH_SPEED : -DASH_SPEED;
            velocity.y = 0;
        }
    }

    // --- ATTACK ---
    public void attack() {
        if (attackTimer <= 0 && !isDashing && !isFocusing) {
            attackTimer = ATTACK_DURATION;
            velocity.x = 0;
            isPogoAttack = false;
            hitRegistered = false;
        }
    }

    public void pogoAttack() {
        if (attackTimer <= 0 && !isDashing && !isFocusing) {
            attackTimer = ATTACK_DURATION;
            velocity.x = 0;
            isPogoAttack = true;
            hitRegistered = false;
        }
    }

    public void doPogoBounce() {
        velocity.y = POGO_BOUNCE;
        setOnGround(false);
        jumpCount = 0;
        isDashing = false;
        dashTimer = 0;
    }

    public boolean isAttacking() { return attackTimer > 0; }
    public boolean isPogoAttack() { return isPogoAttack; }
    public boolean isHitRegistered() { return hitRegistered; }
    public void setHitRegistered(boolean v) { hitRegistered = v; }

    // --- FOCUS ---
    public void startFocus() {
        if (isFocusing || !isOnGround() || isDashing || attackTimer > 0 || hp >= MAX_HP || soul < SOUL_PER_HEAL)
            return;
        isFocusing = true;
        focusTimer = 0;
        velocity.x = 0;
        velocity.y = 0;
    }

    public void cancelFocus() {
        if (!isFocusing) return;
        isFocusing = false;
        focusTimer = 0;
    }

    private void completeFocus() {
        if (spendSoul(SOUL_PER_HEAL)) {
            hp = Math.min(hp + 1, MAX_HP);
        }
        isFocusing = false;
        focusTimer = 0;
    }

    public boolean isFocusing() { return isFocusing; }

    // --- SOUL ---
    public void addSoul(int amount) { soul = Math.min(soul + amount, MAX_SOUL); }
    public boolean spendSoul(int amount) {
        if (soul >= amount) { soul -= amount; return true; }
        return false;
    }
    public int getSoul() { return soul; }
    public int getMaxSoul() { return MAX_SOUL; }

    // --- HP / DAMAGE ---
    public void takeDamage() {
        if (invincibleTimer > 0) return;
        if (isFocusing) cancelFocus();
        hp--;
        invincibleTimer = INVINCIBLE_DURATION;
        velocity.x = isFacingRight() ? -200f : 200f;
        velocity.y = 100f;
        if (hp <= 0) respawn();
    }

    public void respawn() {
        position.set(spawnX, spawnY);
        velocity.set(0, 0);
        hp = MAX_HP;
        setOnGround(false);
        jumpCount = 0;
        isDashing = false;
        dashTimer = 0;
        invincibleTimer = INVINCIBLE_DURATION;
        isFocusing = false;
        focusTimer = 0;
    }

    @Override
    public TextureRegion getFrame(float delta) { return animationSet.getFrame(delta); }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        if (invincibleTimer > 0 && (Math.floor(invincibleTimer * 10) % 2 == 0)) return;
        TextureRegion frame = getFrame(delta);
        float spriteW = boundingBox.width * DRAW_SCALE;
        float spriteH = spriteW * frame.getRegionHeight() / (float) frame.getRegionWidth();
        batch.draw(frame,
            position.x + (boundingBox.width - spriteW) / 2f,
            position.y,
            spriteW / 2f, 0,
            spriteW, spriteH,
            isFacingRight() ? -1 : 1, 1, 0);
    }

    public void resetJump() { jumpCount = 0; }
    public int getHp() { return hp; }
    public int getMaxHp() { return MAX_HP; }
    public float getInvincibleTimer() { return invincibleTimer; }
    public void resetInvincibleTimer() { invincibleTimer = 0; }
}
