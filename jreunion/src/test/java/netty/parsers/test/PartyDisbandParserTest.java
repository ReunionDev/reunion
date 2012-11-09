package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.PartyDisbandPacket;
import netty.parsers.PartyDisbandParser;

import org.junit.Test;

public class PartyDisbandParserTest {

	@Test
	public void test() {
		PartyDisbandParser parser = new PartyDisbandParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "party disband";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		
		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof PartyDisbandPacket);
		assertEquals(msg, packet.toString());		
		
	}

}
