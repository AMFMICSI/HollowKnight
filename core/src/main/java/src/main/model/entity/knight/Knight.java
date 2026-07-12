package src.main.model.entity.knight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import src.main.model.data.KeyBindings;
import src.main.model.entity.animation.AnimStateTracker;
import src.main.model.entity.charm.CharmType;
import src.main.model.entity.charm.CharmEffectCalculator;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.Entity;
import src.main.model.entity.spell.SpellType;
import src.main.model.physics.PhysicsSystem;
import src.main.view.manager.GameMusic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Knight extends Entity {
    private static final float MOVE_SPEED = 200f;
    private static final float JUMP_VELOCITY = 500f;
    private static final float DASH_SPEED = 500f;
    private static final float DASH_DURATION = 0.5f;
    private static final float ATTACK_DURATION = 0.3f;
    private static final float POGO_BOUNCE = JUMP_VELOCITY * 0.7f;
    private static final float INVINCIBLE_DURATION = 1.0f;
    private static final int MAX_HP = 5;
    private static final int MAX_SOUL = 99;
    private static final int SOUL_PER_HIT = 11;
    private static final int SOUL_PER_HEAL = 33;
    private static final float FOCUS_DURATION = 1.5f;
    private static final float WALL_SLIDE_SPEED = 70f;
    private static final float WALL_JUMP_HORIZONTAL = 300f;
    private static final float CAST_DURATION = 0.3f;
    private static final int SOUL_PER_SPELL = 33;
    private static final float DEATH_DURATION = 1.44f;

    private static final float JUMP_HEIGHT_DECAY = 0.85f;
    private static final float JUMP_RELEASE_MODIFIER = 0.4f;
    private static final float DOUBLE_JUMP_FORCE_MODIFIER = 0.9f;
    private static final float KNOCKBACK_FORCE_X = 200f;
    private static final float KNOCKBACK_FORCE_Y = 100f;
    private static final float FOCUS_START_THRESHOLD = 0.3f;
    private static final float FOCUS_GET_THRESHOLD = 0.2f;

    private final AnimStateTracker<KnightAnimationType> animState =
        new AnimStateTracker<>(KnightAnimationType.IDLE);
    private KnightState currentState = KnightState.IDLE;

    private final CheatSystem cheatSystem = new CheatSystem();
    private final HealthSystem healthSystem = new HealthSystem(this, MAX_HP);

    private boolean jumpKeyHeld = false;
    private float dashTimer = 0;
    private boolean isDashing = false;
    private int jumpCount = 0;

    private boolean isOnWall = false;
    private boolean wallToLeft = false;

    private float attackTimer = 0;
    private boolean isPogoAttack = false;
    private boolean isAttackDown = false;
    private boolean isAttackUp = false;
    private boolean hitRegistered = false;

    // خروج از حالت final برای پشتیبانی از تغییر پویای محل ریپاون روی نیمکت‌ها
    private float spawnX;
    private float spawnY;

    private int soul = 99;

    private final Set<CharmType> equippedCharms = new HashSet<>();
    private final int maxNotches = 3;
    private float dashCooldownTimer = 0;
    private static final float DASH_COOLDOWN = 0.5f;
    private final Set<Enemy> sharpShadowHitEnemies = new HashSet<>();

    private boolean isFocusing = false;
    private float focusTimer = 0;

    private boolean isCasting = false;
    private float castTimer = 0;
    private SpellType pendingCastResult = null;
    private SpellType pendingSpellType = null;
    private boolean pendingSoulToast = false;

    private boolean runStartPlayed;
    private final KeyBindings keys;

    public Knight(float x, float y, KeyBindings keys) {
        this.keys = keys;
        this.position.set(x, y);
        this.spawnX = x;
        this.spawnY = y;
        this.boundingBox.setSize(24, 52);
    }

    @Override
    public void update(float delta) {
        if (healthSystem.isDead()) {
            healthSystem.updateTimers(delta);
            updateAnimationState();
            return;
        }

        updateTimers(delta);
        updateFocusAndCast(delta);
        updateJumpHeight();
        updateMovement(delta);

        updateAnimationState();
        animState.advanceTime(delta);
        boundingBox.setPosition(position.x, position.y);
    }

    private void updateTimers(float delta) {
        healthSystem.updateTimers(delta);

        if (isDashing) {
            dashTimer -= delta;
            if (dashTimer <= 0) {
                isDashing = false;
                dashCooldownTimer = getDashCooldown();
                sharpShadowHitEnemies.clear();
            }
        }
        if (dashCooldownTimer > 0) dashCooldownTimer -= delta;

        if (attackTimer > 0) {
            attackTimer -= delta;
            if (attackTimer <= 0) {
                isPogoAttack = false;
                isAttackDown = false;
                isAttackUp = false;
                hitRegistered = false;
            }
        }
    }

    private void updateFocusAndCast(float delta) {
        if (isFocusing) {
            focusTimer += delta;
            velocity.x = 0;
            velocity.y = 0;
            if (!isOnGround() || healthSystem.getHp() >= MAX_HP) {
                cancelFocus();
            }
            if (focusTimer >= getFocusDuration()) {
                completeFocus();
            }
        }

        if (isCasting) {
            castTimer += delta;
            velocity.x = 0;
            velocity.y = 0;
            if (castTimer >= CAST_DURATION) {
                completeCast();
            }
        }
    }

    private void updateJumpHeight() {
        if (!jumpKeyHeld && velocity.y > 0)
            velocity.y *= JUMP_HEIGHT_DECAY;
    }

    private void updateMovement(float delta) {
        if (cheatSystem.isNoclipMode()) {
            velocity.y = 0;
            velocity.x = 0;
            if (isMovingLeft()) velocity.x = -MOVE_SPEED * 2;
            if (isMovingRight()) velocity.x = MOVE_SPEED * 2;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(keys.get("JUMP")))
                velocity.y = MOVE_SPEED * 2;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(keys.get("POGO")))
                velocity.y = -MOVE_SPEED * 2;
        } else {
            if (!isDashing && !isFocusing && !isCasting) {
                if (isOnWall) {
                    velocity.y = -WALL_SLIDE_SPEED;
                    velocity.x = 0;
                } else {
                    if (isMovingLeft() && !isMovingRight()) velocity.x = -MOVE_SPEED;
                    else if (isMovingRight() && !isMovingLeft()) velocity.x = MOVE_SPEED;
                    else velocity.x = 0;
                }
            }
            if (!isDashing && !isOnWall) velocity.y -= PhysicsSystem.GRAVITY * delta;
        }
    }

    public void updateAnimationState() {
        if (healthSystem.isDead()) {
            currentState = KnightState.DEAD;
            animState.setAnimation(KnightAnimationType.DEATH);
            return;
        }
        if (cheatSystem.isNoclipMode() && attackTimer <= 0 && !isFocusing && !isCasting && !isDashing) {
            currentState = KnightState.IDLE;
            animState.setAnimation(KnightAnimationType.IDLE);
            return;
        }
        updateCurrentState();
        animState.setAnimation(selectAnimationType());
    }

    private void updateCurrentState() {
        if (!isCasting) {
            if (isFocusing) {
                currentState = KnightState.FOCUSING;
            } else if (isDashing) {
                currentState = KnightState.DASHING;
            } else if (attackTimer > 0) {
                if (isAttackDown) currentState = KnightState.ATTACKING_DOWN;
                else if (isAttackUp) currentState = KnightState.ATTACKING_UP;
                else currentState = KnightState.ATTACKING;
            } else if (isOnWall) {
                currentState = KnightState.WALL_SLIDING;
            } else if (!isOnGround()) {
                if (jumpCount == 2) currentState = KnightState.DOUBLE_JUMPING;
                else if (velocity.y > 0) currentState = KnightState.JUMPING;
                else currentState = KnightState.FALLING;
            } else if (isMovingLeft() || isMovingRight()) {
                currentState = KnightState.RUNNING;
            } else {
                currentState = KnightState.IDLE;
            }
        }
    }

    private KnightAnimationType selectAnimationType() {
        switch (currentState) {
            case RUNNING:
                if (!runStartPlayed
                    && animState.getCurrentType() == KnightAnimationType.RUN_START
                    && animState.getStateTime() >= animState.getDuration()) {
                    runStartPlayed = true;
                }
                return runStartPlayed ? KnightAnimationType.RUN_LOOP : KnightAnimationType.RUN_START;
            case JUMPING:   return KnightAnimationType.AIRBORNE;
            case DOUBLE_JUMPING: return KnightAnimationType.DOUBLE_JUMP;
            case FALLING:
            case WALL_SLIDING:
                return KnightAnimationType.FALL;
            case ATTACKING:   return KnightAnimationType.SLASH;
            case ATTACKING_DOWN: return KnightAnimationType.DOWN_SLASH;
            case ATTACKING_UP:   return KnightAnimationType.UP_SLASH;
            case DASHING:   return KnightAnimationType.DASH;
            case CASTING_VENGEFUL: return KnightAnimationType.FIREBALL_CAST;
            case CASTING_WRAITHS:  return KnightAnimationType.SCREAM;
            case FOCUSING:
                if (focusTimer < FOCUS_START_THRESHOLD) return KnightAnimationType.FOCUS_START;
                else if (focusTimer > getFocusDuration() - FOCUS_GET_THRESHOLD) return KnightAnimationType.FOCUS_GET;
                else return KnightAnimationType.FOCUS;
            default:
                runStartPlayed = false;
                return KnightAnimationType.IDLE;
        }
    }

    public void jump() {
        if (healthSystem.isDead() || isCasting) return;
        if (isOnWall) {
            velocity.y = JUMP_VELOCITY;
            velocity.x = wallToLeft ? WALL_JUMP_HORIZONTAL : -WALL_JUMP_HORIZONTAL;
            jumpCount = 1;
            jumpKeyHeld = true;
            isOnWall = false;
            return;
        }
        if (isOnGround()) {
            velocity.y = JUMP_VELOCITY;
            setOnGround(false);
            jumpCount = 1;
            jumpKeyHeld = true;
        } else if (jumpCount < 2) {
            velocity.y = JUMP_VELOCITY * DOUBLE_JUMP_FORCE_MODIFIER;
            jumpCount = 2;
            jumpKeyHeld = true;
        }
    }

    public void jumpReleased() {
        if (healthSystem.isDead()) return;
        jumpKeyHeld = false;
        if (velocity.y > 0) velocity.y *= JUMP_RELEASE_MODIFIER;
    }

    public void dash() { startDash(isFacingRight() ? DASH_SPEED : -DASH_SPEED, 0); }
    public void dashDown() { startDash(isFacingRight() ? DASH_SPEED : -DASH_SPEED, -DASH_SPEED); }
    public void dashUp() { startDash(0, DASH_SPEED); }

    private void startDash(float vx, float vy) {
        if (healthSystem.isDead()) return;
        if (!isDashing && !isCasting && dashCooldownTimer <= 0) {
            isDashing = true;
            dashTimer = DASH_DURATION * getDashLengthMultiplier();
            velocity.set(vx, vy);
            GameMusic.DASH.play();
        }
    }

    public void attack() { startAttack(false, false, false); }
    public void attackDown() { startAttack(false, true, false); }
    public void attackUp() { startAttack(false, false, true); }
    public void pogoAttack() { startAttack(true, false, false); }

    private void startAttack(boolean pogo, boolean down, boolean up) {
        if (healthSystem.isDead()) return;
        if (attackTimer <= 0 && !isDashing && !isFocusing && !isCasting) {
            attackTimer = getAttackDuration();
            velocity.x = 0;
            isPogoAttack = pogo;
            isAttackDown = down;
            isAttackUp = up;
            hitRegistered = false;
            GameMusic.NAIL_SLASH.play();
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
    public boolean isAttackDown() { return isAttackDown; }
    public boolean isAttackUp() { return isAttackUp; }
    public float getAttackElapsed() { return getAttackDuration() - attackTimer; }
    public boolean isPogoAttack() { return isPogoAttack; }
    public boolean isHitRegistered() { return hitRegistered; }
    public void setHitRegistered(boolean v) { hitRegistered = v; }

    public void setOnWall(boolean onWall, boolean wallLeft) {
        if (onWall && !this.isOnWall) {
            jumpCount = 0;
            isDashing = false;
            dashTimer = 0;
            velocity.x = 0;
        }
        this.isOnWall = onWall;
        this.wallToLeft = wallLeft;
        if (onWall) setFacingRight(!wallLeft);
    }

    public void startCast(SpellType type) {
        if (healthSystem.isDead() || isCasting || isDashing || attackTimer > 0 || isFocusing || !isOnGround()) return;
        if (soul < SOUL_PER_SPELL) {
            pendingSoulToast = true;
            return;
        }
        isCasting = true;
        castTimer = 0;
        pendingSpellType = type;
        currentState = (type == SpellType.VENGEFUL) ? KnightState.CASTING_VENGEFUL : KnightState.CASTING_WRAITHS;
        velocity.set(0, 0);
    }

    private void completeCast() {
        spendSoul(SOUL_PER_SPELL);
        pendingCastResult = pendingSpellType;
        pendingSpellType = null;
        isCasting = false;
        castTimer = 0;
        currentState = KnightState.IDLE;
    }

    public void cancelCast() {
        if (!isCasting) return;
        isCasting = false;
        castTimer = 0;
        pendingSpellType = null;
    }

    public SpellType consumePendingCastResult() {
        SpellType v = pendingCastResult;
        pendingCastResult = null;
        return v;
    }

    public boolean consumePendingSoulToast() {
        boolean v = pendingSoulToast;
        pendingSoulToast = false;
        return v;
    }

    public Set<CharmType> getEquippedCharms() { return Collections.unmodifiableSet(equippedCharms); }
    public boolean equipCharm(CharmType charm) {
        if (equippedCharms.contains(charm)) return true;
        if (getUsedNotches() + charm.getNotchCost() > maxNotches) return false;
        equippedCharms.add(charm);
        return true;
    }
    public void unequipCharm(CharmType charm) { equippedCharms.remove(charm); }
    public void clearCharms() { equippedCharms.clear(); }
    public boolean isCharmEquipped(CharmType charm) { return equippedCharms.contains(charm); }
    public int getUsedNotches() {
        int sum = 0;
        for (CharmType c : equippedCharms) sum += c.getNotchCost();
        return sum;
    }
    public int getMaxNotches() { return maxNotches; }

    public int getAttackDamage() { return CharmEffectCalculator.getAttackDamage(equippedCharms); }
    public float getAttackDuration() { return CharmEffectCalculator.getAttackDuration(equippedCharms, ATTACK_DURATION); }
    public float getFocusDuration() { return CharmEffectCalculator.getFocusDuration(equippedCharms, FOCUS_DURATION); }
    public float getDashCooldown() { return CharmEffectCalculator.getDashCooldown(equippedCharms, DASH_COOLDOWN); }
    public int getSoulPerHit() { return CharmEffectCalculator.getSoulPerHit(equippedCharms, SOUL_PER_HIT); }
    public int getSpellDamage() { return CharmEffectCalculator.getSpellDamage(equippedCharms); }
    public boolean hasSharpShadow() { return isCharmEquipped(CharmType.SHARP_SHADOW); }
    public float getDashElapsed() { return DASH_DURATION * getDashLengthMultiplier() - dashTimer; }
    public float getDashLengthMultiplier() { return isCharmEquipped(CharmType.SHARP_SHADOW) ? 1.2f : 1.0f; }

    public boolean trySharpShadowHit(Enemy enemy) {
        if (!hasSharpShadow() || !isDashing || sharpShadowHitEnemies.contains(enemy)) return false;
        sharpShadowHitEnemies.add(enemy);
        return true;
    }

    public void startFocus() {
        if (healthSystem.isDead() || isFocusing || !isOnGround() || isDashing || attackTimer > 0 || healthSystem.getHp() >= MAX_HP || isCasting) return;
        if (soul < SOUL_PER_HEAL) {
            pendingSoulToast = true;
            return;
        }
        isFocusing = true;
        focusTimer = 0;
        velocity.set(0, 0);
    }

    public void cancelFocus() {
        if (!isFocusing) return;
        isFocusing = false;
        focusTimer = 0;
    }

    private void completeFocus() {
        if (spendSoul(SOUL_PER_HEAL)) {
            healthSystem.setHp(Math.min(healthSystem.getHp() + 1, MAX_HP));
            GameMusic.FOCUS_HEAL.play();
        }
        isFocusing = false;
        focusTimer = 0;
    }

    public boolean isFocusing() { return isFocusing; }
    public boolean isCasting() { return isCasting; }
    public boolean isDashing() { return isDashing; }

    public void addSoul(int amount) {
        soul = Math.min(soul + amount, MAX_SOUL);
        GameMusic.SOUL_PICKUP.play();
    }
    public boolean spendSoul(int amount) {
        if (soul >= amount) { soul -= amount; return true; }
        return false;
    }
    public int getSoul() { return soul; }
    public int getMaxSoul() { return MAX_SOUL; }

    public void takeDamage() { takeDamage(1); }
    public void takeDamage(int amount) {
        healthSystem.takeDamage(amount, cheatSystem.isGodMode(), INVINCIBLE_DURATION, DEATH_DURATION, KNOCKBACK_FORCE_X, KNOCKBACK_FORCE_Y);
    }

    public boolean consumeJustDamaged() { return healthSystem.consumeJustDamaged(); }
    public void respawn() { healthSystem.respawn(); }
    public boolean consumeJustRespawned() { return healthSystem.consumeJustRespawned(); }

    // متد عمومی برای تنظیم نقطه اسپان مجدد پادشاهی کدهای تو
    public void setSpawnPoint(float x, float y) {
        this.spawnX = x;
        this.spawnY = y;
    }

    public void resetJump() { jumpCount = 0; }
    public void stopDashing() {
        isDashing = false;
        dashTimer = 0;
    }
    public void resetDashCooldown() { dashCooldownTimer = 0; }
    public void clearSharpShadowHitEnemies() { sharpShadowHitEnemies.clear(); }
    public float getSpawnX() { return spawnX; }
    public float getSpawnY() { return spawnY; }

    public KnightAnimationType getAnimType() { return animState.getCurrentType(); }
    public float getStateTime() { return animState.getStateTime(); }
    public float getInvincibleTimer() { return healthSystem.getInvincibleTimer(); }

    public CheatSystem getCheatSystem() { return cheatSystem; }
    public int getHp() { return healthSystem.getHp(); }
    public int getMaxHp() { return MAX_HP; }
    public void setHp(int hp) { healthSystem.setHp(Math.min(hp, MAX_HP)); }
    public void setSoul(int soul) { this.soul = Math.min(soul, MAX_SOUL); }
    public KnightState getCurrentState() { return currentState; }
    public boolean isDead() { return healthSystem.isDead(); }
    public boolean isGodMode() { return cheatSystem.isGodMode(); }
    public boolean isNoclipMode() { return cheatSystem.isNoclipMode(); }
}
