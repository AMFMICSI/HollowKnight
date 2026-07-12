package src.main.model.entity.enemy.groundEnemy;

import com.badlogic.gdx.math.Rectangle;
import src.main.model.entity.enemy.Enemy;
import src.main.model.enviroment.SolidBlock;
import src.main.model.physics.PhysicsSystem;

import java.util.List;

public abstract class GroundEnemy extends Enemy {
    protected enum GroundState { WALK, TURN }
    protected GroundState state = GroundState.WALK;

    protected float walkSpeed;
    protected float turnTimer;
    protected float turnDuration = 0.2f;
    protected boolean diedInAir;

    @Override
    public void update(float delta) {
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) deadAnimationDone = true;
            return;
        }
        if (!isOnGround()) velocity.y -= PhysicsSystem.GRAVITY * delta;

        switch (state) {
            case WALK:
                velocity.x = isFacingRight() ? walkSpeed : -walkSpeed;
                break;
            case TURN:
                turnTimer -= delta;
                if (turnTimer <= 0) state = GroundState.WALK;
                velocity.x = 0;
                break;
        }
        boundingBox.setPosition(position);

        if (zone != null && zone.width > 0) {
            float minX = zone.x;
            float maxX = zone.x + zone.width - boundingBox.width;
            if (position.x < minX) { position.x = minX; doTurn(); }
            if (position.x > maxX) { position.x = maxX; doTurn(); }
        }
    }

    public void onCollisionResolved(float prevVx, List<SolidBlock> blocks) {
        if (prevVx != 0 && Math.abs(getVelocityX()) < 0.01f) {
            doTurn();
        } else if (isCliffAhead(blocks)) {
            doTurn();
        }
    }

    protected boolean isCliffAhead(List<SolidBlock> blocks) {
        float checkX = isFacingRight()
            ? getBoundingBox().x + getBoundingBox().width + 1
            : getBoundingBox().x - 1;
        Rectangle probe = new Rectangle(checkX, getBoundingBox().y - 1,
            getBoundingBox().width, 1);
        for (SolidBlock b : blocks)
            if (probe.overlaps(b.getBounds())) return false;
        return true;
    }

    protected void doTurn() {
        if (state == GroundState.TURN) return;
        state = GroundState.TURN;
        turnTimer = turnDuration;
        setFacingRight(!isFacingRight());
    }

    public boolean isTurning() { return state == GroundState.TURN; }

    @Override
    public void respawn() {
        super.respawn();
        state = GroundState.WALK;
        turnTimer = 0;
        diedInAir = false;
    }

    @Override
    public void takeDamage(int amount) {
        if (!isDead) diedInAir = !isOnGround();
        super.takeDamage(amount);
    }
}
