package src.main.model.entity.enemy.flyingEnemy.crystalHunter;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimStateTracker;
import src.main.model.entity.enemy.flyingEnemy.FlyingEnemy;

import java.util.ArrayList;
import java.util.List;

public class CrystalHunter extends FlyingEnemy {
    private static final float FLY_SPEED = 60f;
    private static final int MAX_HP = 3;
    private static final float ATTACK_RANGE = 250f;

    private final AnimStateTracker<CrystalHunterAnimationType> animState =
        new AnimStateTracker<>(CrystalHunterAnimationType.FLY);
    private CrystalHunterState currentState = CrystalHunterState.TRACKING;
    private final List<CrystalProjectile> projectiles = new ArrayList<>();
    private float stateTimer;
    private boolean projectileSpawned;

    @Override
    public void respawn() {
        super.respawn();
        currentState = CrystalHunterState.TRACKING;
        projectileSpawned = false;
        projectiles.clear();
        stateTimer = 0;
        animState.reset();
    }

    public CrystalHunter(float x, float y, Rectangle zone, KnightRef knightRef) {
        DRAW_SCALE = 2f;
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        boundingBox.setSize(24, 20);
        chaseSpeed = FLY_SPEED;
        this.knightRef = knightRef;
        setFacingRight(true);
        setZone(zone);
    }

    @Override
    protected void updateChase(float delta, Vector2 knightPos, float dist) {
        animState.advanceTime(delta);
        stateTimer -= delta;
        handleChaseState(knightPos, dist);
    }

    private void handleChaseState(Vector2 knightPos, float dist) {
        switch (currentState) {
            case TRACKING:
                animState.setAnimation(CrystalHunterAnimationType.FLY);
                if (dist < ATTACK_RANGE) {
                    changeState(CrystalHunterState.ATTACK_ANTICIPATE);
                } else {
                    moveToward(knightPos, chaseSpeed);
                }
                break;

            case ATTACK_ANTICIPATE:
                velocity.set(0, 0);
                animState.setAnimation(CrystalHunterAnimationType.TURN_TO_FLY);
                if (animState.isFinished()) {
                    changeState(CrystalHunterState.ATTACKING);
                }
                break;

            case ATTACKING:
                velocity.set(0, 0);
                animState.setAnimation(CrystalHunterAnimationType.ATTACK);
                if (!projectileSpawned && animState.getStateTime() >= 0.15f) {
                    projectileSpawned = true;
                    projectiles.add(new CrystalProjectile(
                        position.x, position.y + boundingBox.height / 2,
                        knightPos.x, knightPos.y));
                }
                if (animState.isFinished()) {
                    changeState(CrystalHunterState.ATTACK_RECOVER);
                }
                break;

            case ATTACK_RECOVER:
                velocity.set(0, 0);
                animState.setAnimation(CrystalHunterAnimationType.ATTACK_RECOVER);
                if (stateTimer <= 0) {
                    changeState(CrystalHunterState.TRACKING);
                }
                break;
        }
    }

    private void changeState(CrystalHunterState newState) {
        currentState = newState;
        stateTimer = switch (newState) {
            case ATTACK_RECOVER -> 0.4f;
            default -> 0;
        };
        if (newState == CrystalHunterState.ATTACKING) projectileSpawned = false;
        animState.reset();
    }

    public CrystalHunterAnimationType getAnimType() {
        if (isDead) return diedInAir ? CrystalHunterAnimationType.DEATH_AIR : CrystalHunterAnimationType.DEATH_LAND;
        return animState.getCurrentType();
    }
    public float getStateTime() { return animState.getStateTime(); }

    public List<CrystalProjectile> getProjectiles() { return projectiles; }
}
