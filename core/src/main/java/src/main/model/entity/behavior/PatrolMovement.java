package src.main.model.entity.behavior;

import src.main.model.entity.Entity;

public class PatrolMovement implements MovementBehavior{
    private float speed;
    private float duration;
    private float timer;

    public PatrolMovement(float speed, float duration){
        this.speed = speed;
        this.duration = duration;
        this.timer = duration;
    }

    @Override
    public void update(Entity entity, float delta) {
        timer -= delta;
        entity.setVelocityX(entity.isFacingRight() ? speed : -speed);
    }

    public boolean isFinished() {
        return timer <= 0;
    }
    public void reset(){timer = duration;}
}
