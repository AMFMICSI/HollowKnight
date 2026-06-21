package src.main.model.enviroment;

import com.badlogic.gdx.math.Rectangle;

public class SolidBlock {
    private Rectangle bounds;
    private boolean isDeadly;

    public SolidBlock(float x, float y, float width, float height, boolean isDeadly) {
        this.bounds = new Rectangle(x, y, width, height);
        this.isDeadly = isDeadly;
    }

    public Rectangle getBounds() { return bounds; }
    public boolean isDeadly() { return isDeadly; }
}
