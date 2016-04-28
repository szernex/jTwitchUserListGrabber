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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

class UserListGrabber implements Runnable {
	private IUpdateCallback callback = null;
	private String userName = "";

	void setCallback(IUpdateCallback callback) {
		this.callback = callback;
	}

	void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public void run() {
		if (userName != null && userName.length() > 0 && callback != null) {
			final Set<String> users = new TreeSet<>();
			String tmi_url = String.format("http://tmi.twitch.tv/group/user/%s/chatters", userName);

			try {
				String raw = readURL(tmi_url);
				Gson gson = new Gson();
				Page page = gson.fromJson(raw, Page.class);

				if (page != null) {
					page.chatters.moderators.forEach(s -> users.add("(M) " + s));
					page.chatters.staff.forEach(s -> users.add("(S) " + s));
					page.chatters.admins.forEach(s -> users.add("(A) " + s));
					page.chatters.global_mods.forEach(s -> users.add("(GM) " + s));
					page.chatters.viewers.forEach(users::add);
				}
			} catch (IOException ex) {
				System.err.println("Error parsing URL: " + ex.getMessage());
				ex.printStackTrace();
			}

			Map<String, Object> bundle = new HashMap<>();
			bundle.put(R.bundle.KEY_USERLIST, new ArrayList<>(users));

			callback.update(bundle);
		}
	}

	private String readURL(String url_string) throws IOException {
		URL url = new URL(url_string);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
			StringBuilder buffer = new StringBuilder();
			int read;
			char[] chars = new char[1024];

			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}

			return buffer.toString();
		}
	}
}

class Chatters {
	List<String> moderators;
	List<String> staff;
	List<String> admins;
	List<String> global_mods;
	List<String> viewers;
}

class Page {
	Object _links;
	int chatter_count;
	Chatters chatters;
}