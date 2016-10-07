package com.v5ent.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.MyRpgGame;
import com.v5ent.game.audio.AudioObserver;
import com.v5ent.game.profile.ProfileManager;
import com.v5ent.game.tools.Utility;


public class LoadGameScreen extends GameScreen {
    private Stage _stage;
	private MyRpgGame _game;
	private List _listItems;
	
	public LoadGameScreen(MyRpgGame game){
		_game = game;

		//create
		_stage = new Stage();
		// + Background
        Image imgBackground = new Image(new Texture(Gdx.files.internal("menus/1989C5FC.jpg")));
        _stage.addActor(imgBackground);
		Texture tex = new Texture(Gdx.files.internal("menus/load-button.png"));       
		TextureRegion[][] tmp = TextureRegion.split(tex, 112, 43);
		ImageButton loadButton = new  ImageButton(new TextureRegionDrawable(tmp[0][0]), new TextureRegionDrawable(tmp[0][1]));
		ImageButton backButton = new  ImageButton(new TextureRegionDrawable(tmp[1][0]), new TextureRegionDrawable(tmp[1][1]));
		
//		TextButton loadButton = new TextButton("读取", Utility.STATUSUI_SKIN);
//		TextButton backButton = new TextButton("返回",Utility.STATUSUI_SKIN);

		ProfileManager.getInstance().storeAllProfiles();
		_listItems = new List(Utility.STATUSUI_SKIN, "inventory");
		Array<String> list = ProfileManager.getInstance().getProfileList();
		_listItems.setItems(list);
		ScrollPane scrollPane = new ScrollPane(_listItems);

		scrollPane.setOverscroll(false, false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setScrollbarsOnTop(true);

		Table table = new Table();
		Table bottomTable = new Table();

		//Layout
		table.center();
		table.setFillParent(true);
		table.padBottom(loadButton.getHeight());
		table.add(scrollPane).center();

		bottomTable.setHeight(loadButton.getHeight());
		bottomTable.setWidth(Gdx.graphics.getWidth());
		bottomTable.center();
		bottomTable.add(loadButton).padRight(50);
		bottomTable.add(backButton);

		_stage.addActor(table);
		_stage.addActor(bottomTable);

		//Listeners
		backButton.addListener(new ClickListener() {
								   @Override
								   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
									   return true;
								   }

								   @Override
								   public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
									   _game.setScreen(_game.getScreenType(MyRpgGame.ScreenType.MainMenu));
								   }
							   }
		);

		loadButton.addListener(new ClickListener() {
								   @Override
								   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
									   return true;
								   }

								   @Override
								   public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
									   if( _listItems.getSelected() == null ) return;
									   String fileName = _listItems.getSelected().toString();
									   if (fileName != null && !fileName.isEmpty()) {
										   FileHandle file = ProfileManager.getInstance().getProfileFile(fileName);
										   if (file != null) {
											   ProfileManager.getInstance().setCurrentProfile(fileName);
											   LoadGameScreen.this.notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
											   _game.setScreen(_game.getScreenType(MyRpgGame.ScreenType.MainGame));
										   }
									   }
								   }

							   }
		);
	}
	
	@Override
	public void render(float delta) {
		if( delta == 0){
			return;
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		_stage.act(delta);
		_stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		_stage.getViewport().setScreenSize(width, height);
	}

	@Override
	public void show() {
		Array<String> list = ProfileManager.getInstance().getProfileList();
		_listItems.setItems(list);
		Gdx.input.setInputProcessor(_stage);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		_stage.clear();
		_stage.dispose();
	}

}
