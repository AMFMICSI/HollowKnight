package src.main.model.enviroment;

import com.badlogic.gdx.math.Rectangle;

public class Platform {
    public Rectangle rect;
    public Platform(float x, float y, float width, float height){
        this.rect = new Rectangle(x,y, width, height);
    }
}
