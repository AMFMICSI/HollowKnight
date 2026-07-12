package src.main.view.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardian;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardianLaser;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunter;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalProjectile;
import src.main.view.manager.GameAssetManager;

public class ProjectileRenderer {
    private static final float DRAW_SCALE = 5f;

    public void renderCrystalProjectiles(SpriteBatch batch, CrystalHunter ch, float delta) {
        for (CrystalProjectile p : ch.getProjectiles()) {
            if (p.isDead()) continue;
            TextureRegion frame = GameAssetManager.crystalProjectileRegion;
            float pw = p.getBoundingBox().width * DRAW_SCALE;
            float ph = pw * frame.getRegionHeight() / (float) frame.getRegionWidth();
            batch.draw(frame, p.getPosition().x, p.getPosition().y,
                pw / 2f, 0, pw, ph, 1, 1, 0);
        }
    }

    public void renderLaser(SpriteBatch batch, CrystalGuardian cg, float delta,
                             TextureRegion region, Animation<TextureRegion> impactAnim) {
        CrystalGuardianLaser laser = cg.getLaser();
        if (!laser.isActive()) return;

        float laserLen = laser.getLength();
        float laserWidth = laser.getWidth();
        float startX = laser.getStartX();
        float startY = laser.getStartY();
        TextureRegion impactFrame = impactAnim.getKeyFrame(laser.getAnimTime());

        if (cg.isFacingRight())
            batch.draw(region, startX, startY, 0, laserWidth / 2f, laserLen, laserWidth, 1, 1, 0);
        else
            batch.draw(region, startX, startY, laserLen, laserWidth / 2f, laserLen, laserWidth, 1, 1, 0);

        batch.draw(impactFrame,
            cg.isFacingRight() ? startX + laserLen : startX - laserLen,
            startY - laserWidth / 2f);
    }
}
