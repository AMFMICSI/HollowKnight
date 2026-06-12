package src.main.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.view.GameAssetManager;

public class Knight {
    private Vector2  position = new Vector2();
    private Vector2 velocity =  new Vector2();
    private static final float GRAVITY = 1000f;
        private static final float MOVE_SPEED = 200f;
    private static final float JUMP_VELOCITY = 400f;

    private boolean isOnGround = false;
    public boolean movingLeft =  false;
    public boolean movingRight = false;

    private AnimationSet animationSet;
    private KnightState currentState = KnightState.IDLE;
    private Rectangle boundingBox;

    public Knight(){
        animationSet = new AnimationSet(GameAssetManager.knightAnimations);
    }

    public void update(float delta) {

        isOnGround = position.y <= 0.001f;

        if (!isOnGround) {
            velocity.y -= GRAVITY * delta;
        } else if (velocity.y < 0.01f) {
            velocity.y = 0;
            position.y = 0;
        }

        if (movingLeft) velocity.x =-MOVE_SPEED;
        else if (movingRight) velocity.x = MOVE_SPEED;
        else velocity.x = 0;

        position.add(velocity.x * delta, velocity.y * delta);
        updateAnimationState();
        animationSet.getFrame(delta);
    }

    private void updateAnimationState(){
        KnightAnimationType animType;
        if(currentState == KnightState.IDLE) animType = KnightAnimationType.IDLE;
        else if(currentState == KnightState.RUNNING) animType = KnightAnimationType.RUN;
        else if (currentState == KnightState.JUMPING || currentState == KnightState.FALLING)
            animType = KnightAnimationType.AIRBORNE;
        else if (currentState == KnightState.ATTACKING) animType = KnightAnimationType.SLASH;
        else if (currentState == KnightState.DASHING) animType = KnightAnimationType.DASH;
        else if (currentState == KnightState.DOUBLE_JUMPING) animType = KnightAnimationType.DOUBLE_JUMP;
        else animType = KnightAnimationType.IDLE;

        animationSet.setAnimation(animType);
    }

    public void moveLeft(){
        movingLeft = true;
        movingRight = false;
        currentState = KnightState.RUNNING;
    }
    public void moveRight(){
        movingRight = true;
        movingLeft = false;
        currentState = KnightState.RUNNING;
    }
    public void stop(){
        movingLeft = false;
        movingRight = false;
        currentState = KnightState.IDLE;
    }
    public void jump(){
        if (isOnGround) {
            velocity.y = JUMP_VELOCITY;
            isOnGround = false;
            currentState = KnightState.JUMPING;
        }
    }

    public Vector2 getPosition() { return position; }
    public TextureRegion getFrame(float delta) { return animationSet.getFrame(0); }
    public Rectangle getBoundingBox() { return boundingBox; }
}
