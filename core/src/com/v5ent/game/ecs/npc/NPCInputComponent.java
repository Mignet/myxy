package com.v5ent.game.ecs.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.v5ent.game.ecs.InputComponent;
import com.v5ent.game.entity.Direction;
import com.v5ent.game.entity.Entity;

public class NPCInputComponent extends InputComponent {
    private static final String TAG = NPCInputComponent.class.getSimpleName();

    private float _frameTime = 0.0f;

    public NPCInputComponent(){
        _currentDirection = Direction.getRandomNext();
        _currentState = Entity.State.WALKING;
    }

    @Override
    public void receiveMessage(String message) {
        String[] string = message.split(MESSAGE_TOKEN);

        if( string.length == 0 ) return;

        //Specifically for messages with 1 object payload
        if( string.length == 1 ) {
            if (string[0].equalsIgnoreCase(MESSAGE.COLLISION_WITH_MAP.toString())) {
                _currentDirection = Direction.getRandomNext();
            }else if (string[0].equalsIgnoreCase(MESSAGE.COLLISION_WITH_ENTITY.toString())) {
                _currentState = Entity.State.IDLE;
//                _currentDirection = _currentDirection.getOpposite();
            }
        }

        if( string.length == 2 ) {
            if (string[0].equalsIgnoreCase(MESSAGE.INIT_STATE.toString())) {
                _currentState = _json.fromJson(Entity.State.class, string[1]);
//                Gdx.app.debug(TAG, "_currentState:"+_currentState);
            }else if (string[0].equalsIgnoreCase(MESSAGE.INIT_DIRECTION.toString())) {
                _currentDirection = _json.fromJson(Direction.class, string[1]);
//                Gdx.app.debug(TAG, "current dir:"+_currentDirection);
            }
        }

    }

    @Override
    public void dispose(){

    }

    @Override
    public void update(Entity entity, float delta){
        if(keys.get(Keys.QUIT)) {
            Gdx.app.exit();
        }
//        Gdx.app.debug(TAG, entity.getEntityConfig().getEntityID()+"|"+_currentState+"|"+_currentDirection);
        //If IMMOBILE, don't update anything
        if( _currentState == Entity.State.IMMOBILE ) {
            entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.IMMOBILE));
            return;
        }

        _frameTime += delta;

        //Change direction after so many seconds
        if( _frameTime > MathUtils.random(1,5) ){
            _currentState = Entity.State.getRandomNext();
            _currentDirection = Direction.getRandomNext();
            _frameTime = 0.0f;
        }
//        Gdx.app.debug(TAG, "After Random:"+entity.getEntityConfig().getEntityID()+"|"+_currentState+"|"+_currentDirection);
        if( _currentState == Entity.State.IDLE ){
            entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.IDLE));
//            return;
        }else{
        	entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
        }

        switch( _currentDirection ) {
            case LEFTUP:
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.LEFTUP));
                break;
            case RIGHTUP:
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.RIGHTUP));
                break;
            case LEFTDOWN:
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.LEFTDOWN));
                break;
            case RIGHTDOWN:
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.RIGHTDOWN));
                break;
		default:
			break;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if( keycode == Input.Keys.Q){
            keys.put(Keys.QUIT, true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
