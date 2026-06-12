package src.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.ScreenUtils;
import src.main.view.GameAssetManager;
import src.main.view.UiManager;
import src.main.view.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.Game} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create() {
        UiManager.init(this);
        GameAssetManager.init();

        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        setScreen(mainMenuScreen);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        super.render();
    }

    @Override
    public void dispose() {
    }
}
