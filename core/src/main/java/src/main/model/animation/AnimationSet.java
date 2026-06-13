package src.main.model.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import src.main.model.knight.KnightAnimationType;

import java.util.Map;

public class AnimationSet {
    private Map<KnightAnimationType, Animation<TextureRegion>> animations;
    private KnightAnimationType currentType;
    private float stateTime = 0;

    public AnimationSet(Map<KnightAnimationType, Animation<TextureRegion>> animations) {
        this.animations = animations;
        this.currentType = KnightAnimationType.IDLE;
    }

    public TextureRegion getFrame(float delta){
        stateTime += delta;
        return animations.get(currentType).getKeyFrame(stateTime);
    }
    public void setAnimation(KnightAnimationType type){
        if(currentType != type){
            currentType = type;
            stateTime = 0;
        }
    }
    public boolean isCurrentAnimationFinished(){
        return animations.get(currentType).isAnimationFinished(stateTime);
    }
    public KnightAnimationType getCurrentType() {
        return currentType;
    }

}
