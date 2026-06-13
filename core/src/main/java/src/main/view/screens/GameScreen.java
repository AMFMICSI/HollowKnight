package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import src.main.controller.GameController;
import src.main.model.Game;
import src.main.model.knight.Knight;

public class GameScreen extends AbstractScreen {
    private Game game;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private ShapeRenderer shapeRenderer;

    @Override
    public void show() {
        super.show();    // stage, skin, mainStack, modalStack, toastStack می‌سازه

        game = new Game();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        shapeRenderer = new ShapeRenderer();

        camera.setToOrtho(false, 800, 600);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new GameController(game, game.keyBindings));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        game.update(delta);
        camera.position.set(game.knight.getPosition(), 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        Knight knight = game.knight;
        TextureRegion frame = knight.getFrame(delta);
        float x = knight.getPosition().x;
        float y = knight.getPosition().y;

        batch.draw(frame, x, y,
            frame.getRegionWidth() / 2f, 0,
            frame.getRegionWidth(), frame.getRegionHeight(),
            knight.facingRight ? -1 : 1, 1, 0);

        batch.end();


// کف زمین
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(-2000, -5, 4000, 10);
        shapeRenderer.end();

// bounding box نایت
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        Knight k = game.knight;
        shapeRenderer.rect(k.getPosition().x, k.getPosition().y,
            frame.getRegionWidth(), frame.getRegionHeight());
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(0, 0, 0, 100);
        shapeRenderer.end();

        // Stage رو جدا رندر می‌کنه (UI دکمه‌ها + PauseModal)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
