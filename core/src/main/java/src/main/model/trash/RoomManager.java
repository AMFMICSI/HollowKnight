//package src.main.model;
//
//import java.util.HashMap;
//
//public class RoomManager {
//    private HashMap<String, Room> loadedRooms = new HashMap<>();
//
//    public Room loadRoom(String roomId, Game game) {
//        if (loadedRooms.containsKey(roomId))
//            return loadedRooms.get(roomId); // از cache بخون
//
//        Room room = RoomLoader.fromFile("rooms/" + roomId + ".tmx");
//
//        // مقداردهی اولیه وضعیت اشیاء از WorldState
//        for (Door door : room.doors) {
//            door.isOpen = game.worldState.getFlag(door.id + "_open");
//        }
//        for (Enemy enemy : room.enemies) {
//            enemy.isAlive = game.worldState.getFlag(enemy.id + "_alive");
//        }
//
//        loadedRooms.put(roomId, room);
//        return room;
//    }
//
//    public void unloadRoom(String roomId) {
//        loadedRooms.remove(roomId); // GC خودش پاک میکنه
//    }
//}
