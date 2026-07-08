package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.config.TranslationManager;
import src.main.view.manager.GameMusic;
import src.main.view.manager.UiManager;
import src.main.view.screens.settings.SettingMenuScreen;

public class MainMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        applyThemeBackground();

        TextButton quitBtn = new TextButton(TranslationManager.get("menu.quit"), skin);
        TextButton settingsBtn = new TextButton(TranslationManager.get("menu.settings"), skin);
        TextButton guideBtn = new TextButton(TranslationManager.get("menu.guide"), skin);
        TextButton startBtn = new TextButton(TranslationManager.get("menu.start_game"), skin);
        TextButton achiveBtn = new TextButton(TranslationManager.get("menu.achievements"), skin);

        Table topRow = new Table();
        topRow.add(guideBtn).width(100).left();
        topRow.add(settingsBtn).width(100).expandX().right();

        rootTable.add(topRow).fillX().pad(10).row();

        Table centerMenu = new Table();
        centerMenu.defaults().width(200).spaceBottom(10);
        centerMenu.add(startBtn).row();
        centerMenu.add(achiveBtn).row();

        rootTable.add(centerMenu).expand().center().row();

        Table bottomRow = new Table();
        bottomRow.add(quitBtn).width(100).left();

        rootTable.add(bottomRow).fillX().pad(10);

        setupMenuPointer(quitBtn, settingsBtn, guideBtn, startBtn, achiveBtn);
        listeners(quitBtn, settingsBtn, guideBtn, startBtn, achiveBtn);
        GameMusic.MENU.play();
    }

    private void listeners(TextButton quitBtn, TextButton settingsBtn, TextButton guideBtn,
                           TextButton startBtn, TextButton achieveBtn) {
        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new SettingMenuScreen());
            }
        });
        guideBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new GuideMenuScreen());
            }
        });
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new StartGameMenuScreen());
            }
        });
        achieveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new AchievementMenuScreen());
            }
        });
    }
}
