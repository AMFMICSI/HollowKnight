package src.main.model.entity.npc.zote;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.Entity;
import src.main.model.entity.animation.AnimStateTracker;
import src.main.view.config.TranslationManager;
import src.main.view.manager.GameAssetManager;
import src.main.view.config.GameSettings;

import java.util.Random;

public class Zote extends Entity {
    private static final float INTERACTION_RANGE = 50f;
    private static final float PROXIMITY_GRUNT_MIN = 3f;
    private static final float PROXIMITY_GRUNT_MAX = 7f;
    private static final float ATTACK_DURATION = 0.4f;
    private static final float AGITATION_DURATION = 3f;

    private enum DialogueState { MAIN, PRECEPT }

    private final AnimStateTracker<ZoteAnimationType> animState =
        new AnimStateTracker<>(ZoteAnimationType.IDLE);
    private final String[] dialogues;
    private final String[] precepts;
    private int dialogueIndex;
    private boolean talking;
    private DialogueState dialogueState;
    private int preceptIndex;

    private boolean agitated;
    private float agitationTimer;
    private float attackTimer;
    private boolean attacking;

    private float proximityGruntTimer;
    private final Random random = new Random();

    public Zote(float x, float y) {
        position.set(x, y);
        boundingBox.setSize(20, 24);
        setFacingRight(true);

        dialogues = new String[]{
            TranslationManager.get("zote.line0"),
            TranslationManager.get("zote.line1"),
            TranslationManager.get("zote.line2")
        };

        precepts = new String[]{
            TranslationManager.get("zote.precept1"),
            TranslationManager.get("zote.precept2"),
            TranslationManager.get("zote.precept3"),
            TranslationManager.get("zote.precept4"),
            TranslationManager.get("zote.precept5"),
            TranslationManager.get("zote.precept6"),
            TranslationManager.get("zote.precept7"),
            TranslationManager.get("zote.precept8")
        };

        dialogueState = DialogueState.MAIN;
        preceptIndex = 0;
        proximityGruntTimer = PROXIMITY_GRUNT_MIN + random.nextFloat() * (PROXIMITY_GRUNT_MAX - PROXIMITY_GRUNT_MIN);
    }

    @Override
    public void update(float delta) {
        boundingBox.setPosition(position);
        animState.advanceTime(delta);

        if (agitated) {
            agitationTimer -= delta;
            if (agitationTimer <= 0) {
                agitated = false;
                attacking = false;
            }
            if (attacking) {
                attackTimer -= delta;
                if (attackTimer <= 0)
                    attacking = false;
            }
        }
    }

    public void updateProximity(Vector2 knightPos, float delta) {
        if (!talking && isInRange(knightPos)) {
            proximityGruntTimer -= delta;
            if (proximityGruntTimer <= 0) {
                playRandomGrunt();
                proximityGruntTimer = PROXIMITY_GRUNT_MIN
                    + random.nextFloat() * (PROXIMITY_GRUNT_MAX - PROXIMITY_GRUNT_MIN);
            }
        } else {
            proximityGruntTimer = PROXIMITY_GRUNT_MIN
                + random.nextFloat() * (PROXIMITY_GRUNT_MAX - PROXIMITY_GRUNT_MIN);
        }
    }

    public void playRandomGrunt() {
        if (GameSettings.getInstance().isSfxMuted()) return;
        float vol = GameSettings.getInstance().getSfxVolume();
        Sound s = random.nextBoolean() ? GameAssetManager.zoteGrunt1 : GameAssetManager.zoteGrunt2;
        if (s != null) s.play(vol);
    }

    public ZoteAnimationType getAnimType() {
        if (attacking) return ZoteAnimationType.ATTACK;
        else if (talking) return ZoteAnimationType.TALK;
        else return ZoteAnimationType.IDLE;
    }
    public float getStateTime() { return animState.getStateTime(); }

    public boolean isInRange(Vector2 playerPos) {
        return Vector2.dst(position.x, position.y, playerPos.x, playerPos.y) < INTERACTION_RANGE;
    }

    public void interact() {
        talking = true;
        if (dialogueState == DialogueState.MAIN)
            dialogueIndex = 0;
        playRandomGrunt();
    }

    public void advanceDialogue() {
        if (dialogueState == DialogueState.MAIN) {
            dialogueIndex++;
            if (dialogueIndex >= dialogues.length) {
                dialogueState = DialogueState.PRECEPT;
                preceptIndex = 0;
                talking = false;
            }
        } else {
            preceptIndex = (preceptIndex + 1) % precepts.length;
            talking = false;
        }
        playRandomGrunt();
    }

    public String getCurrentDialogue() {
        if (dialogueState == DialogueState.MAIN)
            return dialogues[dialogueIndex];
        return precepts[preceptIndex];
    }

    public boolean isTalking() { return talking; }

    public void stopTalking() { talking = false; }

    public void takeDamage() {
        if (!agitated)
            agitationTimer = AGITATION_DURATION;
        agitated = true;
        attacking = true;
        attackTimer = ATTACK_DURATION;
    }

    public boolean isAgitated() { return agitated; }
    public boolean isAttacking() { return attacking; }
}
