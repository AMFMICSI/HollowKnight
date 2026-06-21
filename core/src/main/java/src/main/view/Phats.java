package src.main.view;

public enum Phats {
    MenuPointerLeft("menus/menuPointer/MenuPointerLeft.png"),
    MenuPointerRight("menus/menuPointer/MenuPointerRight.png"),
    Map("maps/crystalPeaks/crystalPeaks.tmx"),
//    Map("maps/forgottenCrossroads/forgottenCrossroads.tmx"),
    MapProjectFile("maps/hollowKnight.tiled-project"),
    ;

    private String text;
    private Phats(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }
}
