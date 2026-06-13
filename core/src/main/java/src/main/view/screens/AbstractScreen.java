package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import src.main.view.GameAssetManager;

public abstract class AbstractScreen implements Screen {
    protected Stage stage;
    protected Skin skin;

    private Stack mainStack;
    protected Table rootTable;
    private Stack modalStack;
    private Stack toastStack;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;


    @Override
    public void show() {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("maps/map.tmx");
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(0.5f);
        stage = new Stage(viewport);
        skin = GameAssetManager.skin;

        mainStack = new Stack();
        mainStack.setFillParent(true);
        modalStack = new Stack();
        toastStack = new Stack();
        rootTable = new Table();
        mainStack.add(rootTable);
        mainStack.add(modalStack);
        mainStack.add(toastStack);


        stage.addActor(mainStack);

        Gdx.input.setInputProcessor(stage);
        stage.setDebugAll(false);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Stack getModalStack() {
        return modalStack;
    }

    public void openToast(String message){
        Table wrapper =  new Table();
        wrapper.pad(10).right().bottom();
        Table toast = new Table();
        toast.pad(5);
        toast.setBackground(skin.getDrawable("window"));

        Label messageLabel =   new Label(message, skin);

        toast.add(messageLabel).growX();

        wrapper.add(toast).minWidth(150);
        toastStack.add(wrapper);

        wrapper.setPosition(0, -150);
        wrapper.addAction(
            Actions.moveBy(0, 150, 0.5f, Interpolation.swingOut)
        );

        toast.setTouchable(Touchable.enabled);
        toast.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toast.addAction(
                    Actions.sequence(
                        Actions.alpha(0, 0.75f,  Interpolation.smoother),
                        Actions.run(() -> wrapper.remove())
                    )
                );
            }
        });
    }
}
