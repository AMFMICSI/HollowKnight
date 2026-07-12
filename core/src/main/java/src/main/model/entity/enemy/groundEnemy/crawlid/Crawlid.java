package src.main.model.entity.enemy.groundEnemy.crawlid;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimStateTracker;
import src.main.model.entity.enemy.groundEnemy.GroundEnemy;

public class Crawlid extends GroundEnemy {
    private static final float WALK_SPEED = 30f;
    private static final int MAX_HP = 3;

    private final AnimStateTracker<CrawlidAnimationType> animState =
        new AnimStateTracker<>(CrawlidAnimationType.WALK);

    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    public Crawlid(float x, float y, Rectangle zone, KnightRef knightRef) {
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        boundingBox.setSize(20, 16);
        walkSpeed = WALK_SPEED;
        this.zone = (zone != null) ? zone : new Rectangle(x - 40, y - 20, 80, 40);
        setFacingRight(true);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        animState.advanceTime(delta);
    }

    public CrawlidAnimationType getAnimType() {
        if (isDead) return CrawlidAnimationType.DEATH_LAND;
        else if (state == GroundState.TURN) return CrawlidAnimationType.TURN;
        else return CrawlidAnimationType.WALK;
    }
    public float getStateTime() { return animState.getStateTime(); }
}
