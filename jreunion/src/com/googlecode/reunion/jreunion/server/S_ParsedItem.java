package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_ParsedItem {

	private String name;

	public S_ParsedItem() {
		super();
		
	}

	public S_ParsedItem(String name) {
		super();
		setName(name);
		
	}

	private List<S_ParsedItemMember> memberList = new Vector<S_ParsedItemMember>();

	public void addMember(S_ParsedItemMember member) {
		memberList.add(member);
	}

	public void addMember(String Name, String Value) {
		memberList.add(new S_ParsedItemMember(Name,Value));
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Iterator<S_ParsedItemMember> getMemberListIterator() {
		return memberList.iterator();
	}

	public S_ParsedItemMember getMember(String name) {
		Iterator<S_ParsedItemMember> iter = getMemberListIterator();
		while (iter.hasNext()) {
			S_ParsedItemMember member = (S_ParsedItemMember) iter.next();
			if (name.toLowerCase().equals(member.getName().toLowerCase())) {
				return member;
			}
		}
		return null;
	}

	public String getMemberValue(String name) {
		Iterator<S_ParsedItemMember> iter = getMemberListIterator();
		while (iter.hasNext()) {
			S_ParsedItemMember member = (S_ParsedItemMember) iter.next();
			if (name.toLowerCase().equals(member.getName().toLowerCase())) {
				return member.getValue();
			}
		}
		return null;
	}

	public boolean checkMembers(String[] memberNames) {
		for (int i = 0; i < memberNames.length; i++) {
			if (getMember(memberNames[i]) == null)
				return false;
		}
		return true;
	}
	public void clear()
	{
		memberList.clear();
	
	}
}
