package src.main.model.entity.animation;

public class AnimStateTracker<T extends AnimationType> {
    private T currentType;
    private float stateTime = 0;

    public AnimStateTracker(T defaultType) {
        this.currentType = defaultType;
    }

    public void advanceTime(float delta) {
        stateTime += delta;
    }

    public void setAnimation(T type) {
        if (currentType != type) {
            currentType = type;
            stateTime = 0;
        }
    }

    public void reset() {
        stateTime = 0;
    }

    public T getCurrentType() { return currentType; }
    public float getStateTime() { return stateTime; }

    public boolean isFinished() {
        return stateTime >= getDuration();
    }

    public float getDuration() {
        return currentType.getFrameDuration() * currentType.getFrameCount();
    }
}
