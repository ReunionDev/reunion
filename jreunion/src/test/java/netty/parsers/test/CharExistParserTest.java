package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.*;
import netty.parsers.CharExistParser;

import org.junit.Test;

public class CharExistParserTest {

	@Test
	public void test() {
		CharExistParser parser = new CharExistParser();
		Pattern pattern = parser.getPattern();
		
		String name = "myname";
		String charMsg = "char_exist "+name;
		Matcher matcher = pattern.matcher(charMsg);
		assertTrue(matcher.matches());		
		assertFalse(pattern.matcher(name).matches());
		assertFalse(pattern.matcher("char_exist"+name).matches());
		assertFalse(pattern.matcher("char_exist").matches());
	
		
		Packet packet = parser.parse(matcher, name);
		assertNotNull(packet);
		assertTrue(packet instanceof CharExistPacket);
		assertEquals(name,((CharExistPacket)packet).getName());
		assertEquals(charMsg, packet.toString());		
		
	}

}
