package src.main.view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.model.Game;
import src.main.model.data.SaveData;
import src.main.view.UiManager;

public class StartGameMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        applyThemeBackground();

        Label title = new Label("Select Slot", skin);

        Table slotTable = new Table();
        slotTable.defaults().width(220).spaceBottom(8);

        for (int i = 0; i < 4; i++) {
            int slot = i;
            boolean exists = SaveData.slotExists(i);
            String label = exists ? "Slot " + (i+1) + " (Continue)" : "Slot " + (i+1) + " (Empty)";
            TextButton btn = new TextButton(label, skin);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SaveData data = SaveData.load(slot);
                    UiManager.achievements = new src.main.view.AchievementManager();
                    Game game;
                    if (data != null) {
                        game = new Game(slot, data);
                    } else {
                        game = new Game();
                        game.setSaveSlot(slot);
                    }
                    UiManager.setScreen(new GameScreen(game));
                }
            });
            Table row = new Table();
            row.add(btn).width(200);
            if (exists) {
                TextButton delBtn = new TextButton("X", skin);
                delBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        SaveData.deleteSlot(slot);
                        UiManager.setScreen(new StartGameMenuScreen());
                    }
                });
                row.add(delBtn).width(30).padLeft(4);
            } else {
                row.add().width(34);
            }
            slotTable.add(row).row();
        }

        TextButton newGameBtn = new TextButton("New Game", skin);
        newGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (int i = 0; i < 4; i++) {
                    if (!SaveData.slotExists(i)) {
                        UiManager.achievements = new src.main.view.AchievementManager();
                        Game game = new Game();
                        game.setSaveSlot(i);
                        UiManager.setScreen(new GameScreen(game));
                        return;
                    }
                }
                openToast("All slots are full!");
            }
        });

        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        Table centerMenu = new Table();
        centerMenu.defaults().width(200).spaceBottom(10);
        centerMenu.add(title).padBottom(20).row();
        centerMenu.add(slotTable).row();
        centerMenu.add(newGameBtn).row();
        centerMenu.add(backBtn).row();

        rootTable.add(centerMenu).expand().center();

        setupMenuPointer(newGameBtn, backBtn);
    }
}
