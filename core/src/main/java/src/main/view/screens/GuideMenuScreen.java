package src.main.view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.UiManager;

public class GuideMenuScreen extends AbstractScreen {

    @Override
    public void show() {
        super.show();

        Label title = new Label("Guide", skin);
        Label content = new Label("Use WASD to move, Space to jump,\nLeft Click to attack.", skin);

        TextButton backBtn = new TextButton("Back", skin);

        Table centerTable = new Table();
        centerTable.setFillParent(true);
        centerTable.center();
        centerTable.add(title).padBottom(20).row();
        centerTable.add(content).padBottom(20).row();
        centerTable.add(backBtn).width(200);

        rootTable.add(centerTable).grow();

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UiManager.setScreen(new MainMenuScreen());
            }
        });
    }
}
