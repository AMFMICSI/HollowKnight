package src.main.view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.Phats;
import src.main.view.UiManager;

public class StartGameMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        setBackground(Phats.MainBackGround.getText());

        Label title = new Label("New Game", skin);
        TextButton newGameBtn = new TextButton("New Game", skin);
        TextButton loadGameBtn = new TextButton("Load Game", skin);
        TextButton backBtn = new TextButton("Back", skin);

        Table centerMenu = new Table();
        centerMenu.defaults().width(200).spaceBottom(10);
        centerMenu.add(title).padBottom(20).row();
        centerMenu.add(newGameBtn).row();
        centerMenu.add(loadGameBtn).row();
        centerMenu.add(backBtn).row();

        rootTable.add(centerMenu).expand().center();

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
