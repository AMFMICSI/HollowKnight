package src.main.model.system;

import src.main.model.enviroment.Platform;
import src.main.model.knight.Knight;
import java.util.List;

public class CollisionSystem {
    public static void resolve(Knight knight, List<Platform> platforms, float delta) {
        // حرکت افقی
        knight.getPosition().x += knight.getVelocityX() * delta;
        knight.getBoundingBox().x = knight.getPosition().x;
        knight.getBoundingBox().y = knight.getPosition().y;

        for (Platform p : platforms) {
            if (knight.getBoundingBox().overlaps(p.rect)) {
                if (knight.getVelocityX() > 0)
                    knight.getPosition().x = p.rect.x - knight.getBoundingBox().width;
                else if (knight.getVelocityX() < 0)
                    knight.getPosition().x = p.rect.x + p.rect.width;
                knight.setVelocityX(0);
                knight.getBoundingBox().x = knight.getPosition().x;
            }
        }

        // حرکت عمودی
        knight.getPosition().y += knight.getVelocityY() * delta;
        knight.getBoundingBox().x = knight.getPosition().x;
        knight.getBoundingBox().y = knight.getPosition().y;

        knight.isOnGround = false;

        for (Platform p : platforms) {
            if (knight.getBoundingBox().overlaps(p.rect)) {
                if (knight.getVelocityY() > 0) {
                    knight.getPosition().y = p.rect.y - knight.getBoundingBox().height;
                    knight.setVelocityY(0);
                } else if (knight.getVelocityY() < 0) {
                    knight.getPosition().y = p.rect.y + p.rect.height;
                    knight.setVelocityY(0);
                    knight.isOnGround = true;
                    knight.resetJump();
                }
                knight.getBoundingBox().x = knight.getPosition().x;
                knight.getBoundingBox().y = knight.getPosition().y;
            }
        }
    }
}
