package src.main.model.entity.charm;

public enum CharmType {
    SOUL_CATCHER("Soul Catcher", "Increases soul gained from hitting enemies", 1),
    DASHMASTER("Dashmaster", "Reduces dash cooldown", 1),
    UNBREAKABLE_STRENGTH("Unbreakable Strength", "Increases nail damage", 1),
    QUICK_SLASH("Quick Slash", "Increases attack speed", 1),
    QUICK_FOCUS("Quick Focus", "Increases focus speed", 1),
    HEAVY_BLOW("Heavy Blow", "Increases knockback force", 1),
    SHARP_SHADOW("Sharp Shadow", "Dash through enemies dealing damage", 1),
    VOID_HEART("Void Heart", "Increases spell damage by 50%", 1);

    private final String name;
    private final String description;
    private final int notchCost;

    CharmType(String name, String description, int notchCost) {
        this.name = name;
        this.description = description;
        this.notchCost = notchCost;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getNotchCost() { return notchCost; }
}
