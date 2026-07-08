package src.main.view.screens.settings;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.config.TranslationManager;
import src.main.view.manager.GameMusic;
import src.main.view.config.GameSettings;
import src.main.view.screens.AbstractScreen;
import src.main.view.manager.UiManager;
import src.main.view.screens.MainMenuScreen;

public class SettingMenuScreen extends AbstractScreen {
    private final Runnable onBack;

    public SettingMenuScreen() {
        this(null);
    }

    public SettingMenuScreen(Runnable onBack) {
        this.onBack = onBack;
    }

    @Override
    public void show() {
        super.show();
        applyThemeBackground();
        buildMainMenu();
    }

    private void buildMainMenu() {
        rootTable.clear();

        Table center = new Table();
        center.center();

        Label title = new Label(TranslationManager.get("settings.title"), skin);
        center.add(title).padBottom(30).row();

        TextButton musicMuteBtn = buildMusicRow(center);
        TextButton sfxMuteBtn = buildSfxRow(center);
        Slider musicSlider = (Slider) musicMuteBtn.getUserObject();
        Slider sfxSlider = (Slider) sfxMuteBtn.getUserObject();

        buildBrightnessRow(center);

        TextButton langBtn = buildLangButton(center);
        TextButton themeBtn = buildThemeButton(center);
        TextButton keyBtn = buildKeyBindingsButton(center);
        TextButton resetSoundBtn = buildResetSoundButton(center, musicSlider, sfxSlider, musicMuteBtn, sfxMuteBtn);
        TextButton backBtn = buildBackButton(center);

        buildBottomRow(center, resetSoundBtn, backBtn);

        rootTable.add(center).expand().center();
        setupMenuPointer(musicMuteBtn, sfxMuteBtn, langBtn, themeBtn, keyBtn, resetSoundBtn, backBtn);
    }

    private TextButton buildMusicRow(Table center) {
        Label musicLabel = new Label(TranslationManager.get("settings.music"), skin);
        TextButton musicMuteBtn = new TextButton(
            GameMusic.MENU.isMuted()
                ? TranslationManager.get("settings.unmute")
                : TranslationManager.get("settings.mute"),
            skin);
        Slider musicSlider = new Slider(0, 1, 0.05f, false, skin);
        musicSlider.setValue(GameSettings.getInstance().getMusicVolume());
        musicSlider.addListener(event -> {
            GameSettings.getInstance().setMusicVolume(musicSlider.getValue());
            GameMusic.setVolumeAll(musicSlider.getValue());
            if (GameMusic.MENU.isMuted() && musicSlider.getValue() > 0) {
                GameSettings.getInstance().setMusicMuted(false);
                GameMusic.applyMuteToAll();
                musicMuteBtn.setText(TranslationManager.get("settings.mute"));
            }
            return false;
        });

        musicMuteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                GameSettings.getInstance().setMusicMuted(!GameMusic.MENU.isMuted());
                GameMusic.applyMuteToAll();
                musicMuteBtn.setText(
                    GameMusic.MENU.isMuted()
                        ? TranslationManager.get("settings.unmute")
                        : TranslationManager.get("settings.mute"));
            }
        });

        Table musicRow = new Table();
        musicRow.add(musicLabel).width(100).padRight(10);
        musicRow.add(musicSlider).width(200).padRight(10);
        musicRow.add(musicMuteBtn).width(80);
        center.add(musicRow).padBottom(15).row();

        musicMuteBtn.setUserObject(musicSlider);
        return musicMuteBtn;
    }

    private TextButton buildSfxRow(Table center) {
        Label sfxLabel = new Label(TranslationManager.get("settings.sfx"), skin);
        TextButton sfxMuteBtn = new TextButton(
            GameSettings.getInstance().isSfxMuted()
                ? TranslationManager.get("settings.unmute")
                : TranslationManager.get("settings.mute"),
            skin);
        Slider sfxSlider = new Slider(0, 1, 0.05f, false, skin);
        sfxSlider.setValue(GameSettings.getInstance().getSfxVolume());
        sfxSlider.addListener(event -> {
            GameSettings.getInstance().setSfxVolume(sfxSlider.getValue());
            if (GameSettings.getInstance().isSfxMuted()) {
                GameSettings.getInstance().setSfxMuted(false);
                sfxMuteBtn.setText(TranslationManager.get("settings.mute"));
            }
            return false;
        });

        sfxMuteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                GameSettings.getInstance().setSfxMuted(!GameSettings.getInstance().isSfxMuted());
                sfxMuteBtn.setText(
                    GameSettings.getInstance().isSfxMuted()
                        ? TranslationManager.get("settings.unmute")
                        : TranslationManager.get("settings.mute"));
            }
        });

        Table sfxRow = new Table();
        sfxRow.add(sfxLabel).width(100).padRight(10);
        sfxRow.add(sfxSlider).width(200).padRight(10);
        sfxRow.add(sfxMuteBtn).width(80);
        center.add(sfxRow).padBottom(15).row();

        sfxMuteBtn.setUserObject(sfxSlider);
        return sfxMuteBtn;
    }

    private void buildBrightnessRow(Table center) {
        Label brightnessLabel = new Label(TranslationManager.get("settings.brightness"), skin);
        Slider brightnessSlider = new Slider(0.2f, 1, 0.05f, false, skin);
        brightnessSlider.setValue(GameSettings.getInstance().getBrightness());
        brightnessSlider.addListener(event -> {
            GameSettings.getInstance().setBrightness(brightnessSlider.getValue());
            return false;
        });

        Table brightnessRow = new Table();
        brightnessRow.add(brightnessLabel).width(100).padRight(10);
        brightnessRow.add(brightnessSlider).width(200);
        center.add(brightnessRow).padBottom(15).row();
    }

    private TextButton buildLangButton(Table center) {
        String[] languages = {"en", "fr"};
        TextButton langBtn = new TextButton(
            TranslationManager.get("settings.language") + ": "
                + GameSettings.getInstance().getLanguage().toUpperCase(),
            skin);
        langBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                String current = GameSettings.getInstance().getLanguage();
                String next = languages[0];
                for (int i = 0; i < languages.length; i++) {
                    if (languages[i].equals(current)) {
                        next = languages[(i + 1) % languages.length];
                        break;
                    }
                }
                GameSettings.getInstance().setLanguage(next);
                TranslationManager.reload();
                buildMainMenu();
            }
        });
        center.add(langBtn).width(250).padBottom(15).row();
        return langBtn;
    }

    private TextButton buildThemeButton(Table center) {
        TextButton themeBtn = new TextButton(
            TranslationManager.get("settings.theme") + ": " + (GameSettings.getInstance().getTheme() + 1) + "/3",
            skin);
        themeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                GameSettings s = GameSettings.getInstance();
                int next = (s.getTheme() + 1) % 3;
                s.setTheme(next);
                themeBtn.setText(TranslationManager.get("settings.theme") + ": " + (next + 1) + "/3");
                applyThemeBackground();
            }
        });
        center.add(themeBtn).width(250).padBottom(15).row();
        return themeBtn;
    }

    private TextButton buildKeyBindingsButton(Table center) {
        TextButton keyBtn = new TextButton(TranslationManager.get("settings.key_bindings"), skin);
        keyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                UiManager.setScreen(new KeyBindingScreen(onBack));
            }
        });
        center.add(keyBtn).width(200).padBottom(20).row();
        return keyBtn;
    }

    private TextButton buildResetSoundButton(Table center, Slider musicSlider, Slider sfxSlider,
                                             TextButton musicMuteBtn, TextButton sfxMuteBtn) {
        TextButton resetSoundBtn = new TextButton(TranslationManager.get("settings.reset_sound"), skin);
        resetSoundBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                GameSettings.getInstance().resetSound();
                GameMusic.setVolumeAll(GameSettings.getInstance().getMusicVolume());
                GameMusic.applyMuteToAll();
                musicSlider.setValue(GameSettings.getInstance().getMusicVolume());
                sfxSlider.setValue(GameSettings.getInstance().getSfxVolume());
                musicMuteBtn.setText(
                    GameMusic.MENU.isMuted()
                        ? TranslationManager.get("settings.unmute")
                        : TranslationManager.get("settings.mute"));
                sfxMuteBtn.setText(
                    GameSettings.getInstance().isSfxMuted()
                        ? TranslationManager.get("settings.unmute")
                        : TranslationManager.get("settings.mute"));
            }
        });
        return resetSoundBtn;
    }

    private TextButton buildBackButton(Table center) {
        TextButton backBtn = new TextButton(TranslationManager.get("settings.back"), skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (onBack != null) {
                    onBack.run();
                } else {
                    UiManager.setScreen(new MainMenuScreen());
                }
            }
        });
        return backBtn;
    }

    private void buildBottomRow(Table center, TextButton resetSoundBtn, TextButton backBtn) {
        Table bottomRow = new Table();
        bottomRow.add(resetSoundBtn).width(180).padRight(20);
        bottomRow.add(backBtn).width(120);
        center.add(bottomRow);
    }
}

