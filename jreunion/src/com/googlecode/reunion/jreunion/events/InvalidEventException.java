package com.googlecode.reunion.jreunion.events;

public class InvalidEventException extends RuntimeException {

	public InvalidEventException(Event event, Class class1) {
		super("Invalid event class '"+event.getClass()+"' in filter, expecting '"+class1);
	}

}
