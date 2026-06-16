package src.main.view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.UiManager;

public class StartGameMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        setBackground("menus/mainBackGround.png");
        Label title = new Label("New Game", skin);
        TextButton newGameBtn = new TextButton("New Game", skin);
        TextButton loadGameBtn = new TextButton("Load Game", skin);
        TextButton backBtn = new TextButton("Back", skin);

        Table centerTable = new Table();
        centerTable.setFillParent(true);
        centerTable.center();
        centerTable.defaults().width(200).spaceBottom(10);
        centerTable.add(title).padBottom(20).row();
        centerTable.add(newGameBtn).row();
        centerTable.add(loadGameBtn).row();
        centerTable.add(backBtn).row();

        rootTable.add(centerTable).grow();
        setupMenuPointer(newGameBtn, loadGameBtn, backBtn);
        newGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new GameScreen());
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        loadGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openToast("No save files found!");
            }
        });
    }
}
