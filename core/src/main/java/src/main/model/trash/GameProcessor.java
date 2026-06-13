package src.main.model.trash;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import src.main.view.UiManager;
import src.main.view.actors.modal.PauseModal;
import src.main.model.Game;
import src.main.view.screens.MainMenuScreen;

public class GameProcessor implements InputProcessor {

    private  final  Game game;
    public GameProcessor(Game game) {
        this.game = game;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE, Input.Keys.ALT_RIGHT -> {
                PauseModal pauseModal = new PauseModal() {
                    @Override
                    public void onResume() {
                        hide();
                    }

                    @Override
                    public void onExit() {
                        UiManager.setScreen(new MainMenuScreen());
                    }
                };
                pauseModal.show();
            }

            case Input.Keys.RIGHT ->  game.knight.moveRight();
            case Input.Keys.LEFT -> game.knight.moveLeft();
            case Input.Keys.Z -> game.knight.jump();
            case Input.Keys.C -> game.knight.dash();

        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT, Input.Keys.LEFT -> game.knight.stop();
            case Input.Keys.Z -> game.knight.jumpReleased();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
