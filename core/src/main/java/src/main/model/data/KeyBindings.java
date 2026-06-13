package src.main.model.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.Map;

public class KeyBindings {
    private final Map<String, Integer> bindings = new HashMap<>();
    private static final Map<String, Integer> DEFAULTS = new HashMap<>();

    static {
        DEFAULTS.put("MOVE_LEFT", Input.Keys.LEFT);
        DEFAULTS.put("MOVE_RIGHT", Input.Keys.RIGHT);
        DEFAULTS.put("JUMP", Input.Keys.Z);
        DEFAULTS.put("DASH", Input.Keys.C);
        DEFAULTS.put("ATTACK", Input.Keys.X);
        DEFAULTS.put("POGO", Input.Keys.DOWN);       // ↓ + X همزمان
        DEFAULTS.put("FOCUS", Input.Keys.A);
        DEFAULTS.put("SPELL_VENGEFUL", Input.Keys.Q);  // Vengeful Spirit
        DEFAULTS.put("SPELL_WRAITHS", Input.Keys.W);   // Howling Wraiths
        DEFAULTS.put("INVENTORY", Input.Keys.I);
        DEFAULTS.put("PAUSE", Input.Keys.ESCAPE);
        DEFAULTS.put("INTERACT", Input.Keys.E);
        DEFAULTS.put("DIALOGUE_NEXT", Input.Keys.ENTER);
    }

    public KeyBindings() {
        load();
        if (bindings.isEmpty()) {
            resetToDefaults();
            save();
        }
    }
    public int get(String action) { return bindings.get(action); }
    public void set(String action, int keycode) { bindings.put(action, keycode); }
    public void resetToDefaults() {
        bindings.clear();
        bindings.putAll(DEFAULTS);
    }
    public Map<String, Integer> getAll() { return bindings; }

    public static String keyName(int keycode) {
        if (keycode == Input.Keys.LEFT) return "←";
        if (keycode == Input.Keys.RIGHT) return "→";
        if (keycode == Input.Keys.UP) return "↑";
        if (keycode == Input.Keys.DOWN) return "↓";
        String name = Input.Keys.toString(keycode);
        return name != null ? name : "KEY_" + keycode;
    }

    public void save(){
        Json json = new Json();
        String data = json.toJson(bindings);
        Gdx.files.local("config/keyBindings.json").writeString(data, false);
    }

    @SuppressWarnings("unchecked")
    public void load(){
        FileHandle file =  Gdx.files.local("config/keyBindings.json");
        if(file.exists()){
            Json json = new Json();
            bindings.clear();
            bindings.putAll(json.fromJson(HashMap.class, file.readString()));
        }
    }
}
