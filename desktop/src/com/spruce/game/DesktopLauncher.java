package com.spruce.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
//		config.setWindowedMode(1920, 1080);
		config.setWindowedMode(1280, 720);
//		config.setForegroundFPS(144);
		config.setTitle("Spruce Deforestation");
		new Lwjgl3Application(new SpruceGame(), config);
	}
}
