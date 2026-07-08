package src.main.model.entity.enemy.groundEnemy.crawlid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.enemy.groundEnemy.GroundEnemy;
import src.main.view.manager.GameAssetManager;

public class Crawlid extends GroundEnemy {
    private static final float WALK_SPEED = 30f;
    private static final int MAX_HP = 3;

    private final AnimationSet<CrawlidAnimationType> animSet;

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
        animSet = new AnimationSet<>(GameAssetManager.crawlidAnimations, CrawlidAnimationType.WALK);
        setFacingRight(true);
    }

    @Override
    public TextureRegion getFrame(float delta) {
        CrawlidAnimationType type;
        if (isDead) type = diedInAir ? CrawlidAnimationType.DEATH_AIR : CrawlidAnimationType.DEATH_LAND;
        else if (state == GroundState.TURN) type = CrawlidAnimationType.TURN;
        else type = CrawlidAnimationType.WALK;
        animSet.setAnimation(type);
        return animSet.getFrame(delta);
    }

    @Override
    public TextureRegion getCorpseFrame() {
        return GameAssetManager.crawlidAnimations.get(CrawlidAnimationType.DEATH_LAND).getKeyFrame(0);
    }
}
