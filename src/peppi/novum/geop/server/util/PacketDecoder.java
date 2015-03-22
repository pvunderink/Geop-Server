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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PacketDecoder {

	private static String LINE_SEPARATOR = "/%LINE%/";

	private static Charset cs = Charset.forName("UTF-8");
	
	public static List<String> decodeUpload(String download) {
		List<String> result = new ArrayList<String>();
		
		for (String line : download.split(LINE_SEPARATOR)) {
			result.add(new String(line.getBytes(cs), cs));
		}
		
		return result;
	}

}
