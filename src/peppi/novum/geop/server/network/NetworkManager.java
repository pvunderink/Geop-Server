package peppi.novum.geop.server.network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.List;

import peppi.novum.geop.server.Main;
import peppi.novum.geop.server.util.PacketDecoder;
import peppi.novum.geop.server.util.PacketEncoder;
import peppi.novum.geop.server.util.SaveReader;

public class NetworkManager {

	String IDENTIFIER = "/%GeoP%/";
	String PACKET_END = "/%END%/";
	String ARG_SEPARATOR = "/%ARG%/";

	// Receive
	String LIST_FILES = "/%LF%/";
	String CONFIRM_LIST_FILES = "/%CONLF%/";
	String REQUEST_FILE = "/%RF%/";
	String CONFIRM_REQUEST = "/%CR%/";
	String PING = "/%PING%/";
	String PRE_FILE_UPLOAD = "/%PFU%/";
	String FILE_UPLOAD = "/%UPLOAD%/";
	String DELETE = "/%DELETE%/";

	// Send
	String PRE_FILE_SEND = "/%PRE%/";
	String FILE_SEND = "/%SEND%/";
	String PRE_FILE_LIST = "/%PLF%/";
	String FILE_LIST = "/%LF%/";
	String CONFIRM_PING = "/%CONPING%/";
	String ACCEPT_UPLOAD = "/%ACCEPT%/";

	Charset cs = Charset.forName("UTF-8");

	DatagramSocket socket;
	boolean running = false;

	Thread receive, send;

	long expectedFileSize = 0;
	String expectedFile = "";

	public NetworkManager(int port) {
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException ex) {
			ex.printStackTrace();
		}

		running = true;

		receive();
	}

	public void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					byte[] data;
					
					if (expectedFileSize > 0) {
						data = new byte[(int) expectedFileSize];
					} else {
						data = new byte[1024];
					}
					
					DatagramPacket rawpacket = new DatagramPacket(data, data.length);

					InetAddress ip;
					int port;

					try {
						socket.receive(rawpacket);
						ip = rawpacket.getAddress();
						port = rawpacket.getPort();

						String packet = new String(rawpacket.getData(), cs).trim();
						packet = packet.split(PACKET_END)[0].trim();

						if (packet.startsWith(IDENTIFIER)) {
							packet = packet.substring(IDENTIFIER.length(), packet.length());
							String[] args = packet.split(ARG_SEPARATOR);

							if (args.length > 1) {
								String identifier = args[1];

								if (identifier.equalsIgnoreCase(LIST_FILES)) {
									String message = PacketEncoder.encodeFileListPacket(SaveReader.loadFiles());
									int size = message.length();

									send(ip, port, toBytes(PRE_FILE_LIST, "" + size));
								} else if (identifier.equalsIgnoreCase(CONFIRM_LIST_FILES)) {
									String message = PacketEncoder.encodeFileListPacket(SaveReader.loadFiles());
									
									System.out.println("File list sent to " + ip.toString() + ":" + port);
									
									send(ip, port, toBytes(FILE_LIST, message));
								} else if (identifier.equalsIgnoreCase(REQUEST_FILE)) {
									send(ip, port, toBytes(PRE_FILE_SEND, PacketEncoder.encodePreFilePacket(args[2])));
								} else if (identifier.equalsIgnoreCase(CONFIRM_REQUEST)) {
									try {
										send(ip, port, toBytes(FILE_SEND, PacketEncoder.encodeFilePacket(args[2])));
										System.out.println("File " + args[2] + " sent to " + ip.toString() + ":" + port);
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (identifier.equalsIgnoreCase(PING)) {
									System.out.println("Ping received from " + ip.toString() + ":" + port);

									send(ip, port, toBytes(CONFIRM_PING));
								} else if (identifier.equalsIgnoreCase(PRE_FILE_UPLOAD)) {
									if (args.length > 3) {
										String file = args[2];
										long filesize = Long.parseLong(args[3]);

										expectedFileSize = filesize + 1024 + (filesize / 2);
										expectedFile = file;

										send(ip, port, toBytes(ACCEPT_UPLOAD, file));
									}
								} else if (identifier.equalsIgnoreCase(FILE_UPLOAD)) {
									if (expectedFile != "") {
										if (args.length > 2) {
											File file = new File(Main.FILE_PATH + expectedFile);
											List<String> lines = PacketDecoder.decodeUpload(args[2]);

											try {
												SaveReader.saveUpload(file, lines);
											} catch (Exception e) {
												e.printStackTrace();
											}

											System.out.println("Accepted file " + expectedFile + " from " + ip.getHostAddress() + ":" + port);

											expectedFile = "";
										}
									}
									expectedFileSize = 0;
								} else if (identifier.equalsIgnoreCase(DELETE)) {
									String shortDir = args[2];
									
									File file = new File(Main.FILE_PATH + shortDir);
									SaveReader.deleteFile(file);
									
									System.out.println("Deleted file " + shortDir + ", ordered by " + ip.getHostAddress() + ":" + port);
								}
							}
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		};
		receive.start();
	}

	public void send(final InetAddress ip, final int port, final byte[] message) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(message, message.length, ip, port);

				try {
					socket.send(packet);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		};
		send.start();
	}

	public byte[] toBytes(String identifier, String... args) {
		String msg = IDENTIFIER + ARG_SEPARATOR + identifier;

		for (String s : args) {
			msg += ARG_SEPARATOR + s;
		}

		msg += PACKET_END;

		return msg.getBytes();
	}

}
