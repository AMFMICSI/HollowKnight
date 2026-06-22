package src.main.model.enviroment;

import com.badlogic.gdx.math.Rectangle;
import src.main.model.entity.knight.Knight;

public class Spike {
    private Rectangle bounds;

    public Spike(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() { return bounds; }

    public void pushOut(Knight knight, float vx, float vy) {
        Rectangle b = knight.getBoundingBox();
        if (Math.abs(vx) > Math.abs(vy)) {
            if (vx > 0) knight.getPosition().x = bounds.x - b.width;
            else        knight.getPosition().x = bounds.x + bounds.width;
        } else {
            if (vy > 0) knight.getPosition().y = bounds.y - b.height;
            else        knight.getPosition().y = bounds.y + bounds.height;
        }
        b.setPosition(knight.getPosition().x, knight.getPosition().y);
    }
}
