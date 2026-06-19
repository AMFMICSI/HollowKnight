package src.main.model.entity.behavior;

import src.main.model.entity.Entity;

public interface MovementBehavior {
    void update(Entity entity, float delta);
}
