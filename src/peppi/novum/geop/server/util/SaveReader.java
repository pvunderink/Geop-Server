/*
 * Copyright (C) 2015 Pepijn Vunderink <pj.vunderink@gmail.com>
 *
 * GeoP Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */package peppi.novum.geop.server.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import peppi.novum.geop.server.Main;

public class SaveReader {

	private static Charset cs = Charset.forName("UTF-8");

	public static HashMap<File, List<File>> loadFiles() {
		HashMap<File, List<File>> result = new HashMap<File, List<File>>();

		File folder = new File(Main.FILE_PATH + "Lists");

		if (!folder.exists()) {
			folder.mkdirs();
		} else {
			for (File f : folder.listFiles()) {
				if (f.isDirectory()) {
					List<File> files = new ArrayList<File>();

					for (File f1 : f.listFiles()) {
						if (f1.getName().endsWith(".gop")) {
							files.add(f1);
						}
					}

					if (!files.isEmpty()) {
						result.put(f, files);
					}
				}
			}
		}

		return result;
	}

	public static void saveUpload(File file, List<String> lines) throws Exception {
		if (file.exists()) {
			file.delete();
		} else {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
		}

		file.createNewFile();

		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		for (String line : lines) {
			output.write(new String(line.getBytes(cs), cs) + "\n");
		}
		output.close();
	}

	public static void deleteFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}
}
