package src.main.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import src.main.model.Game;
import src.main.model.data.KeyBindings;
import src.main.view.UiManager;
import src.main.view.actors.modal.PauseModal;
import src.main.view.screens.MainMenuScreen;

public class GameController implements InputProcessor {
    private final Game game;
    private final KeyBindings keys;

    public GameController(Game game, KeyBindings keys) {
        this.game = game;
        this.keys = keys;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == keys.get("PAUSE")){
            PauseModal pauseModal = new PauseModal() {
                @Override public void onResume() {hide();}
                @Override public void onExit() {UiManager.setScreen(new MainMenuScreen());}
            };
            pauseModal.show();
            return true;
        }

        if (keycode == keys.get("MOVE_RIGHT")) { game.knight.movingRight = true; game.knight.movingLeft = false; game.knight.facingRight = true; return true; }
        else if (keycode == keys.get("MOVE_LEFT")) { game.knight.movingLeft = true; game.knight.movingRight = false; game.knight.facingRight = false; return true; }
        else if (keycode == keys.get("JUMP")) { game.knight.jump(); return true; }
        else if (keycode == keys.get("DASH")) { game.knight.dash(); return true; }
        else if (keycode == keys.get("ATTACK")) {
            if (Gdx.input.isKeyPressed(keys.get("POGO"))) {
                game.knight.pogo();
            } else {
                game.knight.attack();
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == keys.get("MOVE_RIGHT")) { game.knight.movingRight = false; return true; }
        if (keycode == keys.get("MOVE_LEFT")) { game.knight.movingLeft = false; return true; }
        else if (keycode == keys.get("JUMP")) { game.knight.jumpReleased(); return true; }
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
