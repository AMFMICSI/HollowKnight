package src.main.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public enum GameMusic {
    MENU(Phats.TitleMusic, Type.MUSIC),
    NAIL_SLASH(Phats.NailSlash, Type.SFX),
    HERO_DAMAGE(Phats.HeroDamage, Type.SFX),
    SOUL_PICKUP(Phats.SoulPickup, Type.SFX),
    FOCUS_HEAL(Phats.FocusHeal, Type.SFX),
    DASH(Phats.HeroDash, Type.SFX),
    BOSS_DEFEAT(Phats.BossDefeat, Type.SFX);

    private enum Type { MUSIC, SFX }

    private final String path;
    private final Type type;
    private Music music;
    private Sound sound;

    GameMusic(Phats phats, Type type) {
        this.path = phats.getText();
        this.type = type;
    }

    public void play() {
        if (type == Type.SFX) {
            if (GameSettings.getInstance().isSfxMuted()) return;
            if (sound == null)
                sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sound.play(GameSettings.getInstance().getSfxVolume());
        } else {
            Music m = getMusic();
            m.setLooping(true);
            m.setVolume(GameSettings.getInstance().isMusicMuted() ? 0 : GameSettings.getInstance().getMusicVolume());
            m.play();
        }
    }

    public Music getMusic() {
        if (type != Type.MUSIC) return null;
        if (music == null)
            music = Gdx.audio.newMusic(Gdx.files.internal(path));
        return music;
    }

    public void toggleMute() {
        if (type != Type.MUSIC) return;
        boolean muted = !GameSettings.getInstance().isMusicMuted();
        GameSettings.getInstance().setMusicMuted(muted);
        getMusic().setVolume(muted ? 0 : GameSettings.getInstance().getMusicVolume());
    }

    public boolean isMuted() { return GameSettings.getInstance().isMusicMuted(); }

    public void stop() {
        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }

    public void setVolume(float vol) {
        if (type != Type.MUSIC) return;
        getMusic().setVolume(vol);
    }

    public float getVolume() {
        if (type != Type.MUSIC) return 0;
        return getMusic().getVolume();
    }

    public static void disposeAll() {
        for (GameMusic g : values()) {
            if (g.music != null) { g.music.dispose(); g.music = null; }
            if (g.sound != null) { g.sound.dispose(); g.sound = null; }
        }
    }
}
