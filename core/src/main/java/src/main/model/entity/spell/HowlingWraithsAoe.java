package src.main.model.entity.spell;

import com.badlogic.gdx.math.Rectangle;

public class HowlingWraithsAoe {
    private Rectangle bounds;
    private float timer = 0;
    private int tickCount = 0;
    private boolean done = false;
    private boolean shadow;
    private static final float DURATION = 0.6f;
    private static final float INTERVAL = 0.2f;
    private static final int MAX_TICKS = 3;

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
    public boolean isShadow() { return shadow; }
    public float getTimer() { return timer; }

    public Rectangle getBounds() { return bounds; }
}
