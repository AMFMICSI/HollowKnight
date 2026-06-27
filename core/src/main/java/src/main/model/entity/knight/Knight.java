package src.main.model.entity.knight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.charm.CharmType;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.Entity;
import src.main.model.entity.spell.SpellType;
import src.main.model.physics.PhysicsSystem;
import src.main.view.GameAssetManager;
import src.main.view.GameMusic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Knight extends Entity {
    private static final float MOVE_SPEED = 200f;
    private static final float JUMP_VELOCITY = 700f;
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
    private static final float WALL_SLIDE_SPEED = 70f;
    private static final float WALL_JUMP_HORIZONTAL = 300f;
    private static final float CAST_DURATION = 0.3f;
    private static final int SOUL_PER_SPELL = 33;

    private final AnimationSet<KnightAnimationType> animationSet;
    private KnightState currentState = KnightState.IDLE;

    // Movement
    private boolean jumpKeyHeld = false;
    private float dashTimer = 0;
    private boolean isDashing = false;
    private int jumpCount = 0;

    // Wall Climb
    private boolean isOnWall = false;
    private boolean wallToLeft = false;

    // Attack
    private float attackTimer = 0;
    private boolean isPogoAttack = false;
    private boolean isAttackDown = false;
    private boolean isAttackUp = false;
    private boolean hitRegistered = false;

    // HP
    private int hp = MAX_HP;
    private float invincibleTimer = 0;
    private float spawnX, spawnY;
    private boolean justDamaged = false;
    private boolean justRespawned = false;

    // Soul
    private int soul = 99;

    // Charms
    private Set<CharmType> equippedCharms = new HashSet<>();
    private int maxNotches = 3;
    private float dashCooldownTimer = 0;
    private static final float DASH_COOLDOWN = 0.5f;
    private Set<Enemy> sharpShadowHitEnemies = new HashSet<>();

    // Focus
    private boolean isFocusing = false;
    private float focusTimer = 0;

    // Casting
    private boolean isCasting = false;
    private float castTimer = 0;
    private SpellType pendingCastResult = null;
    private SpellType pendingSpellType = null;
    private boolean pendingSoulToast = false;

    private boolean runStartPlayed;

    public Knight(float x, float y) {
        animationSet = new AnimationSet<KnightAnimationType>(GameAssetManager.knightAnimations, KnightAnimationType.IDLE);
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

        // Focus
        if (isFocusing) {
            focusTimer += delta;
            velocity.x = 0;
            velocity.y = 0;
            if (!isOnGround() || hp >= MAX_HP) {
                cancelFocus();
            }
            if (focusTimer >= getFocusDuration()) {
                completeFocus();
            }
        }

        // Casting
        if (isCasting) {
            castTimer += delta;
            velocity.x = 0;
            velocity.y = 0;
            if (castTimer >= CAST_DURATION) {
                completeCast();
            }
        }

        // Variable jump height
        if (!jumpKeyHeld && velocity.y > 0)
            velocity.y *= 0.85f;

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

        updateAnimationState();
        boundingBox.setPosition(position.x, position.y);
    }

    public void updateAnimationState() {
        if (isCasting) {
            // keep currentState as set by startCast (CASTING_VENGEFUL or CASTING_WRAITHS)
        } else if (isFocusing) {
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

        KnightAnimationType animType;
        switch (currentState) {
            case RUNNING:
                if (!runStartPlayed
                    && animationSet.getCurrentType() == KnightAnimationType.RUN_START
                    && animationSet.getStateTime() >= animationSet.getAnimationDuration()) {
                    runStartPlayed = true;
                }
                animType = runStartPlayed ? KnightAnimationType.RUN_LOOP : KnightAnimationType.RUN_START;
                break;
            case JUMPING:   animType = KnightAnimationType.AIRBORNE; break;
            case DOUBLE_JUMPING: animType = KnightAnimationType.DOUBLE_JUMP; break;
            case FALLING:   animType = KnightAnimationType.FALL; break;
            case WALL_SLIDING:   animType = KnightAnimationType.FALL; break;
            case ATTACKING:   animType = KnightAnimationType.SLASH; break;
            case ATTACKING_DOWN: animType = KnightAnimationType.DOWN_SLASH; break;
            case ATTACKING_UP:   animType = KnightAnimationType.UP_SLASH; break;
            case DASHING:   animType = KnightAnimationType.DASH; break;
            case CASTING_VENGEFUL: animType = KnightAnimationType.FIREBALL_CAST; break;
            case CASTING_WRAITHS:  animType = KnightAnimationType.SCREAM; break;
            case FOCUSING:
                if (focusTimer < 0.3f) animType = KnightAnimationType.FOCUS_START;
                else if (focusTimer > getFocusDuration() - 0.2f) animType = KnightAnimationType.FOCUS_GET;
                else animType = KnightAnimationType.FOCUS;
                break;
            default:
                runStartPlayed = false;
                animType = KnightAnimationType.IDLE;
        }
        animationSet.setAnimation(animType);
    }

    // --- MOVEMENT ---
    public void jump() {
        if (isCasting) return;
        if (isOnWall) {                                                     // (Wall Jump)
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
        if (!isDashing && !isCasting && dashCooldownTimer <= 0) {
            isDashing = true;
            dashTimer = DASH_DURATION * getDashLengthMultiplier();
            velocity.x = isFacingRight() ? DASH_SPEED : -DASH_SPEED;
            velocity.y = 0;
        }
    }

    public void dashDown() {
        if (!isDashing && !isCasting && dashCooldownTimer <= 0) {
            isDashing = true;
            dashTimer = DASH_DURATION * getDashLengthMultiplier();
            velocity.x = isFacingRight() ? DASH_SPEED : -DASH_SPEED;
            velocity.y = -DASH_SPEED;
        }
    }

    public void dashUp() {
        if (!isDashing && !isCasting && dashCooldownTimer <= 0) {
            isDashing = true;
            dashTimer = DASH_DURATION * getDashLengthMultiplier();
            velocity.x = 0;
            velocity.y = DASH_SPEED;
        }
    }

    // --- ATTACK ---
    public void attack() {
        if (attackTimer <= 0 && !isDashing && !isFocusing && !isCasting) {
            attackTimer = getAttackDuration();
            velocity.x = 0;
            isPogoAttack = false;
            isAttackDown = false;
            isAttackUp = false;
            hitRegistered = false;
            GameMusic.NAIL_SLASH.play();
        }
    }

    public void attackDown() {
        if (attackTimer <= 0 && !isDashing && !isFocusing && !isCasting) {
            attackTimer = getAttackDuration();
            velocity.x = 0;
            isPogoAttack = false;
            isAttackDown = true;
            isAttackUp = false;
            hitRegistered = false;
            GameMusic.NAIL_SLASH.play();
        }
    }

    public void attackUp() {
        if (attackTimer <= 0 && !isDashing && !isFocusing && !isCasting) {
            attackTimer = getAttackDuration();
            velocity.x = 0;
            isPogoAttack = false;
            isAttackDown = false;
            isAttackUp = true;
            hitRegistered = false;
            GameMusic.NAIL_SLASH.play();
        }
    }

    public void pogoAttack() {
        if (attackTimer <= 0 && !isDashing && !isFocusing && !isCasting) {
            attackTimer = getAttackDuration();
            velocity.x = 0;
            isPogoAttack = true;
            isAttackDown = false;
            isAttackUp = false;
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
    public boolean isPogoAttack() { return isPogoAttack; }
    public boolean isHitRegistered() { return hitRegistered; }
    public void setHitRegistered(boolean v) { hitRegistered = v; }

    // --- WALL CLIMB ---
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

    public boolean isOnWall() { return isOnWall; }

    // --- CASTING ---
    public void startCast(SpellType type) {
        if (isCasting || isDashing || attackTimer > 0 || isFocusing || !isOnGround())
            return;
        if (soul < SOUL_PER_SPELL) {
            pendingSoulToast = true;
            return;
        }
        isCasting = true;
        castTimer = 0;
        pendingSpellType = type;
        currentState = (type == SpellType.VENGEFUL)
            ? KnightState.CASTING_VENGEFUL
            : KnightState.CASTING_WRAITHS;
        velocity.x = 0;
        velocity.y = 0;
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

    public boolean isCasting() { return isCasting; }

    // --- CHARMS ---
    public Set<CharmType> getEquippedCharms() {
        return Collections.unmodifiableSet(equippedCharms);
    }

    public boolean equipCharm(CharmType charm) {
        if (equippedCharms.contains(charm)) return true;
        if (getUsedNotches() + charm.getNotchCost() > maxNotches) return false;
        equippedCharms.add(charm);
        return true;
    }

    public void unequipCharm(CharmType charm) {
        equippedCharms.remove(charm);
    }

    public boolean isCharmEquipped(CharmType charm) {
        return equippedCharms.contains(charm);
    }

    public int getUsedNotches() {
        int sum = 0;
        for (CharmType c : equippedCharms) sum += c.getNotchCost();
        return sum;
    }

    public int getMaxNotches() { return maxNotches; }

    // --- CHARM EFFECT MODIFIERS ---
    public int getAttackDamage() {
        return isCharmEquipped(CharmType.UNBREAKABLE_STRENGTH) ? 2 : 1;
    }

    public float getAttackDuration() {
        return isCharmEquipped(CharmType.QUICK_SLASH) ? 0.15f : ATTACK_DURATION;
    }

    public float getFocusDuration() {
        return isCharmEquipped(CharmType.QUICK_FOCUS) ? 0.75f : FOCUS_DURATION;
    }

    public float getDashCooldown() {
        return isCharmEquipped(CharmType.DASHMASTER) ? 0.25f : DASH_COOLDOWN;
    }

    public int getSoulPerHit() {
        return isCharmEquipped(CharmType.SOUL_CATCHER) ? 17 : SOUL_PER_HIT;
    }

    public int getSpellDamage() {
        return isCharmEquipped(CharmType.VOID_HEART) ? 2 : 1;
    }

    public boolean hasSharpShadow() {
        return isCharmEquipped(CharmType.SHARP_SHADOW);
    }

    public boolean isDashing() { return isDashing; }
    public float getDashTimer() { return dashTimer; }

    public float getDashLengthMultiplier() {
        return isCharmEquipped(CharmType.SHARP_SHADOW) ? 1.2f : 1.0f;
    }

    public boolean trySharpShadowHit(Enemy enemy) {
        if (!hasSharpShadow() || !isDashing) return false;
        if (sharpShadowHitEnemies.contains(enemy)) return false;
        sharpShadowHitEnemies.add(enemy);
        return true;
    }

    // --- FOCUS ---
    public void startFocus() {
        if (isFocusing || !isOnGround() || isDashing || attackTimer > 0 || hp >= MAX_HP || isCasting)
            return;
        if (soul < SOUL_PER_HEAL) {
            pendingSoulToast = true;
            return;
        }
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
            GameMusic.FOCUS_HEAL.play();
        }
        isFocusing = false;
        focusTimer = 0;
    }

    public boolean isFocusing() { return isFocusing; }

    // --- SOUL ---
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

    // --- HP / DAMAGE ---
    public void takeDamage() { takeDamage(1); }

    public void takeDamage(int amount) {
        if (invincibleTimer > 0) return;
        if (isFocusing) cancelFocus();
        if (isCasting) cancelCast();
        hp -= amount;
        invincibleTimer = INVINCIBLE_DURATION;
        justDamaged = true;
        velocity.x = isFacingRight() ? -200f : 200f;
        velocity.y = 100f;
        GameMusic.HERO_DAMAGE.play();
        if (hp <= 0) respawn();
    }

    public boolean consumeJustDamaged() {
        boolean v = justDamaged;
        justDamaged = false;
        return v;
    }

    public void respawn() {
        position.set(spawnX, spawnY);
        velocity.set(0, 0);
        hp = MAX_HP;
        setOnGround(false);
        jumpCount = 0;
        isDashing = false;
        dashTimer = 0;
        isOnWall = false;
        invincibleTimer = INVINCIBLE_DURATION;
        isFocusing = false;
        focusTimer = 0;
        isCasting = false;
        castTimer = 0;
        dashCooldownTimer = 0;
        sharpShadowHitEnemies.clear();
        justRespawned = true;
    }

    public void setSpawnPoint(float x, float y) {
        spawnX = x;
        spawnY = y;
    }

    public boolean consumeJustRespawned() {
        boolean v = justRespawned;
        justRespawned = false;
        return v;
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
    public KnightState getCurrentState() { return currentState; }
}
