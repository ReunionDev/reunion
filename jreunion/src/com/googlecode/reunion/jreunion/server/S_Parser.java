package com.googlecode.reunion.jreunion.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Parser {

	private List<S_ParsedItem> itemList = new Vector<S_ParsedItem>();
	
	public S_Parser() {
		super();
		
	}

	public S_ParsedItem getItem(String name) {
		Iterator<S_ParsedItem> itemIter = getItemListIterator();
		while (itemIter.hasNext()) {
			S_ParsedItem parsedItem = (S_ParsedItem) itemIter.next();
			if (parsedItem.getName().toLowerCase().equals(name.toLowerCase()))
				return parsedItem;
		}
		return null;
	}

	public void Parse(String filename) {
		if (!new File("data/" + filename).exists()) {
			System.out.println("Parsing error: \"" + filename
					+ "\" does not exist");
			return;
		}
		try {
			S_ParsedItem parsedItem = null;

			BufferedReader input = null;

			input = new BufferedReader(new FileReader(new File("data/"
					+ filename)));

			String line = null;
			int linenr = 0;

			while ((line = input.readLine()) != null) {
				linenr++;
				line = line.trim();
				if (line.startsWith("#") || line.length() == 0)
					continue;
				line = line.split("#")[0];
				line = line.trim();
				String[] object = line.split("=");
				if (object.length == 1) {
					String hobject = object[0];
					if (hobject.startsWith("[") && hobject.endsWith("]")) {
						String hobjectname = hobject.substring(1, hobject
								.length() - 1);
						hobjectname = hobjectname.trim();
						if (getItem(hobjectname) != null) {
							System.out.println(parseError(filename, linenr,
									"Object with name \"" + hobjectname
											+ "\" already exits", line));
							continue;
						}
						parsedItem = new S_ParsedItem(hobjectname);
						addMember(parsedItem);

					} else {
						System.out.println(parseError(filename, linenr,
								"Line can not be identified", line));
					}
				}

				if (object.length == 2) {
					if (parsedItem == null) {

						System.out.println(parseError(filename, linenr,
								"Member needs an object", line));
						continue;
					}
					String name = object[0].trim();
					String value = object[1].trim();
					S_ParsedItemMember parsedItemMember = new S_ParsedItemMember(
							name, value);
					parsedItem.addMember(parsedItemMember);
				}
				if (object.length < 1 || object.length > 2) {
					System.out.println(parseError(filename, linenr,
							"Invalid member syntax", line));
					continue;
				}

			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

	private String parseError(String filename, int linenr, String errorMessage,
			String line) {
		String parseErrorMessage = new String();
		parseErrorMessage = "Parsing error line " + linenr + " in " + filename
				+ " (" + errorMessage + "): \"" + line + "\"";
		return parseErrorMessage;
	}

	public Iterator<S_ParsedItem> getItemListIterator() {
		return itemList.iterator();
	}

	public String dump() {
		String dumpString = new String("");
		Iterator<S_ParsedItem> itemIter = getItemListIterator();
		while (itemIter.hasNext()) {
			S_ParsedItem parsedItem = (S_ParsedItem) itemIter.next();
			dumpString += "\n[" + parsedItem.getName() + "]\n";
			Iterator<S_ParsedItemMember> memberIter = parsedItem.getMemberListIterator();
			while (memberIter.hasNext()) {
				S_ParsedItemMember parsedItemMember = (S_ParsedItemMember) memberIter
						.next();
				dumpString += parsedItemMember.getName() + " = "
						+ parsedItemMember.getValue() + "\n";
			}

		}
		return dumpString;

	}

	public void addMember(S_ParsedItem item) {
		itemList.add(item);
	}

	public boolean checkItems(String[] itemNames) {
		for (int i = 0; i < itemNames.length; i++) {
			if (getItem(itemNames[i]) == null)
				return false;
		}
		return true;
	}

	public S_ParsedItem find(String membername, String membervalue) {
		Iterator<S_ParsedItem> itemIter = getItemListIterator();
		while (itemIter.hasNext()) {
			S_ParsedItem parsedItem = itemIter.next();
			S_ParsedItemMember member = parsedItem.getMember(membername);
			if (member == null)
				continue;
			if (member.getValue().toLowerCase()
					.equals(membervalue.toLowerCase()))
				return parsedItem;

		}
		return null;
	}
	public S_ParsedItem getItemById(int id)
	{
		
		return find ("id",String.valueOf(id));
	}
	public int getItemListSize()
	{
		return itemList.size();		
	}
	public void clear()
	{
		
		Iterator<S_ParsedItem> iter = getItemListIterator();
		while(iter.hasNext())
		{
			S_ParsedItem item = (S_ParsedItem) iter.next();			
			item.clear();
		}
		itemList.clear();
	
	}
	
}
