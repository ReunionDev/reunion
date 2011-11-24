package org.reunionemu.jreunion.server.parser.basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.parser.PacketHandler;

public class LoginHandler extends PacketHandler {

	@Override
	public void handle(Client client, Matcher matcher) {
		// TODO Auto-generated method stub

	}


	@Override
	public Pattern[] getPatterns() {
		return new Pattern [] {Pattern.compile("^(login|play|\\d+)$")};
		
		
	}

}
