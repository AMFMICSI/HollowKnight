package src.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import src.main.view.GameAssetManager;
import src.main.view.GameSettings;
import src.main.view.UiManager;
import src.main.view.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.Game} implementation shared by all platforms. */
public class Main extends Game {

    private SpriteBatch batch;
    private Texture pixel;

    @Override
    public void create() {
        UiManager.init(this);
        GameAssetManager.init();
        setCursor();
        brightnessOverlay();
        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        setScreen(mainMenuScreen);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        super.render();
        renderBrightness();
    }

    @Override
    public void dispose() {
        super.dispose();
        screen.dispose();
        GameAssetManager.dispose();
        if (pixel != null) pixel.dispose();
        if (batch != null) batch.dispose();
    }

    private void setCursor(){
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor/Cursor.png"));
        Cursor cursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(cursor);
        pixmap.dispose();
    }

    private void brightnessOverlay(){
        batch = new SpriteBatch();
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.drawPixel(0, 0);
        pixel = new Texture(pix);
        pix.dispose();
    }

    private void renderBrightness(){
        float brightness = GameSettings.getInstance().getBrightness();
        if (brightness < 1f) {
            batch.begin();
            batch.setColor(0, 0, 0, 1 - brightness);
            batch.draw(pixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            batch.end();
        }
    }
}
