package src.main.model.entity.knight;

import src.main.view.manager.GameMusic;

public class HealthSystem {
    private final Knight knight;

    private int hp;
    private float invincibleTimer = 0;
    private boolean justDamaged = false;
    private boolean justRespawned = false;
    private float deathTimer = 0;
    private boolean isDead = false;

    public HealthSystem(Knight knight, int maxHp) {
        this.knight = knight;
        this.hp = maxHp;
    }

    public void updateTimers(float delta) {
        if (invincibleTimer > 0) invincibleTimer -= delta;
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) {
                isDead = false;
                respawn();
            }
        }
    }

    public void takeDamage(int amount, boolean godMode, float invincibleDuration, float deathDuration, float knockbackX, float knockbackY) {
        if (isDead || godMode || invincibleTimer > 0) return;
        if (knight.isFocusing()) knight.cancelFocus();
        if (knight.isCasting()) knight.cancelCast(); // حالا از گتر عمومی استفاده می‌کند

        hp -= amount;
        invincibleTimer = invincibleDuration;
        justDamaged = true;

        if (hp <= 0) {
            isDead = true;
            deathTimer = deathDuration;
            knight.setVelocityX(0);
            knight.setVelocityY(0);
            return;
        }

        knight.setVelocityX(knight.isFacingRight() ? -knockbackX : knockbackX);
        knight.setVelocityY(knockbackY);
        GameMusic.HERO_DAMAGE.play();
    }

    public void respawn() {
        isDead = false;
        deathTimer = 0;
        knight.getPosition().set(knight.getSpawnX(), knight.getSpawnY());
        knight.setVelocityX(0);
        knight.setVelocityY(0);
        hp = knight.getMaxHp();
        knight.setOnGround(false);
        knight.resetJump();
        knight.stopDashing(); // استفاده از متد بازنویسی شده بدون نیاز به پارامترهای تکراری
        knight.setOnWall(false, false);
        invincibleTimer = 1.0f;
        knight.cancelFocus();
        knight.cancelCast();
        knight.resetDashCooldown();
        knight.clearSharpShadowHitEnemies();
        justRespawned = true;
    }

    public boolean consumeJustDamaged() {
        boolean v = justDamaged;
        justDamaged = false;
        return v;
    }

    public boolean consumeJustRespawned() {
        boolean v = justRespawned;
        justRespawned = false;
        return v;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public float getInvincibleTimer() {
        return invincibleTimer;
    }

    public boolean isDead() {
        return isDead;
    }
}
