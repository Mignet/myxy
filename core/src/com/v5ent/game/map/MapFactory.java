package com.v5ent.game.map;

import java.util.Hashtable;

public class MapFactory {
    //All maps for the game
    private static Hashtable<MapType,Map> _mapTable = new Hashtable<MapType, Map>();
    public static enum MapType{
    	HOME,
        TOP_WORLD,
        TOWN,
        CASTLE_OF_DOOM
    }

    static public Map getMap(MapType mapType){
        Map map = null;
        switch(mapType){
            case TOP_WORLD:
                map = _mapTable.get(MapType.TOP_WORLD);
                if( map == null ){
                    map = new TopWorldMap();
                    _mapTable.put(MapType.TOP_WORLD, map);
                }else{
                	map.tiledMapInit();
                }
                break;
            case HOME:
                map = _mapTable.get(MapType.HOME);
                if( map == null ){
                    map = new HomeMap();
                    _mapTable.put(MapType.HOME, map);
                }else{
                	map.tiledMapInit();
                }
                break;
            case TOWN:
            	map = _mapTable.get(MapType.TOWN);
            	if( map == null ){
            		map = new TownMap();
            		_mapTable.put(MapType.TOWN, map);
            	}else{
            		map.tiledMapInit();
            	}
            	break;
            case CASTLE_OF_DOOM:
                map = _mapTable.get(MapType.CASTLE_OF_DOOM);
                if( map == null ){
                    map = new CastleDoomMap();
                    _mapTable.put(MapType.CASTLE_OF_DOOM, map);
                }else{
                	map.tiledMapInit();
                }
                break;
            default:
                break;
        }
        return map;
    }

    public static void clearCache(){
        for( Map map: _mapTable.values()){
            map.dispose();
        }
        _mapTable.clear();
    }
}
