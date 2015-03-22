package peppi.novum.geop.server.util;

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
