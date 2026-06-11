package src.main.view.world.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.world.UiManager;

public class GameScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();

        Label title = new Label("Game Screen", skin);
        Label hint = new Label("Press ESC or click Back to return", skin);
        TextButton backBtn = new TextButton("Back", skin);

        Table centerTable = new Table();
        centerTable.setFillParent(true);
        centerTable.center();
        centerTable.add(title).padBottom(20).row();
        centerTable.add(hint).padBottom(20).row();
        centerTable.add(backBtn).width(200);

        rootTable.add(centerTable).grow();

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            UiManager.setScreen(new MainMenuScreen());
        }
    }
}
