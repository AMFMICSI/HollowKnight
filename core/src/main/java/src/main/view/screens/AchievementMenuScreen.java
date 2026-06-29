package src.main.view.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.AchievementManager;
import src.main.view.UiManager;

public class AchievementMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        applyThemeBackground();

        AchievementManager am = UiManager.achievements;

        Label title = new Label("Achievements", skin);

        Table listTable = new Table();
        listTable.defaults().spaceBottom(6);

        for (AchievementManager.AchievementDef d : am.getLockedDefs()) {
            Label lbl = new Label("[LOCKED] " + d.title, skin);
            lbl.setColor(Color.GRAY);
            listTable.add(lbl).row();
        }

        for (AchievementManager.AchievementDef d : am.getUnlockedDefs()) {
            Label lbl = new Label("[UNLOCKED] " + d.title + " - " + d.description, skin);
            listTable.add(lbl).row();
        }

        if (am.getLockedDefs().isEmpty() && am.getUnlockedDefs().isEmpty()) {
            listTable.add(new Label("No achievements defined!", skin)).row();
        }

        if (am.getUnlockedDefs().isEmpty()) {
            listTable.add(new Label("No achievements unlocked yet!", skin)).padTop(10).row();
        }

        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        Table centerMenu = new Table();
        centerMenu.add(title).padBottom(20).row();
        centerMenu.add(listTable).padBottom(20).row();
        centerMenu.add(backBtn).width(200);

        rootTable.add(centerMenu).expand().center();

        setupMenuPointer(backBtn);
    }
}
