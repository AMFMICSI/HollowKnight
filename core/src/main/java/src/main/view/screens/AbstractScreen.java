package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import src.main.view.GameAssetManager;
import src.main.view.GameSettings;

public abstract class AbstractScreen implements Screen {
    protected Stage stage;
    protected Skin skin;

    private Stack mainStack;
    protected Table rootTable;
    private Stack modalStack;
    private Stack toastStack;

    protected Image backgroundImage;
    private Texture backgroundTexture;

    @Override
    public void show() {
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
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    public Stack getModalStack() {
        return modalStack;
    }

    public void openToast(String message){
        Table wrapper = new Table();
        wrapper.pad(10).right().bottom();
        Table toast = new Table();
        toast.pad(5);
        toast.setBackground(skin.getDrawable("bgDark"));

        Label messageLabel = new Label(message, skin);

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
                wrapper.clearActions();
                toast.addAction(
                    Actions.sequence(
                        Actions.alpha(0, 0.75f, Interpolation.smoother),
                        Actions.run(wrapper::remove)
                    )
                );
            }
        });

        wrapper.addAction(
            Actions.sequence(
                Actions.delay(2f),
                Actions.run(() -> {
                    if (toast.getColor().a > 0) {
                        toast.addAction(
                            Actions.sequence(
                                Actions.alpha(0, 0.75f, Interpolation.smoother),
                                Actions.run(wrapper::remove)
                            )
                        );
                    }
                })
            )
        );
    }

    protected void applyThemeBackground() {
        setBackground(GameSettings.getInstance().getThemePath());
    }

    protected void setBackground(String assetPath) {
        if (backgroundTexture != null) backgroundTexture.dispose();
        backgroundTexture = new Texture(Gdx.files.internal(assetPath));
        if (backgroundImage != null) backgroundImage.remove();
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.stretch);
        backgroundImage.setFillParent(true);
        mainStack.addActorAt(0, backgroundImage);
    }

    protected void setupMenuPointer(TextButton... buttons) {
        Image leftB = new Image(GameAssetManager.menuPointerLeft);
        Image rightB = new Image(GameAssetManager.menuPointerRight);

        leftB.setVisible(false);
        rightB.setVisible(false);
        mainStack.add(leftB);
        mainStack.add(rightB);

        for(TextButton button : buttons) {
            button.addListener(new ClickListener(){
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    float pW = leftB.getPrefWidth();
                    float pH = leftB.getPrefHeight();
                    leftB.setSize(pW, pH);
                    rightB.setSize(pW, pH);

                    Vector2 pos = button.localToStageCoordinates(new Vector2(0, 0));
                    pos = mainStack.stageToLocalCoordinates(pos);

                    float centeredY = pos.y + (button.getHeight() - pH) / 2f;

                    leftB.setPosition(pos.x - pW - 5, centeredY);
                    rightB.setPosition(pos.x + button.getWidth() + 5, centeredY);

                    leftB.setVisible(true);
                    rightB.setVisible(true);
                }
                @Override
                public void exit(InputEvent e, float x, float y, int p, Actor to) {
                    leftB.setVisible(false);
                    rightB.setVisible(false);
                }
            });
        }
    }

}
