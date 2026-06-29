package src.main.model.enviroment;

import com.badlogic.gdx.math.Rectangle;

public class CrackedWall {
    private Rectangle bounds;
    private int hitCount = 0;
    private static final int MAX_HITS = 3;

    public CrackedWall(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() { return bounds; }

    public boolean isIntact() {
        return hitCount < MAX_HITS;
    }

    public void registerHit() {
        if (isIntact()) {
            hitCount++;
        }
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getMaxHits() {
        return MAX_HITS;
    }
}