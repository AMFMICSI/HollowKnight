package src.main.model.entity.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.Map;

public class AnimationSet<T> {
    private final Map<T, Animation<TextureRegion>> animations;
    private T currentType;
    private float stateTime = 0;

    public AnimationSet(Map<T, Animation<TextureRegion>> animations, T defaultType) {
        this.animations = animations;
        this.currentType = defaultType;
    }

    public TextureRegion getFrame(float delta) {
        stateTime += delta;
        return animations.get(currentType).getKeyFrame(stateTime);
    }

    public void setAnimation(T type) {
        if (currentType != type) {
            currentType = type;
            stateTime = 0;
        }
    }

    public T getCurrentType() { return currentType; }

    public float getStateTime() { return stateTime; }

    public void resetAnimation() { stateTime = 0; }

    public float getAnimationDuration() {
        return animations.get(currentType).getAnimationDuration();
    }
}
