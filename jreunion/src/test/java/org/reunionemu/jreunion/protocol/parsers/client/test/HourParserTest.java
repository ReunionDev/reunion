package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.packets.server.HourPacket;
import org.reunionemu.jreunion.protocol.parsers.client.HourParser;

public class HourParserTest {

	@Test
	public void test() {
		HourParser parser = new HourParser();
		Pattern pattern = parser.getPattern();

		int hour = 3;
		String msg = "hour " + hour;
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("hour").matches());
		assertFalse(pattern.matcher("" + hour).matches());
		assertFalse(pattern.matcher("hour" + hour).matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof HourPacket);
		assertEquals(hour, ((HourPacket) packet).getHour());

	}

}
