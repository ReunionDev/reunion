package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.server.SkyPacket;
import org.reunionemu.jreunion.protocol.parsers.client.SkyParser;

public class SkyParserTest {

	@Test
	public void test() {
		SkyParser parser = new SkyParser();
		Pattern pattern = parser.getPattern();

		String msg = "sky 2 1";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("sky").matches());

		assertFalse(pattern.matcher("sky" + msg).matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof SkyPacket);
		assertEquals(2, ((SkyPacket) packet).getId());
		assertTrue(((SkyPacket) packet).isFlyStatus());

	}

}
