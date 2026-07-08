package src.main.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import src.main.model.Game;
import src.main.model.data.KeyBindings;
import src.main.model.entity.spell.SpellType;
import src.main.view.config.GameSettings;
import src.main.view.manager.UiManager;
import src.main.view.ui.modal.InventoryModal;
import src.main.view.ui.modal.PauseModal;
import src.main.view.screens.MainMenuScreen;

public class GameController implements InputProcessor {
    private final Game game;
    private final KeyBindings keys;
    private boolean ctrlHeld = false;

    public GameController(Game game, KeyBindings keys) {
        this.game = game;
        this.keys = keys;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F3) {
            GameSettings.getInstance().setDebugMode(!GameSettings.getInstance().isDebugMode());
            return true;
        }

        if (keycode == Input.Keys.CONTROL_LEFT || keycode == Input.Keys.CONTROL_RIGHT) {
            ctrlHeld = true;
        }

        // Cheat codes (Ctrl + key)
        if (ctrlHeld) {
            if (keycode == keys.get("CHEAT_TELEPORT")) { game.teleportToBossArena(); game.setPendingToast("Teleported to Boss Arena"); return true; }
            if (keycode == keys.get("CHEAT_NOCLIP")) { game.getKnight().toggleNoclip(); game.setPendingToast("Noclip: " + (game.getKnight().isNoclipMode() ? "ON" : "OFF")); return true; }
            if (keycode == keys.get("CHEAT_HEAL")) { game.getKnight().emergencyHeal(); game.setPendingToast("Emergency Heal"); return true; }
            if (keycode == keys.get("CHEAT_SOUL")) { game.getKnight().refillSoul(); game.setPendingToast("Soul Refilled"); return true; }
            if (keycode == keys.get("CHEAT_GOD")) { game.getKnight().toggleGodMode(); game.setPendingToast("God Mode: " + (game.getKnight().isGodMode() ? "ON" : "OFF")); return true; }
            if (keycode == keys.get("CHEAT_INSTAKILL")) { game.instaKillAllEnemies(); game.setPendingToast("Insta-Kill"); return true; }
        }

        if (keycode == keys.get("PAUSE")) {
            game.setPaused(true);
            PauseModal pauseModal = new PauseModal(game) {
                @Override public void onExit() { UiManager.setScreen(new MainMenuScreen()); }
            };
            pauseModal.show();
            return true;
        }

        if (keycode == keys.get("INVENTORY")) {
            if (!game.isDialogueActive()) {
                new InventoryModal(game).show();
            }
            return true;
        }

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

        if (keycode == keys.get("MOVE_RIGHT")) { game.getKnight().setMovingRight(true); game.getKnight().setFacingRight(true); return true; }
        if (keycode == keys.get("MOVE_LEFT")) { game.getKnight().setMovingLeft(true); game.getKnight().setFacingRight(false); return true; }
        if (keycode == keys.get("JUMP")) { game.getKnight().jump(); return true; }
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
        if (keycode == Input.Keys.CONTROL_LEFT || keycode == Input.Keys.CONTROL_RIGHT) {
            ctrlHeld = false;
        }
        if (keycode == keys.get("MOVE_RIGHT")) { game.getKnight().setMovingRight(false); return true; }
        if (keycode == keys.get("MOVE_LEFT")) { game.getKnight().setMovingLeft(false); return true; }
        if (keycode == keys.get("JUMP")) { game.getKnight().jumpReleased(); return true; }
        if (keycode == keys.get("FOCUS")) {
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
