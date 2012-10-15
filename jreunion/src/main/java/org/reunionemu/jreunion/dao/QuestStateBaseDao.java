package org.reunionemu.jreunion.dao;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.springframework.data.repository.CrudRepository;

public interface QuestStateBaseDao<A extends QuestState> extends CrudRepository<A, Long>, QuestStateDaoCustom 
{
		
}
