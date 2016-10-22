package com.ocronite.pillapilla.desktop;

import com.ocronite.pillapilla.PillaPilla;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(false);
		new Lwjgl3Application(new PillaPilla(), config);
	}
}
