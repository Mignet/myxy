package com.v5ent.game.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.entity.Direction;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.map.Map;
import com.v5ent.game.map.MapManager;

public abstract class PhysicsComponent extends ComponentSubject implements Component{
    private static final String TAG = PhysicsComponent.class.getSimpleName();

    public abstract void update(Entity entity, MapManager mapMgr, float delta);

    protected Vector2 _nextEntityPosition;
  
    protected Vector2 _currentEntityPosition;
    protected Direction _currentDirection;
    protected Json _json;
    protected Vector2 _velocity;

    protected Array<Entity> _tempEntities;

    public Rectangle _boundingBox;
    protected float origWidth,origHeight;//原始占用宽高
    protected BoundingBoxLocation _boundingBoxLocation;
    protected Ray _selectionRay;
    protected final float _selectRayMaximumDistance = 5*32.0f;

    public static enum BoundingBoxLocation{
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        CENTER,
    }

    public PhysicsComponent(){
        this._nextEntityPosition = new Vector2(0,0);
        this._currentEntityPosition = new Vector2(0,0);
        this._velocity = new Vector2(5*32f,5*32f);
        this._boundingBox = new Rectangle();
        this._json = new Json();
        this._tempEntities = new Array<Entity>();
        _boundingBoxLocation = BoundingBoxLocation.CENTER;
        _selectionRay = new Ray(new Vector3(), new Vector3());
    }

    protected boolean isCollisionWithMapEntities(Entity entity, MapManager mapMgr){
        _tempEntities.clear();
        _tempEntities.addAll(mapMgr.getCurrentMapEntities());
        _tempEntities.addAll(mapMgr.getCurrentMapQuestEntities());
        boolean isCollisionWithMapEntities = false;

        for(Entity mapEntity: _tempEntities){
            //Check for testing against self
            if( mapEntity.equals(entity) ){
                continue;
            }
            //与实体碰撞，使用最小碰撞面积
            Rectangle boxRect = mapEntity.getCurrentBoundingBox();
            Rectangle targetRect = new Rectangle(boxRect.x,boxRect.y,32,32);
            if (_boundingBox.overlaps(targetRect) ){
                //Collision
                entity.sendMessage(MESSAGE.COLLISION_WITH_ENTITY);
//                entity.sendMessage(MESSAGE.FOLLOW_TARGET,_json.toJson(false));
                isCollisionWithMapEntities = true;
                break;
            }
        }
        _tempEntities.clear();
        return isCollisionWithMapEntities;
    }

    protected boolean isCollision(Entity entitySource, Entity entityTarget){
        boolean isCollisionWithMapEntities = false;

        if( entitySource.equals(entityTarget) ){
            return false;
        }
        Rectangle rect = new Rectangle(entitySource.getCurrentBoundingBox().x,entitySource.getCurrentBoundingBox().y,30,30);
        if (rect.overlaps(entityTarget.getCurrentBoundingBox()) ){
            //Collision
            entitySource.sendMessage(MESSAGE.COLLISION_WITH_ENTITY);
            isCollisionWithMapEntities = true;
        }

        return isCollisionWithMapEntities;
    }

    protected boolean isCollisionWithMapLayer(Entity entity, MapManager mapMgr){
//        MapLayer mapCollisionLayer =  mapMgr.getCollisionLayer();
    	TiledMapTileLayer mapCollisionLayer =  mapMgr.getCurrentMap().getGroudLayer();

        if( mapCollisionLayer == null ){
            return false;
        }

        Rectangle rectangle = null;
        //和地图障碍物的碰撞
        int numCols = mapMgr.getCurrentMap().getMapTileWidth();
        int numRows = mapMgr.getCurrentMap().getMapTileHeight();
        int tileWidth = mapMgr.getCurrentMap().getTileWidth();
        int tileHeight = mapMgr.getCurrentMap().getTileHeight();
        for (int y = 0; y < numRows; y++) {
			for (int x = 0; x < numCols; x++) {
				if(mapCollisionLayer.getCell(x, y)!=null){
					rectangle = new Rectangle(x*tileWidth,y*tileHeight,tileWidth,tileHeight);
					if (_boundingBox.overlaps(rectangle)) {
						// Collision
						entity.sendMessage(MESSAGE.COLLISION_WITH_MAP);
//						entity.sendMessage(MESSAGE.FOLLOW_TARGET,_json.toJson(false));
						return true;
					}
				}
			}
        }
        /*for( MapObject object: mapCollisionLayer.getObjects()){
            if(object instanceof RectangleMapObject) {
                rectangle = ((RectangleMapObject)object).getRectangle();
                if( _boundingBox.overlaps(rectangle) ){
                    //Collision
                    entity.sendMessage(MESSAGE.COLLISION_WITH_MAP);
                    return true;
                }
            }
        }*/

        return false;
    }

    protected void setNextPositionToCurrent(Entity entity){
        this._currentEntityPosition.x = _nextEntityPosition.x;
        this._currentEntityPosition.y = _nextEntityPosition.y;

//        Gdx.app.debug(TAG, "SETTING Current Position " + entity.getEntityConfig().getEntityID() + ": (" + _currentEntityPosition.x/32 + "," + _currentEntityPosition.y/32 + ")");
        entity.sendMessage(MESSAGE.CURRENT_POSITION, _json.toJson(_currentEntityPosition));
    }

    protected void calculateNextPosition(float deltaTime){
        if( _currentDirection == null ) return;

        if( deltaTime > .7) return;
        
        float testX = _currentEntityPosition.x;
        float testY = _currentEntityPosition.y;

        _velocity.scl(deltaTime);

        switch (_currentDirection) {
            case LEFT :
                testX -=  _velocity.x;
                break;
            case RIGHT :
                testX += _velocity.x;
                break;
            case UP :
                testY += _velocity.y;
                break;
            case DOWN :
                testY -= _velocity.y;
                break;
            case LEFTUP :
            	testX -=  _velocity.x/1.414;
            	testY +=  _velocity.y/1.414;
            	break;
            case RIGHTUP :
            	testX += _velocity.x/1.414;
            	testY +=  _velocity.y/1.414;
            	break;
            case LEFTDOWN :
            	testX -= _velocity.x/1.414;
            	testY -=  _velocity.y/1.414;
            	break;
            case RIGHTDOWN :
            	testX += _velocity.x/1.414;
            	testY -=  _velocity.y/1.414;
            	break;
            default:
                break;
        }

        _nextEntityPosition.x = testX;
        _nextEntityPosition.y = testY;

        //velocity
        _velocity.scl(1 / deltaTime);
    }
    protected void calculateNextFollowPosition(Vector2 m,float deltaTime){
    	
    	if( deltaTime > .7) return;
    	float testX = _currentEntityPosition.x;
    	float testY = _currentEntityPosition.y;
    	_velocity.scl(deltaTime);
    	
    		testX += _velocity.x*m.x;
    		testY +=  _velocity.y*m.y;
    	
    	_nextEntityPosition.x = testX;
    	_nextEntityPosition.y = testY;
    	
		//velocity
		_velocity.scl(1 / deltaTime);
    }

    protected void initBoundingBox(float boxWidth,float boxHeight,float percentageWidthReduced, float percentageHeightReduced){
        //Update the current bounding box
        float width;
        float height;

        origWidth =  boxWidth;
        origHeight = boxHeight;

        float widthReductionAmount = 1.0f - percentageWidthReduced; //.8f for 20% (1 - .20)
        float heightReductionAmount = 1.0f - percentageHeightReduced; //.8f for 20% (1 - .20)

        if( widthReductionAmount > 0 && widthReductionAmount < 1){
            width = boxWidth * widthReductionAmount;
        }else{
            width = boxWidth;
        }

        if( heightReductionAmount > 0 && heightReductionAmount < 1){
            height = boxHeight * heightReductionAmount;
        }else{
            height = boxHeight;
        }

        if( width == 0 || height == 0){
            Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);
        }

        //Need to account for the unitscale, since the map coordinates will be in pixels
        float minX;
        float minY;

        if( Map.UNIT_SCALE > 0 ) {
            minX = _nextEntityPosition.x / Map.UNIT_SCALE;
            minY = _nextEntityPosition.y / Map.UNIT_SCALE;
        }else{
            minX = _nextEntityPosition.x;
            minY = _nextEntityPosition.y;
        }

        _boundingBox.setWidth(width);
        _boundingBox.setHeight(height);

        switch(_boundingBoxLocation){
            case BOTTOM_LEFT:
                _boundingBox.set(minX, minY, width, height);
                break;
            case BOTTOM_CENTER:
                _boundingBox.setCenter(minX, minY+10);
                break;
            case CENTER:
                _boundingBox.setCenter(minX, minY + origHeight/2);
                break;
        }

        //Gdx.app.debug(TAG, "SETTING Bounding Box for " + entity.getEntityConfig().getEntityID() + ": (" + minX + "," + minY + ")  width: " + width + " height: " + height);
    }

    protected void updateBoundingBoxPosition(Vector2 position){
        //Need to account for the unitscale, since the map coordinates will be in pixels
        float minX;
        float minY;

        if( Map.UNIT_SCALE > 0 ) {
            minX = position.x / Map.UNIT_SCALE;
            minY = position.y / Map.UNIT_SCALE;
        }else{
            minX = position.x;
            minY = position.y;
        }

//        _boundingBox.set(minX, minY, _boundingBox.getWidth(), _boundingBox.getHeight());
        switch(_boundingBoxLocation){
            case BOTTOM_LEFT:
            	_boundingBox.set(minX, minY, origWidth, origHeight);
                break;
            case BOTTOM_CENTER:
                _boundingBox.setCenter(minX, minY+origHeight/4);
                break;
            case CENTER:
                _boundingBox.setCenter(minX, minY + origHeight/2);
                break;
        }

        //Gdx.app.debug(TAG, "SETTING Bounding Box for " + entity.getEntityConfig().getEntityID() + ": (" + minX + "," + minY + ")  width: " + width + " height: " + height);
    }
}
