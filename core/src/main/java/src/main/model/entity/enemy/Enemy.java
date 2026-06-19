package src.main.model.entity.enemy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.Entity;

import com.badlogic.gdx.math.Rectangle;


public abstract class Enemy extends Entity {
    protected int hp, maxHp;
    protected boolean isDead = false, deadAnimationDone = false;
    protected float deathTimer, animTime = 0;
    protected Rectangle zone = new Rectangle();

    public abstract TextureRegion getFrame(float delta);

    public void takeDamage(int amount) {
        if (isDead) return;
        hp -= amount;
        if (hp <= 0) {
            isDead = true;
            deathTimer = 1.0f;
            velocity.x = 0;
            velocity.y = 0;
        }
    }

    public boolean isDead() { return isDead; }
    public boolean isDeadAnimationDone() { return deadAnimationDone; }
    public int getHp(){return hp;}
}
