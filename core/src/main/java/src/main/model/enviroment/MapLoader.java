package src.main.model.enviroment;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
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

        for(int row = 0; row < mainLayer.getHeight(); row++) {
            for(int col = 0; col < mainLayer.getWidth(); col++) {
                if(mainLayer.getCell(col, row) != null){
                    float x = col * tileW;
                    float y = mapH - (row + 1) * tileH;
                    solidBlocks.add(new SolidBlock(x, y, tileW, tileH, false));
                }
            }
        }

        MapObjects objects = tiledMap.getLayers().get("logical").getObjects();
        for (MapObject object : objects) {
            String name = object.getName();
            if("SpawnPlayer".equals(name)){
                if(object instanceof PointMapObject p){
                    spawnPoint.set(p.getPoint().x, mapH - p.getPoint().y);
                }
            }else if(object instanceof RectangleMapObject r){
                solidBlocks.add(new SolidBlock(r.getRectangle().x, r.getRectangle().y, r.getRectangle().width, r.getRectangle().height, false));
            }
        }
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }
}
