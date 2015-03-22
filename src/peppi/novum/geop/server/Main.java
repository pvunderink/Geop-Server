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
 */
package peppi.novum.geop.server;

import java.io.File;

import peppi.novum.geop.server.network.NetworkManager;

public class Main {

	public static String VERSION = "Beta 1.1";
	public static String TITLE = "GeoP Server";
	public static String FILE_PATH = System.getProperty("user.home") + File.separator + "GeoP Server" + File.separator;

	public static void main(String[] args) {
		File file = new File(FILE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		new NetworkManager(8753);
		
		System.out.println("Server started on port " + 8753);
	}

}
