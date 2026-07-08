package src.main.view.ui.modal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import src.main.view.config.TranslationManager;
import src.main.view.manager.GameAssetManager;

import java.util.HashMap;
import java.util.Map;

public class InventoryModal extends Modal {
    private final Game game;
    private CharmType selectedCharm = null;
    private final Label descLabel;
    private Table notchTable;
    private final Map<CharmType, ImageButton> charmButtons = new HashMap<>();
    private final Map<CharmType, Table> charmCells = new HashMap<>();
    private final Image[] notchIcons;
    private TextButton equipBtn;
    private final Drawable highlightBg;
    private final Drawable equippedBg;

    public InventoryModal(Game game) {
        super();
        this.game = game;
        int maxNotches = game.getKnight().getMaxNotches();
        notchIcons = new Image[maxNotches];
        highlightBg = skin.getDrawable("bgGray");

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(0.85f, 0.65f, 0.1f, 0.5f);
        pix.fill();
        equippedBg = new TextureRegionDrawable(new TextureRegion(new Texture(pix)));
        pix.dispose();

        defaults().space(3);
        add(new Label(TranslationManager.get("inventory.title"), skin)).colspan(2).center().padBottom(8).row();

        buildCharmGrid();

        descLabel = new Label(TranslationManager.get("inventory.select"), skin);
        descLabel.setWrap(true);
        add(descLabel).colspan(2).width(370).padTop(8).row();

        buildNotchRow(maxNotches);
        buildEquipButton();
        buildCloseButton();

        updateNotchDisplay();
        updateButtonHighlights();
    }

    private void buildCharmGrid() {
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
    }

    private void buildNotchRow(int maxNotches) {
        notchTable = new Table();
        for (int i = 0; i < maxNotches; i++) {
            notchIcons[i] = new Image();
            notchTable.add(notchIcons[i]).size(24).pad(2);
        }
        add(notchTable).colspan(2).padTop(4).row();
    }

    private void buildEquipButton() {
        equipBtn = new TextButton(TranslationManager.get("inventory.equip"), skin);
        equipBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleCharm();
            }
        });
        add(equipBtn).colspan(2).width(150).padTop(4).row();
    }

    private void buildCloseButton() {
        TextButton closeBtn = new TextButton(TranslationManager.get("inventory.close"), skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClose();
            }
        });
        add(closeBtn).colspan(2).width(100);
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
        equipBtn.setText(
            knight.isCharmEquipped(charm)
                ? TranslationManager.get("inventory.unequip")
                : TranslationManager.get("inventory.equip"));
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
                descLabel.setText(TranslationManager.get("inventory.no_notches"));
                return;
            }
            GameAssetManager.charmClickSound.play();
        }
        updateNotchDisplay();
        updateButtonHighlights();
        equipBtn.setText(
            knight.isCharmEquipped(selectedCharm)
                ? TranslationManager.get("inventory.unequip")
                : TranslationManager.get("inventory.equip"));
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
            btn.setColor(Color.WHITE);
        }
        for (Map.Entry<CharmType, Table> entry : charmCells.entrySet()) {
            CharmType charm = entry.getKey();
            Table cell = entry.getValue();
            if (charm == selectedCharm) {
                cell.setBackground(highlightBg);
            } else if (knight.isCharmEquipped(charm)) {
                cell.setBackground(equippedBg);
            } else {
                cell.setBackground((Drawable) null);
            }
        }
    }

    public void onClose() { hide(); }
}
