package src.main.view.ui.modal;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.model.Game;
import src.main.model.data.KeyBindings;
import src.main.view.config.TranslationManager;
import src.main.view.manager.UiManager;
import src.main.view.screens.MainMenuScreen;
import src.main.view.screens.settings.SettingMenuScreen;

import static src.main.view.screens.GameScreen.getCurrentInstance;

public class PauseModal extends Modal {
    private final Game game;

    public PauseModal(Game game) {
        super();
        this.game = game;

        TextButton resumeBtn = new TextButton(TranslationManager.get("pause.resume"), skin);
        TextButton settingsBtn = new TextButton(TranslationManager.get("pause.settings"), skin);
        TextButton saveQuitBtn = new TextButton(TranslationManager.get("pause.save_quit"), skin);
        TextButton exitBtn = new TextButton(TranslationManager.get("pause.exit"), skin);

        defaults().space(5);
        add(resumeBtn).width(120).row();
        add(settingsBtn).width(120).row();
        add(saveQuitBtn).width(120).row();
        add(exitBtn).width(120).row();

        Label sep = new Label("─────────────────────", skin);
        sep.setColor(0.6f, 0.6f, 0.6f, 1);
        add(sep).padTop(8).padBottom(4).row();

        Label cheatTitle = new Label(TranslationManager.get("pause.cheats_title"), skin);
        add(cheatTitle).padBottom(6).row();

        KeyBindings keys = new KeyBindings();
        String[][] cheats = {
            {"CHEAT_TELEPORT",  TranslationManager.get("cheat.teleport")},
            {"CHEAT_NOCLIP",    TranslationManager.get("cheat.noclip")},
            {"CHEAT_HEAL",      TranslationManager.get("cheat.heal")},
            {"CHEAT_SOUL",      TranslationManager.get("cheat.soul")},
            {"CHEAT_GOD",       TranslationManager.get("cheat.god")},
            {"CHEAT_INSTAKILL", TranslationManager.get("cheat.instakill")},
        };

        for (String[] ch : cheats) {
            Table row = new Table();
            String keyName = "Ctrl+" + KeyBindings.keyName(keys.get(ch[0]));
            Label keyLabel = new Label(keyName, skin);
            keyLabel.setColor(0.8f, 0.8f, 0.2f, 1);
            row.add(keyLabel).left().width(100);
            row.add(new Label(ch[1], skin)).left();
            add(row).padBottom(2).row();
        }

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

    public void onResume() {
        if (game != null) game.setPaused(false);
        hide();
    }
}
