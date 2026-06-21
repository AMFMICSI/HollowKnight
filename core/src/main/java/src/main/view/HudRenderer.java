package src.main.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.hud.SoulFillStage;

public class HudRenderer {
    private OrthographicCamera hudCamera;

    private static final float W = 320, H = 180;
    private static final float SOUL_SIZE = 36;
    private static final float SOUL_X = 8;
    private static final float SOUL_Y = H - 44;
    private static final float MASK_SIZE = 24;
    private static final float MASK_SPACING = 4;
    private static final float MASK_X = SOUL_X + SOUL_SIZE + 4;
    private static final float MASK_Y = SOUL_Y + (SOUL_SIZE - MASK_SIZE) / 2f;

    private TextureRegion emptyMask, filledMask;
    private AnimationSet<SoulFillStage> soulAnim;

    public HudRenderer() {
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, W, H);

        emptyMask = GameAssetManager.hudAtlas.findRegion("EmptyHealth");
        filledMask = GameAssetManager.hudAtlas.findRegion("FilledHealth");
        soulAnim = new AnimationSet<>(GameAssetManager.soulFillAnimations, SoulFillStage.EMPTY);
    }

    public void render(SpriteBatch batch, int hp, int maxHp, int soul, int maxSoul) {
        float fill = (float) soul / maxSoul;
        SoulFillStage stage = SoulFillStage.fromFill(fill);
        soulAnim.setAnimation(stage);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        float ms = MASK_SIZE / emptyMask.getRegionWidth();
        for (int i = 0; i < maxHp; i++) {
            float x = MASK_X + i * (MASK_SIZE + MASK_SPACING);
            TextureRegion r = (i < hp) ? filledMask : emptyMask;
            batch.draw(r, x, MASK_Y, 0, 0,
                r.getRegionWidth(), r.getRegionHeight(), ms, ms, 0);
        }

        TextureRegion frame = soulAnim.getFrame(Gdx.graphics.getDeltaTime());
        float fw = frame.getRegionWidth();
        float fh = frame.getRegionHeight();
        float s = Math.min(SOUL_SIZE / fw, SOUL_SIZE / fh);
        batch.draw(frame, SOUL_X + (SOUL_SIZE - fw * s) / 2f, SOUL_Y + (SOUL_SIZE - fh * s) / 2f,
            fw * s, fh * s);

        batch.end();
    }

    public void dispose() {}
}
