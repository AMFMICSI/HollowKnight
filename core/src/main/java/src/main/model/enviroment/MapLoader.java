package src.main.model.enviroment;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    public TiledMap tiledMap;
    public List<SolidBlock> solidBlocks = new ArrayList<>();
    public Vector2 spawnPoint = new Vector2();

    public MapLoader(String filePath) {
        tiledMap = new TmxMapLoader().load(filePath);

        TiledMapTileLayer mainLayer = (TiledMapTileLayer) tiledMap.getLayers().get("main");

        float tileW = mainLayer.getTileWidth();
        float tileH = mainLayer.getTileHeight();
        float mapH = mainLayer.getHeight() * tileH;



        MapObjects objects = tiledMap.getLayers().get("logical").getObjects();
        for (MapObject object : objects) {
            String name = object.getName();

            if ("SpawnPlayer".equals(name) && object instanceof PointMapObject p) {
                spawnPoint.set(p.getPoint().x, p.getPoint().y);

            } else if (object instanceof RectangleMapObject r) {
                Rectangle rect = r.getRectangle();
                float x = rect.x;
                float y = rect.y;

                boolean deadly = "Spike".equals(name);
                solidBlocks.add(new SolidBlock(x, y, rect.width, rect.height, deadly));
            }
        }
        System.out.println("SolidBlocks count: " + solidBlocks.size());
        if (solidBlocks.size() > 0) {
            SolidBlock first = solidBlocks.get(0);
            System.out.println("First block: (" + first.bounds.x + ", " + first.bounds.y + ", " + first.bounds.width + ", " + first.bounds.height + ")");
        }
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }
}
