package com.v5ent.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.ecs.Component;
import com.v5ent.game.ecs.ComponentObserver;
import com.v5ent.game.ecs.Component.MESSAGE;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.menu.ClockActor;
import com.v5ent.game.profile.ProfileManager;
import com.v5ent.game.profile.ProfileObserver;

public class MapManager implements ProfileObserver {
    private static final String TAG = MapManager.class.getSimpleName();
    
    private static final Vector2 START_POINT=new Vector2(16,14);
    
    private Camera _camera;
    private boolean _mapChanged = false;
    private boolean isDream = false;
    
    private Map _currentMap;
    
    private ScollMap scollMap = null;
    protected Json _json;
    private Entity _player;
    private Vector2 _target;
    private Entity _currentSelectedEntity = null;
    private MapLayer _currentLightMap = null;
    private MapLayer _previousLightMap = null;
    private ClockActor.TimeOfDay _timeOfDay = null;
    private float _currentLightMapOpacity = 0;
    private float _previousLightMapOpacity = 1;
    private boolean _timeOfDayChanged = false;

    public MapManager(){
    	_json = new Json();
    }

    /**
     * 获取角色所在地图坐标
     * @return
     */
    public Vector2 getPlayerPositionInMap(){
    	return new Vector2((int)_player.getCurrentPosition().x/32,(int)_player.getCurrentPosition().y/32);
    }
    
    /**
     * 获取角色所在地图坐标
     * @return
     */
    public Vector2 getPlayerPositionInWorld(){
    	return _player.getCurrentPosition();
    }
    
    @Override
    public void onNotify(ProfileManager profileManager, ProfileEvent event) {
        switch(event){
            case PROFILE_LOADED:
                String currentMap = profileManager.getProperty("currentMapType", String.class);
                Vector2 playerPosition = profileManager.getProperty("playerPosition", Vector2.class);
                MapFactory.MapType mapType;
                if( currentMap == null || currentMap.isEmpty() ){
                    mapType = MapFactory.MapType.TOWN;
                }else{
                    mapType = MapFactory.MapType.valueOf(currentMap);
                }
                if(playerPosition==null || playerPosition.isZero()){
                	playerPosition =  START_POINT.cpy().scl(32);
                }
                //dead and continue
//                if(playerPosition.x%32==0&&playerPosition.y%32==0&&!playerPosition.equals(START_POINT.cpy().scl(32))){
//                	Gdx.app.debug(TAG, "DEAD POINT:"+playerPosition);
//                	loadMap(mapType,playerPosition.scl(1/32));
//                }else{
                	loadMap(mapType,playerPosition);
//                }
                break;
            case SAVING_PROFILE:
                if( _currentMap != null ){
                    profileManager.setProperty("currentMapType", _currentMap._currentMapType.toString());
                    profileManager.setProperty("playerPosition",getPlayerPositionInMap().scl(32));
                    Gdx.app.debug(TAG, "SAVE POINT:"+getPlayerPositionInMap());
                }
                break;
            case CLEAR_CURRENT_PROFILE:
                _currentMap = null;
                profileManager.setProperty("currentMapType", MapFactory.MapType.TOWN.toString());
                profileManager.setProperty("playerPosition", START_POINT.cpy().scl(32));
                MapFactory.clearCache();
                
                break;
            default:
                break;
        }
    }
    
	/**
	 * 加载地图，并设置Player起始点
	 * @param mapType
	 * @param startPoint
	 */
    public void loadMap(MapFactory.MapType mapType,Vector2 startPoint){
        Map map = MapFactory.getMap(mapType);

        if( map == null ){
            Gdx.app.debug(TAG, "Map does not exist!  ");
            return;
        }

        if( _currentMap != null ){
            _currentMap.unloadMusic();
            if( _previousLightMap != null ){
                _previousLightMap.setOpacity(0);
                _previousLightMap = null;
            }
            if( _currentLightMap != null ){
                _currentLightMap.setOpacity(1);
                _currentLightMap = null;
            }
        }

        map.loadMusic();
        _currentMap = map;
        //玩家在该地图最后的地点
        _currentMap.setPlayerStart(startPoint);
        _mapChanged = true;
        _player.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.IDLE));
        _player.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
        clearCurrentSelectedMapEntity();
        Gdx.app.debug(TAG, "Player Start: (" + _currentMap.getPlayerStart().x + "," + _currentMap.getPlayerStart().y + ")---_mapChanged:"+_mapChanged);
    }
    /**
     * 滚动地图块
     */
    public void scollSubMaps(){
    	this.getScollMap().scollSubMaps(this);
    }
    
    public void drawScollImage(Batch batch,boolean bg){
    	this.getScollMap().drawScollImage(batch,bg);
    }
    /**
     * 注销实体观察者
     */
    public void unregisterCurrentMapEntityObservers(){
        if( _currentMap != null ){
            Array<Entity> entities = _currentMap.getMapEntities();
            for(Entity entity: entities){
                entity.unregisterObservers();
            }

            Array<Entity> questEntities = _currentMap.getMapQuestEntities();
            for(Entity questEntity: questEntities){
                questEntity.unregisterObservers();
            }
        }
    }

    public void registerCurrentMapEntityObservers(ComponentObserver observer){
        if( _currentMap != null ){
            Array<Entity> entities = _currentMap.getMapEntities();
            for(Entity entity: entities){
                entity.registerObserver(observer);
            }

            Array<Entity> questEntities = _currentMap.getMapQuestEntities();
            for(Entity questEntity: questEntities){
                questEntity.registerObserver(observer);
            }
        }
    }


    public void disableCurrentmapMusic(){
        _currentMap.unloadMusic();
    }

    public void enableCurrentmapMusic(){
        _currentMap.loadMusic();
    }

    public void setPlayerToTargetPoint(Vector2 position) {
        _currentMap.setPlayerStart(position);
    }

    public MapLayer getCollisionLayer(){
        return _currentMap.getCollisionLayer();
    }

    public MapLayer getPortalLayer(){
        return _currentMap.getPortalLayer();
    }

    public Array<Vector2> getQuestItemSpawnPositions(String objectName, String objectTaskID) {
        return _currentMap.getQuestItemSpawnPositions(objectName, objectTaskID);
    }

    public MapLayer getQuestDiscoverLayer(){
        return _currentMap.getQuestDiscoverLayer();
    }

    public MapLayer getEnemySpawnLayer(){
        return _currentMap.getEnemySpawnLayer();
    }

    public MapFactory.MapType getCurrentMapType(){
        return _currentMap.getCurrentMapType();
    }

    public Vector2 getPlayerStartUnitScaled() {
        return _currentMap.getPlayerStartUnitScaled();
    }

    public TiledMap getCurrentTiledMap(){
        if( _currentMap == null ) {
        	Gdx.app.error(TAG, "!!!!!No Map!!!!!");
            //loadMap(MapFactory.MapType.TOWN,START_POINT.cpy().scl(32));
        }
        return _currentMap.getCurrentTiledMap();
    }

    public MapLayer getPreviousLightMapLayer(){
        return _previousLightMap;
    }

    public MapLayer getCurrentLightMapLayer(){
        return _currentLightMap;
    }

    public void updateLightMaps(ClockActor.TimeOfDay timeOfDay){
        if( _timeOfDay != timeOfDay ){
            _currentLightMapOpacity = 0;
            _previousLightMapOpacity = 1;
            _timeOfDay = timeOfDay;
            _timeOfDayChanged = true;
            _previousLightMap = _currentLightMap;

            Gdx.app.debug(TAG, "Time of Day CHANGED");
        }
        switch(timeOfDay){
            case DAWN:
                _currentLightMap = _currentMap.getLightMapDawnLayer();
                break;
            case AFTERNOON:
                _currentLightMap = _currentMap.getLightMapAfternoonLayer();
                break;
            case DUSK:
                _currentLightMap = _currentMap.getLightMapDuskLayer();
                break;
            case NIGHT:
                _currentLightMap = _currentMap.getLightMapNightLayer();
                break;
            default:
                _currentLightMap = _currentMap.getLightMapAfternoonLayer();
                break;
        }

            if( _timeOfDayChanged ){
                if( _previousLightMap != null && _previousLightMapOpacity != 0 ){
                    _previousLightMap.setOpacity(_previousLightMapOpacity);
                    _previousLightMapOpacity = MathUtils.clamp(_previousLightMapOpacity -= .05, 0, 1);

                    if( _previousLightMapOpacity == 0 ){
                        _previousLightMap = null;
                    }
                }

                if( _currentLightMap != null && _currentLightMapOpacity != 1 ) {
                    _currentLightMap.setOpacity(_currentLightMapOpacity);
                    _currentLightMapOpacity = MathUtils.clamp(_currentLightMapOpacity += .01, 0, 1);
                }
            }else{
                _timeOfDayChanged = false;
            }
    }

    public void updateCurrentMapEntities(MapManager mapMgr, Batch batch, float delta){
        _currentMap.updateMapEntities(mapMgr, batch, delta);
    }

    public void updateCurrentMapEffects(MapManager mapMgr, Batch batch, float delta){
        _currentMap.updateMapEffects(mapMgr, batch, delta);
    }

    public final Array<Entity> getCurrentMapEntities(){
        return _currentMap.getMapEntities();
    }

    public final Array<Entity> getCurrentMapQuestEntities(){
        return _currentMap.getMapQuestEntities();
    }

    public void addMapQuestEntities(Array<Entity> entities){
        _currentMap.getMapQuestEntities().addAll(entities);
    }

    public void removeMapQuestEntity(Entity entity){
        entity.unregisterObservers();

        Array<Vector2> positions = ProfileManager.getInstance().getProperty(entity.getEntityConfig().getEntityID(), Array.class);
        if( positions == null ) return;

        for( Vector2 position : positions){
            if( position.x == entity.getCurrentPosition().x &&
                    position.y == entity.getCurrentPosition().y ){
                positions.removeValue(position, true);
                break;
            }
        }
        _currentMap.getMapQuestEntities().removeValue(entity, true);
        ProfileManager.getInstance().setProperty(entity.getEntityConfig().getEntityID(), positions);
    }

    public void clearAllMapQuestEntities(){
        _currentMap.getMapQuestEntities().clear();
    }

    public Entity getCurrentSelectedMapEntity(){
        return _currentSelectedEntity;
    }

    public void setCurrentSelectedMapEntity(Entity currentSelectedEntity) {
        this._currentSelectedEntity = currentSelectedEntity;
    }

    public void setFollowSwitch(boolean flag){
    	_player.sendMessage(MESSAGE.FOLLOW_ENABLE, _json.toJson(flag));
    }
    
    public void clearCurrentSelectedMapEntity(){
        if( _currentSelectedEntity == null ) return;
        _currentSelectedEntity.sendMessage(Component.MESSAGE.ENTITY_DESELECTED);
        _currentSelectedEntity = null;
    }

    public void setPlayer(Entity entity){
        this._player = entity;
    }

    public Entity getPlayer(){
        return this._player;
    }

    public void setCamera(Camera camera){
        this._camera = camera;
    }

    public Camera getCamera(){
        return _camera;
    }

    public boolean hasMapChanged(){
        return _mapChanged;
    }

    public void setMapChanged(boolean hasMapChanged){
        this._mapChanged = hasMapChanged;
    }

	public Map getCurrentMap() {
		return _currentMap;
	}

	public void setCurrentMap(Map _currentMap) {
		this._currentMap = _currentMap;
	}

	public ScollMap getScollMap() {
		if(scollMap==null){
			scollMap = new ScollMap(this);
		}
		return scollMap;
	}

	public void setScollMap(ScollMap scollMap) {
		this.scollMap = scollMap;
	}

	public Vector2 getTarget() {
		return _target;
	}

	public void setTarget(Vector2 _target) {
		this._target = _target;
	}

	public boolean isDream() {
		return isDream;
	}

	public void setDream(boolean isDream) {
		this.isDream = isDream;
	}
}
