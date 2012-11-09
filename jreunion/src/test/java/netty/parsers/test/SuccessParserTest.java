package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.SuccessPacket;
import netty.parsers.SuccessParser;

import org.junit.Test;

public class SuccessParserTest {

	@Test
	public void test() {
		SuccessParser parser = new SuccessParser();
		Pattern pattern = parser.getPattern();

		String msg = "success";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof SuccessPacket);
		assertEquals(msg, packet.toString());

	}

}
