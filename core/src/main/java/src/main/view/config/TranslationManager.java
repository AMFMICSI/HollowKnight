package src.main.view.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.Reader;
import java.util.Properties;

public class TranslationManager {
    private static final Properties props = new Properties();
    private static String currentLang = null;

    public static String get(String key) {
        String lang = GameSettings.getInstance().getLanguage();
        if (!lang.equals(currentLang)) {
            load(lang);
        }
        String value = props.getProperty(key);
        return value != null ? value : key;
    }

    public static void reload() {
        currentLang = null;
    }

    private static void load(String lang) {
        props.clear();
        currentLang = lang;
        try {
            FileHandle file = Gdx.files.internal("lang/strings_" + lang + ".properties");
            if (file.exists()) {
                try (Reader reader = file.reader("UTF-8")) {
                    props.load(reader);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
