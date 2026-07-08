package src.main.view.ui.modal;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.view.manager.GameAssetManager;
import src.main.view.manager.UiManager;

public class Modal extends Table {
    protected Skin skin;
    private Table wrapperTable;

    public Modal() {
        skin = GameAssetManager.skin;
        wrapperTable = new Table();
        wrapperTable.setTouchable(Touchable.enabled);
        setTouchable(Touchable.enabled);
        setBackground(skin.getDrawable("bgDark"));
        pad(10);
        wrapperTable.add(this);

        wrapperTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(event.getTarget() == wrapperTable){
                    hide();
                }
            }
        });

        wrapperTable.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return true;
            }
        });
    }

    public void show(){
        UiManager.getScreen().getModalStack().add(wrapperTable);
        wrapperTable.getStage().setKeyboardFocus(wrapperTable);
    }

    public void hide(){
        wrapperTable.remove();
    }
}

