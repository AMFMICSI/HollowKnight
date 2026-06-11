package src.main.view.world.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.world.UiManager;

public class MainMenuScreen extends AbstractScreen{

    @Override
    public void show() {
        super.show();
        Stack stack = new Stack();

        Table quitBtnWrapper = new Table();
        quitBtnWrapper.bottom().left().pad(10);
        TextButton quitBtn = new TextButton("Quit", skin);
        quitBtnWrapper.add(quitBtn).width(100);

        Table settingsBtnWrapper = new Table();
        settingsBtnWrapper.top().right().pad(10);
        TextButton settingsBtn = new TextButton("Settings", skin);
        settingsBtnWrapper.add(settingsBtn).width(100);

        Table guideBtnWrapper = new Table();
        guideBtnWrapper.top().left().pad(10);
        TextButton guideBtn = new TextButton("Guide", skin);
        guideBtnWrapper.add(guideBtn).width(100);

        Table playBtnsWrapper = new Table();
        playBtnsWrapper.center().pad(10);
        playBtnsWrapper.defaults().width(200).spaceBottom(10);
        TextButton startBtn = new TextButton("Start Game", skin);
        TextButton achiveBtn = new TextButton("Achievements", skin);
        playBtnsWrapper.add(startBtn).row();
        playBtnsWrapper.add(achiveBtn).row();

        stack.add(quitBtnWrapper);
        stack.add(settingsBtnWrapper);
        stack.add(guideBtnWrapper);
        stack.add(playBtnsWrapper);

        rootTable.add(stack).grow();

        Listeners(quitBtn, settingsBtn, guideBtn, startBtn, achiveBtn);
    }

    private void Listeners(TextButton quitBtn, TextButton settingsBtn, TextButton guideBtn, TextButton startBtn, TextButton achiveBtn) {
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
        achiveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new AchievementMenuScreen());
            }
        });
    }
}
