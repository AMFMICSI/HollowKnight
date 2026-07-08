package src.main.view.screens.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.model.data.KeyBindings;
import src.main.view.config.TranslationManager;
import src.main.view.manager.UiManager;
import src.main.view.screens.AbstractScreen;

public class KeyBindingScreen extends AbstractScreen implements InputProcessor {
    private final KeyBindings keys = new KeyBindings();
    private final Runnable onBack;
    private String pendingAction = null;
    private TextButton pendingButton = null;

    public KeyBindingScreen() {
        this(null);
    }

    public KeyBindingScreen(Runnable onBack) {
        this.onBack = onBack;
    }

    @Override
    public void show() {
        super.show();
        applyThemeBackground();
        buildUI();
    }

    private void buildUI() {
        rootTable.clear();

        Label title = new Label(TranslationManager.get("keybind.title"), skin);
        rootTable.add(title).padBottom(20).row();

        String[][] actions = {
            {TranslationManager.get("keybind.action_left"),   "MOVE_LEFT"},
            {TranslationManager.get("keybind.action_right"),  "MOVE_RIGHT"},
            {TranslationManager.get("keybind.action_jump"),   "JUMP"},
            {TranslationManager.get("keybind.action_dash"),   "DASH"},
            {TranslationManager.get("keybind.action_attack"), "ATTACK"},
            {TranslationManager.get("keybind.action_pogo"),   "POGO"},
            {TranslationManager.get("keybind.action_focus"),  "FOCUS"},
            {TranslationManager.get("keybind.action_spell1"), "SPELL_VENGEFUL"},
            {TranslationManager.get("keybind.action_spell2"), "SPELL_WRAITHS"},
            {TranslationManager.get("keybind.action_inventory"), "INVENTORY"},
            {TranslationManager.get("keybind.action_pause"),  "PAUSE"},
            {TranslationManager.get("keybind.action_interact"), "INTERACT"},
            {TranslationManager.get("keybind.action_dialogue"), "DIALOGUE_NEXT"},
        };

        Table scrollContent = new Table();

        for (String[] pair : actions) {
            String label = pair[0];
            String action = pair[1];
            Label actionLabel = new Label(label, skin);
            TextButton keyBtn = new TextButton(KeyBindings.keyName(keys.get(action)), skin);

            keyBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    pendingAction = action;
                    pendingButton = keyBtn;
                    pendingButton.setText(TranslationManager.get("keybind.waiting"));
                    Gdx.input.setInputProcessor(KeyBindingScreen.this);
                }
            });

            scrollContent.add(actionLabel).width(200).padRight(10);
            scrollContent.add(keyBtn).width(120);
            scrollContent.row().padBottom(8);
            scrollContent.add().height(4).row();
        }

        ScrollPane scrollPane = new ScrollPane(scrollContent, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        rootTable.add(scrollPane).growX().height(350).padBottom(20).row();

        Table bottomRow = new Table();
        TextButton resetBtn = new TextButton(TranslationManager.get("keybind.reset"), skin);
        resetBtn.getLabel().setFontScale(0.65f);
        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                keys.resetToDefaults();
                keys.save();
                buildUI();
            }
        });

        TextButton backBtn = new TextButton(TranslationManager.get("keybind.back"), skin);
        backBtn.getLabel().setFontScale(0.65f);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                UiManager.setScreen(new SettingMenuScreen(onBack));
            }
        });

        bottomRow.add(resetBtn).growX().padRight(10);
        bottomRow.add(backBtn).growX();
        rootTable.add(bottomRow).growX();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (pendingAction != null && pendingButton != null) {
            if (keycode == Input.Keys.ESCAPE) {
                pendingButton.setText(KeyBindings.keyName(keys.get(pendingAction)));
            } else {
                keys.set(pendingAction, keycode);
                keys.save();
                pendingButton.setText(Input.Keys.toString(keycode));
            }
            pendingAction = null;
            pendingButton = null;
            Gdx.input.setInputProcessor(stage);
        }
        return true;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char c) { return false; }
    @Override public boolean touchDown(int x, int y, int p, int b) { return false; }
    @Override public boolean touchUp(int x, int y, int p, int b) { return false; }
    @Override public boolean touchCancelled(int x, int y, int p, int b) { return false; }
    @Override public boolean touchDragged(int x, int y, int p) { return false; }
    @Override public boolean mouseMoved(int x, int y) { return false; }
    @Override public boolean scrolled(float ax, float ay) { return false; }
}
