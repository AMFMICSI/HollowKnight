package src.main.model.enviroment;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.view.Phats;

import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    private TiledMap tiledMap;
    private List<SolidBlock> solidBlocks = new ArrayList<>();
    private List<Spike> spikes = new ArrayList<>();
    private List<ClimbableWall> climbableWalls = new ArrayList<>();
    private Vector2 spawnPoint = new Vector2();
    private Vector2 zoteSpawnPoint = new Vector2();
    private List<EnemySpawnInfo> enemySpawnInfos = new ArrayList<>();
    List<Rectangle> zoneRects = new ArrayList<>();
    private List<Rectangle> bossGates = new ArrayList<>();
    private List<Vector2> safePoints = new ArrayList<>();

    public TiledMap getTiledMap() { return tiledMap; }
    public List<SolidBlock> getSolidBlocks() { return solidBlocks; }
    public List<Spike> getSpikes() { return spikes; }
    public List<ClimbableWall> getClimbableWalls() { return climbableWalls; }
    public Vector2 getSpawnPoint() { return spawnPoint; }
    public List<EnemySpawnInfo> getEnemySpawnInfos() { return enemySpawnInfos; }
    public Vector2 getZoteSpawnPoint() { return zoteSpawnPoint; }
    public List<Rectangle> getZones() { return zoneRects; }
    public List<Rectangle> getBossGates() { return bossGates; }
    public List<Vector2> getSafePoints() { return safePoints; }

    public static class EnemySpawnInfo {
        public Vector2 position;
        public String enemyType;
        public Rectangle zone;
    }

    public MapLoader() {
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.projectFilePath = Phats.MapProjectFile.getText();
        tiledMap = new TmxMapLoader().load(Phats.Map.getText(), params);

        MapObjects objects = tiledMap.getLayers().get("logical").getObjects();

        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject r) {
                String name = obj.getName();
                Rectangle rect = r.getRectangle();
                if ("SolidBlock".equals(name))
                    solidBlocks.add(new SolidBlock(rect.x, rect.y, rect.width, rect.height));
                if ("Spike".equals(name))
                    spikes.add(new Spike(rect.x, rect.y, rect.width, rect.height));
                if ("WallClimb".equals(name)) {
                    climbableWalls.add(new ClimbableWall(rect.x, rect.y, rect.width, rect.height));
                    solidBlocks.add(new SolidBlock(rect.x, rect.y, rect.width, rect.height));
                }
            }
        }

        // Collect Zone rectangles once
        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject r && "Zone".equals(obj.getName())) {
                Rectangle rect = r.getRectangle();
                zoneRects.add(new Rectangle(rect));
            }
        }

        // Collect BossGate rectangles
        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject r && "BossGate".equals(obj.getName())) {
                Rectangle rect = r.getRectangle();
                bossGates.add(new Rectangle(rect));
            }
        }

        for (MapObject obj : objects) {
            if ("SpawnPlayer".equals(obj.getName()) && obj instanceof PointMapObject p) {
                spawnPoint.set(p.getPoint().x, p.getPoint().y);
                continue;
            }
            if ("SpawnZote".equals(obj.getName()) && obj instanceof PointMapObject p) {
                zoteSpawnPoint.set(p.getPoint().x, p.getPoint().y);
                continue;
            }
            if ("SafePoint".equals(obj.getName()) && obj instanceof PointMapObject p) {
                safePoints.add(new Vector2(p.getPoint().x, p.getPoint().y));
                continue;
            }
            if (obj instanceof PointMapObject p) {
                MapProperties props = obj.getProperties();
                String enemyType = props.get("enemyType", String.class);
                if (enemyType == null) continue;
                EnemySpawnInfo info = new EnemySpawnInfo();
                info.position = new Vector2(p.getPoint().x, p.getPoint().y);
                info.enemyType = enemyType;
                for (Rectangle zr : zoneRects) {
                    if (zr.contains(info.position.x, info.position.y)) {
                        info.zone = zr;
                        break;
                    }
                }
                enemySpawnInfos.add(info);
            }
        }
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }
}
