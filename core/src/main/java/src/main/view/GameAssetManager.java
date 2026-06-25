package src.main.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import src.main.model.entity.enemy.constantEnemy.crystalGuardian.CrystalGuardianAnimationType;
import src.main.model.entity.enemy.groundEnemy.huskHornhead.HuskHornheadAnimationType;
import src.main.model.entity.enemy.flyingEnemy.crystalHunter.CrystalHunterAnimationType;
import src.main.model.entity.enemy.groundEnemy.crawlid.CrawlidAnimationType;
import src.main.model.entity.enemy.boss.falseKnight.FalseKnightAnimationType;
import src.main.model.entity.knight.KnightAnimationType;
import src.main.model.entity.animation.AnimationType;
import src.main.model.entity.hud.SoulFillStage;
import src.main.model.entity.npc.zote.ZoteAnimationType;

import java.util.HashMap;
import java.util.Map;

public class GameAssetManager {
    public static Skin skin;
    public static Texture menuPointerLeft;
    public static Texture menuPointerRight;

    private static TextureAtlas knightAtlas;
    public static Map<KnightAnimationType, Animation<TextureRegion>> knightAnimations;
    private static TextureAtlas crawlidAtlas;
    public static Map<CrawlidAnimationType, Animation<TextureRegion>> crawlidAnimations;
    private static TextureAtlas crystalHunterAtlas;
    public static Map<CrystalHunterAnimationType, Animation<TextureRegion>> crystalHunterAnimations;
    public static TextureRegion crystalProjectileRegion;
    private static TextureAtlas huskHornheadAtlas;
    public static Map<HuskHornheadAnimationType, Animation<TextureRegion>> huskHornheadAnimations;
    private static TextureAtlas crystalGuardianAtlas;
    public static Map<CrystalGuardianAnimationType, Animation<TextureRegion>> crystalGuardianAnimations;
    public static TextureRegion laserRegion;
    public static Animation<TextureRegion> vengefulProjectileAnim;
    public static Animation<TextureRegion> wraithsAoeAnim;
    public static Animation<TextureRegion> shadowProjectileAnim;
    public static Animation<TextureRegion> shadowScreamAnim;
    public static Animation<TextureRegion> dashEffectAnim;
    private static TextureAtlas effectsAtlas;
    private static TextureAtlas charmsAtlas;
    private static TextureAtlas falseKnightAtlas;
    public static Map<FalseKnightAnimationType, Animation<TextureRegion>> falseKnightAnimations;
    private static TextureAtlas hudAtlas;
    public static Map<SoulFillStage, Animation<TextureRegion>> soulFillAnimations;
    public static Sound zoteGrunt1;
    public static Sound zoteGrunt2;
    public static Sound charmClickSound;
    public static Texture soulCatcherIcon;
    public static Texture unbreakableStrengthIcon;
    public static Texture quickSlashIcon;
    public static Texture heavyBlowIcon;
    public static Texture quickFocusIcon;
    public static Texture dashmasterIcon;
    public static Texture sharpShadowIcon;
    public static Texture voidHeartIcon;
    public static Texture charmNotch;
    public static Texture notchLit;
    public static Texture notchUnlit;
    private static TextureAtlas zoteAtlas;
    public static Map<ZoteAnimationType, Animation<TextureRegion>> zoteAnimations;

    public static void init(){
        skin  = new Skin(Gdx.files.internal("ui/uiskin.json"));
        loadKnightAnimations();
        loadSpellTextures();
        loadCrawlidAnimations();
        loadCrystalHunterAnimations();
        loadHuskHornheadAnimations();
        loadCrystalGuardianAnimations();
        loadFalseKnightAnimations();
        loadMenuPointers();
        loadHudAtlas();
        loadSoulAnimations();
        loadZoteAnimations();
        loadZoteSounds();
        loadCharmTextures();
        loadCharmSounds();
    }


    public static void loadMenuPointers() {
        menuPointerLeft = new Texture(Gdx.files.internal(Phats.MenuPointerLeft.getText()));
        menuPointerRight = new Texture(Gdx.files.internal(Phats.MenuPointerRight.getText()));
    }

    private static <T extends Enum<T> & AnimationType> Map<T, Animation<TextureRegion>> loadAnimations(
            TextureAtlas atlas, T[] types, String format) {
        Map<T, Animation<TextureRegion>> map = new HashMap<>();
        for (T type : types) {
            TextureRegion[] frames = new TextureRegion[type.getFrameCount()];
            for (int i = 0; i < type.getFrameCount(); i++) {
                frames[i] = atlas.findRegion(String.format(format, type.getFilePrefix(), i));
            }
            Animation<TextureRegion> anim = new Animation<>(type.getFrameDuration(), frames);
            anim.setPlayMode(type.getPlayMode());
            map.put(type, anim);
        }
        return map;
    }

    public static void loadKnightAnimations(){
        knightAtlas = new TextureAtlas(Gdx.files.internal("atlases/knight.atlas"));
        knightAnimations = loadAnimations(knightAtlas, KnightAnimationType.values(), "%s_%03d");
    }

    public static void loadSpellTextures() {
        effectsAtlas = new TextureAtlas(Gdx.files.internal("atlases/effects.atlas"));

        TextureRegion[] vFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++)
            vFrames[i] = effectsAtlas.findRegion("SoulBall_" + String.format("%03d", i));
        vengefulProjectileAnim = new Animation<>(0.1f, vFrames);
        vengefulProjectileAnim.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] wFrames = new TextureRegion[13];
        for (int i = 0; i < 13; i++)
            wFrames[i] = effectsAtlas.findRegion("SoulScream_" + String.format("%03d", i));
        wraithsAoeAnim = new Animation<>(0.05f, wFrames);
        wraithsAoeAnim.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] spFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++)
            spFrames[i] = effectsAtlas.findRegion("ShadowBall_" + String.format("%03d", i));
        shadowProjectileAnim = new Animation<>(0.1f, spFrames);
        shadowProjectileAnim.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] ssFrames = new TextureRegion[13];
        for (int i = 0; i < 13; i++)
            ssFrames[i] = effectsAtlas.findRegion("ShadowScream_" + String.format("%03d", i));
        shadowScreamAnim = new Animation<>(0.05f, ssFrames);
        shadowScreamAnim.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] deFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++)
            deFrames[i] = effectsAtlas.findRegion("DashEffect_" + String.format("%03d", i));
        dashEffectAnim = new Animation<>(0.05f, deFrames);
        dashEffectAnim.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public static void loadCharmTextures() {
        soulCatcherIcon    = new Texture(Gdx.files.internal("charms/soul_catcher.png"));
        unbreakableStrengthIcon = new Texture(Gdx.files.internal("charms/unbreakable_strength.png"));
        quickSlashIcon     = new Texture(Gdx.files.internal("charms/quick_slash.png"));
        heavyBlowIcon      = new Texture(Gdx.files.internal("charms/heavy_blow.png"));
        quickFocusIcon     = new Texture(Gdx.files.internal("charms/quick_focus.png"));
        dashmasterIcon     = new Texture(Gdx.files.internal("charms/dashmaster.png"));
        sharpShadowIcon    = new Texture(Gdx.files.internal("charms/sharp_shadow.png"));
        voidHeartIcon      = new Texture(Gdx.files.internal("charms/void_heart.png"));
        charmNotch = new Texture(Gdx.files.internal("charms/charm_notch.png"));
        notchLit   = new Texture(Gdx.files.internal("charms/notch_lit.png"));
        notchUnlit = new Texture(Gdx.files.internal("charms/notch_unlit.png"));
    }

    public static void loadCharmSounds() {
        charmClickSound = Gdx.audio.newSound(Gdx.files.internal("music/charm_click_in.wav"));
    }

    public static void loadCrawlidAnimations() {
        crawlidAtlas = new TextureAtlas(Gdx.files.internal("atlases/crawlid.atlas"));
        crawlidAnimations = loadAnimations(crawlidAtlas, CrawlidAnimationType.values(), "%s_%03d");
    }

    public static void loadCrystalHunterAnimations() {
        crystalHunterAtlas = new TextureAtlas(Gdx.files.internal("atlases/crystalHunter.atlas"));
        crystalHunterAnimations = loadAnimations(crystalHunterAtlas, CrystalHunterAnimationType.values(), "%s_%03d");
        crystalProjectileRegion = crystalHunterAtlas.findRegion("Crystal_000");
    }

    public static void loadHuskHornheadAnimations() {
        huskHornheadAtlas = new TextureAtlas(Gdx.files.internal("atlases/huskHornhead.atlas"));
        huskHornheadAnimations = loadAnimations(huskHornheadAtlas, HuskHornheadAnimationType.values(), "%s_%03d");
    }

    public static void loadCrystalGuardianAnimations() {
        crystalGuardianAtlas = new TextureAtlas(Gdx.files.internal("atlases/crystalGuardian.atlas"));
        crystalGuardianAnimations = loadAnimations(crystalGuardianAtlas, CrystalGuardianAnimationType.values(), "%s_%03d");
        laserRegion = effectsAtlas.findRegion("CrystalLaser");
    }

    public static void loadFalseKnightAnimations() {
        falseKnightAtlas = new TextureAtlas(Gdx.files.internal("atlases/falseKnight.atlas"));
        falseKnightAnimations = loadAnimations(falseKnightAtlas, FalseKnightAnimationType.values(), "%s_%03d");
    }

    public static void loadZoteAnimations() {
        zoteAtlas = new TextureAtlas(Gdx.files.internal("atlases/zote.atlas"));
        zoteAnimations = loadAnimations(zoteAtlas, ZoteAnimationType.values(), "%s_%03d");
    }

    public static void loadZoteSounds() {
        zoteGrunt1 = Gdx.audio.newSound(Gdx.files.internal("music/Brumm_grunt_01.wav"));
        zoteGrunt2 = Gdx.audio.newSound(Gdx.files.internal("music/Brumm_grunt_double.wav"));
    }

    public static void loadHudAtlas() {
        hudAtlas = new TextureAtlas(Gdx.files.internal("atlases/hud.atlas"));
    }

    public static void loadSoulAnimations() {
        soulFillAnimations = new HashMap<>();
        for (SoulFillStage stage : SoulFillStage.values()) {
            TextureRegion[] frames = new TextureRegion[stage.frameCount];
            for (int i = 0; i < stage.frameCount; i++) {
                String name = stage.filePrefix + "_" + (stage.frameStart + i);
                frames[i] = hudAtlas.findRegion(name);
            }
            Animation<TextureRegion> anim = new Animation<>(stage.frameDuration, frames);
            anim.setPlayMode(stage.playMode);
            soulFillAnimations.put(stage, anim);
        }
    }

    public static TextureRegion getHudRegion(String name) {
        return hudAtlas.findRegion(name);
    }

    public static void dispose() {
        if (skin != null) skin.dispose();
        if (knightAtlas != null) knightAtlas.dispose();
        if (menuPointerLeft != null) menuPointerLeft.dispose();
        if (menuPointerRight != null) menuPointerRight.dispose();
        if(crawlidAtlas != null) crawlidAtlas.dispose();
        if(crystalHunterAtlas != null) crystalHunterAtlas.dispose();
        if(huskHornheadAtlas != null) huskHornheadAtlas.dispose();
        if(crystalGuardianAtlas != null) crystalGuardianAtlas.dispose();
        if(effectsAtlas != null) effectsAtlas.dispose();
        if(charmsAtlas != null) charmsAtlas.dispose();
        if(charmClickSound != null) charmClickSound.dispose();
        if(soulCatcherIcon != null) soulCatcherIcon.dispose();
        if(unbreakableStrengthIcon != null) unbreakableStrengthIcon.dispose();
        if(quickSlashIcon != null) quickSlashIcon.dispose();
        if(heavyBlowIcon != null) heavyBlowIcon.dispose();
        if(quickFocusIcon != null) quickFocusIcon.dispose();
        if(dashmasterIcon != null) dashmasterIcon.dispose();
        if(sharpShadowIcon != null) sharpShadowIcon.dispose();
        if(voidHeartIcon != null) voidHeartIcon.dispose();
        if(charmNotch != null) charmNotch.dispose();
        if(notchLit != null) notchLit.dispose();
        if(notchUnlit != null) notchUnlit.dispose();
        if(falseKnightAtlas != null) falseKnightAtlas.dispose();
        if(hudAtlas != null) hudAtlas.dispose();
        if (zoteAtlas != null) zoteAtlas.dispose();
        if (zoteGrunt1 != null) zoteGrunt1.dispose();
        if (zoteGrunt2 != null) zoteGrunt2.dispose();
    }
}
