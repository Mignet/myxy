package com.v5ent.game.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.v5ent.game.MyRpgGame;

public class RpgDebugDesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "梦幻西游之长生诀";
		config.useGL30 = false;
		config.width = 800;
		config.height = 600;
		config.addIcon("menus/logo.png", Files.FileType.Internal);
		Application app = new LwjglApplication(new MyRpgGame(), config);

		Gdx.app = app;
//		Gdx.app.setLogLevel(Application.LOG_INFO);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		//Gdx.app.setLogLevel(Application.LOG_ERROR);
		//Gdx.app.setLogLevel(Application.LOG_NONE);
	}
}
