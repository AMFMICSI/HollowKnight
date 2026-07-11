package src.main.model.entity.charm;

import java.util.Set;

public class CharmEffectCalculator {

    public static int getAttackDamage(Set<CharmType> equippedCharms) {
        return equippedCharms.contains(CharmType.UNBREAKABLE_STRENGTH) ? 2 : 1;
    }

    public static float getAttackDuration(Set<CharmType> equippedCharms, float baseDuration) {
        return equippedCharms.contains(CharmType.QUICK_SLASH) ? 0.15f : baseDuration;
    }

    public static float getFocusDuration(Set<CharmType> equippedCharms, float baseDuration) {
        return equippedCharms.contains(CharmType.QUICK_FOCUS) ? 0.75f : baseDuration;
    }

    public static float getDashCooldown(Set<CharmType> equippedCharms, float baseCooldown) {
        return equippedCharms.contains(CharmType.DASHMASTER) ? 0.25f : baseCooldown;
    }

    public static int getSoulPerHit(Set<CharmType> equippedCharms, int baseSoul) {
        return equippedCharms.contains(CharmType.SOUL_CATCHER) ? 17 : baseSoul;
    }

    public static int getSpellDamage(Set<CharmType> equippedCharms) {
        return equippedCharms.contains(CharmType.VOID_HEART) ? 2 : 1;
    }
}
