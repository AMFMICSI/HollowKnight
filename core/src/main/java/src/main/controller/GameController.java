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
            return true;
        }

        // دسته‌بندی و هدایت کلیدها به متدهای کپسوله شده اختصاصی
        if (ctrlHeld) return handleCheatKeys(keycode);
        if (handleSystemKeys(keycode)) return true;
        if (handleMovementKeys(keycode, true)) return true;

        return handleActionKeys(keycode);
    }

    private boolean handleSystemKeys(int keycode) {
        if (keycode == keys.get("PAUSE")) {
            game.setPaused(true);
            new PauseModal(game) {
                @Override public void onExit() { UiManager.setScreen(new MainMenuScreen()); }
            }.show();
            return true;
        }

        if (keycode == keys.get("INVENTORY")) {
            if (!game.isDialogueActive()) new InventoryModal(game).show();
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
        return false;
    }

    private boolean handleMovementKeys(int keycode, boolean isPressed) {
        if (keycode == keys.get("MOVE_RIGHT")) {
            game.getKnight().setMovingRight(isPressed);
            if (isPressed) game.getKnight().setFacingRight(true);
            return true;
        }
        if (keycode == keys.get("MOVE_LEFT")) {
            game.getKnight().setMovingLeft(isPressed);
            if (isPressed) game.getKnight().setFacingRight(false);
            return true;
        }
        return false;
    }

    private boolean handleCheatKeys(int keycode) {
        if (keycode == keys.get("CHEAT_TELEPORT")) {
            game.teleportToBossArena();
            game.setPendingToast("Teleported to Boss Arena");
            return true;
        }
        if (keycode == keys.get("CHEAT_NOCLIP")) {
            game.getKnight().getCheatSystem().toggleNoclip();
            game.setPendingToast("Noclip: " + (game.getKnight().isNoclipMode() ? "ON" : "OFF"));
            return true;
        }
        if (keycode == keys.get("CHEAT_HEAL")) {
            game.getKnight().getCheatSystem().emergencyHeal(game.getKnight());
            game.setPendingToast("Emergency Heal");
            return true;
        }
        if (keycode == keys.get("CHEAT_SOUL")) {
            game.getKnight().getCheatSystem().refillSoul(game.getKnight());
            game.setPendingToast("Soul Refilled");
            return true;
        }
        if (keycode == keys.get("CHEAT_GOD")) {
            game.getKnight().getCheatSystem().toggleGodMode();
            game.setPendingToast("God Mode: " + (game.getKnight().isGodMode() ? "ON" : "OFF"));
            return true;
        }
        if (keycode == keys.get("CHEAT_INSTAKILL")) {
            game.instaKillAllEnemies();
            game.setPendingToast("Insta-Kill");
            return true;
        }
        return false;
    }

    private boolean handleActionKeys(int keycode) {
        if (keycode == keys.get("JUMP")) { game.getKnight().jump(); return true; }
        if (keycode == keys.get("DASH")) { executeDashCommand(); return true; }
        if (keycode == keys.get("ATTACK")) { executeAttackCommand(); return true; }
        if (keycode == keys.get("FOCUS")) { game.getKnight().startFocus(); return true; }
        if (keycode == keys.get("SPELL_VENGEFUL")) { game.getKnight().startCast(SpellType.VENGEFUL); return true; }
        if (keycode == keys.get("SPELL_WRAITHS")) { game.getKnight().startCast(SpellType.WRAITHS); return true; }
        return false;
    }

    private void executeDashCommand() {
        if (Gdx.input.isKeyPressed(keys.get("POGO"))) game.getKnight().dashDown();
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)) game.getKnight().dashUp();
        else game.getKnight().dash();
    }

    private void executeAttackCommand() {
        if (Gdx.input.isKeyPressed(keys.get("POGO"))) {
            if (!game.getKnight().isOnGround()) game.getKnight().pogoAttack();
            else game.getKnight().attackDown();
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) game.getKnight().attackUp();
        else game.getKnight().attack();
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.CONTROL_LEFT || keycode == Input.Keys.CONTROL_RIGHT) {
            ctrlHeld = false;
            return true;
        }
        if (handleMovementKeys(keycode, false)) return true;
        if (keycode == keys.get("JUMP")) { game.getKnight().jumpReleased(); return true; }
        if (keycode == keys.get("FOCUS")) { game.getKnight().cancelFocus(); return true; }
        return false;
    }

    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
