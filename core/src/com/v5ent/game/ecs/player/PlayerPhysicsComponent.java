package com.v5ent.game.ecs.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.ecs.Component;
import com.v5ent.game.ecs.ComponentObserver;
import com.v5ent.game.ecs.PhysicsComponent;
import com.v5ent.game.entity.Direction;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.entity.EntityConfig;
import com.v5ent.game.map.Map;
import com.v5ent.game.map.MapFactory;
import com.v5ent.game.map.MapManager;
import com.v5ent.game.pfa.GraphGenerator;
import com.v5ent.game.pfa.ManhattanDistance;
import com.v5ent.game.pfa.MyGraph;
import com.v5ent.game.pfa.MyNode;
import com.v5ent.game.pfa.MyRaycastCollisionDetector;
import com.v5ent.game.pfa.MyPathSmoother;

public class PlayerPhysicsComponent extends PhysicsComponent {
    private static final String TAG = PlayerPhysicsComponent.class.getSimpleName();

    private Entity.State _state;
    private Vector3 _mouseSelectCoordinates;
    private boolean _isMouseSelectEnabled = false;
    private boolean _isFollowEnabled = true;
    private String _previousDiscovery;
    private String _previousEnemySpawn;
    /**每一小步的行走目标*/
    protected Vector2 _targetPosition;
    //行走路径
	private Array<MyNode> path = new Array<MyNode>(true,10);

	private boolean hasTarget; 

    public PlayerPhysicsComponent(){
        _boundingBoxLocation = BoundingBoxLocation.BOTTOM_CENTER;
        initBoundingBox(32,32,.2f,0f);
        _previousDiscovery = "";
        _previousEnemySpawn = "0";

        _mouseSelectCoordinates = new Vector3(0,0,0);
    }

    @Override
    public void dispose(){
    }

    @Override
    public void receiveMessage(String message) {
        //Gdx.app.debug(TAG, "Got message " + message);
        String[] string = message.split(Component.MESSAGE_TOKEN);

        if( string.length == 0 ) return;

        //Specifically for messages with 1 object payload
        if( string.length == 2 ) {
        	if (string[0].equalsIgnoreCase(MESSAGE.LOAD_ANIMATIONS.toString())) {
        		EntityConfig config = _json.fromJson(EntityConfig.class, string[1]);
        		origWidth = config.getBoxWidth();
        		origHeight = config.getBoxHeight();
        		initBoundingBox(origWidth,origHeight,.2f,0f);
        		Gdx.app.debug(TAG, "["+config.getEntityID()+"]:"+origWidth+","+origHeight);
        	}else if (string[0].equalsIgnoreCase(MESSAGE.INIT_START_POSITION.toString())) {
                _currentEntityPosition = _json.fromJson(Vector2.class, string[1]);
                _nextEntityPosition.set(_currentEntityPosition.x, _currentEntityPosition.y);
                _previousDiscovery = "";
                _previousEnemySpawn = "0";
                notify(_previousEnemySpawn, ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED);
            } else if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_STATE.toString())) {
                _state = _json.fromJson(Entity.State.class, string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_DIRECTION.toString())) {
                _currentDirection = _json.fromJson(Direction.class, string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.INIT_SELECT_ENTITY.toString())) {
                _mouseSelectCoordinates = _json.fromJson(Vector3.class, string[1]);
                _isMouseSelectEnabled = true;
	        } else if (string[0].equalsIgnoreCase(MESSAGE.FOLLOW_ENABLE.toString())) {
	        	_isFollowEnabled = _json.fromJson(Boolean.class, string[1]);
	        } else if (string[0].equalsIgnoreCase(MESSAGE.FOLLOW_TARGET.toString())) {
	        	hasTarget = _json.fromJson(Boolean.class, string[1]);
				if (!hasTarget) {
					path.clear();
					_targetPosition = null;
				}
	        } 
        }
    }

    @Override
    public void update(Entity entity, MapManager mapMgr, float delta) {
        //We want the hitbox to be at the feet for a better feel
        updateBoundingBoxPosition(_nextEntityPosition);
        updatePortalLayerActivation(entity,mapMgr);
        updateDiscoverLayerActivation(mapMgr);
        updateEnemySpawnLayerActivation(mapMgr);

        if( _isMouseSelectEnabled ){
            selectMapEntityCandidate(mapMgr);
            if(_isFollowEnabled){
            	setFollowPathTarget(mapMgr);
            }
            _isMouseSelectEnabled = false;
        }
        //当跟随时，不做地面碰撞检测和实体碰撞检测
        if ( ( !isCollisionWithMapLayer(entity, mapMgr) &&
                !isCollisionWithMapEntities(entity, mapMgr) ||hasTarget) &&
                _state == Entity.State.WALKING){
            setNextPositionToCurrent(entity);

            Camera camera = mapMgr.getCamera();
            camera.position.set(_currentEntityPosition.x, _currentEntityPosition.y, 0f);
            camera.update();
        }else{
            updateBoundingBoxPosition(_currentEntityPosition);
        }
        float currentX = _currentEntityPosition.x;
        float currentY = _currentEntityPosition.y;
        if(_targetPosition!=null){
        	Vector2 t = _targetPosition.cpy().scl(32);
        	if( Math.abs(t.x-currentX)<8&& Math.abs(t.y-currentY)<8){
	        	Gdx.app.debug(TAG, "到达:"+_targetPosition+",实际坐标:"+_currentEntityPosition+",目标:"+_targetPosition.cpy().scl(32));
	        	//终点
	        	if(path==null||path.size<=0){
	        		Gdx.app.debug(TAG, "到达终点:"+_targetPosition+",实际坐标:"+_currentEntityPosition+",目标:"+_targetPosition.cpy().scl(32));
	        		entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.IDLE));
	        		entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
	        		mapMgr.setTarget(null);
	        		hasTarget = false;
	        	}
	        	_targetPosition = null;
	        }else{
	        	//行进中
	        	entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
	        	float deltaX = _targetPosition.x*32 - _currentEntityPosition.x;
				float deltaY = _targetPosition.y*32 - _currentEntityPosition.y;
				//calculateNextPosition(delta);
	        	calculateNextFollowPosition(new Vector2(deltaX,deltaY).nor(),delta);
	        }
        }
        if((path==null||path.size<=0)&&_targetPosition==null){
        	if(mapMgr.getTarget()!=null){
        		mapMgr.setTarget(null);
        	}
        	calculateNextPosition(delta);
        }
        if(path!=null&&path.size>0&&_targetPosition==null){
        	MyNode node = path.pop();
        	Vector2 p = new Vector2(node.getX(),node.getY());
        	_targetPosition = p;
        	Gdx.app.debug(TAG, "开始获取节点 node:"+p.x+","+p.y);
        	float deltaX = p.x-currentX/32;
        	float deltaY = p.y-currentY/32;
        	if(deltaX<0 && deltaY <0){
        		_currentDirection = Direction.LEFTDOWN;
        	}else if(deltaX==0 && deltaY <0){
        		_currentDirection = Direction.DOWN;
        	}else if(deltaX>0 && deltaY <0){
        		_currentDirection = Direction.RIGHTDOWN;
        	}else if(deltaX<0 && deltaY ==0){
        		_currentDirection = Direction.LEFT;
        	}else if(deltaX>0 && deltaY ==0){
        		_currentDirection = Direction.RIGHT;
        	}else if(deltaX==0 && deltaY >0){
        		_currentDirection = Direction.UP;
        	}else if(deltaX<0 && deltaY >0){
        		_currentDirection = Direction.LEFTUP;
        	}else if(deltaX>0 && deltaY >0){
        		_currentDirection = Direction.RIGHTUP;
        	}
        	entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
        	entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(_currentDirection));
        	entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(true));
        	calculateNextFollowPosition(new Vector2(deltaX,deltaY).nor(),delta);
        }
    }

    private void selectMapEntityCandidate(MapManager mapMgr){
        _tempEntities.clear();
        _tempEntities.addAll(mapMgr.getCurrentMapEntities());
        _tempEntities.addAll(mapMgr.getCurrentMapQuestEntities());

        //Convert screen coordinates to world coordinates, then to unit scale coordinates
        mapMgr.getCamera().unproject(_mouseSelectCoordinates);
        _mouseSelectCoordinates.x /= Map.UNIT_SCALE;
        _mouseSelectCoordinates.y /= Map.UNIT_SCALE;

        Gdx.app.debug(TAG, "Mouse Coordinates " + "(" + _mouseSelectCoordinates.x + "," + _mouseSelectCoordinates.y + ")");

        for( Entity mapEntity : _tempEntities ) {
            //Don't break, reset all entities
            mapEntity.sendMessage(MESSAGE.ENTITY_DESELECTED);
            Rectangle mapEntityBoundingBox = mapEntity.getCurrentBoundingBox();
//            Gdx.app.debug(TAG, "Entity Candidate Location " + "(" + mapEntityBoundingBox.x + "," + mapEntityBoundingBox.y + ")");
            if (mapEntity.getCurrentBoundingBox().contains(_mouseSelectCoordinates.x, _mouseSelectCoordinates.y)) {
                //Check distance
                _selectionRay.set(_boundingBox.x, _boundingBox.y, 0.0f, mapEntityBoundingBox.x, mapEntityBoundingBox.y, 0.0f);
                float distance =  _selectionRay.origin.dst(_selectionRay.direction);

                if( distance <= _selectRayMaximumDistance ){
                    //We have a valid entity selection
                    //Picked/Selected
                    Gdx.app.debug(TAG, "Selected Entity! " + mapEntity.getEntityConfig().getEntityID());
                    mapEntity.sendMessage(MESSAGE.ENTITY_SELECTED);
                    notify(_json.toJson(mapEntity.getEntityConfig()), ComponentObserver.ComponentEvent.LOAD_CONVERSATION);
                }
            }
        }
        _tempEntities.clear();
    }
    /**
     * 根据鼠标点击来行动
     * @param mapMgr
     */
    private void setFollowPathTarget(MapManager mapMgr){
    	//鼠标的点击位置
    	_mouseSelectCoordinates.x /= Map.UNIT_SCALE;
    	_mouseSelectCoordinates.y /= Map.UNIT_SCALE;
    	Vector2 start = mapMgr.getPlayerPositionInMap();
		Vector2 end = new Vector2((int)(_mouseSelectCoordinates.x/32),(int)(_mouseSelectCoordinates.y/32));
		Gdx.app.debug(TAG, "Mouse Coordinates " + "(" +end + ")");
//		Vector2 mousePoint = new Vector2(_mouseSelectCoordinates.x,_mouseSelectCoordinates.y);
    	 _tempEntities.clear();
         _tempEntities.addAll(mapMgr.getCurrentMapEntities());
         _tempEntities.addAll(mapMgr.getCurrentMapQuestEntities());
         boolean isCollisionWithMapEntities = false;
         //物体障碍物点击无效
         for(Entity mapEntity: _tempEntities){
             Rectangle targetRect = mapEntity.getCurrentBoundingBox();
			if (targetRect.contains(_mouseSelectCoordinates.x,_mouseSelectCoordinates.y)  ){
				Gdx.app.debug(TAG, "CollisionWithMapEntity: " + "(" +targetRect + ")|"+_mouseSelectCoordinates);
                 isCollisionWithMapEntities = true;
                 break;
             }
         }
         //地面层障碍物点击无效
         int numCols = mapMgr.getCurrentMap().getMapTileWidth();
         int numRows = mapMgr.getCurrentMap().getMapTileHeight();
         TiledMapTileLayer groudLayer = mapMgr.getCurrentMap().getGroudLayer();
         for (int y = 0; y < numRows; y++) {
 			for (int x = 0; x < numCols; x++) {
 				if(groudLayer.getCell(x, y)!=null ){
// 					Rectangle rectangle = new Rectangle(x*tileWidth,y*tileHeight,tileWidth,tileHeight);
					if (new Vector2(x,y).equals(end)) {
						Gdx.app.debug(TAG, "CollisionWithGroudLayer: "+ end.cpy().scl(32));
	 					isCollisionWithMapEntities = true;
	 	                break;
					}
 				}
        	 }        	 
         }
         //如果点击点没有任何碰撞物，那么可以计算
         if(!isCollisionWithMapEntities){
        	 path.clear();
        	 _targetPosition = null;
        	 Gdx.app.debug(TAG, "From:"+start+" to "+end+"|numCols:"+numCols);
        	 int s = (int)start.x + ((int)start.y)*numCols;
        	 int t = (int)end.x+ ((int)(end.y))*numCols;
        	 final MyGraph graph = GraphGenerator.generateGraph(groudLayer,_tempEntities,numCols,numRows,mapMgr.getCurrentMap().getTileWidth(),mapMgr.getCurrentMap().getTileHeight(),start);
				final IndexedAStarPathFinder<MyNode> pathfinder = new IndexedAStarPathFinder<MyNode>(graph);
				final GraphPath<MyNode> outPath = new DefaultGraphPath<MyNode>();
				final boolean searchResult = pathfinder.searchNodePath(graph.getNodes().get(s), graph.getNodes().get(t), new ManhattanDistance(),outPath);
				MyPathSmoother pathSmoother = new MyPathSmoother(new MyRaycastCollisionDetector(graph));
				pathSmoother.smoothPath(outPath);
				StringBuilder sb = new StringBuilder();
				for(int i=outPath.getCount()-1;i>=0;i--){
					sb.append(outPath.get(i).getX()+","+outPath.get(i).getY()+"|");
					path.add(outPath.get(i));
				}
//				System.out.println(sb.toString());
				if(searchResult){
					//把StartPoint弹出
					path.pop();
					mapMgr.setTarget(end.cpy().scl(32));
					hasTarget = true;
				}
         }else{
        	 Gdx.app.debug(TAG, "Fobbiden: " + "(" +end + ")");
         }
         _tempEntities.clear();
    }

    private boolean updateDiscoverLayerActivation(MapManager mapMgr){
        MapLayer mapDiscoverLayer =  mapMgr.getQuestDiscoverLayer();

        if( mapDiscoverLayer == null ){
            return false;
        }

        Rectangle rectangle = null;

        for( MapObject object: mapDiscoverLayer.getObjects()){
            if(object instanceof RectangleMapObject) {
                rectangle = ((RectangleMapObject)object).getRectangle();

                if (_boundingBox.overlaps(rectangle) ){
                    String questID = object.getName();
                    String questTaskID = (String)object.getProperties().get("taskID");
                    String val = questID + MESSAGE_TOKEN + questTaskID;

                    if( questID == null ) {
                        return false;
                    }

                    if( _previousDiscovery.equalsIgnoreCase(val) ){
                        return true;
                    }else{
                        _previousDiscovery = val;
                    }

                    notify(_json.toJson(val), ComponentObserver.ComponentEvent.QUEST_LOCATION_DISCOVERED);
                    Gdx.app.debug(TAG, "Discover Area Activated");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean updateEnemySpawnLayerActivation(MapManager mapMgr){
        MapLayer mapEnemySpawnLayer =  mapMgr.getEnemySpawnLayer();

        if( mapEnemySpawnLayer == null ){
            return false;
        }

        Rectangle rectangle = null;

        for( MapObject object: mapEnemySpawnLayer.getObjects()){
            if(object instanceof RectangleMapObject) {
                rectangle = ((RectangleMapObject)object).getRectangle();

                if (_boundingBox.overlaps(rectangle) ){
                    String enemySpawnID = object.getName();

                    if( enemySpawnID == null ) {
                        return false;
                    }

                    if( _previousEnemySpawn.equalsIgnoreCase(enemySpawnID) ){
                        //Gdx.app.debug(TAG, "Enemy Spawn Area already activated " + enemySpawnID);
                        return true;
                    }else{
                        Gdx.app.debug(TAG, "Enemy Spawn Area " + enemySpawnID + " Activated with previous Spawn value: " + _previousEnemySpawn);
                        _previousEnemySpawn = enemySpawnID;
                    }

                    notify(enemySpawnID, ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED);
                    return true;
                }
            }
        }

        //If no collision, reset the value
        if( !_previousEnemySpawn.equalsIgnoreCase(String.valueOf(0)) ){
            Gdx.app.debug(TAG, "Enemy Spawn Area RESET with previous value " + _previousEnemySpawn);
            _previousEnemySpawn = String.valueOf(0);
            notify(_previousEnemySpawn, ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED);
        }

        return false;
    }

    /**
     * 传送门
     * @param mapMgr
     * @return
     */
    private boolean updatePortalLayerActivation(Entity entity,MapManager mapMgr){
        MapLayer mapPortalLayer =  mapMgr.getPortalLayer();

        if( mapPortalLayer == null ){
            Gdx.app.debug(TAG, "Portal Layer doesn't exist!");
            return false;
        }

        Rectangle rectangle = null;

        for( MapObject object: mapPortalLayer.getObjects()){
            if(object instanceof RectangleMapObject) {
                rectangle = ((RectangleMapObject)object).getRectangle();

                if (_boundingBox.overlaps(rectangle) ){
                    String mapName = object.getName();
                    if( mapName == null ) {
                        return false;
                    }
                    mapMgr.setTarget(null);
                    hasTarget = false;
                    path.clear();
                    _targetPosition = null;
                    entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
                    int x = Integer.valueOf(object.getProperties().get("TX").toString());
                    int y = Integer.valueOf(object.getProperties().get("TY").toString());
                    _currentEntityPosition.x = x*32;
                    _currentEntityPosition.y = y*32;
                    mapMgr.setPlayerToTargetPoint(_currentEntityPosition);
                    mapMgr.loadMap(MapFactory.MapType.valueOf(mapName),_currentEntityPosition);

                    _currentEntityPosition.x = mapMgr.getPlayerStartUnitScaled().x;
                    _currentEntityPosition.y = mapMgr.getPlayerStartUnitScaled().y;
                    _nextEntityPosition.x = mapMgr.getPlayerStartUnitScaled().x;
                    _nextEntityPosition.y = mapMgr.getPlayerStartUnitScaled().y;

                    Gdx.app.debug(TAG, "Portal Activated");
                    return true;
                }
            }
        }
        return false;
    }


}
