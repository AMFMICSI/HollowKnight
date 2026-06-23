package src.main.model.entity.npc.zote;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import src.main.model.entity.Entity;
import src.main.model.entity.animation.AnimationSet;
import src.main.view.GameAssetManager;
import src.main.view.GameSettings;

import java.util.Random;

public class Zote extends Entity {
    private static final float INTERACTION_RANGE = 50f;
    private static final float PROXIMITY_GRUNT_MIN = 3f;
    private static final float PROXIMITY_GRUNT_MAX = 7f;
    private static final float ATTACK_DURATION = 0.4f;
    private static final float AGITATION_DURATION = 3f;

    private enum DialogueState { MAIN, PRECEPT }

    private final AnimationSet<ZoteAnimationType> animSet;
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
        animSet = new AnimationSet<>(GameAssetManager.zoteAnimations, ZoteAnimationType.IDLE);
        setFacingRight(true);

        dialogues = new String[]{
            "You dare approach me? I am Zote the Mighty!",
            "I, Zote, will be the one to defeat the Radiance!",
            "Stand aside, lest you be crushed by my greatness!"
        };

        precepts = new String[]{
            "Precept One: Always win your battles.",
            "Precept Two: Never let them laugh at you.",
            "Precept Three: If you are stronger, protect the weak.",
            "Precept Four: Remember your dreams.",
            "Precept Five: Do not eat immediately before sleeping.",
            "Precept Six: Strength has many forms.",
            "Precept Seven: Let dreams be your guide.",
            "Precept Eight: There is no shame in a quiet life."
        };

        dialogueState = DialogueState.MAIN;
        preceptIndex = 0;
        proximityGruntTimer = PROXIMITY_GRUNT_MIN + random.nextFloat() * (PROXIMITY_GRUNT_MAX - PROXIMITY_GRUNT_MIN);
    }

    @Override
    public void update(float delta) {
        boundingBox.setPosition(position);

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
                proximityGruntTimer = PROXIMITY_GRUNT_MIN + random.nextFloat() * (PROXIMITY_GRUNT_MAX - PROXIMITY_GRUNT_MIN);
            }
        } else {
            proximityGruntTimer = PROXIMITY_GRUNT_MIN + random.nextFloat() * (PROXIMITY_GRUNT_MAX - PROXIMITY_GRUNT_MIN);
        }
    }

    public void playRandomGrunt() {
        if (GameSettings.getInstance().isSfxMuted()) return;
        float vol = GameSettings.getInstance().getSfxVolume();
        Sound s = random.nextBoolean() ? GameAssetManager.zoteGrunt1 : GameAssetManager.zoteGrunt2;
        if (s != null) s.play(vol);
    }

    @Override
    public TextureRegion getFrame(float delta) {
        if (attacking)
            animSet.setAnimation(ZoteAnimationType.ATTACK);
        else if (talking)
            animSet.setAnimation(ZoteAnimationType.TALK);
        else
            animSet.setAnimation(ZoteAnimationType.IDLE);
        return animSet.getFrame(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        TextureRegion frame = getFrame(delta);
        float spriteW = boundingBox.width * DRAW_SCALE;
        float spriteH = spriteW * frame.getRegionHeight() / (float) frame.getRegionWidth();
        batch.draw(frame,
            boundingBox.x + (boundingBox.width - spriteW) / 2f,
            boundingBox.y,
            spriteW / 2f, 0,
            spriteW, spriteH,
            isFacingRight() ? -1 : 1, 1, 0);
    }

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
}
