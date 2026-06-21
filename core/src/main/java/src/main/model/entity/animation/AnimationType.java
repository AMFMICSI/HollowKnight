package src.main.model.entity.animation;

import com.badlogic.gdx.graphics.g2d.Animation;

public interface AnimationType {
    String getFilePrefix();
    int getFrameCount();
    float getFrameDuration();
    Animation.PlayMode getPlayMode();
}
