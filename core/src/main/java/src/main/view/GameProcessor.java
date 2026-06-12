package src.main.view;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
            case Input.Keys.D -> {
                game.knight.moveRight();
            }
            case Input.Keys.A -> {
                game.knight.moveLeft();
            }
            case Input.Keys.SPACE -> game.knight.jump();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.D -> game.knight.movingRight = false;
            case Input.Keys.A -> game.knight.movingLeft = false;
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
