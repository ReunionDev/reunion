package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.EventPacket;
import netty.parsers.EventParser;

import org.junit.Test;

public class EventParserTest {

	@Test
	public void test() {
		EventParser parser = new EventParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "this is an info message!";
		String eventMsg = "event "+msg;
		Matcher matcher = pattern.matcher(eventMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("fail").matches());
		assertFalse(pattern.matcher(msg).matches());
		assertFalse(pattern.matcher("fail"+msg).matches());
	
		
		Packet packet = parser.parse(matcher, eventMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof EventPacket);
		assertEquals(msg,((EventPacket)packet).getMessage());
		assertEquals(eventMsg, packet.toString());		
		
	}

}
