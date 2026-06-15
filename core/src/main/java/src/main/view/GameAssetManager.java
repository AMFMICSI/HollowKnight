package src.main.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import src.main.model.entity.knight.KnightAnimationType;

import java.util.HashMap;
import java.util.Map;

public class GameAssetManager {
    public static Skin skin;
    public static Map<KnightAnimationType, Animation<TextureRegion>> knightAnimations;
    public static TextureAtlas knightAtlas;

    public static void init(){
        skin  = new Skin(Gdx.files.internal("ui/uiskin.json"));
        loadKnightAnimations();
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
}
