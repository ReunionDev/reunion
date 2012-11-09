package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.TellPacket;
import netty.parsers.TellParser;

import org.junit.Test;

public class TellParserTest {

	@Test
	public void test() {
		TellParser parser = new TellParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "this is a say message";
		String tellMsg = "tell me " + msg;
		Matcher matcher = pattern.matcher(tellMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("say").matches());
		assertFalse(pattern.matcher(""+msg).matches());
		assertFalse(pattern.matcher("say"+msg).matches());
	
		
		Packet packet = parser.parse(matcher, tellMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof TellPacket);
		assertEquals("me", ((TellPacket)packet).getName());
		assertEquals(msg, ((TellPacket)packet).getMessage());
		
	}

}
