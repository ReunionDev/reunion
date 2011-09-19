package com.googlecode.reunion.jreunion.server;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Quest;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class QuestFactory {

	public static Quest create(int questId) {
		
		Quest quest = new Quest(questId);

		//String className = "com.googlecode.reunion.jreunion.game.Quest";		
		
		//quest = (Quest)ClassFactory.create(className, questId);
		
		return quest;
	}

	public static Quest loadQuest(int questId) {		
		return DatabaseUtils.getStaticInstance().loadQuest(questId);
		
	}

	public QuestFactory() {
		super();

	}

}
