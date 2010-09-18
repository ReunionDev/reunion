package com.googlecode.reunion.jcommon;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ParsedItemMember {

	private String name;

	private String value;

	public ParsedItemMember() {
		super();

	}

	public ParsedItemMember(String name, String value) {
		super();
		setName(name);
		setValue(value);

	}

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
