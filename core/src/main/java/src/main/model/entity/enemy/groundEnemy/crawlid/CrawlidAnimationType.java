package src.main.model.entity.enemy.groundEnemy.crawlid;

import com.badlogic.gdx.graphics.g2d.Animation;
import src.main.model.entity.animation.AnimationType;

public enum CrawlidAnimationType implements AnimationType {
    WALK("Walk", 4, 0.15f, Animation.PlayMode.LOOP),
    TURN("turn", 2, 0.1f, Animation.PlayMode.NORMAL),
    DEATH_LAND("Death Land", 2, 0.15f, Animation.PlayMode.NORMAL),
    DEATH_AIR("Death Air", 3, 0.15f, Animation.PlayMode.NORMAL);


    public final String filePrefix;
    public final  int frameCount;
    public final  float frameDuration;
    public final Animation.PlayMode playMode;

    CrawlidAnimationType(String filePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
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
