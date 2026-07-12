package src.main.view.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.main.model.entity.particle.ButterflyParticle;

public class ParticleRenderer {
    private static final float SIZE = 24f;

    public void renderButterfly(SpriteBatch batch, ButterflyParticle p, Animation<TextureRegion> anim) {
        TextureRegion frame = anim.getKeyFrame(p.getStateTime());
        float alpha = 1f;
        if (p.getElapsed() > p.getLifetime() - 0.5f)
            alpha = (p.getLifetime() - p.getElapsed()) / 0.5f;
        batch.setColor(1, 1, 1, alpha);
        batch.draw(frame, p.getX() - SIZE / 2, p.getY() - SIZE / 2, SIZE, SIZE);
        batch.setColor(1, 1, 1, 1);
    }
}
