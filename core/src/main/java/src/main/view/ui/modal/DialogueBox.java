package src.main.view.ui.modal;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class DialogueBox extends Window {
    private static final float CHAR_INTERVAL = 0.03f;

    private Label textLabel;
    private String fullText;
    private int visibleChars;
    private float charTimer;
    private boolean animationComplete;

    public DialogueBox(Skin skin) {
        super("", skin);
        setModal(true);
        setMovable(false);
        textLabel = new Label("", skin);
        textLabel.setWrap(true);
        add(textLabel).width(400).pad(20);
        pack();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!animationComplete && fullText != null) {
            charTimer += delta;
            while (charTimer >= CHAR_INTERVAL && visibleChars < fullText.length()) {
                visibleChars++;
                charTimer -= CHAR_INTERVAL;
            }
            if (visibleChars >= fullText.length()) {
                visibleChars = fullText.length();
                animationComplete = true;
            }
            textLabel.setText(fullText.substring(0, visibleChars));
            pack();
        }
    }

    public void show(Stage stage, String text) {
        fullText = text;
        visibleChars = 0;
        charTimer = 0;
        animationComplete = text == null || text.isEmpty();
        textLabel.setText("");
        pack();
        stage.addActor(this);
        setPosition(
            (stage.getWidth() - getWidth()) / 2f,
            stage.getHeight() - getHeight() - 50
        );
    }

    public void setText(String text) {
        if (fullText != null && fullText.equals(text)) return;
        fullText = text;
        visibleChars = 0;
        charTimer = 0;
        animationComplete = text == null || text.isEmpty();
        textLabel.setText("");
        pack();
    }

    public void skipAnimation() {
        if (fullText == null) return;
        visibleChars = fullText.length();
        animationComplete = true;
        textLabel.setText(fullText);
        pack();
    }

    public boolean isAnimationComplete() { return animationComplete; }
}
