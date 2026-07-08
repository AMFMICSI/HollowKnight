package src.main.model.entity.enemy.flyingEnemy.crystalHunter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.animation.AnimationSet;
import src.main.model.entity.enemy.flyingEnemy.FlyingEnemy;
import src.main.view.manager.GameAssetManager;

import java.util.ArrayList;
import java.util.List;

public class CrystalHunter extends FlyingEnemy {
    private static final float FLY_SPEED = 60f;
    private static final int MAX_HP = 3;
    private static final float ATTACK_RANGE = 250f;

    private final AnimationSet<CrystalHunterAnimationType> animSet;
    private CrystalHunterState currentState = CrystalHunterState.TRACKING;
    private final List<CrystalProjectile> projectiles = new ArrayList<>();
    private float stateTimer;
    private boolean projectileSpawned;

    public CrystalHunter(float x, float y, Rectangle zone, KnightRef knightRef) {
        DRAW_SCALE = 2f;
        spawnPosition.set(x, y);
        hp = maxHp = MAX_HP;
        position.set(x, y);
        boundingBox.setSize(24, 20);
        chaseSpeed = FLY_SPEED;
        respawnDistance = 2500f;
        animSet = new AnimationSet<>(GameAssetManager.crystalHunterAnimations, CrystalHunterAnimationType.FLY);
        this.knightRef = knightRef;
        setFacingRight(true);
        setZone(zone);
    }

    @Override
    protected void updateChase(float delta, Vector2 knightPos, float dist) {
        stateTimer -= delta;
        handleChaseState(knightPos, dist);
    }

    private void handleChaseState(Vector2 knightPos, float dist) {
        switch (currentState) {
            case TRACKING:
                animSet.setAnimation(CrystalHunterAnimationType.FLY);
                if (dist < ATTACK_RANGE) {
                    changeState(CrystalHunterState.ATTACK_ANTICIPATE);
                } else {
                    moveToward(knightPos, chaseSpeed);
                }
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
        }
    }

    private void changeState(CrystalHunterState newState) {
        currentState = newState;
        stateTimer = switch (newState) {
            case ATTACK_RECOVER -> 0.4f;
            default -> 0;
        };
        animSet.resetAnimation();
    }

    @Override
    public TextureRegion getFrame(float delta) {
        CrystalHunterAnimationType type;
        if (isDead) type = diedInAir ? CrystalHunterAnimationType.DEATH_AIR : CrystalHunterAnimationType.DEATH_LAND;
        else type = animSet.getCurrentType();
        animSet.setAnimation(type);
        return animSet.getFrame(delta);
    }

    public List<CrystalProjectile> getProjectiles() { return projectiles; }

    @Override
    public TextureRegion getCorpseFrame() {
        return GameAssetManager.crystalHunterAnimations.get(
            diedInAir ? CrystalHunterAnimationType.DEATH_AIR : CrystalHunterAnimationType.DEATH_LAND
        ).getKeyFrame(0);
    }
}
