package com.v5ent.game.ecs.player;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.ecs.ComponentObserver;
import com.v5ent.game.ecs.GraphicsComponent;
import com.v5ent.game.entity.Direction;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.entity.EntityConfig;
import com.v5ent.game.entity.EntityConfig.AnimationConfig;
import com.v5ent.game.map.Map;
import com.v5ent.game.map.MapManager;
import com.v5ent.game.screens.MainGameScreen.VIEWPORT;

public class PlayerGraphicsComponent extends GraphicsComponent {

    private static final String TAG = PlayerGraphicsComponent.class.getSimpleName();

    protected Vector2 _previousPosition;

    public PlayerGraphicsComponent(){
        _previousPosition = new Vector2(0,0);
    }

    @Override
    public void receiveMessage(String message) {
        //Gdx.app.debug(TAG, "Got message " + message);
        String[] string = message.split(MESSAGE_TOKEN);

        if( string.length == 0 ) return;

        //Specifically for messages with 1 object payload
        if( string.length == 2 ) {
            if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_POSITION.toString())) {
                setCurrentPosition(_json.fromJson(Vector2.class, string[1]));
            } else if (string[0].equalsIgnoreCase(MESSAGE.INIT_START_POSITION.toString())) {
                setCurrentPosition(_json.fromJson(Vector2.class, string[1]));
            } else if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_STATE.toString())) {
                _currentState = _json.fromJson(Entity.State.class, string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_DIRECTION.toString())) {
                _currentDirection = _json.fromJson(Direction.class, string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.LOAD_ANIMATIONS.toString())) {
                EntityConfig entityConfig = _json.fromJson(EntityConfig.class, string[1]);
                Array<AnimationConfig> animationConfigs = entityConfig.getAnimationConfig();

                for( AnimationConfig animationConfig : animationConfigs ){
                    Array<String> textureNames = animationConfig.getTexturePaths();
                    Array<GridPoint2> points = animationConfig.getGridPoints();
                    Entity.AnimationType animationType = animationConfig.getAnimationType();
                    float frameDuration = animationConfig.getFrameDuration();
                    Animation animation = null;

                    if( textureNames.size == 1) {
                        animation = loadAnimation(textureNames.get(0),animationConfig.getFrameWidth(),animationConfig.getFrameHeight(), points, frameDuration);
                    }else if( textureNames.size == 2){
                        animation = loadAnimation(textureNames.get(0), textureNames.get(1), animationConfig.getFrameWidth(),animationConfig.getFrameHeight(),points, frameDuration);
                    }

                    _animations.put(animationType, animation);
                }
            }
        }
    }

    /**
     * 
     * offset:make camera always in map
     * @author dxtx
     * @param width
     * @param height
     * @param cam
     * @since JDK 1.6
     */
	private void offsetCameraByMap(Camera cam,TiledMap tiledMap){
		MapProperties prop = tiledMap.getProperties();
		int mapWidth = prop.get("width", Integer.class);
		int mapHeight = prop.get("height", Integer.class);
		int tilePixelWidth = prop.get("tilewidth", Integer.class);
		int tilePixelHeight = prop.get("tileheight", Integer.class);
		
		int mapPixelWidth = mapWidth * tilePixelWidth;
		int mapPixelHeight = mapHeight * tilePixelHeight;

		// These values likely need to be scaled according to your world coordinates.
		// The left boundary of the map (x)
		float mapLeft =0;
		// The right boundary of the map (x + width)
		float mapRight =0 + mapPixelWidth;
		// The bottom boundary of the map (y)
		float mapBottom = 0;
		// The top boundary of the map (y + height)
		float mapTop = 0 + mapPixelHeight;
		// The camera dimensions, halved
		float cameraHalfWidth = VIEWPORT.viewportWidth * .5f;
		float cameraHalfHeight = VIEWPORT.viewportHeight * .5f;

		// Move camera after player as normal

		float cameraLeft = cam.position.x - cameraHalfWidth;
		float cameraRight = cam.position.x + cameraHalfWidth;
		float cameraBottom = cam.position.y - cameraHalfHeight;
		float cameraTop = cam.position.y + cameraHalfHeight;

		// Horizontal axis
		if(mapPixelWidth < VIEWPORT.viewportWidth)
		{
		    cam.position.x = mapRight / 2;
		}
		else if(cameraLeft <= mapLeft)
		{
		    cam.position.x = mapLeft + cameraHalfWidth;
		}
		else if(cameraRight >= mapRight)
		{
		    cam.position.x = mapRight - cameraHalfWidth;
		}

		// Vertical axis
		if(mapPixelHeight < VIEWPORT.viewportHeight)
		{
		    cam.position.y = mapTop / 2;
		}
		else if(cameraBottom <= mapBottom)
		{
		    cam.position.y = mapBottom + cameraHalfHeight;
		}
		else if(cameraTop >= mapTop)
		{
		    cam.position.y = mapTop - cameraHalfHeight;
		}
	}
    @Override
    public void update(Entity entity, MapManager mapMgr, Batch batch, float delta){
        updateAnimations(delta);

        //Player has moved
        if( _previousPosition.x != getCurrentPosition().x ||
                _previousPosition.y != getCurrentPosition().y){
            notify("", ComponentObserver.ComponentEvent.PLAYER_HAS_MOVED);
            _previousPosition = getCurrentPosition().cpy();
        }

        Camera camera = mapMgr.getCamera();
        //camera.position.set(_currentPosition.x, _currentPosition.y, 0f);
        offsetCameraByMap(camera,mapMgr.getCurrentTiledMap());
        camera.update();
        //anchor锚点的位置
        batch.begin();
        batch.draw(_currentFrame, getCurrentPosition().x-_currentFrame.getRegionWidth()/2, getCurrentPosition().y-10,  _currentFrame.getRegionWidth(), _currentFrame.getRegionHeight());
//        Gdx.app.debug(TAG, "图像盒子:"+(getCurrentPosition().x-_currentFrame.getRegionWidth()/2)+","+(getCurrentPosition().y-20)+","+_currentFrame.getRegionWidth()+","+ _currentFrame.getRegionHeight());
        batch.end();

        if(Application.LOG_DEBUG == Gdx.app.getLogLevel()){
        	//Used to graphically debug boundingboxes
        	Rectangle rect = entity.getCurrentBoundingBox();
        	//Gdx.app.debug(TAG, "碰撞盒子:"+rect.getX()+","+rect.getY()+","+rect.getWidth()+","+rect.getHeight());
        	_shapeRenderer.setProjectionMatrix(camera.combined);
        	_shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        	_shapeRenderer.setColor(Color.RED);
        	_shapeRenderer.rect(rect.getX() * Map.UNIT_SCALE , rect.getY() * Map.UNIT_SCALE, rect.getWidth() * Map.UNIT_SCALE, rect.getHeight()*Map.UNIT_SCALE);
        	_shapeRenderer.end();
        }
        
    }

    @Override
    public void dispose(){
    }

}
