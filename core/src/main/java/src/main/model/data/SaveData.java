package src.main.model.data;

import com.badlogic.gdx.utils.Json;
import src.main.model.entity.charm.CharmType;
import src.main.model.entity.knight.Knight;
import src.main.view.manager.AchievementManager;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    public float playerX, playerY;
    public int hp, soul;
    public List<String> equippedCharms;
    public List<String> unlockedAchievements;
    public boolean bossDefeated;
    public float playTime;

    public void applyTo(Knight knight, AchievementManager achievements) {
        knight.getPosition().set(playerX, playerY);
        knight.setSpawnPoint(playerX, playerY);
        knight.setHp(hp);
        knight.setSoul(soul);
        knight.clearCharms();
        if (equippedCharms != null) {
            for (String name : equippedCharms) {
                try { knight.equipCharm(CharmType.valueOf(name)); } catch (Exception ignored) {}
            }
        }
        if (unlockedAchievements != null)
            achievements.loadFrom(unlockedAchievements);
    }

    public static void save(Knight knight, AchievementManager achievements, float playTime, int slot) {
        SaveData data = new SaveData();
        data.playerX = knight.getPosition().x;
        data.playerY = knight.getPosition().y;
        data.hp = knight.getHp();
        data.soul = knight.getSoul();
        data.equippedCharms = new ArrayList<>();
        for (CharmType c : knight.getEquippedCharms())
            data.equippedCharms.add(c.name());
        data.unlockedAchievements = new ArrayList<>(achievements.getAll());
        data.bossDefeated = achievements.isUnlocked("defeat_false_knight");
        data.playTime = playTime;

        Json json = new Json();
        DatabaseManager.getInstance().saveGame(slot, json.toJson(data));
    }

    public static SaveData load(int slot) {
        String dataJson = DatabaseManager.getInstance().loadGame(slot);
        if (dataJson == null) return null;
        Json json = new Json();
        return json.fromJson(SaveData.class, dataJson);
    }

    public static boolean slotExists(int slot) {
        return DatabaseManager.getInstance().slotExists(slot);
    }

    public static void deleteSlot(int slot) {
        DatabaseManager.getInstance().deleteSlot(slot);
    }
}
