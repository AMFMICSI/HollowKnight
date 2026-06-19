package src.main.model.entity.behavior;

import src.main.model.entity.Entity;

public class PlayerMovement implements MovementBehavior{
    private float moveSpeed;

    public  PlayerMovement(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void update(Entity entity, float delta) {
        if (entity.movingLeft && !entity.movingRight)
            entity.setVelocityX(-moveSpeed);
        else if (entity.movingRight && !entity.movingLeft)
            entity.setVelocityX(moveSpeed);
        else
            entity.setVelocityX(0);
    }
}
