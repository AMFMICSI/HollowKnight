package src.main.model.entity.enemy.flyingEnemy.crystalHunter;

import com.badlogic.gdx.graphics.g2d.Animation;
import src.main.model.entity.animation.AnimationType;

public enum CrystalHunterAnimationType implements AnimationType {
    FLY("Fly", 4, 0.12f, Animation.PlayMode.LOOP),
    TURN_TO_FLY("Turn To Fly", 3, 0.1f, Animation.PlayMode.NORMAL),
    ATTACK("Attack", 4, 0.1f, Animation.PlayMode.NORMAL),
    ATTACK_RECOVER("Attack Recover", 1, 0.3f, Animation.PlayMode.NORMAL),
    DEATH_AIR("Death Air", 3, 0.15f, Animation.PlayMode.NORMAL),
    DEATH_LAND("Death Land", 1, 0.3f, Animation.PlayMode.NORMAL);

    public final String filePrefix;
    public final int frameCount;
    public final float frameDuration;
    public final Animation.PlayMode playMode;

    CrystalHunterAnimationType(String filePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
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
