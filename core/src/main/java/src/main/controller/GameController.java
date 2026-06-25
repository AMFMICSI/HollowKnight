package src.main.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import src.main.model.Game;
import src.main.model.data.KeyBindings;
import src.main.model.entity.spell.SpellType;
import src.main.view.GameSettings;
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
        if (game.isDialogueActive()) {
            if (keycode == keys.get("INTERACT") || keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                game.requestDialogueAdvance();
            }
            return true;
        }

        if (keycode == keys.get("INTERACT")) {
            game.interact();
            return true;
        }

        if (keycode == Input.Keys.F3) {
            GameSettings.getInstance().setDebugMode(!GameSettings.getInstance().isDebugMode());
            return true;
        }

        if (keycode == keys.get("PAUSE")) {
            PauseModal pauseModal = new PauseModal() {
                @Override public void onResume() { hide(); }
                @Override public void onExit() { UiManager.setScreen(new MainMenuScreen()); }
            };
            pauseModal.show();
            return true;
        }

        if (keycode == keys.get("MOVE_RIGHT")) { game.getKnight().setMovingRight(true); game.getKnight().setMovingLeft(false); game.getKnight().setFacingRight(true); return true; }
        else if (keycode == keys.get("MOVE_LEFT")) { game.getKnight().setMovingLeft(true); game.getKnight().setMovingRight(false); game.getKnight().setFacingRight(false); return true; }
        else if (keycode == keys.get("JUMP")) { game.getKnight().jump(); return true; }
        else if (keycode == keys.get("DASH")) {
            if (Gdx.input.isKeyPressed(keys.get("POGO"))) {
                game.getKnight().dashDown();
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                game.getKnight().dashUp();
            } else {
                game.getKnight().dash();
            }
            return true;
        }
        else if (keycode == keys.get("ATTACK")) {
            if (Gdx.input.isKeyPressed(keys.get("POGO"))) {
                if (!game.getKnight().isOnGround()) {
                    game.getKnight().pogoAttack();
                } else {
                    game.getKnight().attackDown();
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                game.getKnight().attackUp();
            } else {
                game.getKnight().attack();
            }
            return true;
        }
        else if (keycode == keys.get("FOCUS")) {
            game.getKnight().startFocus();
            return true;
        }
        else if (keycode == keys.get("SPELL_VENGEFUL")) {
            game.getKnight().startCast(SpellType.VENGEFUL);
            return true;
        }
        else if (keycode == keys.get("SPELL_WRAITHS")) {
            game.getKnight().startCast(SpellType.WRAITHS);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (game.isDialogueActive()) {
            if (keycode == keys.get("MOVE_RIGHT")) { game.getKnight().setMovingRight(false); return true; }
            if (keycode == keys.get("MOVE_LEFT")) { game.getKnight().setMovingLeft(false); return true; }
            return true;
        }

        if (keycode == keys.get("MOVE_RIGHT")) { game.getKnight().setMovingRight(false); return true; }
        if (keycode == keys.get("MOVE_LEFT")) { game.getKnight().setMovingLeft(false); return true; }
        else if (keycode == keys.get("JUMP")) { game.getKnight().jumpReleased(); return true; }
        else if (keycode == keys.get("FOCUS")) {
            game.getKnight().cancelFocus();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(float amountX, float amountY) { return false; }
}
