package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.StartPacket;
import netty.parsers.StartParser;

import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;

public class StartParserTest {

	@Test
	public void test() {
		StartParser parser = new StartParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "start 1 2";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());	
		
		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof StartPacket);
		assertEquals(msg, packet.toString());
		
		StartPacket newChar = (StartPacket)packet;
		assertEquals(1, newChar.getSlot());
		assertEquals(2, newChar.getZone());
		
	}

}
