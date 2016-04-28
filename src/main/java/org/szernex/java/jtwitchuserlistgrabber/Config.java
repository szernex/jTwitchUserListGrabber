/*
The MIT License (MIT)

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
	private static ConfigObject globalConfig = null;

	public static ConfigObject getGlobalConfig() {
		return globalConfig;
	}

	public static void setGlobalConfig(ConfigObject globalConfig) {
		Config.globalConfig = globalConfig;
	}

	public static ConfigObject load(Path path) {
		if (!Files.exists(path)) {
			System.out.println("Creating new empty config file " + path.toString());

			ConfigObject empty_config = new ConfigObject();

			if (!save(empty_config, path)) {
				System.err.println("Could not create empty config file");
			}

			return empty_config;
		}

		try {
			StringBuilder raw = new StringBuilder();

			Files.readAllLines(path).forEach(raw::append);

			Gson gson = new Gson();
			ConfigObject config = gson.fromJson(raw.toString(), ConfigObject.class);

			System.out.println("Config file " + path.toString() + " loaded");

			return config;
		} catch (IOException ex) {
			System.err.println("Error reading config file " + path.toString() + ": " + ex.getMessage());
			ex.printStackTrace();

			return null;
		}
	}

	public static boolean save(ConfigObject config, Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException ex) {
				System.err.println("Error creating config file " + path + ": " + ex.getMessage());
				ex.printStackTrace();

				return false;
			}
		}

		try {
			Gson gson = new Gson();

			Files.write(path, gson.toJson(config).getBytes());

			return true;
		} catch (IOException ex) {
			System.err.println("Error writing config to file " + path + ": " + ex.getMessage());
			ex.printStackTrace();

			return false;
		}
	}
}

class ConfigObject {
	String username = "";
	double pos_x = 0.0;
	double pos_y = 0.0;
	double width = 350.0;
	double height = 250.0;
}