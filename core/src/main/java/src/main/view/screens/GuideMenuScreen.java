package src.main.view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.model.data.KeyBindings;
import src.main.view.Phats;
import src.main.view.UiManager;

public class GuideMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        setBackground(Phats.MainBackGround.getText());

        Table content = new Table();
        content.center();
        content.defaults().padBottom(8);

        Label title = new Label("GUIDE", skin, "subtitle");
        content.add(title).padBottom(20).row();

        KeyBindings keys = new KeyBindings();
        String[][] controls = {
            {"Move Left",       KeyBindings.keyName(keys.get("MOVE_LEFT"))},
            {"Move Right",      KeyBindings.keyName(keys.get("MOVE_RIGHT"))},
            {"Jump",            KeyBindings.keyName(keys.get("JUMP"))},
            {"Dash",            KeyBindings.keyName(keys.get("DASH"))},
            {"Attack (Nail)",   KeyBindings.keyName(keys.get("ATTACK"))},
            {"Pogo",            "\u2193 + " + KeyBindings.keyName(keys.get("ATTACK"))},
            {"Focus / Heal",    KeyBindings.keyName(keys.get("FOCUS"))},
            {"Vengeful Spirit", KeyBindings.keyName(keys.get("SPELL_VENGEFUL"))},
            {"Howling Wraiths", KeyBindings.keyName(keys.get("SPELL_WRAITHS"))},
            {"Inventory",       KeyBindings.keyName(keys.get("INVENTORY"))},
            {"Pause",           KeyBindings.keyName(keys.get("PAUSE"))},
            {"Interact",        KeyBindings.keyName(keys.get("INTERACT"))},
            {"Dialogue Next",   KeyBindings.keyName(keys.get("DIALOGUE_NEXT"))},
        };

        for (String[] c : controls) {
            content.add(new Label(c[0], skin)).left().width(200);
            content.add(new Label(c[1], skin)).right().width(100);
            content.row();
        }

        content.add().height(15).row();

        Label abilitiesTitle = new Label("\u2014\u2014 Abilities & Knight \u2014\u2014", skin);
        content.add(abilitiesTitle).padBottom(10).row();

        String[][] abilities = {
            {"Nail Attack",         "Swing your nail to damage nearby enemies"},
            {"Jump / Double Jump",  "Press Jump once, then again mid-air"},
            {"Dash",                "Quick horizontal dash with cooldown"},
            {"Pogo (\u2193+Attack)",     "Bounce on enemies & spikes"},
            {"Focus / Heal",        "Hold to consume 33 Soul, recover 1 Mask"},
            {"Vengeful Spirit",     "Fire a projectile forward (33 Soul)"},
            {"Howling Wraiths",     "AoE blast upward (33 Soul)"},
            {"Wall Slide",          "Hold toward a wall to slide down"},
            {"Soul System",         "Gain 11 Soul per hit on enemy (max 99)"},
            {"Health (Masks)",      "5 Masks total \u2014 lose 1 per hit"},
        };

        for (String[] a : abilities) {
            content.add(new Label(a[0], skin)).left().width(200);
            content.add(new Label(a[1], skin)).left().width(350);
            content.row();
        }

        content.add().height(15).row();

        Label cheatTitle = new Label("\u2014\u2014 Cheat Codes \u2014\u2014", skin);
        content.add(cheatTitle).padBottom(10).row();

        String[][] cheats = {
            {"Ctrl + B", "Boss Arena Teleport"},
            {"Ctrl + N", "Noclip / Spectator Mode"},
            {"Ctrl + H", "Emergency Heal"},
            {"Ctrl + R", "Refill Soul Vessel"},
            {"Ctrl + G", "God Mode (invincible)"},
        };

        for (String[] ch : cheats) {
            content.add(new Label(ch[0], skin)).left().width(120);
            content.add(new Label(ch[1], skin)).left().width(350);
            content.row();
        }

        content.add().height(20).row();

        TextButton backBtn = new TextButton("Back", skin);
        content.add(backBtn).width(200);

        ScrollPane scroll = new ScrollPane(content, skin);
        scroll.setFadeScrollBars(false);
        rootTable.add(scroll).grow().pad(20);

        setupMenuPointer(backBtn);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });
    }
}
