package src.main.view.actors.modal;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseModal extends Modal{
    public PauseModal(){
        super();
        TextButton resumeBtn = new TextButton("Resume" , skin);
        TextButton exitBtn = new TextButton("Exit" , skin);

        defaults().space(5);
        add(resumeBtn).width(100).row();
        add(exitBtn).width(100).row();


        resumeBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onResume();
            }
        });

        exitBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onExit();
            }
        });
    }

    public void onExit(){

    }

    public void onResume(){

    }
}
