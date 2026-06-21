package src.main.model.entity.behavior;

import src.main.model.entity.Entity;

public class ChargeMovement implements   MovementBehavior {
    private float speed;

    public  ChargeMovement(float speed) {
        this.speed = speed;
    }

    @Override
    public void update(Entity entity, float delta) {
        entity.setVelocityX(entity.isFacingRight() ? speed : -speed);
    }

    public void reset() {}
}
