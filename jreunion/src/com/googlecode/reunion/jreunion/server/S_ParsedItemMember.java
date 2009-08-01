package com.googlecode.reunion.jreunion.server;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_ParsedItemMember {

	public S_ParsedItemMember() {
		super();
		
	}

	public S_ParsedItemMember(String name, String value) {
		super();
		setName(name);
		setValue(value);
		
	}

	private String name;

	private String value;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
