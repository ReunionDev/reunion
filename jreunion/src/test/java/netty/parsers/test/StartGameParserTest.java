package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.StartGamePacket;
import netty.parsers.StartGameParser;

import org.junit.Test;

public class StartGameParserTest {

	@Test
	public void test() {
		StartGameParser parser = new StartGameParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "start_game";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		
		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof StartGamePacket);
		assertEquals(msg, packet.toString());		
		
	}

}
