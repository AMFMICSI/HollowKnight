package src.main.view.popup;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import src.main.view.manager.AchievementManager;
import src.main.view.manager.GameAssetManager;
import src.main.view.manager.UiManager;

public class AchievementPopup implements AchievementManager.Listener {
    private final Stage stage;
    private final Skin skin;
    private static final float DISPLAY_DURATION = 3f;

    public AchievementPopup(Stage stage) {
        this.stage = stage;
        this.skin = GameAssetManager.skin;
    }

    @Override
    public void onAchievementUnlocked(String id) {
        AchievementManager.AchievementDef def = UiManager.achievements.getDef(id);
        if (def == null) return;

        Table wrapper = new Table();
        wrapper.pad(10).top().right();

        Table toast = new Table();
        toast.pad(8);
        toast.setBackground(skin.getDrawable("bgDark"));

        Label title = new Label("Achievement Unlocked!", skin);
        Label name = new Label(def.title, skin);
        Label desc = new Label(def.description, skin);

        toast.add(title).row();
        toast.add(name).padTop(4).row();
        toast.add(desc).padTop(2);

        wrapper.add(toast).minWidth(200);
        stage.addActor(wrapper);
        wrapper.pack();

        wrapper.setPosition(stage.getWidth(), stage.getHeight() - wrapper.getHeight());
        wrapper.addAction(Actions.sequence(
            Actions.moveBy(-wrapper.getWidth() - 20, 0, 0.4f, Interpolation.swingOut),
            Actions.delay(DISPLAY_DURATION),
            Actions.alpha(0, 0.5f),
            Actions.run(wrapper::remove)
        ));
    }
}
