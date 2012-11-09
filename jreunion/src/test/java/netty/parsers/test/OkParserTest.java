package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.OkPacket;
import netty.parsers.OkParser;

import org.junit.Test;

public class OkParserTest {

	@Test
	public void test() {
		OkParser parser = new OkParser();
		Pattern pattern = parser.getPattern();

		String msg = "OK";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof OkPacket);
		assertEquals(msg, packet.toString());

	}

}
