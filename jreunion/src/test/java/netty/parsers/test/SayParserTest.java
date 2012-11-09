package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.SayPacket;
import netty.parsers.SayParser;

import org.junit.Test;

public class SayParserTest {

	@Test
	public void test() {
		SayParser parser = new SayParser();
		Pattern pattern = parser.getPattern();

		String msg = "this is a say message";
		String sayMsg = "say -1 " + msg;
		Matcher matcher = pattern.matcher(sayMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("say").matches());
		assertFalse(pattern.matcher("" + msg).matches());
		assertFalse(pattern.matcher("say" + msg).matches());

		Packet packet = parser.parse(matcher, sayMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof SayPacket);
		assertEquals(-1, ((SayPacket) packet).getId());
		assertEquals(msg, ((SayPacket) packet).getMessage());

	}

}
