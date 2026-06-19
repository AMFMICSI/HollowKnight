package src.main.model.enviroment;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import src.main.view.Phats;

import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    public TiledMap tiledMap;
    public List<SolidBlock> solidBlocks = new ArrayList<>();
    public Vector2 spawnPoint = new Vector2();

    public List<EnemySpawnInfo> enemySpawnInfos = new ArrayList<>();
    public static class EnemySpawnInfo {
        public Vector2 position;
        public String enemyType;
        public Rectangle zone;
    }

    public MapLoader() {
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.projectFilePath = Phats.MapProjectFile.getText();
        tiledMap = new TmxMapLoader().load(Phats.Map.getText() , params);

        MapObjects objects = tiledMap.getLayers().get("logical").getObjects();
//        TiledMapTileLayer mainLayer = (TiledMapTileLayer) tiledMap.getLayers().get("main");
//        float tileW = mainLayer.getTileWidth();
//        float tileH = mainLayer.getTileHeight();
//        float mapH = mainLayer.getHeight() * tileH;

        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject r) {
                if ("SolidBlock".equals(obj.getName()))
                    solidBlocks.add(new SolidBlock(
                        r.getRectangle().x, r.getRectangle().y,
                        r.getRectangle().width, r.getRectangle().height, false));
            }
        }
        for (MapObject obj : objects) {
            if ("SpawnPlayer".equals(obj.getName()) && obj instanceof PointMapObject p) {
                spawnPoint.set(p.getPoint().x, p.getPoint().y);
                continue;
            }
            if (obj instanceof PointMapObject p) {
                MapProperties props = obj.getProperties();
                String enemyType = props.get("enemyType", String.class);
                if (enemyType == null) continue;
                EnemySpawnInfo info = new EnemySpawnInfo();
                info.position = new Vector2(p.getPoint().x, p.getPoint().y);
                info.enemyType = enemyType;
                if (props.get("zoneRef") instanceof RectangleMapObject zr)
                    info.zone = zr.getRectangle();
                enemySpawnInfos.add(info);
            }
        }
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }
}
