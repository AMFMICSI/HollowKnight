package src.main.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameSettings {
    private static final String PREFS = "hollowknight";
    private static GameSettings instance;
    private final Preferences prefs;

    private GameSettings() { prefs = Gdx.app.getPreferences(PREFS); }

    public static GameSettings getInstance() {
        if (instance == null) instance = new GameSettings();
        return instance;
    }

    public float getMusicVolume() { return prefs.getFloat("music_volume", 0.5f); }
    public void setMusicVolume(float v) { prefs.putFloat("music_volume", Math.min(1, Math.max(0, v))).flush(); }
    public boolean isMusicMuted() { return prefs.getBoolean("music_muted", false); }
    public void setMusicMuted(boolean v) { prefs.putBoolean("music_muted", v).flush(); }

    public float getSfxVolume() { return prefs.getFloat("sfx_volume", 1f); }
    public void setSfxVolume(float v) { prefs.putFloat("sfx_volume", Math.min(1, Math.max(0, v))).flush(); }
    public boolean isSfxMuted() { return prefs.getBoolean("sfx_muted", false); }
    public void setSfxMuted(boolean v) { prefs.putBoolean("sfx_muted", v).flush(); }

    public void resetSound() {
        setMusicVolume(0.5f); setMusicMuted(false);
        setSfxVolume(1f);     setSfxMuted(false);
    }

    public float getBrightness() { return prefs.getFloat("brightness", 1f); }
    public void setBrightness(float v) { prefs.putFloat("brightness", Math.min(1, Math.max(0.2f, v))).flush(); }

    public String getLanguage() { return prefs.getString("language", "en"); }
    public void setLanguage(String v) { prefs.putString("language", v).flush(); }

    private boolean debugMode;
    public boolean isDebugMode() { return debugMode; }
    public void setDebugMode(boolean v) { debugMode = v; }
}
