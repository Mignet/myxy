package com.v5ent.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.v5ent.game.tools.AnimationUtils;
import com.v5ent.game.tools.Utility;

public class MenuStage extends Stage {
	private static final String TAG = MenuStage.class.getSimpleName();
	Animation xysrz;
	private Image imgBackground,imgBackground_,mask,xysr;
	private float _frameTime = 0;
	float speed = .875f;
	public MenuStage(){
		Utility.loadTextureAsset("menus/07B8C541.jpg");
		Utility.loadTextureAsset("menus/66279210.jpg");
		Utility.loadTextureAsset("menus/mask.png");
		Texture bg = Utility.getTextureAsset("menus/07B8C541.jpg");
		Texture bg_ = Utility.getTextureAsset("menus/66279210.jpg");
		imgBackground = new Image(bg);
		imgBackground.setPosition(Gdx.graphics.getWidth() - bg.getWidth(), 0);
		imgBackground.addAction(Actions.forever(Actions.moveBy(32f, 0,speed)));
		this.addActor(imgBackground);
		imgBackground_ = new Image(bg_);
		imgBackground_.setPosition(Gdx.graphics.getWidth() - bg.getWidth() - bg_.getWidth(), 0);
		imgBackground_.addAction(Actions.forever(Actions.moveBy(32f, 0,speed)));
		this.addActor(imgBackground_);
		
		xysrz = AnimationUtils.loadAnimation("menus/xysrz_strip5.png",480,240,5,1,0.25f);
		xysr = new Image(xysrz.getKeyFrames()[0]);
		xysr.setPosition(Gdx.graphics.getWidth()/2-240, 28);
		this.addActor(xysr);
		
		//mask
		mask = new Image(Utility.getTextureAsset("menus/mask.png"));
		mask.setPosition(Gdx.graphics.getWidth() - 8000, 0);
		mask.addAction(Actions.forever(Actions.moveBy(32f, 0, speed)));
		this.addActor(mask);
	}
	
	@Override
	public void act(float delta) {
		if(imgBackground.getX()>=Gdx.graphics.getWidth()){
			imgBackground.setX(Gdx.graphics.getWidth()- imgBackground.getImageWidth()-imgBackground_.getImageWidth());
			imgBackground_.setX(Gdx.graphics.getWidth()- imgBackground_.getImageWidth());
		}
		if(imgBackground_.getX()>=Gdx.graphics.getWidth()){
			imgBackground.setX(Gdx.graphics.getWidth()- imgBackground.getImageWidth());
			imgBackground_.setX(Gdx.graphics.getWidth()- imgBackground.getImageWidth()-imgBackground_.getImageWidth());
		}
		if(mask.getX()>=Gdx.graphics.getWidth()){
			mask.setX(Gdx.graphics.getWidth()- mask.getImageWidth());
		}
		imgBackground.act(delta);
		imgBackground_.act(delta);
		Drawable drawable = xysr.getDrawable();
		if (drawable == null) {
			return;
		}
		_frameTime = (_frameTime + delta) % 5;
		TextureRegion region = xysrz.getKeyFrame(_frameTime, true);
		((TextureRegionDrawable) drawable).setRegion(region);
		xysr.act(delta);
		mask.act(delta);
		super.act(delta);
	}
    
}
