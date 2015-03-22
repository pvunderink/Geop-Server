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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import peppi.novum.geop.server.Main;

public class PacketEncoder {

	private static Charset cs = Charset.forName("UTF-8");

	private static String DIR_SEPARATOR = "/%DIR%/";
	private static String FILE_SEPARATOR = "/%FILE%/";
	private static String LINE_SEPARATOR = "/%LINE%/";
	private static String ARG_SEPARATOR = "/%ARG%/";

	public static String encodeFileListPacket(HashMap<File, List<File>> files) {
		String result = "";

		for (File dir : files.keySet()) {
			result += DIR_SEPARATOR + new String(dir.getName().getBytes(cs), cs);

			for (File f : files.get(dir)) {
				result += FILE_SEPARATOR + new String(f.getName().getBytes(cs), cs);
			}
		}

		return result;
	}

	public static String encodePreFilePacket(String s) {
		String result = "";

		File file = new File(Main.FILE_PATH + s);

		if (file != null && file.exists()) {
			result += new String(s.getBytes(cs), cs) + ARG_SEPARATOR + file.length();
		}

		return result;
	}

	public static String encodeFilePacket(String s) throws Exception {
		String result = "";

		File file = new File(Main.FILE_PATH + s);

		if (file != null && file.exists()) {
			BufferedReader br;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), cs));

			if (br.ready()) {
				String line = new String(br.readLine().getBytes(cs), cs);

				while (line != null) {
					result += line;

					String unsafeLine = br.readLine();

					if (unsafeLine != null) {
						result += LINE_SEPARATOR;
						line = new String(unsafeLine.getBytes(cs), cs);
					} else {
						line = null;
					}
				}
			}
			br.close();
		} else {
			System.err.println("Invalid file");
		}

		return result;
	}

}