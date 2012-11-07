package netty.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import netty.ClientsideParser;
import netty.parsers.*;

import org.junit.Test;

public class ClientsideParserTest {

	@Test
	public void test() {
		ClientsideParser parser = new ClientsideParser();
		parser.setParsers(Arrays.asList(new PacketParser[]{new FailParser()}));		
		assertEquals(1, parser.size());
		parser.setParsers(Arrays.asList(new PacketParser[]{new FailParser(), new UseSkillParser()}));		
		assertEquals(1, parser.size());
	}

}
