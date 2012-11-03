package netty;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherProtocol {

	public static Pattern locationRegex = Pattern
			.compile("(place|walk) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+)(?: (?:-?\\d+) (?:-?\\d+))?\\n");

	short[] location = new short[4];
	public short iter = -1;
	public short iterCheck = -1;

	short magic0 = -1;
	short magic1 = -1;
	boolean isLastLocationPlace = false;

	private InetAddress address;
	private int port = 4005;

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
		this.magic0 = magic(address, 0);
		this.magic1 = magic(address, 1);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	private int version = -1;
	private int mapId = 4;

	public char decryptServer(byte data) {
		char c = decryptServer(data, iter, iterCheck);

		// handleChanges(result);
		return c;
	}

	public void handleChanges(String data) {

		if (data.contains("walk") || data.contains("place")) {
			Matcher matcher = locationRegex.matcher(data);
			while (matcher.find()) {

				isLastLocationPlace = matcher.group(1).equals("place");

				for (int i = 0; i < location.length; i++) {
					location[i] = Short.parseShort(matcher.group(i + 2));
				}
			}
		}

		if (data.contains("encrypt_key")) {
			String debug = "data: ";
			debug += data + "\n";
			debug += "isLastLocationPlace: " + isLastLocationPlace + "\n";
			debug += "place: " + location[0] + " " + location[1] + " "
					+ location[2] + "\n";
			debug += "\nbefore: \n";
			debug += "iter: " + iter + "\n";
			;
			debug += "iterCheck: " + iterCheck + "\n";

			System.out.println("place: " + location[0] + " " + location[1]
					+ " " + location[2]);
			short magicx = -1;
			if (isLastLocationPlace) {
				magicx = (short) (magic1 + location[0] + location[1] - location[2]);
			} else {
				magicx = (short) Math
						.abs((short) (location[0] + location[1] - magic0));
			}

			debug += "old magicx: " + magicx + "\n";
			magicx = (short) getMagicKey(location[0], location[1], location[2],
					location[3], isLastLocationPlace);

			debug += "new magicx: " + magicx + "\n";

			iter = (short) (magicx % 4);
			iterCheck += (magic1 ^ (magicx + 2 * magic1 - mapId));

			debug += "\nafter: \n";
			debug += "iter: " + iter + "\n";
			debug += "iterCheck: " + iterCheck + "\n";

			/*
			 * try { bos.write(debug.getBytes()); bos.flush(); } catch
			 * (IOException e) { e.printStackTrace(); }
			 */
			System.out.println(debug);
		}
	}

	public char decryptServer(byte b, short iter, short iterCheck) {
		switch (iter + 1) {
		case 0:
			int magic3 = (port - 17) % 131;
			return (char)(byte) (((magic0 ^ b) + 49) ^ magic3);
		case 1:
			short magic4 = (short) (iterCheck - version + 10);
			return (char)(byte) (b ^ magic4 ^ version);

		case 2:
			short magic5 = (short) (iterCheck + magic1 - magic0);
			return (char)(byte) (((b ^ magic5) - 19) ^ mapId);

		case 3:
			short magic6 = (short) ((byte) (port + 3 * mapId + mapId % 3));
			return (char)(byte) (((b ^ magic6) + 4) ^ iterCheck);

		case 4:
			return (char)(byte) (((b ^ (iterCheck + 111)) + 33) ^ version);

		default:
			throw new RuntimeException("Unable to Decrypt");
		}
	}

	public char decryptClient(byte b) {
		int magic4 = magic0 - port - mapId + version;

		int step1 = magic1 ^ b;
		int step2 = step1 - 19;
		int step3 = step2 ^ magic4;
		return (char)(byte) step3;

	}

	public byte encryptClient(char packet) {
		byte result = encryptClient(packet, iter, iterCheck);
		// handleChanges(packet);

		return result;
	}

	public byte encryptClient(char c, short iter, short iterCheck) {

		switch (iter + 1) {
		case 0:
			int magic3 = (port - 17) % 131;

			return (byte) (magic0 ^ ((c ^ magic3) - 49));

		case 1:
			int magic4 = iterCheck - version + 10;

			return (byte) (c ^ version ^ magic4);

		case 2:
			int magic5 = iterCheck + magic1 - magic0;

			return (byte) (magic5 ^ ((mapId ^ c) + 19));

		case 3: // ?
			int magic6 = port + 3 * mapId + mapId % 3;

			return (byte) (((iterCheck ^ c) - 4) ^ magic6);

		case 4:

			return (byte) ((iterCheck + 111) ^ ((c ^ version) - 33));

		default:
			throw new RuntimeException("Unable to Encrypt");
		}

	}

	public byte encryptServer(char c) {
		// refresh version because its not always available on connect

		int magic4 = magic0 - port - mapId + version;
		int rstep3 = c ^ magic4;
		int rstep2 = rstep3 + 19;
		int rstep1 = magic1 ^ rstep2;
		return (byte) rstep1;

	}

	public static short magic(InetAddress ip, int mask) {
		byte[] rip = ip.getAddress();

		if (mask == 1)
			return (short) (rip[0] ^ rip[1] ^ rip[2] ^ rip[3]);
		else
			return (short) (rip[0] + rip[1] + rip[2] + rip[3]);
	}

	int getMagicKey(int x, int y, int z, int rotation, boolean isPlacePacket) {
		int magic;
		int version;
		int v12;
		int v13;
		int v14;
		int v15;
		int v16;
		int result;
		boolean v19;
		int v20;

		magic = rotation + x + y + z;

		if (isPlacePacket) {
			version = 100;
		} else {
			version = 2000;// version

		}
		v13 = version - mapId + magic;
		v12 = v13;
		if (v13 < 0)
			v12 = -v13;

		if (isPlacePacket) {
			v14 = v12 % 3;
			if (v14 == 0) {
				v15 = z;
				if (v13 < 0) {
					// continue LABEL_10;
					{
						v16 = v15 + 1;
					}
				} else {
					LABEL_21: v16 = v15 - 1;
				}
				// continue LABEL_11;
				{
					z = v16;
					// continue LABEL_12;
					{
						if (isPlacePacket) {
							result = magic1 + x + y - z;
						} else {
							result = Math.abs(x + y - magic0);
						}
						return result;
					}
				}
			}
			v19 = v14 == 1;
		} else {
			v20 = v12 % 3;
			if (v20 == 2) {
				v15 = z;
				if (v13 >= 0) {
					v16 = v15 - 1;
				} else {
					LABEL_10: v16 = v15 + 1;
				}
				LABEL_11: z = v16;
				// continue LABEL_12;
				{
					if (isPlacePacket) {
						result = magic1 + x + y - z;
					} else {
						result = Math.abs(x + y - magic0);
					}
					return result;
				}
			}
			v19 = v20 == 0;
		}
		if (!v19) {
			LABEL_12: if (isPlacePacket) {
				result = magic1 + x + y - z;
			} else {
				result = Math.abs(x + y - magic0);
			}
			return result;
		}
		v15 = z;
		if (v13 >= 0) {
			// continue LABEL_10;
			{
				v16 = v15 + 1;
				z = v16;
				if (isPlacePacket) {
					result = magic1 + x + y - z;
				} else {
					result = Math.abs(x + y - magic0);
				}
				return result;

			}
		}
		// continue LABEL_21;
		{
			v16 = v15 - 1;
			z = v16;

			if (isPlacePacket) {
				result = magic1 + x + y - z;
			} else {
				result = Math.abs(x + y - magic0);
			}
			return result;
		}
	}
}
