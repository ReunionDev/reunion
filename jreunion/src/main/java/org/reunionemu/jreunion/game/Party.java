package org.reunionemu.jreunion.game;

import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.server.LocalMap;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class Party {
	
	private Player leader;

	private List<Player> members = new Vector<Player> ();
	
	private int expOption;
	
	private int itemOption;
	
	private LocalMap map;

	public Party(Player leader, int expOption, int itemOption) {
		setLeader(leader);
		addMember(leader);
		setExpOption(expOption);
		setItemOption(itemOption);
		leader.setParty(this);
		setMap(leader.getPosition().getLocalMap());
	}

	public Player getLeader() {
		return leader;
	}

	public void setLeader(Player leader) {
		this.leader = leader;
	}

	public List<Player> getMembers() {
		return members;
	}	
	
	public void addMember(Player newMember){
		if(!members.contains(newMember)){
			members.add(newMember);
		}
	}
	
	public void removeMember(Player member){
		while(members.contains(member)){
			members.remove(member);
		}
	}
	
	public Player getMember(int memberEntityId){
		for(Player player : members){
			if(player.getEntityId() == memberEntityId)
				return player;
		}
		
		return null;
	}
	
	public int getMemberPosition(Player member){
		return members.indexOf(member)+1;
	}

	public int getExpOption() {
		return expOption;
	}

	public void setExpOption(int expOption) {
		this.expOption = expOption;
	}

	public int getItemOption() {
		return itemOption;
	}

	public void setItemOption(int itemOption) {
		this.itemOption = itemOption;
	}
	
	public LocalMap getMap() {
		return map;
	}

	public void setMap(LocalMap map) {
		this.map = map;
	}
	
	public void request(Player newMember){
		newMember.getClient().sendPacket(Type.PARTY_REQUEST, this);
	}
	
	public void reject(int inviterEntityId, Player newMember){
		Player member = getMember(inviterEntityId);
		member.getClient().sendPacket(Type.PARTY_SECESSION, inviterEntityId);
		member.getClient().sendPacket(Type.MSG, newMember.getName()+" rejected the party invitation.");
		
		if(getMembers().size() == 1){
			cancel();
		}
	}
	
	public void exit(Player memberLeaving){
		for(Player member : members){
			member.getClient().sendPacket(Type.PARTY_SECESSION, memberLeaving.getEntityId());
		}
		removeMember(memberLeaving);
		memberLeaving.setParty(null);
		
		if(memberLeaving == getLeader()){
			for(Player member : members){
				setLeader(member);
				break;
			}
		}
		
		if(getMembers().size() == 1){
			//getLeader().getClient().sendPacket(Type.PARTY_DISBAND);
			cancel();
		}
	}
	
	public void cancel(){
		for(Player member : members){
			member.setParty(null);
			member.getClient().sendPacket(Type.PARTY_DISBAND);
		}
		getMap().removeParty(this);
	}
	
	public void accept(int inviterEntityId, Player newMember){
		Player member = getMember(inviterEntityId);
		
		member.getClient().sendPacket(Type.SAY, "*Party* "+newMember.getName()+" becomes a party member.");
		newMember.getClient().sendPacket(Type.SAY, "*Party* You become a party member.");
		
		addMember(newMember);
		newMember.setParty(this);
		
		for(Player player : getMembers()){
			player.getClient().sendPacket(Type.PARTY_LIST, getMembers().size());
			for(Player temp : getMembers()){
				player.getClient().sendPacket(Type.PARTY_MEMBER, temp);
			}
			for(Player temp : getMembers()){
				player.getClient().sendPacket(Type.PARTY_INFO, temp);
			}
			
			player.getClient().sendPacket(Type.PARTY_CHANGE, 1, getExpOption());
			player.getClient().sendPacket(Type.PARTY_CHANGE, 2, getItemOption());
		}
		
	}
}