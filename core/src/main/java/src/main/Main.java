package src.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import src.main.view.world.GameAssetManager;
import src.main.view.world.UiManager;
import src.main.view.world.screens.MainMenuScreen;

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
