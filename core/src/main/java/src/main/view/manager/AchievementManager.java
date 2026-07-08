package src.main.view.manager;

import java.util.*;

public class AchievementManager {
    private final Map<String, AchievementDef> defs = new LinkedHashMap<>();
    private final Set<String> unlocked = new HashSet<>();
    private final List<Listener> listeners = new ArrayList<>();
    private final Set<String> killedEnemyTypes = new HashSet<>();
    private int pogoChainCount;
    private float lastPogoTime;

    public static class AchievementDef {
        public final String id, title, description;
        public AchievementDef(String id, String title, String description) {
            this.id = id; this.title = title; this.description = description;
        }
    }

    public interface Listener {
        void onAchievementUnlocked(String id);
    }

    public AchievementManager() {
        defs.put("defeat_false_knight", new AchievementDef("defeat_false_knight",
            "Defeat False Knight", "Vanquish the False Knight"));
        defs.put("true_hunter", new AchievementDef("true_hunter",
            "True Hunter", "Kill every enemy type in the game"));
        defs.put("airborne", new AchievementDef("airborne",
            "Airborne", "Perform 3 consecutive pogo bounces without touching the ground"));
        defs.put("completion", new AchievementDef("completion",
            "Completion", "Finish the game"));
        defs.put("speedrun", new AchievementDef("speedrun",
            "Speedrun", "Finish the game within 5 minutes"));
    }

    public void addListener(Listener l) { listeners.add(l); }
    public void removeListener(Listener l) { listeners.remove(l); }

    public void unlock(String id) {
        if (unlocked.contains(id) || !defs.containsKey(id)) return;
        unlocked.add(id);
        for (Listener l : listeners) l.onAchievementUnlocked(id);
    }

    public boolean isUnlocked(String id) { return unlocked.contains(id); }
    public Set<String> getAll() { return Collections.unmodifiableSet(unlocked); }
    public Collection<AchievementDef> getDefs() { return defs.values(); }
    public AchievementDef getDef(String id) { return defs.get(id); }

    public void loadFrom(Collection<String> saved) {
        unlocked.clear();
        unlocked.addAll(saved);
    }

    public List<AchievementDef> getUnlockedDefs() {
        List<AchievementDef> r = new ArrayList<>();
        for (AchievementDef d : defs.values())
            if (unlocked.contains(d.id)) r.add(d);
        return r;
    }

    public List<AchievementDef> getLockedDefs() {
        List<AchievementDef> r = new ArrayList<>();
        for (AchievementDef d : defs.values())
            if (!unlocked.contains(d.id)) r.add(d);
        return r;
    }

    public void onEnemyKilled(String enemyType) {
        killedEnemyTypes.add(enemyType);
        String[] allTypes = {"Crawlid", "CrystalHunter", "HuskHornhead", "CrystalGuardian", "FalseKnight", "Zote"};
        if (killedEnemyTypes.size() >= allTypes.length) {
            boolean allFound = true;
            for (String t : allTypes) {
                if (!killedEnemyTypes.contains(t)) { allFound = false; break; }
            }
            if (allFound) unlock("true_hunter");
        }
    }

    public void onPogoBounce(float currentTime) {
        if (currentTime - lastPogoTime < 0.5f) {
            pogoChainCount++;
        } else {
            pogoChainCount = 1;
        }
        lastPogoTime = currentTime;
        if (pogoChainCount >= 3) unlock("airborne");
    }

    public void onLand() {
        pogoChainCount = 0;
    }

    public void onBossDefeated() {
        unlock("defeat_false_knight");
    }

    public void onGameCompleted(float playTime) {
        unlock("completion");
        if (playTime < 300) unlock("speedrun");
    }
}
