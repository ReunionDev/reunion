package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.*;
import netty.parsers.GoToParser;

import org.junit.Test;

public class GoToParserTest {

	@Test
	public void test() {
		GoToParser parser = new GoToParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "goto 1 2 3 "+Math.PI;
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());	
		
		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof GoToPacket);
		
		assertEquals(msg, packet.toString());
		GoToPacket outPacket = (GoToPacket)packet;
		assertEquals(1, outPacket.getX());
		assertEquals(2, outPacket.getY());
		assertEquals(3, outPacket.getZ());
		assertEquals(Math.PI, outPacket.getRotation(), 0);
		
	}

}
