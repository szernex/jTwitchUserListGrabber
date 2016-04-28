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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class GUIController implements Initializable {
	@FXML
	public TextField tfUsername;

	@FXML
	public ListView<String> lvUserList;
	private ObservableList<String> userList = FXCollections.observableArrayList();
	private int lastUserListHash = -1;

	@FXML
	public Label lblLastUpdated;

	private UserListGrabber userListGrabber = new UserListGrabber();
	private Thread lastThread = null;
	private ConfigObject config;

	@FXML
	public void updateUsername() {
		config.username = tfUsername.getText();
		startUpdateThread();
	}

	private synchronized void startUpdateThread() {
		if (lastThread != null) {
			if (lastThread.getState() == Thread.State.TERMINATED)
				lastThread = null;
			else
				return;
		}

		userListGrabber.setUserName(config.username.toLowerCase());
		lastThread = new Thread(userListGrabber);
		lastThread.start();
	}

	private void updateGui() {
		if (userList != null && userListGrabber.getUserList().hashCode() != lastUserListHash) {
			ArrayList<String> list = new ArrayList<>(userListGrabber.getUserList());
			String lastSelected = lvUserList.getSelectionModel().getSelectedItem();

			lastUserListHash = userListGrabber.getUserList().hashCode();
			userList.clear();
			userList.addAll(list);
			lvUserList.getSelectionModel().select(lastSelected);
			lblLastUpdated.setText(String.format("%d users. Last updated: %s",
					userList.size(), new SimpleDateFormat("HH:mm:ss yyyy-MM-dd").format(new Date(System.currentTimeMillis()))));
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		config = Config.getGlobalConfig();
		lvUserList.setItems(userList);
		tfUsername.setText(config.username);

		Timeline poll_timeline = new Timeline(
				new KeyFrame(
						Duration.seconds(R.gui.DELAY_USERLIST_POLL),
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
}
