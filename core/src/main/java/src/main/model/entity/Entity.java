package src.main.model.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position = new Vector2();
    protected Vector2 velocity =  new Vector2();
    public boolean isOnGround = false;
    public boolean movingLeft = false, movingRight = false;
    public boolean facingRight = false;
    protected Rectangle boundingBox = new Rectangle();

    public abstract void update(float delta);
    public abstract TextureRegion getFrame(float delta);

    public void moveLeft(float speed) { velocity.x = -speed; facingRight = false; }
    public void moveRight(float speed) { velocity.x = speed; facingRight = true; }
    public void stopX() { velocity.x = 0; }
    public void applyGravity(float gravity, float delta) { velocity.y -= gravity * delta; }
    public Rectangle getBoundingBox() { return boundingBox; }
    public Vector2 getPosition() { return position; }

    public float getVelocityX() { return velocity.x; }
    public float getVelocityY() { return velocity.y; }
    public void setVelocityX(float vx) { velocity.x = vx; }
    public void setVelocityY(float vy) { velocity.y = vy; }
    public void setOnGround(boolean b) { isOnGround = b; }
}
