package com.googlecode.reunion.jcommon;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_ParsedItem {

	private String name;

	private List<S_ParsedItemMember> memberList = new Vector<S_ParsedItemMember>();

	public S_ParsedItem() {
		super();

	}

	public S_ParsedItem(String name) {
		super();
		setName(name);

	}

	public void addMember(S_ParsedItemMember member) {
		memberList.add(member);
	}

	public void addMember(String Name, String Value) {
		memberList.add(new S_ParsedItemMember(Name, Value));
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

	public S_ParsedItemMember getMember(String name) {
		Iterator<S_ParsedItemMember> iter = getMemberListIterator();
		while (iter.hasNext()) {
			S_ParsedItemMember member = iter.next();
			if (name.toLowerCase().equals(member.getName().toLowerCase())) {
				return member;
			}
		}
		return null;
	}

	public Iterator<S_ParsedItemMember> getMemberListIterator() {
		return memberList.iterator();
	}

	public String getMemberValue(String name) {
		Iterator<S_ParsedItemMember> iter = getMemberListIterator();
		while (iter.hasNext()) {
			S_ParsedItemMember member = iter.next();
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
}
