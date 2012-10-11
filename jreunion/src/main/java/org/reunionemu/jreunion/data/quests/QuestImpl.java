package org.reunionemu.jreunion.data.quests;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.data.quests.objectives.impl.MobObjectiveImpl;
import org.reunionemu.jreunion.data.quests.objectives.impl.PointsObjectiveImpl;
import org.reunionemu.jreunion.data.quests.restrictions.impl.LevelRestrictionImpl;
import org.reunionemu.jreunion.data.quests.restrictions.impl.RaceRestrictionImpl;
import org.reunionemu.jreunion.data.quests.rewards.impl.ExperienceRewardImpl;
import org.reunionemu.jreunion.data.quests.rewards.impl.ItemRewardImpl;
import org.reunionemu.jreunion.data.quests.rewards.impl.LimeRewardImpl;

@XmlType(name="quest")
public class QuestImpl implements Quest {
	
	@XmlAttribute(required=true)
	protected int id;

	@XmlAttribute(required=false)
	protected String name;
	
	@XmlElement(required=false)
	protected String description;
	
	@XmlElements(value={
		@XmlElement(type=ExperienceRewardImpl.class, name="experience"),
		@XmlElement(type=LimeRewardImpl.class, name="lime"),
		@XmlElement(type=ItemRewardImpl.class, name="item")
		})
	protected List<Reward> rewards = new LinkedList<Reward>();
	
	
	@XmlElements(value={
			@XmlElement(type=MobObjectiveImpl.class, name="mob"),
			@XmlElement(type=PointsObjectiveImpl.class, name="points")
		})
	
	protected List<Objective> objectives = new LinkedList<Objective>();
	
	@XmlElements(value={
			@XmlElement(type=LevelRestrictionImpl.class, name="level"),
			@XmlElement(type=RaceRestrictionImpl.class, name="race")
		})
	protected List<Restriction> restrictions = new LinkedList<Restriction>();
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<Reward> getRewards(){
		return rewards;
	}

	@Override
	public List<Objective> getObjectives() {
		return objectives;
	}

}
