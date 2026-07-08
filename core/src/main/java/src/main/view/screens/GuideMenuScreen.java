package src.main.view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.model.data.KeyBindings;
import src.main.view.config.TranslationManager;
import src.main.view.manager.UiManager;
import src.main.view.screens.settings.KeyBindingScreen;

public class GuideMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();
        applyThemeBackground();

        Table content = new Table();
        content.center();
        content.defaults().padBottom(8);

        Label title = new Label(TranslationManager.get("guide.title"), skin, "title");
        content.add(title).padBottom(20).row();

        KeyBindings keys = new KeyBindings();
        String[][] controls = {
            {TranslationManager.get("control.move_left"),       KeyBindings.keyName(keys.get("MOVE_LEFT"))},
            {TranslationManager.get("control.move_right"),      KeyBindings.keyName(keys.get("MOVE_RIGHT"))},
            {TranslationManager.get("control.jump"),            KeyBindings.keyName(keys.get("JUMP"))},
            {TranslationManager.get("control.dash"),            KeyBindings.keyName(keys.get("DASH"))},
            {TranslationManager.get("control.attack_nail"),     KeyBindings.keyName(keys.get("ATTACK"))},
            {TranslationManager.get("control.pogo"),            "v + " + KeyBindings.keyName(keys.get("ATTACK"))},
            {TranslationManager.get("control.focus_heal"),      KeyBindings.keyName(keys.get("FOCUS"))},
            {TranslationManager.get("control.vengeful_spirit"), KeyBindings.keyName(keys.get("SPELL_VENGEFUL"))},
            {TranslationManager.get("control.howling_wraiths"), KeyBindings.keyName(keys.get("SPELL_WRAITHS"))},
            {TranslationManager.get("control.inventory"),       KeyBindings.keyName(keys.get("INVENTORY"))},
            {TranslationManager.get("control.pause"),           KeyBindings.keyName(keys.get("PAUSE"))},
            {TranslationManager.get("control.interact"),        KeyBindings.keyName(keys.get("INTERACT"))},
            {TranslationManager.get("control.dialogue_next"),   KeyBindings.keyName(keys.get("DIALOGUE_NEXT"))},
        };

        for (String[] c : controls) {
            content.add(new Label(c[0], skin)).left().width(200);
            content.add(new Label(c[1], skin)).right().width(100);
            content.row();
        }

        content.add().height(15).row();

        Label abilitiesTitle = new Label(TranslationManager.get("guide.abilities_title"), skin);
        content.add(abilitiesTitle).padBottom(10).row();

        String[][] abilities = {
            {TranslationManager.get("ability.nail_attack"),         TranslationManager.get("ability_desc.nail_attack")},
            {TranslationManager.get("ability.jump_double"),         TranslationManager.get("ability_desc.jump_double")},
            {TranslationManager.get("ability.dash"),                TranslationManager.get("ability_desc.dash")},
            {TranslationManager.get("ability.pogo"),                TranslationManager.get("ability_desc.pogo")},
            {TranslationManager.get("ability.focus_heal"),          TranslationManager.get("ability_desc.focus_heal")},
            {TranslationManager.get("ability.vengeful_spirit"),     TranslationManager.get("ability_desc.vengeful_spirit")},
            {TranslationManager.get("ability.howling_wraiths"),     TranslationManager.get("ability_desc.howling_wraiths")},
            {TranslationManager.get("ability.wall_slide"),          TranslationManager.get("ability_desc.wall_slide")},
            {TranslationManager.get("ability.soul_system"),         TranslationManager.get("ability_desc.soul_system")},
            {TranslationManager.get("ability.health_masks"),        TranslationManager.get("ability_desc.health_masks")},
        };

        for (String[] a : abilities) {
            content.add(new Label(a[0], skin)).left().width(200);
            content.add(new Label(a[1], skin)).left().width(350);
            content.row();
        }

        content.add().height(15).row();

        Label cheatTitle = new Label(TranslationManager.get("guide.cheats_title"), skin);
        content.add(cheatTitle).padBottom(10).row();

        String[][] cheats = {
            {"Ctrl + B", TranslationManager.get("cheat.teleport")},
            {"Ctrl + N", TranslationManager.get("cheat.noclip")},
            {"Ctrl + H", TranslationManager.get("cheat.heal")},
            {"Ctrl + R", TranslationManager.get("cheat.soul")},
            {"Ctrl + G", TranslationManager.get("cheat.god")},
        };

        for (String[] ch : cheats) {
            content.add(new Label(ch[0], skin)).left().width(120);
            content.add(new Label(ch[1], skin)).left().width(350);
            content.row();
        }

        content.add().height(20).row();

        TextButton keybindBtn = new TextButton(TranslationManager.get("guide.key_bindings"), skin);
        TextButton backBtn = new TextButton(TranslationManager.get("guide.back"), skin);

        Table btnRow = new Table();
        btnRow.add(keybindBtn).width(200).padRight(10);
        btnRow.add(backBtn).width(200);
        content.add(btnRow);

        ScrollPane scroll = new ScrollPane(content, skin);
        scroll.setFadeScrollBars(false);
        rootTable.add(scroll).grow().pad(20);

        setupMenuPointer(keybindBtn, backBtn);

        keybindBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new KeyBindingScreen());
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });
    }
}
