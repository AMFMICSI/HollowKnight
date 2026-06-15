package src.main.model.entity.knight;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.animation.AnimationSet;
import src.main.model.entity.Entity;
import src.main.view.GameAssetManager;

public class Knight extends Entity {
    private static final float GRAVITY = 1000f;
    private static final float MOVE_SPEED = 200f;
    private static final float JUMP_VELOCITY = 400f;
    private static final float DASH_SPEED = 500f;
    private static final float DASH_DURATION = 0.2f;
    private static final float ATTACK_DURATION = 0.3f;

    private final AnimationSet animationSet;
    private KnightState currentState = KnightState.IDLE;
    private final Rectangle boundingBox =  new Rectangle();

    private boolean jumpKeyHeld = false;
    private float dashTimer = 0;
    private boolean isDashing = false;
    private int jumpCount = 0;    // 0 = on ground, 1 = one jump used, 2 = both used
    private float attackTimer = 0;

    public Knight(float x, float y) {
        animationSet = new AnimationSet(GameAssetManager.knightAnimations);
        position.set(x, y);
        boundingBox.setSize(50,70);
    }
    @Override
    public void update(float delta) {
        //Timers:
        if (isDashing) {
            dashTimer -= delta;
            if (dashTimer <= 0) isDashing = false;
        }
        if (attackTimer > 0) {
            attackTimer -= delta;
            if (attackTimer <= 0) attackTimer = 0;
        }

        //  Variable jump height
        if (!jumpKeyHeld && velocity.y > 0)
            velocity.y *= 0.85f;

        // velocity.y
        if(!isDashing) {
            if (movingLeft && !movingRight) {
                velocity.x = -MOVE_SPEED;
            } else if (movingRight && !movingLeft) {
                velocity.x = MOVE_SPEED;
            } else {
                velocity.x = 0;
            }
        }

        // gravity:
        velocity.y -= GRAVITY * delta;

        // animation and boundingBox:
        updateAnimationState();
        animationSet.getFrame(delta);
        boundingBox.setPosition(position.x, position.y);
    }

    public void updateAnimationState(){
        // current state update
        if (isDashing) {
            currentState = KnightState.DASHING;
        } else if (attackTimer > 0) {
            currentState = KnightState.ATTACKING;
        } else if (!isOnGround) {
            if (jumpCount == 2)      currentState = KnightState.DOUBLE_JUMPING;
            else if (velocity.y > 0) currentState = KnightState.JUMPING;
            else                     currentState = KnightState.FALLING;
        } else if (movingLeft || movingRight) {
            currentState = KnightState.RUNNING;
        } else {
            currentState = KnightState.IDLE;
        }

        KnightAnimationType animType;
        switch (currentState) {
            case RUNNING:   animType = KnightAnimationType.RUN;       break;
            case JUMPING:   animType = KnightAnimationType.AIRBORNE;  break;
            case DOUBLE_JUMPING: animType = KnightAnimationType.DOUBLE_JUMP; break;
            case FALLING:   animType = KnightAnimationType.FALL;      break;
            case ATTACKING: animType = KnightAnimationType.SLASH;     break;
            case DASHING:   animType = KnightAnimationType.DASH;      break;
            default:        animType = KnightAnimationType.IDLE;
        }
        animationSet.setAnimation(animType);
    }

    public void jump(){
        if (isOnGround) {
            velocity.y = JUMP_VELOCITY;
            isOnGround = false;
            jumpCount = 1;
            jumpKeyHeld = true;
        } else if (jumpCount < 2) {
            velocity.y = JUMP_VELOCITY * 0.9f;
            jumpCount = 2;
            jumpKeyHeld = true;
        }
    }

    public void attack() {
        if (attackTimer <= 0 && !isDashing) {
            attackTimer = ATTACK_DURATION;
            velocity.x = 0;
        }
    }

    public void jumpReleased(){
        jumpKeyHeld = false;
        if(velocity.y > 0){
            velocity.y *= 0.4f;
        }
    }

    public void dash(){
        if(!isDashing){
            isDashing = true;
            dashTimer = DASH_DURATION;
            velocity.x = facingRight ? DASH_SPEED : -DASH_SPEED;
            velocity.y = 0;
        }
    }

    public void pogo() {
        velocity.y = JUMP_VELOCITY * 0.7f;
    }

    public Vector2 getPosition() { return position; }
    @Override
    public TextureRegion getFrame(float delta) { return animationSet.getFrame(delta); }
    public Rectangle getBoundingBox() { return boundingBox; }
    public void setVelocityY(float vy) { velocity.y = vy; }
    public float getVelocityY() { return velocity.y; }
    public void setVelocityX(float vx) { velocity.x = vx; }
    public float getVelocityX(){return velocity.x;}
    public void resetJump() { jumpCount = 0; }
}
