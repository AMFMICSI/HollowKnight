package src.main.view.actors.modal;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.model.Game;
import src.main.view.UiManager;
import src.main.view.screens.MainMenuScreen;
import src.main.view.screens.settings.SettingMenuScreen;
import src.main.view.screens.GameScreen;

import static src.main.view.screens.GameScreen.getCurrentInstance;

public class PauseModal extends Modal {
    private final Game game;

    public PauseModal(Game game) {
        super();
        this.game = game;

        TextButton resumeBtn = new TextButton("Resume", skin);
        TextButton settingsBtn = new TextButton("Settings", skin);
        TextButton saveQuitBtn = new TextButton("Save & Quit", skin);
        TextButton exitBtn = new TextButton("Exit", skin);

        defaults().space(5);
        add(resumeBtn).width(120).row();
        add(settingsBtn).width(120).row();
        add(saveQuitBtn).width(120).row();
        add(exitBtn).width(120).row();

        resumeBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onResume();
            }
        });

        settingsBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                UiManager.switchScreen(new SettingMenuScreen(() -> UiManager.setScreen(getCurrentInstance())));
            }
        });

        saveQuitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game != null) game.saveGame();
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        exitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onExit();
            }
        });
    }

    public void onExit(){ }

    public void onResume(){ }
}
