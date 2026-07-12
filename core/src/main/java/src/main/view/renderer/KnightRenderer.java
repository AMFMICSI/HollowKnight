package src.main.view.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.knight.Knight;
import src.main.model.entity.knight.KnightAnimationType;
import src.main.model.entity.animation.AnimationSet;
import src.main.view.manager.GameAssetManager;

public class KnightRenderer {
    private static final float DRAW_SCALE = 5f;
    private final AnimationSet<KnightAnimationType> animSet;

    public KnightRenderer() {
        animSet = new AnimationSet<>(GameAssetManager.knightAnimations, KnightAnimationType.IDLE);
    }

    public void render(SpriteBatch batch, Knight knight, float delta) {
        if (knight.isDead()) return;
        if (!knight.isDead() && knight.getInvincibleTimer() > 0
            && (Math.floor(knight.getInvincibleTimer() * 10) % 2 == 0)) return;

        animSet.setAnimation(knight.getAnimType());
        animSet.syncStateTime(knight.getStateTime());

        TextureRegion frame;
        if (knight.hasSharpShadow() && knight.isDashing()) {
            frame = GameAssetManager.shadowDashAnim.getKeyFrame(knight.getDashElapsed());
        } else {
            frame = animSet.getCurrentFrame();
        }
        if (frame == null) frame = animSet.getCurrentFrame();

        float spriteW = knight.getBoundingBox().width * DRAW_SCALE;
        float spriteH = spriteW * frame.getRegionHeight() / (float) frame.getRegionWidth();
        batch.draw(frame,
            knight.getPosition().x + (knight.getBoundingBox().width - spriteW) / 2f,
            knight.getPosition().y,
            spriteW / 2f, 0,
            spriteW, spriteH,
            knight.isFacingRight() ? -1 : 1, 1, 0);
    }

    public void dispose() {}
}
