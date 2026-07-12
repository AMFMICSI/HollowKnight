package src.main.view.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.enemy.boss.falseKnight.FalseKnight;
import src.main.model.entity.enemy.boss.falseKnight.FalseKnightAnimationType;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardian;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardianAnimationType;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunter;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunterAnimationType;
import src.main.model.entity.enemy.groundEnemy.crawlid.Crawlid;
import src.main.model.entity.enemy.groundEnemy.crawlid.CrawlidAnimationType;
import src.main.model.entity.enemy.groundEnemy.huskHornhead.HuskHornhead;
import src.main.model.entity.enemy.groundEnemy.huskHornhead.HuskHornheadAnimationType;
import src.main.model.entity.animation.AnimationSet;
import src.main.view.manager.GameAssetManager;

public class EnemyRenderer {
    private final AnimationSet<CrawlidAnimationType> crawlidAnimSet;
    private final AnimationSet<HuskHornheadAnimationType> huskAnimSet;
    private final AnimationSet<CrystalHunterAnimationType> hunterAnimSet;
    private final AnimationSet<CrystalGuardianAnimationType> guardianAnimSet;
    private final AnimationSet<FalseKnightAnimationType> fkAnimSet;

    public EnemyRenderer() {
        crawlidAnimSet = new AnimationSet<>(GameAssetManager.crawlidAnimations, CrawlidAnimationType.WALK);
        huskAnimSet = new AnimationSet<>(GameAssetManager.huskHornheadAnimations, HuskHornheadAnimationType.WALK);
        hunterAnimSet = new AnimationSet<>(GameAssetManager.crystalHunterAnimations, CrystalHunterAnimationType.FLY);
        guardianAnimSet = new AnimationSet<>(GameAssetManager.crystalGuardianAnimations, CrystalGuardianAnimationType.IDLE);
        fkAnimSet = new AnimationSet<>(GameAssetManager.falseKnightAnimations, FalseKnightAnimationType.IDLE);
    }

    public void render(SpriteBatch batch, Enemy enemy, float delta) {
        if (enemy.isDeadAnimationDone() && !(enemy instanceof FalseKnight)) return;

        TextureRegion frame = null;

        if (enemy instanceof Crawlid c) {
            if (c.isDead() && c.isDeadAnimationDone()) return;
            frame = getCrawlidFrame(c);
        } else if (enemy instanceof HuskHornhead h) {
            if (h.isDead() && h.isDeadAnimationDone()) return;
            frame = getHuskFrame(h);
        } else if (enemy instanceof CrystalHunter h) {
            if (h.isDead() && h.isDeadAnimationDone()) return;
            frame = getHunterFrame(h);
        } else if (enemy instanceof CrystalGuardian g) {
            if (g.isDead() && g.isDeadAnimationDone()) return;
            frame = getGuardianFrame(g);
        } else if (enemy instanceof FalseKnight fk) {
            frame = getFalseKnightFrame(fk);
        }

        if (frame == null) return;

        float spriteW = enemy.getBoundingBox().width * enemy.getDrawScale();
        float spriteH = spriteW * frame.getRegionHeight() / (float) frame.getRegionWidth();
        batch.draw(frame,
            enemy.getBoundingBox().x + (enemy.getBoundingBox().width - spriteW) / 2f,
            enemy.getBoundingBox().y,
            spriteW / 2f, 0,
            spriteW, spriteH,
            enemy.isFacingRight() ? -1 : 1, 1, 0);
    }

    private TextureRegion getCrawlidFrame(Crawlid c) {
        crawlidAnimSet.setAnimation(c.getAnimType());
        crawlidAnimSet.syncStateTime(c.getStateTime());
        return crawlidAnimSet.getCurrentFrame();
    }

    private TextureRegion getHuskFrame(HuskHornhead h) {
        huskAnimSet.setAnimation(h.getAnimType());
        huskAnimSet.syncStateTime(h.getStateTime());
        return huskAnimSet.getCurrentFrame();
    }

    private TextureRegion getHunterFrame(CrystalHunter h) {
        hunterAnimSet.setAnimation(h.getAnimType());
        hunterAnimSet.syncStateTime(h.getStateTime());
        return hunterAnimSet.getCurrentFrame();
    }

    private TextureRegion getGuardianFrame(CrystalGuardian g) {
        guardianAnimSet.setAnimation(g.getAnimType());
        guardianAnimSet.syncStateTime(g.getStateTime());
        return guardianAnimSet.getCurrentFrame();
    }

    private TextureRegion getFalseKnightFrame(FalseKnight fk) {
        fkAnimSet.setAnimation(fk.getAnimType());
        fkAnimSet.syncStateTime(fk.getStateTime());
        return fkAnimSet.getCurrentFrame();
    }
}
