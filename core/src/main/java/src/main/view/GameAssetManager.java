package src.main.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//import src.main.model.entity.enemy.HuskHornheadAnimationType;
import com.badlogic.gdx.utils.Array;
import src.main.model.entity.knight.KnightAnimationType;

import java.util.HashMap;
import java.util.Map;

public class GameAssetManager {
    public static Skin skin;
    public static Texture menuPointerLeft;
    public static Texture menuPointerRight;

    public static Map<KnightAnimationType, Animation<TextureRegion>> knightAnimations;
//    public static Map<HuskHornheadAnimationType, Animation<TextureRegion>> huskHornheadAnimations;

    public static TextureAtlas knightAtlas;
    private static final Array<Texture> enemyTextures = new Array<>();

    public static void init(){
        skin  = new Skin(Gdx.files.internal("ui/uiskin.json"));
        loadKnightAnimations();
//        loadHuskHornheadAnimations();
        loadMenuPointers();
    }


    public static void loadMenuPointers() {
        menuPointerLeft = new Texture(Gdx.files.internal(Phats.MenuPointerLeft.getText()));
        menuPointerRight = new Texture(Gdx.files.internal(Phats.MenuPointerRight.getText()));
    }

    public static void loadKnightAnimations(){
        knightAnimations = new HashMap<>();
        knightAtlas = new TextureAtlas(Gdx.files.internal("animation/knight.atlas"));

        for(KnightAnimationType type : KnightAnimationType.values()){
            TextureRegion[] frames = new TextureRegion[type.frameCount];

            for(int i = 0; i < type.frameCount; i++){
                String regionName = type.filePrefix + "_" + String.format("%03d", i);
                frames[i] = knightAtlas.findRegion(regionName);
            }

            Animation<TextureRegion> animation = new Animation<>(type.frameDuration, frames);
            animation.setPlayMode(type.playMode);
            knightAnimations.put(type, animation);
        }
        System.out.println("Loaded " + knightAnimations.size() + " KnightAnimations");
    }


//    public static void loadHuskHornheadAnimations() {
//        huskHornheadAnimations = new HashMap<>();
//        String base = "animation/Husk_Hornhead/";
//
//        for (HuskHornheadAnimationType type : HuskHornheadAnimationType.values()) {
//            TextureRegion[] frames = new TextureRegion[type.frameCount];
//            for (int i = 0; i < type.frameCount; i++) {
//                String fileName = base + type.filePrefix + "_" + String.format("%03d", i) + ".png";
//                frames[i] = new TextureRegion(new Texture(Gdx.files.internal(fileName)));
//            }
//            Animation<TextureRegion> anim = new Animation<>(type.frameDuration, frames);
//            anim.setPlayMode(type.playMode);
//            huskHornheadAnimations.put(type, anim);
//        }
//        System.out.println("Loaded " + huskHornheadAnimations.size() + " HuskHornhead animations");
//    }

    public static void dispose() {
        if (skin != null) skin.dispose();
        if (knightAtlas != null) knightAtlas.dispose();
        if (menuPointerLeft != null) menuPointerLeft.dispose();
        if (menuPointerRight != null) menuPointerRight.dispose();
        for (Texture tex : enemyTextures) {
            if (tex != null) tex.dispose();
        }
        enemyTextures.clear();
    }
}
