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
    private static Texture vengefulProjectileTex;
    private static Texture wraithsAoeTex;
    private static TextureAtlas falseKnightAtlas;
    public static Map<FalseKnightAnimationType, Animation<TextureRegion>> falseKnightAnimations;
    private static TextureAtlas hudAtlas;
    public static Map<SoulFillStage, Animation<TextureRegion>> soulFillAnimations;
    public static Sound zoteGrunt1;
    public static Sound zoteGrunt2;
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
        vengefulProjectileTex = new Texture(Gdx.files.internal("animation/Projectile/SoulBall.png"));
        int vfw = vengefulProjectileTex.getWidth() / 4;
        int vfh = vengefulProjectileTex.getHeight();
        TextureRegion[] vFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++)
            vFrames[i] = new TextureRegion(vengefulProjectileTex, i * vfw, 0, vfw, vfh);
        vengefulProjectileAnim = new Animation<>(0.1f, vFrames);
        vengefulProjectileAnim.setPlayMode(Animation.PlayMode.LOOP);

        wraithsAoeTex = new Texture(Gdx.files.internal("animation/Effects/SoulScream.png"));
        int wfw = wraithsAoeTex.getWidth() / 13;
        int wfh = wraithsAoeTex.getHeight();
        TextureRegion[] wFrames = new TextureRegion[13];
        for (int i = 0; i < 13; i++)
            wFrames[i] = new TextureRegion(wraithsAoeTex, i * wfw, 0, wfw, wfh);
        wraithsAoeAnim = new Animation<>(0.05f, wFrames);
        wraithsAoeAnim.setPlayMode(Animation.PlayMode.NORMAL);
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
        laserRegion = new TextureRegion(new Texture(Gdx.files.internal("animation/Crystallized/atlas0 #25304.png")));
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
        if(laserRegion != null) laserRegion.getTexture().dispose();
        if(vengefulProjectileTex != null) vengefulProjectileTex.dispose();
        if(wraithsAoeTex != null) wraithsAoeTex.dispose();
        if(falseKnightAtlas != null) falseKnightAtlas.dispose();
        if(hudAtlas != null) hudAtlas.dispose();
        if (zoteAtlas != null) zoteAtlas.dispose();
        if (zoteGrunt1 != null) zoteGrunt1.dispose();
        if (zoteGrunt2 != null) zoteGrunt2.dispose();
    }
}
