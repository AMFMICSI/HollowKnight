package src.main.view.actors.modal;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import src.main.model.Game;
import src.main.model.entity.charm.CharmType;
import src.main.model.entity.knight.Knight;
import src.main.view.GameAssetManager;

import java.util.HashMap;
import java.util.Map;

public class InventoryModal extends Modal {
    private final Game game;
    private CharmType selectedCharm = null;
    private final Label descLabel;
    private final Table notchTable;
    private final Map<CharmType, ImageButton> charmButtons = new HashMap<>();
    private final Map<CharmType, Table> charmCells = new HashMap<>();
    private final Image[] notchIcons;
    private final TextButton equipBtn;
    private final Drawable highlightBg;

    public InventoryModal(Game game) {
        super();
        this.game = game;
        int maxNotches = game.getKnight().getMaxNotches();
        notchIcons = new Image[maxNotches];
        highlightBg = skin.getDrawable("selection");

        defaults().space(3);
        add(new Label("Inventory", skin)).colspan(2).center().padBottom(8).row();

        int col = 0;
        for (CharmType charm : CharmType.values()) {
            Table cell = new Table();
            cell.setName(charm.name());

            TextureRegionDrawable drawable = new TextureRegionDrawable(getCharmTexture(charm));
            ImageButton btn = new ImageButton(drawable);
            charmButtons.put(charm, btn);

            Label name = new Label(charm.getName(), skin);
            name.setAlignment(Align.center);

            cell.add(btn).size(36).row();
            cell.add(name).width(80).center().padTop(2);

            cell.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectCharm(charm);
                }
            });

            charmCells.put(charm, cell);
            add(cell).pad(4);
            col++;
            if (col % 2 == 0) row();
        }
        if (col % 2 != 0) row();

        descLabel = new Label("Select a charm", skin);
        descLabel.setWrap(true);
        add(descLabel).colspan(2).width(370).padTop(8).row();

        notchTable = new Table();
        for (int i = 0; i < maxNotches; i++) {
            notchIcons[i] = new Image();
            notchTable.add(notchIcons[i]).size(24).pad(2);
        }
        add(notchTable).colspan(2).padTop(4).row();

        equipBtn = new TextButton("Equip", skin);
        equipBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleCharm();
            }
        });
        add(equipBtn).colspan(2).width(150).padTop(4).row();

        TextButton closeBtn = new TextButton("Close", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClose();
            }
        });
        add(closeBtn).colspan(2).width(100);

        updateNotchDisplay();
        updateButtonHighlights();
    }

    private TextureRegion getCharmTexture(CharmType charm) {
        return switch (charm) {
            case SOUL_CATCHER          -> GameAssetManager.soulCatcherIcon;
            case DASHMASTER            -> GameAssetManager.dashmasterIcon;
            case UNBREAKABLE_STRENGTH  -> GameAssetManager.unbreakableStrengthIcon;
            case QUICK_SLASH           -> GameAssetManager.quickSlashIcon;
            case QUICK_FOCUS           -> GameAssetManager.quickFocusIcon;
            case HEAVY_BLOW            -> GameAssetManager.heavyBlowIcon;
            case SHARP_SHADOW          -> GameAssetManager.sharpShadowIcon;
            case VOID_HEART            -> GameAssetManager.voidHeartIcon;
        };
    }

    private void selectCharm(CharmType charm) {
        selectedCharm = charm;
        descLabel.setText(charm.getName() + ": " + charm.getDescription());
        Knight knight = game.getKnight();
        equipBtn.setText(knight.isCharmEquipped(charm) ? "Unequip" : "Equip");
        updateButtonHighlights();
    }

    private void toggleCharm() {
        if (selectedCharm == null) return;
        Knight knight = game.getKnight();
        if (knight.isCharmEquipped(selectedCharm)) {
            knight.unequipCharm(selectedCharm);
            GameAssetManager.charmClickSound.play();
        } else {
            if (!knight.equipCharm(selectedCharm)) {
                descLabel.setText("Not enough notches!");
                return;
            }
            GameAssetManager.charmClickSound.play();
        }
        updateNotchDisplay();
        updateButtonHighlights();
        equipBtn.setText(knight.isCharmEquipped(selectedCharm) ? "Unequip" : "Equip");
    }

    private void updateNotchDisplay() {
        Knight knight = game.getKnight();
        int used = knight.getUsedNotches();
        int max = knight.getMaxNotches();
        for (int i = 0; i < max; i++) {
            notchIcons[i].setDrawable(new TextureRegionDrawable(
                i < used ? GameAssetManager.notchLit : GameAssetManager.notchUnlit));
        }
    }

    private void updateButtonHighlights() {
        Knight knight = game.getKnight();
        for (Map.Entry<CharmType, ImageButton> entry : charmButtons.entrySet()) {
            CharmType charm = entry.getKey();
            ImageButton btn = entry.getValue();
            btn.setChecked(knight.isCharmEquipped(charm));
        }
        for (Map.Entry<CharmType, Table> entry : charmCells.entrySet()) {
            CharmType charm = entry.getKey();
            Table cell = entry.getValue();
            cell.setBackground(charm == selectedCharm ? highlightBg : null);
        }
    }

    public void onClose() { hide(); }
}
