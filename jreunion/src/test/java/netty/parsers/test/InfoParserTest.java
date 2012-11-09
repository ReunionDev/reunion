package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.InfoPacket;
import netty.parsers.InfoParser;

import org.junit.Test;

public class InfoParserTest {

	@Test
	public void test() {
		InfoParser parser = new InfoParser();
		Pattern pattern = parser.getPattern();

		String msg = "this is an info message!";
		String infoMsg = "info " + msg;
		Matcher matcher = pattern.matcher(infoMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("infp").matches());
		assertFalse(pattern.matcher(msg).matches());
		assertFalse(pattern.matcher("info" + msg).matches());

		Packet packet = parser.parse(matcher, infoMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof InfoPacket);
		assertEquals(msg, ((InfoPacket) packet).getMessage());
		assertEquals(infoMsg, packet.toString());

	}

}
