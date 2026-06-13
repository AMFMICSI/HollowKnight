package src.main.model.enviroment;

import java.util.ArrayList;
import java.util.List;

public class Room {
    public List<Platform> platforms = new ArrayList<>();
    public Room(){
        platforms.add(new Platform(-2000, -10, 4000, 30));   // کف
        platforms.add(new Platform(-50, -10, 50, 550));       // دیوار چپ
        platforms.add(new Platform(3000, -10, 50, 550));      // دیوار راست

        platforms.add(new Platform(150, 130, 160, 20));       // سکوی ۱
        platforms.add(new Platform(450, 260, 160, 20));       // سکوی ۲
        platforms.add(new Platform(150, 390, 160, 20));       // سکوی ۳
        platforms.add(new Platform(450, 520, 160, 20));       // سکوی ۴

        platforms.add(new Platform(800, -10, 30, 120));       // دیوار وسط
        platforms.add(new Platform(1100, 150, 200, 20));      // سکو راست
        platforms.add(new Platform(1500, 350, 180, 20));      // سکوی بالا راست
    }
}
