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
