package src.main.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import src.main.model.knight.KnightAnimationType;

import java.util.HashMap;
import java.util.Map;

public class GameAssetManager {
    public static Skin skin;
    public static Map<KnightAnimationType, Animation<TextureRegion>> knightAnimations;
    public static void init(){
        skin  = new Skin(Gdx.files.internal("ui/uiskin.json"));
        loadKnightAnimations();
    }

    public static void loadKnightAnimations(){
        knightAnimations = new HashMap<>();

        for(KnightAnimationType type : KnightAnimationType.values()){
            TextureRegion[] frames = new TextureRegion[type.frameCount];

            for(int i = 0; i < type.frameCount; i++){
                String filename = "animation/" + type.filePrefix + "_" + String.format("%03d", i) + ".png";
                frames[i] = new TextureRegion(new Texture(Gdx.files.internal(filename)));
            }

            Animation<TextureRegion> animation = new Animation<>(type.frameDuration, frames);
            animation.setPlayMode(type.playMode);
            knightAnimations.put(type, animation);
        }
        System.out.println("Loaded " + knightAnimations.size() + " KnightAnimations");
    }
//
//    public static void loadAnimation(AnimationType animationType){
//        Texture texture = new Texture(animationType.path);
//
//        TextureRegion[][] split = TextureRegion.split(
//            texture,
//            texture.getWidth() / animationType.colCount,
//            texture.getHeight() / animationType.rowCount
//        );
//
//        int frameCount = animationType.frameCount;
//        TextureRegion[] animationFrames = new TextureRegion[frameCount];
//
//        int cols = split[0].length;
//
//        for(int i = 0 ; i < frameCount; i++){
//            int row  =  i / cols;
//            int col = i % cols;
//            animationFrames[i] = split[row][col];
//        }
//
//        Animation<TextureRegion> animation = new Animation<>(1/30f, animationFrames);
//        animation.setPlayMode(Animation.PlayMode.LOOP);
//
//        animationMap.put(animationType, animation);
//    }
}
