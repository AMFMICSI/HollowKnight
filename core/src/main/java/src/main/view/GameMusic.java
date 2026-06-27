package src.main.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public enum GameMusic {
    MENU(Phats.TitleMusic.getText()),
    ;
    private final String path;
    private Music music;

    GameMusic(String path) {
        this.path = path;
    }

    public Music getMusic() {
        if(music == null) {
            music = Gdx.audio.newMusic(Gdx.files.internal(path));
        }
        return music;
    }
    public void play() {
        Music m = getMusic();
        m.setLooping(true);
        m.setVolume(GameSettings.getInstance().isMusicMuted() ? 0 : GameSettings.getInstance().getMusicVolume());
        m.play();
    }
    public void toggleMute() {
        boolean muted = !GameSettings.getInstance().isMusicMuted();
        GameSettings.getInstance().setMusicMuted(muted);
        getMusic().setVolume(muted ? 0 : GameSettings.getInstance().getMusicVolume());
    }
    public boolean isMuted() { return GameSettings.getInstance().isMusicMuted(); }

    public void stop(){
        if(music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }

    public void setVolume(float vol) {
        getMusic().setVolume(vol);
    }

    public float getVolume() {
        return getMusic().getVolume();
    }
}

