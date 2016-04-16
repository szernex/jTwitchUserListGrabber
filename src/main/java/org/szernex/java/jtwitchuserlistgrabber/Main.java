package org.szernex.java.jtwitchuserlistgrabber;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class Main {
	static class Chatters {
		List<String> moderators;
		List<String> staff;
		List<String> admins;
		List<String> global_mods;
		List<String> viewers;
	}

	static class Page {
		Object _links;
		int chatter_count;
		Chatters chatters;
	}

	public static void main(String[] args) throws IOException {
		String raw = readURL("http://tmi.twitch.tv/group/user/szernex/chatters");

		System.out.println(raw);

		Gson gson = new Gson();
		Page page = gson.fromJson(raw, Page.class);

		page.chatters.viewers.forEach(System.out::println);
	}

	private static String readURL(String url_string) throws IOException {
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
