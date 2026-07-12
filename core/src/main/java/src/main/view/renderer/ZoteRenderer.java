package src.main.view.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.npc.zote.Zote;
import src.main.model.entity.npc.zote.ZoteAnimationType;
import src.main.model.entity.animation.AnimationSet;
import src.main.view.manager.GameAssetManager;

public class ZoteRenderer {
    private final AnimationSet<ZoteAnimationType> animSet;

    public ZoteRenderer() {
        animSet = new AnimationSet<>(GameAssetManager.zoteAnimations, ZoteAnimationType.IDLE);
    }

    public void render(SpriteBatch batch, Zote zote, float delta) {
        ZoteAnimationType type;
        if (zote.isAttacking()) type = ZoteAnimationType.ATTACK;
        else if (zote.isTalking()) type = ZoteAnimationType.TALK;
        else type = ZoteAnimationType.IDLE;

        animSet.setAnimation(type);
        animSet.syncStateTime(zote.getStateTime());

        TextureRegion frame = animSet.getCurrentFrame();
        if (frame == null) return;

        float spriteW = zote.getBoundingBox().width * zote.getDrawScale();
        float spriteH = spriteW * frame.getRegionHeight() / (float) frame.getRegionWidth();
        batch.draw(frame,
            zote.getBoundingBox().x + (zote.getBoundingBox().width - spriteW) / 2f,
            zote.getBoundingBox().y,
            spriteW / 2f, 0,
            spriteW, spriteH,
            zote.isFacingRight() ? -1 : 1, 1, 0);
    }
}
