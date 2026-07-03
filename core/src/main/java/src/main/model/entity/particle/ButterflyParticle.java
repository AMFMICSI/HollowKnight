package src.main.model.entity.particle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ButterflyParticle {
    private float x, y;
    private float vx, vy;
    private float stateTime;
    private float lifetime;
    private float elapsed;
    private float floatPhase;
    private boolean ambient;
    private float originX, originY;
    private static final float SIZE = 24f;

    public ButterflyParticle(float x, float y) {
        this(x, y, false);
    }

    public ButterflyParticle(float x, float y, boolean ambient) {
        this.x = x;
        this.y = y;
        this.ambient = ambient;
        this.floatPhase = (float)(Math.random() * Math.PI * 2);
        if (ambient) {
            originX = x;
            originY = y;
            this.vx = (float)(Math.random() - 0.5f) * 8f;
            this.vy = (float)(Math.random() - 0.5f) * 6f;
            this.lifetime = 8f + (float)Math.random() * 4f;
        } else {
            this.vx = 20 + (float)Math.random() * 50;
            this.vy = 50 + (float)Math.random() * 40;
            this.lifetime = 2f + (float)Math.random() * 1.5f;
        }
    }

    public void update(float delta) {
        elapsed += delta;
        stateTime += delta;
        if (ambient) {
            x = originX + (float)Math.sin(elapsed * 0.7f + floatPhase) * 20f;
            y = originY + (float)Math.sin(elapsed * 1.2f + floatPhase * 1.5f) * 15f;
            x += vx * delta;
            y += vy * delta;
        } else {
            x += vx * delta;
            y += vy * delta;
            y += (float)Math.sin(elapsed * 4 + floatPhase) * 60f * delta;
            vy -= 20f * delta;
        }
    }

    public boolean isDead() { return elapsed >= lifetime; }
    public boolean isAmbient() { return ambient; }

    public void draw(SpriteBatch batch, Animation<TextureRegion> anim) {
        TextureRegion frame = anim.getKeyFrame(stateTime);
        float alpha = 1f;
        if (elapsed > lifetime - 0.5f)
            alpha = (lifetime - elapsed) / 0.5f;
        batch.setColor(1, 1, 1, alpha);
        batch.draw(frame, x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
        batch.setColor(1, 1, 1, 1);
    }
}
