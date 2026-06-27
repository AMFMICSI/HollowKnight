package src.main.model.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected float DRAW_SCALE = 5f;
    protected Vector2 position = new Vector2();
    protected Vector2 velocity = new Vector2();
    private boolean isOnGround = false;
    private boolean movingLeft = false, movingRight = false;
    private boolean facingRight = false;
    protected Rectangle boundingBox = new Rectangle();

    public abstract void update(float delta);
    public abstract TextureRegion getFrame(float delta);
    public abstract void draw(SpriteBatch batch, float delta);

    public void moveLeft(float speed) { velocity.x = -speed; setFacingRight(false); }
    public void moveRight(float speed) { velocity.x = speed; setFacingRight(true); }
    public void stopX() { velocity.x = 0; }
    public void applyGravity(float gravity, float delta) { velocity.y -= gravity * delta; }
    public Rectangle getBoundingBox() { return boundingBox; }
    public Vector2 getPosition() { return position; }

    public float getVelocityX() { return velocity.x; }
    public float getVelocityY() { return velocity.y; }
    public void setVelocityX(float vx) { velocity.x = vx; }
    public void setVelocityY(float vy) { velocity.y = vy; }
    public boolean isOnGround() { return isOnGround; }
    public void setOnGround(boolean b) { isOnGround = b; }
    public boolean isMovingLeft() { return movingLeft; }
    public void setMovingLeft(boolean v) { movingLeft = v; }
    public boolean isMovingRight() { return movingRight; }
    public void setMovingRight(boolean v) { movingRight = v; }
    public boolean isFacingRight() { return facingRight; }
    public void setFacingRight(boolean v) { facingRight = v; }
}
