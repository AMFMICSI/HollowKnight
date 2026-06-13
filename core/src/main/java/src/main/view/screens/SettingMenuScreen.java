package src.main.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.model.data.KeyBindings;
import src.main.view.UiManager;

public class SettingMenuScreen extends AbstractScreen implements InputProcessor {
    private final KeyBindings keys = new KeyBindings();
    private String pendingAction = null;
    private TextButton pendingButton = null;

    @Override
    public void show() {
        super.show();
        buildUI();
    }

    private void buildUI() {
        rootTable.clear();

        Label title = new Label("Settings", skin);
        rootTable.add(title).padBottom(20).row();

        // هر ردیف: [نام فارسی | نام انگلیسی | دکمه کلید]
        String[][] actions = {
            {"Move Left",           "MOVE_LEFT"},
            {"Move Right",          "MOVE_RIGHT"},
            {"Jump",                "JUMP"},
            {"Dash",                "DASH"},
            {"Attack (Nail)",       "ATTACK"},
            {"Pogo (↓ + Attack)",   "POGO"},
            {"Focus / Heal",        "FOCUS"},
            {"Vengeful Spirit",     "SPELL_VENGEFUL"},
            {"Howling Wraiths",     "SPELL_WRAITHS"},
            {"Inventory",           "INVENTORY"},
            {"Pause",               "PAUSE"},
            {"Interact",            "INTERACT"},
            {"Dialogue Next",       "DIALOGUE_NEXT"},
        };

        // جدول داخلی برای اسکرول
        Table scrollContent = new Table();

        for (String[] pair : actions) {
            String label = pair[0];
            String action = pair[1];

            Label actionLabel = new Label(label, skin);
            TextButton keyBtn = new TextButton(KeyBindings.keyName(keys.get(action)), skin);

            keyBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    pendingAction = action;
                    pendingButton = keyBtn;
                    pendingButton.setText("...");
                    Gdx.input.setInputProcessor(SettingMenuScreen.this);
                }
            });

            scrollContent.add(actionLabel).width(200).padRight(10);
            scrollContent.add(keyBtn).width(120);
            scrollContent.row().padBottom(8);
            scrollContent.add().height(4).row(); // فاصله بین ردیف‌ها
        }

        // ScrollPane
        ScrollPane scrollPane = new ScrollPane(scrollContent, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // فقط عمودی اسکرول

        rootTable.add(scrollPane).width(400).height(350).padBottom(20).row();

        // دکمه‌های پایین
        Table bottomRow = new Table();
        TextButton resetBtn = new TextButton("Reset to Defaults", skin);
        resetBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                keys.resetToDefaults();
                keys.save();
                buildUI();
            }
        });

        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });

        bottomRow.add(resetBtn).width(180).padRight(20);
        bottomRow.add(backBtn).width(120);
        rootTable.add(bottomRow);
    }

    @Override public boolean keyDown(int keycode) {
        if (pendingAction != null && pendingButton != null) {
            if (keycode == Input.Keys.ESCAPE) {
                // کنسل — برگردون به قبلی
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
