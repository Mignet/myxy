package com.v5ent.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.v5ent.game.screens.CreditScreen;
import com.v5ent.game.screens.CutSceneScreen;
import com.v5ent.game.screens.GameOverScreen;
import com.v5ent.game.screens.LoadGameScreen;
import com.v5ent.game.screens.LoadingScreen;
import com.v5ent.game.screens.MainGameScreen;
import com.v5ent.game.screens.MainMenuScreen;
import com.v5ent.game.screens.NewGameScreen;


public class MyRpgGame extends Game {

	private static MainGameScreen _mainGameScreen;
	private static MainMenuScreen _mainMenuScreen;
	private static LoadGameScreen _loadGameScreen;
	private static LoadingScreen _loadingScreen;
	private static NewGameScreen _newGameScreen;
	private static GameOverScreen _gameOverScreen;
	private static CutSceneScreen _cutSceneScreen;
	private static CreditScreen _creditScreen;

	public static enum ScreenType{
		MainMenu,
		MainGame,
		LoadingGame,
		LoadGame,
		NewGame,
		GameOver,
		WatchIntro,
		Credits
	}

	public Screen getScreenType(ScreenType screenType){
		switch(screenType){
			case MainMenu:
				return _mainMenuScreen;
			case MainGame:
				return _mainGameScreen;
			case LoadGame:
				return _loadGameScreen;
			case LoadingGame:
				return _loadingScreen;
			case NewGame:
				return _newGameScreen;
			case GameOver:
				return _gameOverScreen;
			case WatchIntro:
				return _cutSceneScreen;
			case Credits:
				return _creditScreen;
			default:
				return _mainMenuScreen;
		}

	}

	@Override
	public void create(){
		_mainGameScreen = new MainGameScreen(this);
		_mainMenuScreen = new MainMenuScreen(this);
		_loadGameScreen = new LoadGameScreen(this);
		_loadingScreen = new LoadingScreen(this);
		_newGameScreen = new NewGameScreen(this);
		_gameOverScreen = new GameOverScreen(this);
		_cutSceneScreen = new CutSceneScreen(this);
		_creditScreen = new CreditScreen(this);
		setScreen(_mainMenuScreen);
	}

	@Override
	public void dispose(){
		_mainGameScreen.dispose();
		_mainMenuScreen.dispose();
		_loadGameScreen.dispose();
		_loadingScreen.dispose();
		_newGameScreen.dispose();
		_gameOverScreen.dispose();
		_creditScreen.dispose();
	}

}
