package src.main.view.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {
    public static Skin skin;

    public static void init(){
        skin  = new Skin(Gdx.files.internal("ui/uiskin.json"));

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
