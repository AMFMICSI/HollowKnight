package src.main.view;

public enum Phats {
    MenuPointerLeft("menus/menuPointer/MenuPointerLeft.png"),
    MenuPointerRight("menus/menuPointer/MenuPointerRight.png"),
    Map("maps/MainMap.tmx"),
    MapProjectFile("maps/hollowKnight.tiled-project"),
    MainBackGround("menus/mainBackGround.png"),
    Cursor("cursor/Cursor.png"),
    UiSkin("ui/skin/Hollow Knight skin.json"),
    KnightAtlas("atlases/knight.atlas"),
    EffectsAtlas("atlases/effects.atlas"),
    CharmsAtlas("atlases/charms.atlas"),
    CrawlidAtlas("atlases/crawlid.atlas"),
    CrystalHunterAtlas("atlases/crystalHunter.atlas"),
    HuskHornheadAtlas("atlases/huskHornhead.atlas"),
    CrystalGuardianAtlas("atlases/crystalGuardian.atlas"),
    FalseKnightAtlas("atlases/falseKnight.atlas"),
    ZoteAtlas("atlases/zote.atlas"),
    HudAtlas("atlases/hud.atlas"),
    TitleMusic("music/Title.wav"),
    CharmClickSound("music/charm_click_in.wav"),
    BrummGrunt1("music/Brumm_grunt_01.wav"),
    BrummGruntDouble("music/Brumm_grunt_double.wav"),
    KeyBindingsConfig("config/keyBindings.json"),
    NailSlash("music/sword_hit_reject.wav"),
    HeroDamage("music/hero_damage.wav"),
    SoulPickup("music/soul_pickup_1.wav"),
    FocusHeal("music/spa_heal.wav"),
    HeroDash("music/hero_dash.wav"),
    BossDefeat("music/Boss Defeat.wav"),
    ;

    private final String text;

    Phats(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }

    public static String saveSlotPath(int slot) {
        return "config/save_" + slot + ".json";
    }
}
