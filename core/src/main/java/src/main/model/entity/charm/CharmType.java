package src.main.model.entity.charm;

public enum CharmType {
    SOUL_CATCHER("Soul Catcher", "افزایش روح دریافتی", 1),
    DASHMASTER("Dashmaster", "کاهش کول‌داون دش", 1),
    UNBREAKABLE_STRENGTH("Unbreakable Strength", "افزایش دمیج سلاح", 1),
    QUICK_SLASH("Quick Slash", "افزایش سرعت ضربه", 1),
    QUICK_FOCUS("Quick Focus", "افزایش سرعت فوکوس", 1),
    HEAVY_BLOW("Heavy Blow", "افزایش ناک‌بک", 1),
    SHARP_SHADOW("Sharp Shadow", "عبور از دشمنان در دش + دمیج", 2),
    VOID_HEART("Void Heart", "افزایش ۵۰٪ دمیج جادو + انیمیشن سیاه", 2);

    final String name, description;
    final int notchCost;

    CharmType(String name, String description, int notchCost) {
        this.name = name;
        this.description = description;
        this.notchCost = notchCost;
    }
    public String getDescription() {
        return description;
    }
    public int getNotchCost() {
        return notchCost;
    }
}
