package org.reunionemu.jreunion.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.model.quests.Objective;
import org.reunionemu.jreunion.model.quests.Restriction;
import org.reunionemu.jreunion.model.quests.Reward;
import org.reunionemu.jreunion.model.quests.objectives.MobObjectiveImpl;
import org.reunionemu.jreunion.model.quests.objectives.PointsObjectiveImpl;
import org.reunionemu.jreunion.model.quests.restrictions.LevelRestrictionImpl;
import org.reunionemu.jreunion.model.quests.restrictions.RaceRestrictionImpl;
import org.reunionemu.jreunion.model.quests.restrictions.RepeatRestrictionImpl;
import org.reunionemu.jreunion.model.quests.rewards.ExperienceRewardImpl;
import org.reunionemu.jreunion.model.quests.rewards.ItemRewardImpl;
import org.reunionemu.jreunion.model.quests.rewards.LimeRewardImpl;

@XmlType(name="quest")
public class QuestImpl implements Quest {
	
	@XmlAttribute(required=true)
	protected int id;

	@XmlAttribute(required=false)
	protected String name;
	
	@XmlElement(required=false)
	protected String description;
	
	@XmlElementWrapper(required=false)
	@XmlElements(value={
		@XmlElement(type=ExperienceRewardImpl.class, name="experience"),
		@XmlElement(type=LimeRewardImpl.class, name="lime"),
		@XmlElement(type=ItemRewardImpl.class, name="item")
		})
	protected List<Reward> rewards;
	
	@XmlElementWrapper(required=false)
	@XmlElements(value={
			@XmlElement(type=MobObjectiveImpl.class, name="mob"),
			@XmlElement(type=PointsObjectiveImpl.class, name="points")
		})	
	protected List<Objective> objectives;
	
	@XmlElementWrapper(required=false)
	@XmlElements(value={
			@XmlElement(type=LevelRestrictionImpl.class, name="level"),
			@XmlElement(type=RaceRestrictionImpl.class, name="race"),
			@XmlElement(type=RepeatRestrictionImpl.class, name="norepeat")
		})
	protected List<Restriction> restrictions;

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
		if(rewards==null){
			rewards = new LinkedList<Reward>();
		}
		return rewards;
	}

	@Override
	public List<Objective> getObjectives() {
		if(objectives==null){
			objectives = new LinkedList<Objective>();
		}
		return objectives;
	}
	@Override
	public List<Restriction> getRestrictions() {
		if(restrictions==null){
			restrictions = new LinkedList<Restriction>();
		}
		return restrictions;
	}

	
	@Override
	public boolean isAllowed(Player player) {
		for(Restriction restriction: getRestrictions()){
			if(!restriction.isAllowed(player)){
				return false;
			}
		}
		return true;
	}

}
