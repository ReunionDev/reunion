package com.googlecode.reunion.jreunion.events;

public class InvalidEventException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidEventException(Event event, Class<?> expected) {
		super("Invalid event class '"+event.getClass()+"' in filter, expecting '"+expected);
	}

}
