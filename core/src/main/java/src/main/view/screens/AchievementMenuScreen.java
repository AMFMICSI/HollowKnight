package src.main.view.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.config.TranslationManager;
import src.main.view.manager.AchievementManager;
import src.main.view.manager.UiManager;

public class AchievementMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        applyThemeBackground();

        AchievementManager am = UiManager.achievements;

        Label title = new Label(TranslationManager.get("achievement.title"), skin);

        Table listTable = new Table();
        listTable.defaults().spaceBottom(6);

        for (AchievementManager.AchievementDef d : am.getLockedDefs()) {
            Label lbl = new Label(TranslationManager.get("achievement.locked") + " " + d.title, skin);
            lbl.setColor(Color.GRAY);
            listTable.add(lbl).row();
        }

        for (AchievementManager.AchievementDef d : am.getUnlockedDefs()) {
            Label lbl = new Label(TranslationManager.get("achievement.unlocked") + " " + d.title + " - " + d.description, skin);
            listTable.add(lbl).row();
        }

        if (am.getLockedDefs().isEmpty() && am.getUnlockedDefs().isEmpty()) {
            listTable.add(new Label(TranslationManager.get("achievement.none_defined"), skin)).row();
        }

        if (am.getUnlockedDefs().isEmpty()) {
            listTable.add(new Label(TranslationManager.get("achievement.none_unlocked"), skin)).padTop(10).row();
        }

        TextButton backBtn = new TextButton(TranslationManager.get("achievement.back"), skin);
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
