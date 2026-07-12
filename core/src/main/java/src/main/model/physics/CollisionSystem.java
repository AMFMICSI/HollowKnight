package src.main.model.physics;

import com.badlogic.gdx.math.Rectangle;
import src.main.model.entity.Entity;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.knight.Knight;
import src.main.model.enviroment.SolidBlock;
import src.main.model.enviroment.Spike;
import src.main.model.enviroment.ClimbableWall;
import src.main.model.enviroment.CrackedWall;

import java.util.List;

public class CollisionSystem {
    public static void resolve(Knight knight, List<SolidBlock> blocks, List<Spike> spikes,
                               List<ClimbableWall> climbableWalls, List<CrackedWall> crackedWalls, float delta) {
        boolean wasOnGround = knight.isOnGround();
        resolveEntity(knight, blocks, delta);
        if (!wasOnGround && knight.isOnGround())
            knight.resetJump();

        // --- Cracked Wall (only when intact) ---
        for (CrackedWall wall : crackedWalls) {
            if (!wall.isIntact()) continue;
            knight.getBoundingBox().setPosition(knight.getPosition().x, knight.getPosition().y);
            if (knight.getBoundingBox().overlaps(wall.getBounds()))
                resolveCollisionX(knight, wall.getBounds());
            knight.getBoundingBox().setPosition(knight.getPosition().x, knight.getPosition().y);
            boolean wasOnGroundBefore = knight.isOnGround();
            knight.setOnGround(false);
            if (knight.getBoundingBox().overlaps(wall.getBounds()))
                resolveCollisionY(knight, wall.getBounds());
            else
                knight.setOnGround(wasOnGroundBefore);
        }

        // --- Spike ---
        for (Spike spike : spikes) {
            if (knight.getBoundingBox().overlaps(spike.getBounds())) {
                knight.takeDamage();
                knight.getPosition().x += knight.getVelocityX() * 0.3f;
                knight.getPosition().y += knight.getVelocityY() * 0.3f;
                knight.teleportToLastSafePosition();
                break;
            }
        }

        // --- Wall Climb ---
        boolean onWall = false;
        boolean wallLeft = false;
        Rectangle b = knight.getBoundingBox();
        float tol = 2f;

        for (ClimbableWall w : climbableWalls) {
            Rectangle wb = w.getBounds();
            boolean touchesLeft = Math.abs(b.x - (wb.x + wb.width)) <= tol
                && b.x + b.width > wb.x;
            boolean touchesRight = Math.abs((b.x + b.width) - wb.x) <= tol
                && b.x < wb.x + wb.width;
            boolean yOverlap = b.y < wb.y + wb.height && b.y + b.height > wb.y;

            if (yOverlap && (touchesLeft || touchesRight)) {
                onWall = true;
                wallLeft = touchesLeft;
                break;
            }
        }

        boolean pressingTowardWall = (wallLeft && knight.isMovingLeft())
            || (!wallLeft && knight.isMovingRight());
        knight.setOnWall(onWall && pressingTowardWall, wallLeft);
    }

    public static void resolve(Enemy enemy, List<SolidBlock> blocks, float delta) {
        resolveEntity(enemy, blocks, delta);
    }

    private static void resolveEntity(Entity entity, List<SolidBlock> blocks, float delta) {
        entity.getPosition().x += entity.getVelocityX() * delta;
        entity.getBoundingBox().setPosition(entity.getPosition().x, entity.getPosition().y);
        for (SolidBlock block : blocks) {
            if (entity.getBoundingBox().overlaps(block.getBounds()))
                resolveCollisionX(entity, block.getBounds());
        }
        entity.getPosition().y += entity.getVelocityY() * delta;
        entity.getBoundingBox().setPosition(entity.getPosition().x, entity.getPosition().y);
        entity.setOnGround(false);
        for (SolidBlock block : blocks) {
            if (entity.getBoundingBox().overlaps(block.getBounds()))
                resolveCollisionY(entity, block.getBounds());
        }
    }

    private static void resolveCollisionX(Entity entity, Rectangle bounds) {
        if (entity.getVelocityX() > 0)
            entity.getPosition().x = bounds.x - entity.getBoundingBox().width;
        else if (entity.getVelocityX() < 0)
            entity.getPosition().x = bounds.x + bounds.width;
        entity.setVelocityX(0);
        entity.getBoundingBox().x = entity.getPosition().x;
    }

    private static void resolveCollisionY(Entity entity, Rectangle bounds) {
        if (entity.getVelocityY() > 0) {
            entity.getPosition().y = bounds.y - entity.getBoundingBox().height;
            entity.setVelocityY(0);
        } else if (entity.getVelocityY() < 0) {
            entity.getPosition().y = bounds.y + bounds.height;
            entity.setVelocityY(0);
            entity.setOnGround(true);
            if (entity instanceof Knight k) k.updateLastSafePosition();
        }
        entity.getBoundingBox().setPosition(entity.getPosition().x, entity.getPosition().y);
    }
}
