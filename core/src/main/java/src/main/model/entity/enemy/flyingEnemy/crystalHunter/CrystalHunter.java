package src.main.model.entity.enemy.flyingEnemy.crystalHunter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.enemy.Enemy;
import src.main.view.GameAssetManager;

import java.util.ArrayList;
import java.util.List;

public class CrystalHunter extends Enemy {
    private static final float FLY_SPEED = 60f;
    private static final int MAX_HP = 3;
    private static final float DETECTION_RANGE = 400f;
    private static final float ATTACK_RANGE = 250f;
    private static final float MIN_DISTANCE = 120f;
    private static final float DEATH_DURATION = 1.0f;

    private final AnimationSet<CrystalHunterAnimationType> animSet;
    private CrystalHunterState currentState = CrystalHunterState.IDLE;
    private List<CrystalProjectile> projectiles = new ArrayList<>();
    private float stateTimer;
    private boolean projectileSpawned;
    private boolean diedInAir;

    @FunctionalInterface
    public interface KnightRef {
        Vector2 getPosition();
    }

    private KnightRef knightRef;

    private float zoneMinX, zoneMaxX, zoneMinY, zoneMaxY;

    public CrystalHunter(float x, float y, Rectangle zone, KnightRef knightRef) {
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        boundingBox.setSize(24, 20);
        this.zone = (zone != null) ? zone : new Rectangle(x - 150, y - 100, 300, 200);
        zoneMinX = this.zone.x;
        zoneMaxX = this.zone.x + this.zone.width - boundingBox.width;
        zoneMinY = this.zone.y;
        zoneMaxY = this.zone.y + this.zone.height - boundingBox.height;
        animSet = new AnimationSet<>(GameAssetManager.crystalHunterAnimations, CrystalHunterAnimationType.FLY);
        this.knightRef = knightRef;
        setFacingRight(true);
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            deathTimer -= delta;
            if (deathTimer <= 0) deadAnimationDone = true;
            return;
        }

        stateTimer -= delta;
        Vector2 knightPos = knightRef.getPosition();
        float dist = position.dst(knightPos);

        switch (currentState) {
            case IDLE:
                animSet.setAnimation(CrystalHunterAnimationType.FLY);
                velocity.set(0, 0);
                if (dist < DETECTION_RANGE) {
                    changeState(CrystalHunterState.TRACKING);
                }
                break;

            case TRACKING:
                animSet.setAnimation(CrystalHunterAnimationType.FLY);
                if (dist > DETECTION_RANGE * 1.3f) {
                    changeState(CrystalHunterState.IDLE);
                    break;
                }
                if (dist < ATTACK_RANGE) {
                    changeState(CrystalHunterState.ATTACK_ANTICIPATE);
                    break;
                }
                moveToward(knightPos, dist);
                break;

            case ATTACK_ANTICIPATE:
                velocity.set(0, 0);
                if (animSet.getCurrentType() != CrystalHunterAnimationType.TURN_TO_FLY) {
                    animSet.setAnimation(CrystalHunterAnimationType.TURN_TO_FLY);
                }
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(CrystalHunterState.ATTACKING);
                }
                break;

            case ATTACKING:
                velocity.set(0, 0);
                if (animSet.getCurrentType() != CrystalHunterAnimationType.ATTACK) {
                    animSet.setAnimation(CrystalHunterAnimationType.ATTACK);
                    projectileSpawned = false;
                }
                if (!projectileSpawned && animSet.getStateTime() >= 0.15f) {
                    projectileSpawned = true;
                    projectiles.add(new CrystalProjectile(
                        position.x, position.y + boundingBox.height / 2,
                        knightPos.x, knightPos.y));
                }
                if (animSet.getAnimationDuration() <= 0 || animSet.getStateTime() >= animSet.getAnimationDuration()) {
                    changeState(CrystalHunterState.ATTACK_RECOVER);
                }
                break;

            case ATTACK_RECOVER:
                velocity.set(0, 0);
                if (animSet.getCurrentType() != CrystalHunterAnimationType.ATTACK_RECOVER) {
                    animSet.setAnimation(CrystalHunterAnimationType.ATTACK_RECOVER);
                }
                if (stateTimer <= 0) {
                    changeState(CrystalHunterState.TRACKING);
                }
                break;

            case DEATH:
                break;
        }

        position.x = Math.max(zoneMinX, Math.min(zoneMaxX, position.x));
        position.y = Math.max(zoneMinY, Math.min(zoneMaxY, position.y));
        boundingBox.setPosition(position);
    }

    private void moveToward(Vector2 target, float dist) {
        float dx = target.x - position.x;
        float dy = target.y - position.y;

        if (dist > MIN_DISTANCE) {
            float nx = dx / dist;
            float ny = dy / dist;
            velocity.set(nx * FLY_SPEED, ny * FLY_SPEED);
        } else {
            velocity.set(0, 0);
        }

        if (dx > 0) setFacingRight(true);
        else if (dx < 0) setFacingRight(false);
    }

    private void changeState(CrystalHunterState newState) {
        currentState = newState;
        stateTimer = switch (newState) {
            case ATTACK_RECOVER -> 0.4f;
            default -> 0;
        };
        animSet.resetAnimation();
    }

    private void changeAnimType(CrystalHunterAnimationType newType) {
        animSet.setAnimation(newType);
    }

    @Override
    public void takeDamage(int amount) {
        if (!isDead) diedInAir = true;
        super.takeDamage(amount);
    }

    private CrystalHunterAnimationType getCurrentAnimType() {
        if (isDead) return diedInAir ? CrystalHunterAnimationType.DEATH_AIR : CrystalHunterAnimationType.DEATH_LAND;
        return animSet.getCurrentType();
    }

    @Override
    public TextureRegion getFrame(float delta) {
        animSet.setAnimation(getCurrentAnimType());
        return animSet.getFrame(delta);
    }

    public CrystalHunterState getCurrentState() { return currentState; }
    public List<CrystalProjectile> getProjectiles() { return projectiles; }

    @Override
    public TextureRegion getCorpseFrame() {
        return GameAssetManager.crystalHunterAnimations.get(
            diedInAir ? CrystalHunterAnimationType.DEATH_AIR : CrystalHunterAnimationType.DEATH_LAND
        ).getKeyFrame(0);
    }
}
