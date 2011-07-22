package com.googlecode.reunion.jreunion.server.parser.basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.parser.PacketHandler;
import com.googlecode.reunion.jreunion.server.parser.Parser;

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
