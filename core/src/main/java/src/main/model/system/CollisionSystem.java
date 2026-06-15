package src.main.model.system;

import src.main.model.enviroment.SolidBlock;
import src.main.model.entity.knight.Knight;
import java.util.List;

public class CollisionSystem {
    public static void resolve(Knight knight, List<SolidBlock> blocks, float delta) {
        knight.getPosition().x += knight.getVelocityX() * delta;
        knight.getBoundingBox().setPosition(knight.getPosition().x, knight.getPosition().y);

        for (SolidBlock block : blocks) {
            if (knight.getBoundingBox().overlaps(block.bounds)) {
                if (knight.getVelocityX() > 0)
                    knight.getPosition().x = block.bounds.x - knight.getBoundingBox().width;
                else if (knight.getVelocityX() < 0)
                    knight.getPosition().x = block.bounds.x + block.bounds.width;
                knight.setVelocityX(0);
                knight.getBoundingBox().x = knight.getPosition().x;
            }
        }

        knight.getPosition().y += knight.getVelocityY() * delta;
        knight.getBoundingBox().setPosition(knight.getPosition().x, knight.getPosition().y);

        knight.isOnGround = false;

        for (SolidBlock block : blocks) {
            if (knight.getBoundingBox().overlaps(block.bounds)) {
                if (knight.getVelocityY() > 0) {
                    knight.getPosition().y = block.bounds.y - knight.getBoundingBox().height;
                    knight.setVelocityY(0);
                } else if (knight.getVelocityY() < 0) {
                    knight.getPosition().y = block.bounds.y + block.bounds.height;
                    knight.setVelocityY(0);
                    knight.isOnGround = true;
                    knight.resetJump();
                }
                knight.getBoundingBox().setPosition(knight.getPosition().x, knight.getPosition().y);
            }
        }
    }
}
