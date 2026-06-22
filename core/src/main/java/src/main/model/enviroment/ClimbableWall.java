package src.main.model.enviroment;
import com.badlogic.gdx.math.Rectangle;

public class ClimbableWall {
    private Rectangle bounds;
    public ClimbableWall(float x, float y, float w, float h) {
        this.bounds = new Rectangle(x, y, w, h);
    }
    public Rectangle getBounds() { return bounds; }
}
