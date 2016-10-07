package com.v5ent.game.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationUtils {
	/**
	 * 
	 * @param textureName
	 * @param frameWidth
	 * @param frameHeight
	 * @param rows
	 * @param cols
	 * @param frameDuration
	 * @return
	 */
	public static Animation loadAnimation(String textureName, int frameWidth, int frameHeight, int rows,int cols,float frameDuration){
        Utility.loadTextureAsset(textureName);
        Texture texture = Utility.getTextureAsset(textureName);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, frameWidth, frameHeight);
        Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(rows*cols);
        for( int x=0;x<cols;x++){
        	for( int y=0;y<rows;y++){
        		animationKeyFrames.add(textureFrames[x][y]);
        	}
        }
        return new Animation(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP);
    }
}
