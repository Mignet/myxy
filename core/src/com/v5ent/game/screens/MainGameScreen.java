package com.v5ent.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.MyRpgGame;
import com.v5ent.game.audio.AudioManager;
import com.v5ent.game.ecs.Component;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.entity.EntityFactory;
import com.v5ent.game.map.Map;
import com.v5ent.game.map.MapFactory;
import com.v5ent.game.map.MapManager;
import com.v5ent.game.profile.ProfileManager;
import com.v5ent.game.tools.AnimationUtils;
import com.v5ent.game.tools.TextureUtils;
import com.v5ent.game.ui.PlayerHUD;

public class MainGameScreen extends GameScreen {
	private static final String TAG = MainGameScreen.class.getSimpleName();

	public static class VIEWPORT {
		public static float viewportWidth;
		public static float viewportHeight;
		public static float virtualWidth;
		public static float virtualHeight;
		public static float physicalWidth;
		public static float physicalHeight;
		public static float aspectRatio;
	}

	public static enum GameState {
		SAVING,
		LOADING,
		RUNNING,
		PAUSED,
		GAME_OVER
	}
	private static GameState _gameState;

	protected OrthogonalTiledMapRenderer _mapRenderer = null;
	protected MapManager _mapMgr;
	protected OrthographicCamera _camera = null;
	protected OrthographicCamera _hudCamera = null;

	private Json _json;
	private MyRpgGame _game;
	private InputMultiplexer _multiplexer;

	private Entity _player;
	Animation targetAni;
	private float _frameTime = 0;
	private PlayerHUD _playerHUD;

	//debug专用
//	private Texture debugBox,targetCycle;
	private Texture dawnBox,afternoonBox,duskBox;

	public MainGameScreen(MyRpgGame game){
		_game = game;
		_mapMgr = new MapManager();
		_json = new Json();

		setGameState(GameState.RUNNING);

		//_camera setup
		setupViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());//10, 10

		//get the current size
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

		_player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.PLAYER);
		_mapMgr.setPlayer(_player);
		_mapMgr.setCamera(_camera);
        // Create a new texture from pixmap data
//        debugBox = new Texture(TextureUtils.createProceduralPixmap(32, 32));
//        targetCycle = new Texture(TextureUtils.createCyclePixmap(32));
        dawnBox = new Texture(TextureUtils.createBoxImagePixmap(800,600,255f/255,255f/255,150f/255,0.8f));
        duskBox = new Texture(TextureUtils.createBoxImagePixmap(800,600,255f/255,192f/255,0f,0.8f));
        afternoonBox = new Texture(TextureUtils.createBoxImagePixmap(800,600,250f/255,250f/255,250f/255,0.8f));
        targetAni = AnimationUtils.loadAnimation("menus/target_strip8.png",40,16,8,1,0.1f);
		//HUD
		_hudCamera = new OrthographicCamera();
		_hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);

		_playerHUD = new PlayerHUD(_hudCamera, _player, _mapMgr);

		_multiplexer = new InputMultiplexer();
		_multiplexer.addProcessor(_playerHUD.getStage());
		_multiplexer.addProcessor(_player.getInputProcessor());
		Gdx.input.setInputProcessor(_multiplexer);

//		Gdx.app.debug(TAG, "UnitScale value is: " + _mapRenderer.getUnitScale());
	}

	@Override
	public void show() {
		ProfileManager.getInstance().addObserver(_mapMgr);
		ProfileManager.getInstance().addObserver(_playerHUD);

		setGameState(GameState.LOADING);
		Gdx.input.setInputProcessor(_multiplexer);

		if( _mapRenderer == null ){
			_mapRenderer = new OrthogonalTiledMapRenderer(_mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
		}
	}

	@Override
	public void hide() {
		if( _gameState != GameState.GAME_OVER ){
			setGameState(GameState.SAVING);
		}

		Gdx.input.setInputProcessor(null);
	}
	
	
	
	@Override
	public void render(float delta) {
		if( _gameState == GameState.GAME_OVER ){
			 _mapMgr.disableCurrentmapMusic();
			_game.setScreen(_game.getScreenType(MyRpgGame.ScreenType.GameOver));
		}

		if( _gameState == GameState.PAUSED ){
			_player.updateInput(delta);
			_playerHUD.render(delta);
			return;
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		_mapRenderer.setView(_camera);

		_mapRenderer.getBatch().enableBlending();
		_mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		if(_gameState == GameState.RUNNING){
			if(_mapMgr.getCurrentMap()==null){
				_mapMgr.getCurrentTiledMap();
			}
		}
		if( _mapMgr.hasMapChanged() ){
			_mapRenderer.setMap(_mapMgr.getCurrentTiledMap());
			_player.sendMessage(Component.MESSAGE.INIT_START_POSITION, _json.toJson(_mapMgr.getPlayerStartUnitScaled()));

			_camera.position.set(_mapMgr.getPlayerStartUnitScaled().x, _mapMgr.getPlayerStartUnitScaled().y, 0f);
			_camera.update();

			_playerHUD.updateEntityObservers();

			_mapMgr.setMapChanged(false);

			_playerHUD.addTransitionToScreen();
		}

		_mapMgr.updateLightMaps(_playerHUD.getCurrentTimeOfDay());
		MapLayer lightMap = _mapMgr.getCurrentLightMapLayer();
		MapLayer previousLightMap = _mapMgr.getPreviousLightMapLayer();
		
			_mapRenderer.getBatch().begin();
			
			TiledMapTileLayer backgroundMapLayer = (TiledMapTileLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.BACKGROUND_LAYER);
			if( backgroundMapLayer != null ){
				_mapRenderer.renderTileLayer(backgroundMapLayer);
			}
			
			//地图背景
			TiledMapImageLayer backgroundImageLayer = (TiledMapImageLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.BACKGROUND_IMAGE);
			if( backgroundImageLayer != null ){
				_mapRenderer.renderImageLayer(backgroundImageLayer);
				_mapRenderer.getBatch().end();
			}else{
				TiledMapImageLayer minimapImageLayer = (TiledMapImageLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.MINI_MAP);
				_mapRenderer.renderImageLayer(minimapImageLayer);
				_mapRenderer.getBatch().end();
				//滚动地图数据
				_mapMgr.scollSubMaps();
				if(!_mapMgr.isDream()){
					_mapMgr.drawScollImage(_mapRenderer.getBatch(),true);
				}
			}
			
			_mapRenderer.getBatch().begin();
			TiledMapTileLayer groundMapLayer = (TiledMapTileLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.GROUND_LAYER);
			if( groundMapLayer != null && Gdx.app.getLogLevel() == Application.LOG_DEBUG){
				_mapRenderer.renderTileLayer(groundMapLayer);
			}

			TiledMapTileLayer decorationMapLayer = (TiledMapTileLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.DECORATION_LAYER);
			if( decorationMapLayer != null ){
				_mapRenderer.renderTileLayer(decorationMapLayer);
			}
			_mapRenderer.getBatch().end();

			_mapMgr.updateCurrentMapEntities(_mapMgr, _mapRenderer.getBatch(), delta);
			_player.update(_mapMgr, _mapRenderer.getBatch(), delta);
			//如果只有灯火特效，可以只在晚上有
			if(lightMap!=null && lightMap instanceof TiledMapImageLayer){
				_mapMgr.updateCurrentMapEffects(_mapMgr, _mapRenderer.getBatch(), delta);
			}

			//目标光标
			if(_mapMgr.getTarget()!=null){
				_mapRenderer.getBatch().begin();
				_frameTime = (_frameTime + delta) % 5;
				_mapRenderer.getBatch().draw(targetAni.getKeyFrame(_frameTime,true), _mapMgr.getTarget().x, _mapMgr.getTarget().y);
				_mapRenderer.getBatch().end();
			}
			//透明遮罩层
			TiledMapImageLayer foregroundImage = (TiledMapImageLayer)_mapMgr.getCurrentTiledMap().getLayers().get(Map.FOREGROUND_IMAGE);
			if( foregroundImage != null ){
				_mapRenderer.getBatch().begin();
				_mapRenderer.renderImageLayer(foregroundImage);
			}else{
				_mapMgr.drawScollImage(_mapRenderer.getBatch(),false);
				_mapRenderer.getBatch().begin();
			}
			//碰撞层对象
			/*if(Application.LOG_DEBUG == Gdx.app.getLogLevel()){
				MapLayer mapCollisionLayer = _mapMgr.getCollisionLayer();
				if (mapCollisionLayer != null) {
					for (MapObject e : mapCollisionLayer.getObjects()) {
						_mapRenderer.getBatch().draw(
								debugBox,
								e.getProperties().get("x",Float.class),
								e.getProperties().get("y",Float.class));
					}
				}
			}*/
			_mapRenderer.getBatch().end();
		if( lightMap != null) {
			_mapRenderer.getBatch().begin();
			//天气效果
			_mapRenderer.getBatch().setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);
			if(lightMap instanceof TiledMapImageLayer){
				_mapRenderer.renderImageLayer((TiledMapImageLayer)lightMap);
			}else{
				if(Map.LIGHTMAP_DAWN_LAYER.equals(lightMap.getName())){
					_mapRenderer.getBatch().draw(dawnBox, _camera.position.x-Gdx.graphics.getWidth()/2, _camera.position.y-Gdx.graphics.getHeight()/2);
				}
				if(Map.LIGHTMAP_DUSK_LAYER.equals(lightMap.getName())){
					_mapRenderer.getBatch().draw(duskBox, _camera.position.x-Gdx.graphics.getWidth()/2, _camera.position.y-Gdx.graphics.getHeight()/2);
				}
				if(Map.LIGHTMAP_AFTERNOON_LAYER.equals(lightMap.getName())){
					_mapRenderer.getBatch().draw(afternoonBox, _camera.position.x-Gdx.graphics.getWidth()/2, _camera.position.y-Gdx.graphics.getHeight()/2);
				}
			}
			_mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			_mapRenderer.getBatch().end();

			if( previousLightMap != null ){
				_mapRenderer.getBatch().begin();
				_mapRenderer.getBatch().setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
				if(previousLightMap instanceof TiledMapImageLayer){
					_mapRenderer.renderImageLayer((TiledMapImageLayer)previousLightMap);
				}else{
					if(Map.LIGHTMAP_DAWN_LAYER.equals(previousLightMap.getName())){
						_mapRenderer.getBatch().draw(dawnBox, _camera.position.x-Gdx.graphics.getWidth()/2, _camera.position.y-Gdx.graphics.getHeight()/2);
					}
					if(Map.LIGHTMAP_DUSK_LAYER.equals(previousLightMap.getName())){
						_mapRenderer.getBatch().draw(duskBox, _camera.position.x-Gdx.graphics.getWidth()/2, _camera.position.y-Gdx.graphics.getHeight()/2);
					}
					if(Map.LIGHTMAP_AFTERNOON_LAYER.equals(previousLightMap.getName())){
						_mapRenderer.getBatch().draw(afternoonBox, _camera.position.x-Gdx.graphics.getWidth()/2, _camera.position.y-Gdx.graphics.getHeight()/2);
					}
				}
				_mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				_mapRenderer.getBatch().end();
			}
		}

		_playerHUD.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		setupViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());//10, 10
		_camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		_playerHUD.resize((int) VIEWPORT.physicalWidth, (int) VIEWPORT.physicalHeight);
	}

	@Override
	public void pause() {
		setGameState(GameState.SAVING);
		_playerHUD.pause();
	}

	@Override
	public void resume() {
		setGameState(GameState.LOADING);
		_playerHUD.resume();
	}

	@Override
	public void dispose() {
		if( _player != null ){
			_player.unregisterObservers();
			_player.dispose();
		}

		if( _mapRenderer != null ){
			_mapRenderer.dispose();
		}
		if(_mapMgr.getScollMap()!=null){
			_mapMgr.getScollMap().dispose();
		}
		AudioManager.getInstance().dispose();
		MapFactory.clearCache();
	}

	public static void setGameState(GameState gameState){
		switch(gameState){
			case RUNNING:
				_gameState = GameState.RUNNING;
				break;
			case LOADING:
				ProfileManager.getInstance().loadProfile();
				_gameState = GameState.RUNNING;
				break;
			case SAVING:
				ProfileManager.getInstance().saveProfile();
				_gameState = GameState.PAUSED;
				break;
			case PAUSED:
				if( _gameState == GameState.PAUSED ){
					_gameState = GameState.RUNNING;
				}else if( _gameState == GameState.RUNNING ){
					_gameState = GameState.PAUSED;
				}
				break;
			case GAME_OVER:
				_gameState = GameState.GAME_OVER;
				break;
			default:
				_gameState = GameState.RUNNING;
				break;
		}

	}

	private void setupViewport(int width, int height){
		//Make the viewport a percentage of the total display area
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		//Current viewport dimensions
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		//pixel dimensions of display
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		//aspect ratio for current viewport
		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		//update viewport if there could be skewing
		if( VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio){
			//Letterbox left and right
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		}else{
			//letterbox above and below
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
		}

		Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")" );
	}
}
