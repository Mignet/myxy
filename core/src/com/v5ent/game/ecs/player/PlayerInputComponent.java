package com.v5ent.game.ecs.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.v5ent.game.ecs.InputComponent;
import com.v5ent.game.ecs.Component.MESSAGE;
import com.v5ent.game.entity.Direction;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.screens.MainGameScreen;

public class PlayerInputComponent extends InputComponent {

	private final static String TAG = PlayerInputComponent.class.getSimpleName();
	private Vector3 _lastMouseCoordinates;
    /**跟踪目标 */
    protected boolean _hasTarget = false;

	public PlayerInputComponent(){
		this._lastMouseCoordinates = new Vector3();
	}

	@Override
	public void receiveMessage(String message) {
		String[] string = message.split(MESSAGE_TOKEN);

		if( string.length == 0 ) return;

		//Specifically for messages with 1 object payload
		if( string.length == 2 ) {
			if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_DIRECTION.toString())) {
				_currentDirection = _json.fromJson(Direction.class, string[1]);
			}
			if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_STATE.toString())) {
				_currentState = _json.fromJson(Entity.State.class, string[1]);
			}
			if (string[0].equalsIgnoreCase(MESSAGE.FOLLOW_TARGET.toString())) {
				_hasTarget = _json.fromJson(Boolean.class, string[1]);
			}
		}
	}

	@Override
	public void dispose(){
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void update(Entity entity, float delta){
		//Keyboard input
		if(keys.get(Keys.PAUSE)) {
			MainGameScreen.setGameState(MainGameScreen.GameState.PAUSED);
			pauseReleased();
		}else if( keys.get(Keys.LEFT) && !keys.get(Keys.RIGHT) && !keys.get(Keys.UP) && !keys.get(Keys.DOWN)){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.LEFT));
			entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
			_hasTarget = false;
		}else if( keys.get(Keys.LEFT) && !keys.get(Keys.RIGHT) && keys.get(Keys.UP) && !keys.get(Keys.DOWN)){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.LEFTUP));
			entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
			_hasTarget = false;
		}else if( keys.get(Keys.LEFT) && !keys.get(Keys.RIGHT) && !keys.get(Keys.UP) && keys.get(Keys.DOWN)){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.LEFTDOWN));
			entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
			_hasTarget = false;
		}else if( !keys.get(Keys.LEFT) && keys.get(Keys.RIGHT) && !keys.get(Keys.UP) && !keys.get(Keys.DOWN)){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.RIGHT));
			entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
			_hasTarget = false;
		}else if( !keys.get(Keys.LEFT) && keys.get(Keys.RIGHT) && keys.get(Keys.UP) && !keys.get(Keys.DOWN)){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.RIGHTUP));
			entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
			_hasTarget = false;
		}else if( !keys.get(Keys.LEFT) && keys.get(Keys.RIGHT) && !keys.get(Keys.UP) && keys.get(Keys.DOWN)){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.RIGHTDOWN));
			entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
			_hasTarget = false;
		}else if( !keys.get(Keys.LEFT) && !keys.get(Keys.RIGHT) && keys.get(Keys.UP) && !keys.get(Keys.DOWN)){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.UP));
			entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
			_hasTarget = false;
		}else if( !keys.get(Keys.LEFT) && !keys.get(Keys.RIGHT) && !keys.get(Keys.UP) && keys.get(Keys.DOWN)){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
			entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.DOWN));
			entity.sendMessage(MESSAGE.FOLLOW_TARGET, _json.toJson(false));
			_hasTarget = false;
		}else if(keys.get(Keys.QUIT)) {
			quitReleased();
			Gdx.app.exit();
		}else if(_hasTarget){
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.WALKING));
		}else{
			entity.sendMessage(MESSAGE.CURRENT_STATE, _json.toJson(Entity.State.IDLE));
			if( _currentDirection == null ){
				entity.sendMessage(MESSAGE.CURRENT_DIRECTION, _json.toJson(Direction.RIGHTDOWN));
			}
		}
		//Mouse input
		if( mouseButtons.get(Mouse.SELECT)) {
			//Gdx.app.debug(TAG, "Mouse LEFT click at : (" + _lastMouseCoordinates.x + "," + _lastMouseCoordinates.y + ")" );
			entity.sendMessage(MESSAGE.INIT_SELECT_ENTITY, _json.toJson(_lastMouseCoordinates));
			mouseButtons.put(Mouse.SELECT, false);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		if( keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
			this.leftPressed();
		}
		if( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D){
			this.rightPressed();
		}
		if( keycode == Input.Keys.UP || keycode == Input.Keys.W){
			this.upPressed();
		}
		if( keycode == Input.Keys.DOWN || keycode == Input.Keys.S){
			this.downPressed();
		}
		if( keycode == Input.Keys.Q){
			this.quitPressed();
		}
		if( keycode == Input.Keys.P ){
			this.pausePressed();
		}

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if( keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
			this.leftReleased();
		}
		if( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D){
			this.rightReleased();
		}
		if( keycode == Input.Keys.UP || keycode == Input.Keys.W ){
			this.upReleased();
		}
		if( keycode == Input.Keys.DOWN || keycode == Input.Keys.S){
			this.downReleased();
		}
		if( keycode == Input.Keys.Q){
			this.quitReleased();
		}
		if( keycode == Input.Keys.P ){
			this.pauseReleased();
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//Gdx.app.debug(TAG, "GameScreen: MOUSE DOWN........: (" + screenX + "," + screenY + ")" );

		if( button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT ){
			this.setClickedMouseCoordinates(screenX, screenY);
		}

		//left is selection, right is context menu
		if( button == Input.Buttons.LEFT){
			this.selectMouseButtonPressed(screenX, screenY);
		}
		if( button == Input.Buttons.RIGHT){
			this.doActionMouseButtonPressed(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//left is selection, right is context menu
		if( button == Input.Buttons.LEFT){
			this.selectMouseButtonReleased(screenX, screenY);
		}
		if( button == Input.Buttons.RIGHT){
			this.doActionMouseButtonReleased(screenX, screenY);
		}
		return true;
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
	
	//Key presses
	public void leftPressed(){
		keys.put(Keys.LEFT, true);
	}
	
	public void rightPressed(){
		keys.put(Keys.RIGHT, true);
	}
	
	public void upPressed(){
		keys.put(Keys.UP, true);
	}
	
	public void downPressed(){
		keys.put(Keys.DOWN, true);
	}
	
	public void quitPressed(){
		keys.put(Keys.QUIT, true);
	}

	public void pausePressed() {
		keys.put(Keys.PAUSE, true);
	}
	
	public void setClickedMouseCoordinates(int x,int y){
		_lastMouseCoordinates.set(x, y, 0);
	}
	
	public void selectMouseButtonPressed(int x, int y){
		mouseButtons.put(Mouse.SELECT, true);
	}
	
	public void doActionMouseButtonPressed(int x, int y){
		mouseButtons.put(Mouse.DOACTION, true);
	}
	
	//Releases
	
	public void leftReleased(){
		keys.put(Keys.LEFT, false);
	}
	
	public void rightReleased(){
		keys.put(Keys.RIGHT, false);
	}
	
	public void upReleased(){
		keys.put(Keys.UP, false);
	}
	
	public void downReleased(){
		keys.put(Keys.DOWN, false);
	}
	
	public void quitReleased(){
		keys.put(Keys.QUIT, false);
	}

	public void pauseReleased() { keys.put(Keys.PAUSE, false);}
	
	public void selectMouseButtonReleased(int x, int y){
		mouseButtons.put(Mouse.SELECT, false);
	}
	
	public void doActionMouseButtonReleased(int x, int y){
		mouseButtons.put(Mouse.DOACTION, false);
	}

	public static void clear(){
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.LEFTUP, false);
		keys.put(Keys.RIGHTUP, false);
		keys.put(Keys.LEFTDOWN, false);
		keys.put(Keys.RIGHTDOWN, false);
		keys.put(Keys.QUIT, false);
	}
}
