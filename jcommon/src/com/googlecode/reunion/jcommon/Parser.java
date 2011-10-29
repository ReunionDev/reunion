package com.googlecode.reunion.jcommon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Parser implements Iterable<ParsedItem> {

	private List<ParsedItem> itemList = new Vector<ParsedItem>();

	public Parser() {
		super();

	}

	public void addMember(ParsedItem item) {
		itemList.add(item);
	}

	public boolean checkItems(String[] itemNames) {
		for (String itemName : itemNames) {
			if (getItem(itemName) == null) {
				return false;
			}
		}
		return true;
	}

	public void clear() {

		Iterator<ParsedItem> iter = getItemListIterator();
		while (iter.hasNext()) {
			ParsedItem item = iter.next();
			item.clear();
		}
		itemList.clear();

	}

	public String dump() {
		String dumpString = new String("");
		Iterator<ParsedItem> itemIter = getItemListIterator();
		while (itemIter.hasNext()) {
			ParsedItem parsedItem = itemIter.next();
			dumpString += "\n[" + parsedItem.getName() + "]\n";
			Iterator<ParsedItemMember> memberIter = parsedItem
					.getMemberListIterator();
			while (memberIter.hasNext()) {
				ParsedItemMember parsedItemMember = memberIter.next();
				dumpString += parsedItemMember.getName() + " = "
						+ parsedItemMember.getValue() + "\n";
			}

		}
		return dumpString;

	}

	public ParsedItem find(String membername, String membervalue) {
		Iterator<ParsedItem> itemIter = getItemListIterator();
		while (itemIter.hasNext()) {
			ParsedItem parsedItem = itemIter.next();
			ParsedItemMember member = parsedItem.getMember(membername);
			if (member == null) {
				continue;
			}
			if (member.getValue().toLowerCase()
					.equals(membervalue.toLowerCase())) {
				return parsedItem;
			}

		}
		return null;
	}

	public ParsedItem getItem(String name) {
		Iterator<ParsedItem> itemIter = getItemListIterator();
		while (itemIter.hasNext()) {
			ParsedItem parsedItem = itemIter.next();
			if (parsedItem.getName().toLowerCase().equals(name.toLowerCase())) {
				return parsedItem;
			}
		}
		return null;
	}

	public ParsedItem getItemById(int id) {

		return find("id", String.valueOf(id));
	}

	public Iterator<ParsedItem> getItemListIterator() {
		return itemList.iterator();
	}

	public int getItemListSize() {
		return itemList.size();
	}

	public void Parse(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			Logger.getLogger(Parser.class).info("Parsing error: '" + file.getAbsolutePath()
					+ "' does not exist");
			throw new FileNotFoundException(file.getAbsolutePath());
			
		}
		try {
			ParsedItem parsedItem = null;

			BufferedReader input = new BufferedReader(new FileReader(file));

			String line = null;
			int linenr = 0;

			while ((line = input.readLine()) != null) {
				linenr++;
				line = line.trim();
				if (line.startsWith("#") || line.length() == 0) {
					continue;
				}
				line = line.split("#")[0];
				line = line.trim();
				String[] object = line.split("=");
				if (object.length == 1) {
					String hobject = object[0];
					if (hobject.startsWith("[") && hobject.endsWith("]")) {
						String hobjectname = hobject.substring(1,
								hobject.length() - 1);
						hobjectname = hobjectname.trim();
						if (getItem(hobjectname) != null) {
							Logger.getLogger(Parser.class).info(parseError(filename, linenr,
									"Object with name \"" + hobjectname
											+ "\" already exits", line));
							continue;
						}
						parsedItem = new ParsedItem(hobjectname);
						addMember(parsedItem);

					} else {
						Logger.getLogger(Parser.class).info(parseError(filename, linenr,
								"Line can not be identified", line));
					}
				}

				if (object.length == 2) {
					if (parsedItem == null) {

						Logger.getLogger(Parser.class).info(parseError(filename, linenr,
								"Member needs an object", line));
						continue;
					}
					String name = object[0].trim();
					String value = object[1].trim();
					ParsedItemMember parsedItemMember = new ParsedItemMember(
							name, value);
					parsedItem.addMember(parsedItemMember);
				}
				if (object.length < 1 || object.length > 2) {
					Logger.getLogger(Parser.class).info(parseError(filename, linenr,
							"Invalid member syntax", line));
					continue;
				}

			}
			input.close();

		} catch (Exception e) {

			Logger.getLogger(this.getClass()).warn("Exception",e);
		}

	}

	private String parseError(String filename, int linenr, String errorMessage,
			String line) {
		String parseErrorMessage = new String();
		parseErrorMessage = "Parsing error line " + linenr + " in " + filename
				+ " (" + errorMessage + "): \"" + line + "\"";
		return parseErrorMessage;
	}

	@Override
	public Iterator<ParsedItem> iterator() {
		return getItemListIterator();
	}

}
