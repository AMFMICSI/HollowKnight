package src.main.model.entity.enemy.constantEnemy.crystalGuardian;

import com.badlogic.gdx.graphics.g2d.Animation;
import src.main.model.entity.animation.AnimationType;

public enum CrystalGuardianAnimationType implements AnimationType {
    IDLE("Idle", 5, 0.15f, Animation.PlayMode.LOOP),
    SHOOT("Shoot", 7, 0.1f, Animation.PlayMode.NORMAL),
    RUN("Run", 6, 0.1f, Animation.PlayMode.LOOP),
    EVADE("Evade", 7, 0.1f, Animation.PlayMode.NORMAL),
    TURN("Turn", 3, 0.1f, Animation.PlayMode.NORMAL),
    DEATH_AIR("Death Air", 3, 0.15f, Animation.PlayMode.NORMAL),
    DEATH_LAND("Death Land", 3, 0.15f, Animation.PlayMode.NORMAL);

    public final String filePrefix;
    public final int frameCount;
    public final float frameDuration;
    public final Animation.PlayMode playMode;

    CrystalGuardianAnimationType(String filePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
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
