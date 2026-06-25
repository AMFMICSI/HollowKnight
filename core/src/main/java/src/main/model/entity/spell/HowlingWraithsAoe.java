package src.main.model.entity.spell;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import src.main.view.GameAssetManager;

public class HowlingWraithsAoe {
    private Rectangle bounds;
    private float timer = 0;
    private int tickCount = 0;
    private boolean done = false;
    private boolean shadow;
    private static final float DURATION = 0.6f;
    private static final float INTERVAL = 0.2f;
    private static final int MAX_TICKS = 3;
    private static final float EXPAND_SIZE = 60f;

    public HowlingWraithsAoe(float x, float y, float w, float h, boolean shadow) {
        this.shadow = shadow;
        bounds = new Rectangle(x - 30, y - 20, w + 60, h + 80);
    }

    public void update(float delta) {
        timer += delta;
        int newTick = (int)(timer / INTERVAL);
        if (newTick > tickCount) tickCount = newTick;
        if (timer >= DURATION) done = true;
    }

    public int getTickCount() { return tickCount; }

    public boolean isDone() { return done; }

    public void draw(SpriteBatch batch, float delta) {
        if (done) return;
        Animation<TextureRegion> anim = shadow ? GameAssetManager.shadowScreamAnim : GameAssetManager.wraithsAoeAnim;
        if (anim != null) {
            TextureRegion frame = anim.getKeyFrame(timer);
            float s = 0.5f;
            float w = frame.getRegionWidth() * s;
            float h = frame.getRegionHeight() * s;
            float cx = bounds.x + bounds.width / 2f;
            float cy = bounds.y + bounds.height / 2f;
            batch.draw(frame, cx - w / 2f, cy - h / 2f, w, h);
        }
    }

    public Rectangle getBounds() { return bounds; }
}
