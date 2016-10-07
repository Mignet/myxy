package com.v5ent.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.v5ent.game.MyRpgGame;
import com.v5ent.game.MyRpgGame.ScreenType;
import com.v5ent.game.audio.AudioObserver;
import com.v5ent.game.menu.MenuStage;
import com.v5ent.game.tools.Utility;

public class MainMenuScreen extends GameScreen {

	private MenuStage _stage;
	private MyRpgGame _game;

	public MainMenuScreen(MyRpgGame game){
		_game = game;
		//creation
		_stage = new MenuStage();
		Table table = new Table();
		table.setFillParent(true);
		Texture tex = new Texture(Gdx.files.internal("menus/sp_button.png"));       
		TextureRegion[][] tmp = TextureRegion.split(tex, 112, 43);
//		Image title = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("game_title"));
		Image title = new Image(new Texture(Gdx.files.internal("menus/title.png")));
		ImageButton newGameButton = new  ImageButton(new TextureRegionDrawable(tmp[0][0]), new TextureRegionDrawable(tmp[0][1]));
		ImageButton loadGameButton = new  ImageButton(new TextureRegionDrawable(tmp[1][0]), new TextureRegionDrawable(tmp[1][1]));
		ImageButton watchIntroButton = new  ImageButton(new TextureRegionDrawable(tmp[2][0]), new TextureRegionDrawable(tmp[2][1]));
		ImageButton creditsButton = new  ImageButton(new TextureRegionDrawable(tmp[3][0]), new TextureRegionDrawable(tmp[3][1]));
		ImageButton exitButton = new  ImageButton(new TextureRegionDrawable(tmp[4][0]), new TextureRegionDrawable(tmp[4][1]));
		/*TextButton newGameButton = new TextButton("新的冒险", Utility.STATUSUI_SKIN);
		TextButton loadGameButton = new TextButton("继续征程", Utility.STATUSUI_SKIN);
		TextButton watchIntroButton = new TextButton("观看介绍", Utility.STATUSUI_SKIN);
		TextButton creditsButton = new TextButton("鸣谢名单", Utility.STATUSUI_SKIN);
		TextButton exitButton = new TextButton("退出游戏",Utility.STATUSUI_SKIN);*/

		//Layout
		table.add(title).spaceBottom(20).row();
		table.add(newGameButton).spaceBottom(10).row();
		table.add(loadGameButton).spaceBottom(10).row();
		table.add(watchIntroButton).spaceBottom(10).row();
		table.add(creditsButton).spaceBottom(10).row();
		table.add(exitButton).spaceBottom(10).row();

		_stage.addActor(table);

		//Listeners
		newGameButton.addListener(new ClickListener() {
									  @Override
									  public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
										  return true;
									  }

									  @Override
									  public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
										  _game.setScreen(_game.getScreenType(ScreenType.NewGame));
									  }
								  }
		);

		loadGameButton.addListener(new ClickListener() {

									   @Override
									   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
										   return true;
									   }

									   @Override
									   public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
										   _game.setScreen(_game.getScreenType(ScreenType.LoadGame));
									   }
								   }
		);

		exitButton.addListener(new ClickListener() {

								   @Override
								   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
									   return true;
								   }

								   @Override
								   public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
									   Gdx.app.exit();
								   }

							   }
		);

		watchIntroButton.addListener(new ClickListener() {

										 @Override
										 public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
											 return true;
										 }

										 @Override
										 public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
											 MainMenuScreen.this.notify(AudioObserver.AudioCommand.MUSIC_STOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
											 _game.setScreen(_game.getScreenType(ScreenType.WatchIntro));
										 }
									 }
		);

		creditsButton.addListener(new ClickListener() {

										 @Override
										 public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
											 return true;
										 }

										 @Override
										 public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
											 _game.setScreen(_game.getScreenType(ScreenType.Credits));
										 }
									 }
		);

		notify(AudioObserver.AudioCommand.MUSIC_LOAD, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
	}
	
	@Override
	public void render(float delta) {
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
		notify(AudioObserver.AudioCommand.MUSIC_PLAY_LOOP, AudioObserver.AudioTypeEvent.MUSIC_TITLE);
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
		_stage.dispose();
	}

}



