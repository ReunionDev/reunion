package com.googlecode.reunion.jcommon;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ParsedItem implements Iterable<ParsedItemMember> {

	private String name;

	private List<ParsedItemMember> memberList = new Vector<ParsedItemMember>();

	public ParsedItem() {
		super();
	}

	public ParsedItem(String name) {
		super();
		setName(name);
	}

	public void addMember(ParsedItemMember member) {
		memberList.add(member);
	}

	public void addMember(String Name, String Value) {
		memberList.add(new ParsedItemMember(Name, Value));
	}

	public boolean checkMembers(String[] memberNames) {
		for (String memberName : memberNames) {
			if (getMember(memberName) == null) {
				return false;
			}
		}
		return true;
	}

	public void clear() {
		memberList.clear();

	}

	public ParsedItemMember getMember(String name) {
		Iterator<ParsedItemMember> iter = getMemberListIterator();
		while (iter.hasNext()) {
			ParsedItemMember member = iter.next();
			if (name.toLowerCase().equals(member.getName().toLowerCase())) {
				return member;
			}
		}
		return null;
	}

	public Iterator<ParsedItemMember> getMemberListIterator() {
		return memberList.iterator();
	}

	public String getMemberValue(String name) {
		Iterator<ParsedItemMember> iter = getMemberListIterator();
		while (iter.hasNext()) {
			ParsedItemMember member = iter.next();
			if (name.toLowerCase().equals(member.getName().toLowerCase())) {
				return member.getValue();
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Iterator<ParsedItemMember> iterator() {
		return getMemberListIterator();
	}
}
