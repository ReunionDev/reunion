package org.reunionemu.jreunion.protocol.parsers.server.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.client.TellPacket;
import org.reunionemu.jreunion.protocol.parsers.server.TellParser;

public class TellParserTest {

	@Test
	public void test() {
		TellParser parser = new TellParser();
		Pattern pattern = parser.getPattern();

		String msg = "this is a tell message";
		String tellMsg = "tell me " + msg;
		Matcher matcher = pattern.matcher(tellMsg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("tell").matches());
		assertFalse(pattern.matcher("" + msg).matches());
		assertFalse(pattern.matcher("tell" + msg).matches());

		Packet packet = parser.parse(matcher, tellMsg);
		assertNotNull(packet);
		assertTrue(packet instanceof TellPacket);
		assertEquals("me", ((TellPacket) packet).getName());
		assertEquals(msg, ((TellPacket) packet).getMessage());

	}

}
