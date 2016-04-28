/*The MIT License (MIT)

Copyright (c) 2016 Szernex

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.szernex.java.jtwitchuserlistgrabber;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class GUI extends Application {
	private Stage mainStage;
	private ConfigObject config;

	public static void main(String[] args) {
		Config.setGlobalConfig(Config.load(Paths.get(R.CONFIG_FILE)));

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		System.out.println("-- start()");

		config = Config.getGlobalConfig();

		if (config == null) {
			System.err.println("Could not load configuration.");
		}

		Parent root = FXMLLoader.load(ClassLoader.getSystemResource("gui.fxml"));

		mainStage = primaryStage;
		mainStage.setTitle("jTwitchUserListGrabber");
		mainStage.setScene(new Scene(root, config.width, config.height));
		mainStage.setX(config.pos_x);
		mainStage.setY(config.pos_y);
		mainStage.show();
	}

	@Override
	public void stop() throws Exception {
		System.out.println("-- stop()");
		System.out.println("Stopping application, saving config");

		config.pos_x = mainStage.getX();
		config.pos_y = mainStage.getY();
		config.width = mainStage.getWidth();
		config.height = mainStage.getHeight();
		Config.save(config, Paths.get(R.CONFIG_FILE));
	}
}
