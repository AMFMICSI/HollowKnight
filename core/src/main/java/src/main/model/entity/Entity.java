package src.main.model.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position = new Vector2();
    protected Vector2 velocity =  new Vector2();
    public boolean isOnGround = false;
    public boolean movingLeft = false, movingRight = false;
    public boolean facingRight = false;
    public abstract void update(float delta);
    public abstract TextureRegion getFrame(float delta);
}
