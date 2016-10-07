package com.v5ent.game.ecs.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.ecs.Component;
import com.v5ent.game.ecs.PhysicsComponent;
import com.v5ent.game.ecs.Component.MESSAGE;
import com.v5ent.game.ecs.PhysicsComponent.BoundingBoxLocation;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.entity.EntityConfig;
import com.v5ent.game.entity.Direction;
import com.v5ent.game.entity.Entity.State;
import com.v5ent.game.map.MapManager;

public class NPCPhysicsComponent extends PhysicsComponent {
    private static final String TAG = NPCPhysicsComponent.class.getSimpleName();

    private Entity.State _state;

    public NPCPhysicsComponent(){
        _boundingBoxLocation = BoundingBoxLocation.BOTTOM_CENTER;
        initBoundingBox(64,64,0.4f, 0.1f);
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
        		initBoundingBox(origWidth,origHeight,.4f,.1f);
        		Gdx.app.debug(TAG, "["+config.getEntityID()+"]:"+origWidth+","+origHeight);
        	}else if (string[0].equalsIgnoreCase(MESSAGE.INIT_START_POSITION.toString())) {
                _currentEntityPosition = _json.fromJson(Vector2.class, string[1]);
                _nextEntityPosition.set(_currentEntityPosition.x, _currentEntityPosition.y);
            } else if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_STATE.toString())) {
                _state = _json.fromJson(Entity.State.class, string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_DIRECTION.toString())) {
                _currentDirection = _json.fromJson(Direction.class, string[1]);
            }
        }
    }

    @Override
    public void update(Entity entity, MapManager mapMgr, float delta) {
        updateBoundingBoxPosition(_nextEntityPosition);

        if( isEntityFarFromPlayer(mapMgr) ){
            entity.sendMessage(MESSAGE.ENTITY_DESELECTED);
        }

        if( _state == Entity.State.IMMOBILE ) return;

        if (    !isCollisionWithMapLayer(entity, mapMgr) &&
                !isCollisionWithMapEntities(entity, mapMgr) &&
                _state == Entity.State.WALKING){
            setNextPositionToCurrent(entity);
        } else {
            updateBoundingBoxPosition(_currentEntityPosition);
        }
        calculateNextPosition(delta);
    }

    private boolean isEntityFarFromPlayer(MapManager mapMgr){
        //Check distance
        _selectionRay.set(mapMgr.getPlayer().getCurrentBoundingBox().x, mapMgr.getPlayer().getCurrentBoundingBox().y, 0.0f, _boundingBox.x, _boundingBox.y, 0.0f);
        float distance =  _selectionRay.origin.dst(_selectionRay.direction);

        if( distance <= _selectRayMaximumDistance ){
            return false;
        }else{
            return true;
        }
    }

    @Override
    protected boolean isCollisionWithMapEntities(Entity entity, MapManager mapMgr){
        //Test against player
        if( isCollision(entity, mapMgr.getPlayer()) ) {
            return true;
        }

        if( super.isCollisionWithMapEntities(entity, mapMgr) ){
            return true;
        }

        return false;
    }
}
