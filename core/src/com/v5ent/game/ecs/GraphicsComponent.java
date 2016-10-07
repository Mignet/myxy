package com.v5ent.game.ecs;

import java.util.Hashtable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.v5ent.game.entity.Direction;
import com.v5ent.game.entity.Entity;
import com.v5ent.game.map.MapManager;
import com.v5ent.game.tools.Utility;

public abstract class GraphicsComponent extends ComponentSubject implements Component {
    protected TextureRegion _currentFrame = null;
    protected float _frameTime = 0f;
    protected Entity.State _currentState;
    protected Direction _currentDirection;
    protected Json _json;
    protected Vector2 _currentPosition;
    protected Hashtable<Entity.AnimationType, Animation> _animations;
    protected ShapeRenderer _shapeRenderer;

    protected GraphicsComponent(){
        setCurrentPosition(new Vector2(0,0));
        _currentState = Entity.State.WALKING;
        _currentDirection = Direction.RIGHTDOWN;
        _json = new Json();
        _animations = new Hashtable<Entity.AnimationType, Animation>();
        _shapeRenderer = new ShapeRenderer();
    }

    public abstract void update(Entity entity, MapManager mapManager, Batch batch, float delta);

    protected void updateAnimations(float delta){
        _frameTime = (_frameTime + delta)%5; //Want to avoid overflow

        //Look into the appropriate variable when changing position
        switch (_currentDirection) {
            case DOWN:
                if (_currentState == Entity.State.WALKING) {
                    Animation animation = _animations.get(Entity.AnimationType.WALK_DOWN);
                    if( animation == null ) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if(_currentState == Entity.State.IDLE) {
                    Animation animation = _animations.get(Entity.AnimationType.IDLE_DOWN);
                    if( animation == null ) {
                    	animation = _animations.get(Entity.AnimationType.IDLE);
                    	_currentFrame = animation.getKeyFrames()[0];
                    };
                    if ( animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if(_currentState == Entity.State.IMMOBILE) {
                    Animation animation = _animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                }
                break;
            case LEFT:
                if (_currentState == Entity.State.WALKING) {
                    Animation animation = _animations.get(Entity.AnimationType.WALK_LEFT);
                    if( animation == null ) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if(_currentState == Entity.State.IDLE) {
                    Animation animation = _animations.get(Entity.AnimationType.IDLE_LEFT);
                    if( animation == null ) {
                    	animation = _animations.get(Entity.AnimationType.IDLE);
                    	_currentFrame = animation.getKeyFrames()[0];
                    };
                    if ( animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if(_currentState == Entity.State.IMMOBILE) {
                    Animation animation = _animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                }
                break;
            case UP:
                if (_currentState == Entity.State.WALKING) {
                    Animation animation = _animations.get(Entity.AnimationType.WALK_UP);
                    if( animation == null ) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if(_currentState == Entity.State.IDLE) {
                    Animation animation = _animations.get(Entity.AnimationType.IDLE_UP);
                    if( animation == null ) {
                    	animation = _animations.get(Entity.AnimationType.IDLE);
                    	_currentFrame = animation.getKeyFrames()[0];
                    };
                    if ( animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if(_currentState == Entity.State.IMMOBILE) {
                    Animation animation = _animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                }
                break;
            case RIGHT:
                if (_currentState == Entity.State.WALKING) {
                    Animation animation = _animations.get(Entity.AnimationType.WALK_RIGHT);
                    if( animation == null ) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if(_currentState == Entity.State.IDLE) {
                    Animation animation = _animations.get(Entity.AnimationType.IDLE_RIGHT);
                    if( animation == null ) {
                    	animation = _animations.get(Entity.AnimationType.IDLE);
                    	_currentFrame = animation.getKeyFrames()[0];
                    };
                    if ( animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                } else if(_currentState == Entity.State.IMMOBILE) {
                    Animation animation = _animations.get(Entity.AnimationType.IMMOBILE);
                    if( animation == null ) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
                }
                break;
            case LEFTUP:
            	if (_currentState == Entity.State.WALKING) {
            		Animation animation = _animations.get(Entity.AnimationType.WALK_LEFTUP);
            		if( animation == null ) return;
            		_currentFrame = animation.getKeyFrame(_frameTime);
            	} else if(_currentState == Entity.State.IDLE) {
            		Animation animation = _animations.get(Entity.AnimationType.IDLE_LEFTUP);
            		if( animation == null ) {
                    	animation = _animations.get(Entity.AnimationType.IDLE);
                    	_currentFrame = animation.getKeyFrames()[0];
                    };
                    if ( animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
            	} else if(_currentState == Entity.State.IMMOBILE) {
            		Animation animation = _animations.get(Entity.AnimationType.IMMOBILE);
            		if( animation == null ) return;
            		_currentFrame = animation.getKeyFrame(_frameTime);
            	}
            	break;
            case LEFTDOWN:
            	if (_currentState == Entity.State.WALKING) {
            		Animation animation = _animations.get(Entity.AnimationType.WALK_LEFTDOWN);
            		if( animation == null ) return;
            		_currentFrame = animation.getKeyFrame(_frameTime);
            	} else if(_currentState == Entity.State.IDLE) {
            		Animation animation = _animations.get(Entity.AnimationType.IDLE_LEFTDOWN);
            		if( animation == null ) {
                    	animation = _animations.get(Entity.AnimationType.IDLE);
                    	_currentFrame = animation.getKeyFrames()[0];
                    };
                    if ( animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
            	} else if(_currentState == Entity.State.IMMOBILE) {
            		Animation animation = _animations.get(Entity.AnimationType.IMMOBILE);
            		if( animation == null ) return;
            		_currentFrame = animation.getKeyFrame(_frameTime);
            	}
            	break;
            case RIGHTUP:
            	if (_currentState == Entity.State.WALKING) {
            		Animation animation = _animations.get(Entity.AnimationType.WALK_RIGHTUP);
            		if( animation == null ) return;
            		_currentFrame = animation.getKeyFrame(_frameTime);
            	} else if(_currentState == Entity.State.IDLE) {
            		Animation animation = _animations.get(Entity.AnimationType.IDLE_RIGHTUP);
            		if( animation == null ) {
                    	animation = _animations.get(Entity.AnimationType.IDLE);
                    	_currentFrame = animation.getKeyFrames()[0];
                    };
                    if ( animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
            	} else if(_currentState == Entity.State.IMMOBILE) {
            		Animation animation = _animations.get(Entity.AnimationType.IMMOBILE);
            		if( animation == null ) return;
            		_currentFrame = animation.getKeyFrame(_frameTime);
            	}
            	break;
            case RIGHTDOWN:
            	if (_currentState == Entity.State.WALKING) {
            		Animation animation = _animations.get(Entity.AnimationType.WALK_RIGHTDOWN);
            		if( animation == null ) return;
            		_currentFrame = animation.getKeyFrame(_frameTime);
            	} else if(_currentState == Entity.State.IDLE) {
            		Animation animation = _animations.get(Entity.AnimationType.IDLE_RIGHTDOWN);
            		if( animation == null ) {
                    	animation = _animations.get(Entity.AnimationType.IDLE);
                    	_currentFrame = animation.getKeyFrames()[0];
                    };
                    if ( animation == null) return;
                    _currentFrame = animation.getKeyFrame(_frameTime);
            	} else if(_currentState == Entity.State.IMMOBILE) {
            		Animation animation = _animations.get(Entity.AnimationType.IMMOBILE);
            		if( animation == null ) return;
            		_currentFrame = animation.getKeyFrame(_frameTime);
            	}
            	break;
            default:
                break;
        }
    }

    //Specific to two frame animations where each frame is stored in a separate texture
    protected Animation loadAnimation(String firstTexture, String secondTexture, int frameWidth,int frameHeight,Array<GridPoint2> points, float frameDuration){
        Utility.loadTextureAsset(firstTexture);
        Texture texture1 = Utility.getTextureAsset(firstTexture);

        Utility.loadTextureAsset(secondTexture);
        Texture texture2 = Utility.getTextureAsset(secondTexture);

        TextureRegion[][] texture1Frames = TextureRegion.split(texture1, frameWidth, frameHeight);
        TextureRegion[][] texture2Frames = TextureRegion.split(texture2, frameWidth, frameHeight);

        Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(2);

        GridPoint2 point = points.first();

        animationKeyFrames.add(texture1Frames[point.x][point.y]);
        animationKeyFrames.add(texture2Frames[point.x][point.y]);

        return new Animation(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP);
    }

    protected Animation loadAnimation(String textureName, int frameWidth, int frameHeight, Array<GridPoint2> points, float frameDuration){
        Utility.loadTextureAsset(textureName);
        Texture texture = Utility.getTextureAsset(textureName);

        TextureRegion[][] textureFrames = TextureRegion.split(texture, frameWidth, frameHeight);

        Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(points.size);

        for( GridPoint2 point : points){
            animationKeyFrames.add(textureFrames[point.x][point.y]);
        }

        return new Animation(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP);
    }

    public Animation getAnimation(Entity.AnimationType type){
        return _animations.get(type);
    }

	/**
	 * @return the _currentPosition
	 */
	public Vector2 getCurrentPosition() {
		return _currentPosition;
	}

	/**
	 * @param _currentPosition the _currentPosition to set
	 */
	public void setCurrentPosition(Vector2 _currentPosition) {
		this._currentPosition = _currentPosition;
	}
}
