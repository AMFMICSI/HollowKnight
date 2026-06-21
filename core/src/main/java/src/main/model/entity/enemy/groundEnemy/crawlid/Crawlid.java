package src.main.model.entity.enemy.groundEnemy.crawlid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.behavior.PatrolMovement;
import src.main.model.entity.enemy.Enemy;
import src.main.model.physics.PhysicsSystem;
import src.main.view.GameAssetManager;

public class Crawlid extends Enemy {
    private static final float WALK_SPEED = 30f;
    private static final int MAX_HP = 3;
    private static final float DEATH_DURATION = 1.0f;

    private CrawlidState currentState = CrawlidState.PATROL;
    private final AnimationSet<CrawlidAnimationType> animSet;
    private PatrolMovement patrol;

    private float zoneMinX, zoneMaxX;
    private float turnTimer;
    private boolean diedInAir;

    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    public Crawlid(float x, float y, Rectangle zone, KnightRef knightRef) {
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        boundingBox.setSize(20, 16);
        this.zone = (zone != null) ? zone : new Rectangle(x - 40, y - 20, 80, 40);
        zoneMinX = this.zone.x;
        zoneMaxX = this.zone.x + this.zone.width - boundingBox.width;
        animSet = new AnimationSet<>(GameAssetManager.crawlidAnimations, CrawlidAnimationType.WALK);
        patrol = new PatrolMovement(WALK_SPEED, 2f);
        setFacingRight(true);
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) deadAnimationDone = true;
            return;
        }
        if (!isOnGround())
            velocity.y -= PhysicsSystem.GRAVITY * delta;

        if (currentState == CrawlidState.PATROL) {
            patrol.update(this, delta);
            if (position.x <= zoneMinX) { position.x = zoneMinX; turnAround(); }
            if (position.x >= zoneMaxX) { position.x = zoneMaxX; turnAround(); }
        } else if (currentState == CrawlidState.TURNING) {
            turnTimer -= delta;
            if (turnTimer <= 0) {
                currentState = CrawlidState.PATROL;
                patrol.reset();
            }
        }
        boundingBox.setPosition(position);
    }

    public CrawlidState getCurrentState() { return currentState; }

    public void turnAround() {
        setFacingRight(!isFacingRight());
        velocity.x = isFacingRight() ? WALK_SPEED : -WALK_SPEED;
        currentState = CrawlidState.TURNING;  // → state جدید
        turnTimer = 0.2f;                      // مدت انیمیشن
    }

    @Override
    public void takeDamage(int amount) {
        if (!isDead) diedInAir = !isOnGround();
        super.takeDamage(amount);
    }

    private CrawlidAnimationType getCurrentAnimType() {
        if (isDead) return diedInAir ? CrawlidAnimationType.DEATH_AIR : CrawlidAnimationType.DEATH_LAND;
        if (currentState == CrawlidState.TURNING) return CrawlidAnimationType.TURN;
        return CrawlidAnimationType.WALK;
    }

    @Override
    public TextureRegion getFrame(float delta) {
        animSet.setAnimation(getCurrentAnimType());
        return animSet.getFrame(delta);
    }

    @Override
    public TextureRegion getCorpseFrame() {
        return GameAssetManager.crawlidAnimations.get(CrawlidAnimationType.DEATH_LAND).getKeyFrame(0);
    }
}
