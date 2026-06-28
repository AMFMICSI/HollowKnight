package src.main.view;

import com.badlogic.gdx.Screen;
import src.main.Main;
import src.main.view.screens.AbstractScreen;

public class UiManager {
    private static Main main;
    public static AchievementManager achievements = new AchievementManager();

    public static void init(Main main) {
        UiManager.main = main;
    }

    public static void setScreen(Screen screen) {
        main.setScreen(screen);
    }

    public static void switchScreen(Screen screen) {
        main.switchScreen(screen);
    }

    public static AbstractScreen getScreen(){
        if(main.getScreen() instanceof AbstractScreen abstractScreen){
            return abstractScreen;
        }
        return null;
    }
}
