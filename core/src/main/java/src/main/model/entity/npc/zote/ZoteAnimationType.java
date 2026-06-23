package src.main.model.entity.npc.zote;

import com.badlogic.gdx.graphics.g2d.Animation;
import src.main.model.entity.animation.AnimationType;

public enum ZoteAnimationType implements AnimationType {
    IDLE("Idle", 5, 0.15f, Animation.PlayMode.LOOP),
    TALK("Talk", 5, 0.15f, Animation.PlayMode.LOOP),
    ATTACK("Attack", 4, 0.1f, Animation.PlayMode.NORMAL),
    FALL("Fall", 5, 0.15f, Animation.PlayMode.NORMAL),
    ROLL("Roll", 3, 0.1f, Animation.PlayMode.NORMAL),
    GET_UP("Get Up", 4, 0.1f, Animation.PlayMode.NORMAL),
    TURN("Turn", 2, 0.1f, Animation.PlayMode.NORMAL);

    public final String filePrefix;
    public final int frameCount;
    public final float frameDuration;
    public final Animation.PlayMode playMode;

    ZoteAnimationType(String filePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
        this.filePrefix = filePrefix;
        this.frameCount = frameCount;
        this.frameDuration = frameDuration;
        this.playMode = playMode;
    }

    @Override public String getFilePrefix() { return filePrefix; }
    @Override public int getFrameCount() { return frameCount; }
    @Override public float getFrameDuration() { return frameDuration; }
    @Override public Animation.PlayMode getPlayMode() { return playMode; }
}
