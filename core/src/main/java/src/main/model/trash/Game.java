//package src.main.model;
//
//public class Game {
//    public Knight player;
//    public Room currentRoom;
//    public RoomManager roomManager; // لود کردن اتاق‌ها از فایل
//    public WorldState worldState;   // وضعیت کل دنیا (کدوم در بازه، کدوم باس مرده)
//
//    public void update(){
//        // چک کن پلیر به مرز اتاق رسیده؟
//        for (RoomTransition t : currentRoom.transitions) {
//            if (t.triggerArea.contains(player.position)) {
//                Room nextRoom = roomManager.loadRoom(t.targetRoomId);
//                player.position.set(t.spawnPosition);
//                currentRoom = nextRoom;
//                break;
//            }
//        }
//    }
//}
