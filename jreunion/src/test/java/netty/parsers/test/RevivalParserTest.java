package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.RevivalPacket;
import netty.parsers.RevivalParser;

import org.junit.Test;

public class RevivalParserTest {

	@Test
	public void test() {
		RevivalParser parser = new RevivalParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "revival";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		
		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof RevivalPacket);
		assertEquals(msg, packet.toString());		
		
	}
}
