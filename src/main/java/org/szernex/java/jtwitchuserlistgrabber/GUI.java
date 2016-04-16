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

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class GUI extends Application implements Initializable, IUpdateCallback {
	@FXML
	private TextField tfUsername;

	@FXML
	private ListView lvUserList;
	private Set<String> userSet = new TreeSet<>();
	private ListProperty<String> userListProperty = new SimpleListProperty<>();

	@FXML
	private Label lblLastUpdated;

	private UserListGrabber userListGrabber = new UserListGrabber();
	private Thread lastThread = null;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(ClassLoader.getSystemResource("gui.fxml"));

		primaryStage.setTitle("jTwitchUserListGrabber");
		primaryStage.setScene(new Scene(root, 350, 250));
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initialize(URL location, ResourceBundle resources) {
		lvUserList.itemsProperty().bind(userListProperty);

		userListGrabber.setCallback(this);

		Timeline poll_timeline = new Timeline(
				new KeyFrame(
						Duration.seconds(R.gui.DELAY_GUI_UPDATE),
						event -> startUpdateThread()
				)
		);
		poll_timeline.setCycleCount(Animation.INDEFINITE);
		poll_timeline.play();

		Timeline update_timeline = new Timeline(
				new KeyFrame(
						Duration.seconds(R.gui.DELAY_GUI_UPDATE),
						event -> updateGui()
				)
		);
		update_timeline.setCycleCount(Animation.INDEFINITE);
		update_timeline.play();
	}

	@FXML
	public void updateUsername(ActionEvent event) {
		startUpdateThread();
		updateGui();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(Map<String, Object> bundle) {
		userSet = (TreeSet<String>) bundle.get(R.bundle.KEY_USERLIST);
	}

	private synchronized void startUpdateThread() {
		if (lastThread != null) {
			if (lastThread.getState() == Thread.State.TERMINATED)
				lastThread = null;
			else
				return;
		}

		userListGrabber.setUserName(tfUsername.getText().toLowerCase());
		lastThread = new Thread(userListGrabber);
		lastThread.start();
	}

	private void updateGui() {
		if (userSet != null) {
			userListProperty.set(FXCollections.observableArrayList(userSet));
			lblLastUpdated.setText(
					String.format(
							"Last updated: %s",
							new SimpleDateFormat("HH:mm:ss yyyy-MM-dd").format(new Date(System.currentTimeMillis()))
					)
			);
		}
	}
}
