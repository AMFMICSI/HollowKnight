package src.main.model.physics;

import src.main.model.entity.Entity;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.knight.Knight;
import src.main.model.enviroment.SolidBlock;
import java.util.List;

public class CollisionSystem {
    public static void resolve(Knight knight, List<SolidBlock> blocks, float delta) {
        boolean wasOnGround = knight.isOnGround();
        resolveEntity(knight, blocks, delta);
        if (!wasOnGround && knight.isOnGround())
            knight.resetJump();
    }

    public static void resolve(Enemy enemy, List<SolidBlock> blocks, float delta) {
        resolveEntity(enemy, blocks, delta);
    }

    private static void resolveEntity(Entity entity, List<SolidBlock> blocks, float delta) {
        entity.getPosition().x += entity.getVelocityX() * delta;
        entity.getBoundingBox().setPosition(entity.getPosition().x, entity.getPosition().y);
        for (SolidBlock block : blocks) {
            if (entity.getBoundingBox().overlaps(block.getBounds())) {
                if (entity.getVelocityX() > 0)
                    entity.getPosition().x = block.getBounds().x - entity.getBoundingBox().width;
                else if (entity.getVelocityX() < 0)
                    entity.getPosition().x = block.getBounds().x + block.getBounds().width;
                entity.setVelocityX(0);
                entity.getBoundingBox().x = entity.getPosition().x;
            }
        }
        entity.getPosition().y += entity.getVelocityY() * delta;
        entity.getBoundingBox().setPosition(entity.getPosition().x, entity.getPosition().y);
        entity.setOnGround(false);
        for (SolidBlock block : blocks) {
            if (entity.getBoundingBox().overlaps(block.getBounds())) {
                if (entity.getVelocityY() > 0) {
                    entity.getPosition().y = block.getBounds().y - entity.getBoundingBox().height;
                    entity.setVelocityY(0);
                } else if (entity.getVelocityY() < 0) {
                    entity.getPosition().y = block.getBounds().y + block.getBounds().height;
                    entity.setVelocityY(0);
                    entity.setOnGround(true);
                }
                entity.getBoundingBox().setPosition(entity.getPosition().x, entity.getPosition().y);
            }
        }
    }
}
