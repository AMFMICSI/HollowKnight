package src.main.model.entity.knight;

import com.badlogic.gdx.graphics.g2d.Animation;
import src.main.model.entity.animation.AnimationType;

public enum KnightAnimationType implements AnimationType {
    IDLE("Idle", 9, 0.12f, Animation.PlayMode.LOOP),
    RUN_START("Run_start", 2, 0.1f, Animation.PlayMode.NORMAL),
    RUN_LOOP("Run_loop", 11, 0.08f, Animation.PlayMode.LOOP),
    AIRBORNE("Airborne", 12, 0.10f, Animation.PlayMode.LOOP),
    FALL("Fall", 6, 0.10f, Animation.PlayMode.LOOP),
    SLASH("SlashAlt", 5, 0.06f, Animation.PlayMode.NORMAL),
    DASH("Dash", 12, 0.07f, Animation.PlayMode.LOOP),
    DOWN_SLASH("DownSlash", 5, 0.06f, Animation.PlayMode.NORMAL),
    UP_SLASH("UpSlash", 5, 0.06f, Animation.PlayMode.NORMAL),
    DOUBLE_JUMP("Double Jump", 8, 0.08f, Animation.PlayMode.LOOP),
    LANDING("Landing", 4, 0.08f, Animation.PlayMode.NORMAL),
    FOCUS_START("Focus Start", 3, 0.1f, Animation.PlayMode.NORMAL),
    FOCUS("Focus", 7, 0.12f, Animation.PlayMode.LOOP),
    FOCUS_END("Focus End", 3, 0.1f, Animation.PlayMode.NORMAL),
    FOCUS_GET("Focus Get", 6, 0.08f, Animation.PlayMode.NORMAL),
    FIREBALL_CAST("Fireball Cast", 9, 0.04f, Animation.PlayMode.NORMAL),
    SCREAM("Scream", 7, 0.04f, Animation.PlayMode.NORMAL);


    public final String filePrefix;
    public final  int frameCount;
    public final  float frameDuration;
    public final Animation.PlayMode playMode;

    KnightAnimationType(String filePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
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
