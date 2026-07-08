package src.main.view.ui.modal;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.model.Game;
import src.main.view.config.TranslationManager;
import src.main.view.manager.GameMusic;
import src.main.view.manager.UiManager;
import src.main.view.screens.MainMenuScreen;
import src.main.view.screens.StartGameMenuScreen;

public class EndGameModal extends Modal {
    public EndGameModal(Game.EndGameData data) {
        pad(16);
        defaults().space(8).center();

        Label title = new Label(TranslationManager.get("endgame.title"), skin);
        title.setFontScale(1.2f);
        add(title).colspan(2).row();

        add(new Label(TranslationManager.get("endgame.deaths") + " " + data.deathCount(), skin)).left().row();
        add(new Label(TranslationManager.get("endgame.killed") + " " + data.totalKilled(), skin)).left().row();
        int sec = (int) data.playTime();
        add(new Label(
            TranslationManager.get("endgame.time") + " " + (sec / 60) + ":" + String.format("%02d", sec % 60),
            skin)).left().row();

        TextButton restartBtn = new TextButton(TranslationManager.get("endgame.restart"), skin);
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                UiManager.setScreen(new StartGameMenuScreen());
            }
        });
        add(restartBtn).width(150).padTop(12);

        TextButton mainMenuBtn = new TextButton(TranslationManager.get("endgame.main_menu"), skin);
        mainMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                UiManager.setScreen(new MainMenuScreen());
            }
        });
        add(mainMenuBtn).width(150).padTop(12).row();

        GameMusic.BOSS_DEFEAT.play();
    }
}
