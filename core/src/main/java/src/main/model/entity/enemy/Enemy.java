package src.main.model.entity.enemy;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.Entity;
import src.main.model.enviroment.SolidBlock;

import java.util.List;

public abstract class Enemy extends Entity {
    protected int hp, maxHp;
    protected boolean isDead = false, deadAnimationDone = false;
    protected float deathTimer;
    protected Rectangle zone;
    protected Vector2 spawnPosition = new Vector2();
    public float respawnDistance = 900f;
    protected List<SolidBlock> solidBlocks;

    public void takeDamage(int amount) {
        if (isDead) return;
        hp -= amount;
        if (hp <= 0) {
            isDead = true;
            deathTimer = 1.0f;
            velocity.x = 0;
            velocity.y = 0;
        } else {
            applyKnockback(150f, 80f);
        }
    }

    public void applyKnockback(float forceX, float forceY) {
        velocity.x = isFacingRight() ? -forceX : forceX;
        velocity.y = forceY;
    }

    public boolean canRespawn(float playerDist, float threshold) {
        return isDead && deadAnimationDone && playerDist > threshold;
    }

    public void respawn() {
        isDead = false;
        deadAnimationDone = false;
        hp = maxHp;
        deathTimer = 0;
        position.set(spawnPosition);
        boundingBox.setPosition(spawnPosition);
        velocity.set(0, 0);
    }

    public boolean isDead() { return isDead; }
    public boolean isDeadAnimationDone() { return deadAnimationDone; }
    public int getHp(){return hp;}
    public void setSolidBlocks(List<SolidBlock> solidBlocks) { this.solidBlocks = solidBlocks; }
    public Rectangle getZone() { return zone; }
    public void setZone(Rectangle zone) { this.zone = zone; }

    public float getRespawnDistance() {
        return respawnDistance;
    }
}
