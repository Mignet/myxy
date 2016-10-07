package com.v5ent.game.map;

import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.audio.AudioManager;
import com.v5ent.game.audio.AudioObserver;
import com.v5ent.game.audio.AudioSubject;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.pfa.GraphGenerator;
import com.v5ent.game.pfa.MyGraph;
import com.v5ent.game.sfx.ParticleEffectFactory;
import com.v5ent.game.tools.Utility;

public abstract class Map implements AudioSubject{
    private static final String TAG = Map.class.getSimpleName();

    public final static float UNIT_SCALE  = 1.0f;//1/16f
    
    //大地图切割成320*240小图
    public final static float SUB_BACKGROUD_WIDTH  = 10f;
    public final static float SUB_BACKGROUD_HEIGHT  = 7.5f;

    private Array<AudioObserver> _observers;

    //Map layers
    public final static String COLLISION_LAYER = "MAP_COLLISION_LAYER";
    protected final static String SPAWNS_LAYER = "MAP_SPAWNS_LAYER";
    protected final static String PORTAL_LAYER = "MAP_PORTAL_LAYER";
    protected final static String QUEST_ITEM_SPAWN_LAYER = "MAP_QUEST_ITEM_SPAWN_LAYER";
    protected final static String QUEST_DISCOVER_LAYER = "MAP_QUEST_DISCOVER_LAYER";
    protected final static String ENEMY_SPAWN_LAYER = "MAP_ENEMY_SPAWN_LAYER";
    protected final static String PARTICLE_EFFECT_SPAWN_LAYER = "PARTICLE_EFFECT_SPAWN_LAYER";

    public final static String BACKGROUND_IMAGE = "BACKGROUND_IMAGE";
    public final static String MINI_MAP = "MINI_MAP";
    public final static String FOREGROUND_IMAGE = "FOREGROUND_IMAGE";
    public final static String BACKGROUND_LAYER = "Background_Layer";
    public final static String GROUND_LAYER = "Ground_Layer";
    public final static String DECORATION_LAYER = "Decoration_Layer";

    public final static String LIGHTMAP_DAWN_LAYER = "MAP_LIGHTMAP_LAYER_DAWN";
    public final static String LIGHTMAP_AFTERNOON_LAYER = "MAP_LIGHTMAP_LAYER_AFTERNOON";
    public final static String LIGHTMAP_DUSK_LAYER = "MAP_LIGHTMAP_LAYER_DUSK";
    public final static String LIGHTMAP_NIGHT_LAYER = "MAP_LIGHTMAP_LAYER_NIGHT";

    //Starting locations
//    protected final static String PLAYER_START = "PLAYER_START";
    //卫兵
    protected final static String NPC_START = "NPC_START";

    protected Json _json;

    protected TiledMap _currentMap = null;
    public static String _mapName;
    
    private static int mapTileWidth;
    private static int mapTileHeight;
    private static int tileWidth;
    private static int tileHeight;
    protected static int mapPixelWidth;
    protected static int mapPixelHeight;
    private MyGraph graph;
    
    /**player的位置**/
    protected Vector2 _playerStart;
    protected Array<Vector2> _npcStartPositions;
    protected Hashtable<String, Vector2> _specialNPCStartPositions;

    protected TiledMapTileLayer _groudLayer = null;
    protected MapLayer _collisionLayer = null;
    protected MapLayer _portalLayer = null;
    protected MapLayer _spawnsLayer = null;
    protected MapLayer _questItemSpawnLayer = null;
    protected MapLayer _questDiscoverLayer = null;
    protected MapLayer _enemySpawnLayer = null;
    protected MapLayer _particleEffectSpawnLayer = null;

    protected MapLayer _lightMapDawnLayer = null;
    protected MapLayer _lightMapAfternoonLayer = null;
    protected MapLayer _lightMapDuskLayer = null;
    protected MapLayer _lightMapNightLayer = null;

    protected MapFactory.MapType _currentMapType;
    protected Array<Entity> _mapEntities;
    protected Array<Entity> _mapQuestEntities;
    protected Array<ParticleEffect> _mapParticleEffects;

    Map( MapFactory.MapType mapType, String fullMapPath){
        _json = new Json();
        _mapEntities = new Array<Entity>(10);
        _observers = new Array<AudioObserver>();
        _mapQuestEntities = new Array<Entity>();
        _mapParticleEffects = new Array<ParticleEffect>();
        _currentMapType = mapType;
        _playerStart = new Vector2(0,0);

        if( fullMapPath == null || fullMapPath.isEmpty() ) {
            Gdx.app.debug(TAG, "Map is invalid");
            return;
        }

        Utility.loadMapAsset(fullMapPath);
        if( Utility.isAssetLoaded(fullMapPath) ) {
        	_currentMap = Utility.getMapAsset(fullMapPath);
           tiledMapInit();
        }else{
            Gdx.app.debug(TAG, "Map not loaded");
            return;
        }

        _collisionLayer = _currentMap.getLayers().get(COLLISION_LAYER);
        if( _collisionLayer == null ){
            Gdx.app.debug(TAG, "No collision layer!");
        }
        //根据地面层生成地图行走路径
        _groudLayer = (TiledMapTileLayer)_currentMap.getLayers().get("Ground_Layer");
        if( _collisionLayer == null ){
            Gdx.app.error(TAG, "No Ground layer!");
        }
        setGraph(GraphGenerator.generateGraph(_groudLayer,getMapTileWidth(),getMapTileHeight(),getTileWidth(),getTileHeight()));

        _portalLayer = _currentMap.getLayers().get(PORTAL_LAYER);
        if( _portalLayer == null ){
            Gdx.app.debug(TAG, "No portal layer!");
        }

        _spawnsLayer = _currentMap.getLayers().get(SPAWNS_LAYER);
        if( _spawnsLayer == null ){
            Gdx.app.debug(TAG, "No spawn layer!");
        }else{
        	setPlayerStart(_playerStart);
        }

        _questItemSpawnLayer = _currentMap.getLayers().get(QUEST_ITEM_SPAWN_LAYER);
        if( _questItemSpawnLayer == null ){
            Gdx.app.debug(TAG, "No quest item spawn layer!");
        }

        _questDiscoverLayer = _currentMap.getLayers().get(QUEST_DISCOVER_LAYER);
        if( _questDiscoverLayer == null ){
            Gdx.app.debug(TAG, "No quest discover layer!");
        }

        _enemySpawnLayer = _currentMap.getLayers().get(ENEMY_SPAWN_LAYER);
        if( _enemySpawnLayer == null ){
            Gdx.app.debug(TAG, "No enemy layer found!");
        }

        _lightMapDawnLayer = _currentMap.getLayers().get(LIGHTMAP_DAWN_LAYER);
        if( _lightMapDawnLayer == null ){
        	_lightMapDawnLayer = new MapLayer();
        	_lightMapDawnLayer.setName(LIGHTMAP_DAWN_LAYER);
            Gdx.app.debug(TAG, "No dawn lightmap layer found! We 'll do it.");
        }

        _lightMapAfternoonLayer = _currentMap.getLayers().get(LIGHTMAP_AFTERNOON_LAYER);
        if( _lightMapAfternoonLayer == null ){
        	_lightMapAfternoonLayer = new MapLayer();
        	_lightMapAfternoonLayer.setName(LIGHTMAP_AFTERNOON_LAYER);
            Gdx.app.debug(TAG, "No afternoon lightmap layer found!");
        }


        _lightMapDuskLayer = _currentMap.getLayers().get(LIGHTMAP_DUSK_LAYER);
        if( _lightMapDuskLayer == null ){
        	_lightMapDuskLayer = new MapLayer();
        	_lightMapDuskLayer.setName(LIGHTMAP_DUSK_LAYER);
            Gdx.app.debug(TAG, "No dusk lightmap layer found!");
        }
        //night
        _lightMapNightLayer = _currentMap.getLayers().get(LIGHTMAP_NIGHT_LAYER);
        if( _lightMapNightLayer == null ){
            Gdx.app.debug(TAG, "No night lightmap layer found!");
        }else{
        	_lightMapNightLayer.setName(LIGHTMAP_NIGHT_LAYER);
        }

        _particleEffectSpawnLayer = _currentMap.getLayers().get(PARTICLE_EFFECT_SPAWN_LAYER);
        if( _particleEffectSpawnLayer == null ){
            Gdx.app.debug(TAG, "No particle effect spawn layer!");
        }

        _npcStartPositions = getNPCStartPositions();
        _specialNPCStartPositions = getSpecialNPCStartPositions();

        //Observers
        this.addObserver(AudioManager.getInstance());
    }

    void tiledMapInit() {
         MapProperties props =  _currentMap.getProperties();
         _mapName = props.get("mapName",String.class);
          setMapTileWidth(props.get("width",Integer.class));
          setMapTileHeight(props.get("height",Integer.class));
          setTileWidth(props.get("tilewidth",Integer.class));
          setTileHeight(props.get("tileheight",Integer.class));
          mapPixelWidth = getMapTileWidth() * getTileWidth();
          mapPixelHeight = getMapTileHeight() * getTileHeight();
	}

	public MapLayer getLightMapDawnLayer(){
        return _lightMapDawnLayer;
    }

    public MapLayer getLightMapAfternoonLayer(){
        return _lightMapAfternoonLayer;
    }

    public MapLayer getLightMapDuskLayer(){
        return _lightMapDuskLayer;
    }

    public MapLayer getLightMapNightLayer(){
        return _lightMapNightLayer;
    }

    public Array<Vector2> getParticleEffectSpawnPositions(ParticleEffectFactory.ParticleEffectType particleEffectType) {
        Array<Vector2> positions = new Array<Vector2>();
        if(_particleEffectSpawnLayer==null)return positions;
        for( MapObject object: _particleEffectSpawnLayer.getObjects()){
            String name = object.getName();

            if(     name == null || name.isEmpty() ||
                    !name.equalsIgnoreCase(particleEffectType.toString())){
                continue;
            }

            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            //Get center of rectangle
            float x = rect.getX() + (rect.getWidth()/2);
            float y = rect.getY() + (rect.getHeight()/2);

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            positions.add(new Vector2(x,y));
        }
        return positions;
    }

    public Array<Vector2> getQuestItemSpawnPositions(String objectName, String objectTaskID) {
        Array<MapObject> objects = new Array<MapObject>();
        Array<Vector2> positions = new Array<Vector2>();

        for( MapObject object: _questItemSpawnLayer.getObjects()){
            String name = object.getName();
            String taskID = (String)object.getProperties().get("taskID");

            if(        name == null || taskID == null ||
                       name.isEmpty() || taskID.isEmpty() ||
                       !name.equalsIgnoreCase(objectName) ||
                       !taskID.equalsIgnoreCase(objectTaskID)){
                continue;
            }
            //Get center of rectangle
            float x = ((RectangleMapObject)object).getRectangle().getX();
            float y = ((RectangleMapObject)object).getRectangle().getY();

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            positions.add(new Vector2(x,y));
        }
        return positions;
    }

    public Array<Entity> getMapEntities(){
        return _mapEntities;
    }

    public Array<Entity> getMapQuestEntities(){
        return _mapQuestEntities;
    }

    public Array<ParticleEffect> getMapParticleEffects(){
        return _mapParticleEffects;
    }

    public void addMapQuestEntities(Array<Entity> entities){
        _mapQuestEntities.addAll(entities);
    }

    public MapFactory.MapType getCurrentMapType(){
        return _currentMapType;
    }

    public Vector2 getPlayerStart() {
        return _playerStart;
    }

    public void setPlayerStart(Vector2 playerStart) {
        this._playerStart = playerStart;
    }

    protected void updateMapEntities(MapManager mapMgr, Batch batch, float delta){
        for( int i=0; i < _mapEntities.size; i++){
            _mapEntities.get(i).update(mapMgr, batch, delta);
        }
        for( int i=0; i < _mapQuestEntities.size; i++){
            _mapQuestEntities.get(i).update(mapMgr, batch, delta);
        }
    }

    protected void updateMapEffects(MapManager mapMgr, Batch batch, float delta){
        for( int i=0; i < _mapParticleEffects.size; i++){
            batch.begin();
            _mapParticleEffects.get(i).draw(batch, delta);
            batch.end();
        }
    }

    protected void dispose(){
        for( int i=0; i < _mapEntities.size; i++){
            _mapEntities.get(i).dispose();
        }
        for( int i=0; i < _mapQuestEntities.size; i++){
            _mapQuestEntities.get(i).dispose();
        }
        for( int i=0; i < _mapParticleEffects.size; i++){
            _mapParticleEffects.get(i).dispose();
        }
    }

    public MapLayer getCollisionLayer(){
        return _collisionLayer;
    }

    public MapLayer getPortalLayer(){
        return _portalLayer;
    }

    public MapLayer getQuestItemSpawnLayer(){
        return _questItemSpawnLayer;
    }

    public MapLayer getQuestDiscoverLayer(){
        return _questDiscoverLayer;
    }

    public MapLayer getEnemySpawnLayer() {
        return _enemySpawnLayer;
    }

    public TiledMap getCurrentTiledMap() {
        return _currentMap;
    }

    public Vector2 getPlayerStartUnitScaled(){
        Vector2 playerStart = _playerStart.cpy();
        playerStart.set(_playerStart.x * UNIT_SCALE, _playerStart.y * UNIT_SCALE);
        return playerStart;
    }

    private Array<Vector2> getNPCStartPositions(){
        Array<Vector2> npcStartPositions = new Array<Vector2>();

        for( MapObject object: _spawnsLayer.getObjects()){
            String objectName = object.getName();

            if( objectName == null || objectName.isEmpty() ){
                continue;
            }

            if( objectName.equalsIgnoreCase(NPC_START) ){
                //Get center of rectangle
                float x = ((RectangleMapObject)object).getRectangle().getX();
                float y = ((RectangleMapObject)object).getRectangle().getY();

                //scale by the unit to convert from map coordinates
                x *= UNIT_SCALE;
                y *= UNIT_SCALE;

                npcStartPositions.add(new Vector2(x,y));
            }
        }
        return npcStartPositions;
    }

    private Hashtable<String, Vector2> getSpecialNPCStartPositions(){
        Hashtable<String, Vector2> specialNPCStartPositions = new Hashtable<String, Vector2>();

        for( MapObject object: _spawnsLayer.getObjects()){
            String objectName = object.getName();

            if( objectName == null || objectName.isEmpty() ){
                continue;
            }

            //This is meant for all the special spawn locations, a catch all, so ignore known ones
            if(     objectName.equalsIgnoreCase(NPC_START)  ){
                continue;
            }

            //Get center of rectangle
            float x = ((RectangleMapObject)object).getRectangle().getX();
            float y = ((RectangleMapObject)object).getRectangle().getY();

            //scale by the unit to convert from map coordinates
            x *= UNIT_SCALE;
            y *= UNIT_SCALE;

            specialNPCStartPositions.put(objectName, new Vector2(x,y));
        }
        return specialNPCStartPositions;
    }

    abstract public void unloadMusic();
    abstract public void loadMusic();

    @Override
    public void addObserver(AudioObserver audioObserver) {
        _observers.add(audioObserver);
    }

    @Override
    public void removeObserver(AudioObserver audioObserver) {
        _observers.removeValue(audioObserver, true);
    }

    @Override
    public void removeAllObservers() {
        _observers.removeAll(_observers, true);
    }

    @Override
    public void notify(AudioObserver.AudioCommand command, AudioObserver.AudioTypeEvent event) {
        for(AudioObserver observer: _observers){
            observer.onNotify(command, event);
        }
    }

	/**
	 * @return the graph
	 */
	public MyGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(MyGraph graph) {
		this.graph = graph;
	}

	public TiledMapTileLayer getGroudLayer() {
		return _groudLayer;
	}

	public void setGroudLayer(TiledMapTileLayer _groudLayer) {
		this._groudLayer = _groudLayer;
	}

	/**
	 * @return the mapTileWidth
	 */
	public int getMapTileWidth() {
		return mapTileWidth;
	}

	/**
	 * @param mapTileWidth the mapTileWidth to set
	 */
	public void setMapTileWidth(int mapTileWidth) {
		Map.mapTileWidth = mapTileWidth;
	}

	/**
	 * @return the mapTileHeight
	 */
	public int getMapTileHeight() {
		return mapTileHeight;
	}

	/**
	 * @param mapTileHeight the mapTileHeight to set
	 */
	public void setMapTileHeight(int mapTileHeight) {
		Map.mapTileHeight = mapTileHeight;
	}

	/**
	 * @return the tileWidth
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * @param tileWidth the tileWidth to set
	 */
	public void setTileWidth(int tileWidth) {
		Map.tileWidth = tileWidth;
	}

	/**
	 * @return the tileHeight
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * @param tileHeight the tileHeight to set
	 */
	public void setTileHeight(int tileHeight) {
		Map.tileHeight = tileHeight;
	}
	
}
