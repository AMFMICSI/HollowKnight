package src.main.model.physics;

import src.main.model.entity.Entity;

public class PhysicsSystem {
    public static final float GRAVITY = 1000f;

    public static void applyGravity(Entity entity, float delta) {
        entity.setVelocityY(entity.getVelocityY() - GRAVITY * delta);
    }
}
