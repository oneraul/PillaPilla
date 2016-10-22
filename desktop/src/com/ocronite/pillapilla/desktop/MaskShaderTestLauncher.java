package com.ocronite.pillapilla.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ocronite.pillapilla.MaskShaderTest;

public class MaskShaderTestLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(false);
		new Lwjgl3Application(new MaskShaderTest(), config);
	}
}
