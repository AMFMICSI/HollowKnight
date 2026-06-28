package src.main.view.screens.settings;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.GameMusic;
import src.main.view.GameSettings;
import src.main.view.Phats;
import src.main.view.screens.AbstractScreen;
import src.main.view.UiManager;
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
        setBackground(Phats.MainBackGround.getText());
        buildMainMenu();
    }

    private void buildMainMenu() {
        rootTable.clear();

        Table center = new Table();
        center.center();

        Label title = new Label("Settings", skin);
        center.add(title).padBottom(30).row();

        Label musicLabel = new Label("Music", skin);
        TextButton musicMuteBtn = new TextButton(GameMusic.MENU.isMuted() ? "Unmute" : "Mute", skin);
        Slider musicSlider = new Slider(0, 1, 0.05f, false, skin);
        musicSlider.setValue(GameSettings.getInstance().getMusicVolume());
        musicSlider.addListener(event -> {
            GameSettings.getInstance().setMusicVolume(musicSlider.getValue());
            GameMusic.MENU.setVolume(musicSlider.getValue());
            if (GameMusic.MENU.isMuted() && musicSlider.getValue() > 0) {
                GameMusic.MENU.toggleMute();
                musicMuteBtn.setText("Mute");
            }
            return false;
        });

        musicMuteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                GameMusic.MENU.toggleMute();
                musicMuteBtn.setText(GameMusic.MENU.isMuted() ? "Unmute" : "Mute");
            }
        });

        Table musicRow = new Table();
        musicRow.add(musicLabel).width(100).padRight(10);
        musicRow.add(musicSlider).width(200).padRight(10);
        musicRow.add(musicMuteBtn).width(80);
        center.add(musicRow).padBottom(15).row();

        Label sfxLabel = new Label("SFX", skin);
        TextButton sfxMuteBtn = new TextButton(GameSettings.getInstance().isSfxMuted() ? "Unmute" : "Mute", skin);
        Slider sfxSlider = new Slider(0, 1, 0.05f, false, skin);
        sfxSlider.setValue(GameSettings.getInstance().getSfxVolume());
        sfxSlider.addListener(event -> {
            GameSettings.getInstance().setSfxVolume(sfxSlider.getValue());
            if (GameSettings.getInstance().isSfxMuted()) {
                GameSettings.getInstance().setSfxMuted(false);
                sfxMuteBtn.setText("Mute");
            }
            return false;
        });

        sfxMuteBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                GameSettings.getInstance().setSfxMuted(!GameSettings.getInstance().isSfxMuted());
                sfxMuteBtn.setText(GameSettings.getInstance().isSfxMuted() ? "Unmute" : "Mute");
            }
        });

        Table sfxRow = new Table();
        sfxRow.add(sfxLabel).width(100).padRight(10);
        sfxRow.add(sfxSlider).width(200).padRight(10);
        sfxRow.add(sfxMuteBtn).width(80);
        center.add(sfxRow).padBottom(15).row();

        Label brightnessLabel = new Label("Brightness", skin);
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

        String[] languages = {"en", "fa"};
        TextButton langBtn = new TextButton("Language: " + GameSettings.getInstance().getLanguage().toUpperCase(), skin);
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
                langBtn.setText("Language: " + next.toUpperCase());
            }
        });
        center.add(langBtn).width(250).padBottom(15).row();

        TextButton keyBtn = new TextButton("Key Bindings", skin);
        keyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                UiManager.setScreen(new KeyBindingScreen(onBack));
            }
        });
        center.add(keyBtn).width(200).padBottom(20).row();

        Table bottomRow = new Table();
        TextButton resetSoundBtn = new TextButton("Reset Sound", skin);
        resetSoundBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                GameSettings.getInstance().resetSound();
                GameMusic.MENU.setVolume(GameSettings.getInstance().getMusicVolume());
                musicSlider.setValue(GameSettings.getInstance().getMusicVolume());
                sfxSlider.setValue(GameSettings.getInstance().getSfxVolume());
                musicMuteBtn.setText(GameMusic.MENU.isMuted() ? "Unmute" : "Mute");
                sfxMuteBtn.setText(GameSettings.getInstance().isSfxMuted() ? "Unmute" : "Mute");
            }
        });

        TextButton backBtn = new TextButton("Back", skin);
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

        bottomRow.add(resetSoundBtn).width(180).padRight(20);
        bottomRow.add(backBtn).width(120);
        center.add(bottomRow);

        rootTable.add(center).expand().center();
        setupMenuPointer(musicMuteBtn, sfxMuteBtn, langBtn, keyBtn, resetSoundBtn, backBtn);
    }
}

