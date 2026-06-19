package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.GameMusic;
import src.main.view.UiManager;
import src.main.view.screens.settings.SettingMenuScreen;

public class MainMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        setBackground("menus/mainBackGround.png");

        TextButton quitBtn = new TextButton("Quit", skin);
        TextButton settingsBtn = new TextButton("Settings", skin);
        TextButton guideBtn = new TextButton("Guide", skin);
        TextButton startBtn = new TextButton("Start Game", skin);
        TextButton achiveBtn = new TextButton("Achievements", skin);

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

    private void listeners(TextButton quitBtn, TextButton settingsBtn, TextButton guideBtn, TextButton startBtn, TextButton achieveBtn) {
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
