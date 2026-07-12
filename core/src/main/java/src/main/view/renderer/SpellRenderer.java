package src.main.view.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.spell.HowlingWraithsAoe;
import src.main.model.entity.spell.VengefulProjectile;
import src.main.view.manager.GameAssetManager;

public class SpellRenderer {
    public void renderProjectile(SpriteBatch batch, VengefulProjectile p, float delta) {
        if (p.isDead()) return;
        Animation<TextureRegion> anim = p.isShadow()
            ? GameAssetManager.shadowProjectileAnim
            : GameAssetManager.vengefulProjectileAnim;
        if (anim != null) {
            TextureRegion frame = anim.getKeyFrame(p.getAnimTimer());
            float s = 0.5f;
            float w = frame.getRegionWidth() * s;
            float h = frame.getRegionHeight() * s;
            if (p.isFacingRight())
                batch.draw(frame, p.getPosition().x, p.getPosition().y, w, h);
            else
                batch.draw(frame, p.getPosition().x + w, p.getPosition().y, -w, h);
        }
    }

    public void renderAoe(SpriteBatch batch, HowlingWraithsAoe aoe, float delta) {
        if (aoe.isDone()) return;
        Animation<TextureRegion> anim = aoe.isShadow()
            ? GameAssetManager.shadowScreamAnim
            : GameAssetManager.wraithsAoeAnim;
        if (anim != null) {
            TextureRegion frame = anim.getKeyFrame(aoe.getTimer());
            float s = 0.5f;
            float w = frame.getRegionWidth() * s;
            float h = frame.getRegionHeight() * s;
            float cx = aoe.getBounds().x + aoe.getBounds().width / 2f;
            float cy = aoe.getBounds().y + aoe.getBounds().height / 2f;
            batch.draw(frame, cx - w / 2f, cy - h / 2f, w, h);
        }
    }
}
