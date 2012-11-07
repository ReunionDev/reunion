package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.FailPacket;
import netty.parsers.FailParser;

import org.junit.Test;

public class FailParserTest {

	@Test
	public void test() {
		FailParser parser = new FailParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "this is a fail message!";
		String failMsg = "fail "+msg;
		Matcher matcher = pattern.matcher(failMsg);
		assertTrue(matcher.matches());		
		assertFalse(pattern.matcher(msg).matches());
		assertFalse(pattern.matcher("fail"+msg).matches());
	
		
		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof FailPacket);
		assertEquals(msg,((FailPacket)packet).getMessage());
		assertEquals(failMsg, packet.toString());
		
		
		
	}

}
