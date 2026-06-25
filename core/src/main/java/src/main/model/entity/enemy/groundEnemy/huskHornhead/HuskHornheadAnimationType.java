package src.main.model.entity.enemy.groundEnemy.huskHornhead;

import com.badlogic.gdx.graphics.g2d.Animation;
import src.main.model.entity.animation.AnimationType;

public enum HuskHornheadAnimationType implements AnimationType {
    IDLE("Idle", 6, 0.15f, Animation.PlayMode.LOOP),
    WALK("Walk", 7, 0.12f, Animation.PlayMode.LOOP),
    ATTACK_ANTICIPATE("Attack Anticipate", 5, 0.10f, Animation.PlayMode.NORMAL),
    ATTACK_LUNGE("Attack Lunge", 12, 0.08f, Animation.PlayMode.NORMAL),
    DEATH_LAND("Death Land", 8, 0.12f, Animation.PlayMode.NORMAL);

    public final String filePrefix;
    public final int frameCount;
    public final float frameDuration;
    public final Animation.PlayMode playMode;

    HuskHornheadAnimationType(String filePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
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
