package src.main.model.entity.hud;

import com.badlogic.gdx.graphics.g2d.Animation;

public enum SoulFillStage {
    EMPTY("HUD Cln", 237, 237, 0.2f, Animation.PlayMode.LOOP),
    LOW("HUD Cln", 238, 243, 0.2f, Animation.PlayMode.LOOP),
    MID("HUD Cln", 244, 248, 0.2f, Animation.PlayMode.LOOP),
    HIGH("HUD Cln", 249, 254, 0.2f, Animation.PlayMode.LOOP);

    public final String filePrefix;
    public final int frameStart;
    public final int frameEnd;
    public final float frameDuration;
    public final int frameCount;
    public final Animation.PlayMode playMode;

    SoulFillStage(String filePrefix, int frameStart, int frameEnd, float frameDuration, Animation.PlayMode playMode) {
        this.filePrefix = filePrefix;
        this.frameStart = frameStart;
        this.frameEnd = frameEnd;
        this.frameCount = frameEnd - frameStart + 1;
        this.frameDuration = frameDuration;
        this.playMode = playMode;
    }

    public static SoulFillStage fromFill(float fill) {
        if (fill < 0.01f) return EMPTY;
        if (fill <= 0.33f) return LOW;
        if (fill <= 0.67f) return MID;
        return HIGH;
    }
}
