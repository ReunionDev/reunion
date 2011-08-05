package com.googlecode.reunion.jreunion.server.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Parseable {
	
	public Pattern [] getPatterns();
	
	public Parseable create(Matcher matcher);
}
