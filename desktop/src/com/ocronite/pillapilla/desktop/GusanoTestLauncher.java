package com.ocronite.pillapilla.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ocronite.pillapilla.GusanoTest;

public class GusanoTestLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(false);
		config.setWindowedMode(800, 600);
		new Lwjgl3Application(new GusanoTest(), config);
	}
}
