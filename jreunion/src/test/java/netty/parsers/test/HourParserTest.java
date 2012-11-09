package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.HourPacket;
import netty.parsers.HourParser;

import org.junit.Test;

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
