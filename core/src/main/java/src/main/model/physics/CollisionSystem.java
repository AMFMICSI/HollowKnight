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
            // Resolve X
            knight.getBoundingBox().setPosition(knight.getPosition().x, knight.getPosition().y);
            if (knight.getBoundingBox().overlaps(wall.getBounds())) {
                if (knight.getVelocityX() > 0)
                    knight.getPosition().x = wall.getBounds().x - knight.getBoundingBox().width;
                else if (knight.getVelocityX() < 0)
                    knight.getPosition().x = wall.getBounds().x + wall.getBounds().width;
                knight.setVelocityX(0);
                knight.getBoundingBox().x = knight.getPosition().x;
            }
            // Resolve Y
            knight.getBoundingBox().setPosition(knight.getPosition().x, knight.getPosition().y);
            boolean wasOnGroundBefore = knight.isOnGround();
            knight.setOnGround(false);
            if (knight.getBoundingBox().overlaps(wall.getBounds())) {
                if (knight.getVelocityY() > 0) {
                    knight.getPosition().y = wall.getBounds().y - knight.getBoundingBox().height;
                    knight.setVelocityY(0);
                } else if (knight.getVelocityY() < 0) {
                    knight.getPosition().y = wall.getBounds().y + wall.getBounds().height;
                    knight.setVelocityY(0);
                    knight.setOnGround(true);
                }
                knight.getBoundingBox().setPosition(knight.getPosition().x, knight.getPosition().y);
            } else {
                knight.setOnGround(wasOnGroundBefore);
            }
        }

        // --- Spike ---
        for (Spike spike : spikes) {
            if (knight.getBoundingBox().overlaps(spike.getBounds())) {
                float vx = knight.getVelocityX();
                float vy = knight.getVelocityY();
                knight.takeDamage();
                spike.pushOut(knight, vx, vy);
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
