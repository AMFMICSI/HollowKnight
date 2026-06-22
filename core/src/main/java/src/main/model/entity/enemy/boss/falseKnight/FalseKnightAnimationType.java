package src.main.model.entity.enemy.boss.falseKnight;

import com.badlogic.gdx.graphics.g2d.Animation;
import src.main.model.entity.animation.AnimationType;

public enum FalseKnightAnimationType implements AnimationType {
    IDLE("Idle", 5, 0.15f, Animation.PlayMode.LOOP),
    RUN("Run", 5, 0.1f, Animation.PlayMode.LOOP),
    RUN_ANTIC("Run Antic", 2, 0.12f, Animation.PlayMode.NORMAL),
    ATTACK_ANTIC("Attack Antic", 6, 0.1f, Animation.PlayMode.NORMAL),
    ATTACK("Attack", 3, 0.1f, Animation.PlayMode.NORMAL),
    ATTACK_RECOVER("Attack Recover", 5, 0.12f, Animation.PlayMode.NORMAL),
    JUMP("Jump", 4, 0.1f, Animation.PlayMode.NORMAL),
    JUMP_ATTACK("Jump Attack", 8, 0.1f, Animation.PlayMode.NORMAL),
    LAND("Land", 5, 0.1f, Animation.PlayMode.NORMAL),
    TURN("Turn", 2, 0.1f, Animation.PlayMode.NORMAL),
    STUN_RECOVER("Stun Recover", 6, 0.1f, Animation.PlayMode.NORMAL),
    DEATH_FALL("DeathFall", 3, 0.15f, Animation.PlayMode.NORMAL),
    DEATH_HIT("DeathHit", 3, 0.15f, Animation.PlayMode.NORMAL),
    DEATH_LAND("DeathLand", 11, 0.15f, Animation.PlayMode.NORMAL),
    BODY("Body", 5, 0.2f, Animation.PlayMode.NORMAL);

    public final String filePrefix;
    public final int frameCount;
    public final float frameDuration;
    public final Animation.PlayMode playMode;

    FalseKnightAnimationType(String filePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
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
