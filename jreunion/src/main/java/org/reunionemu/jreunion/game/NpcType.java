package org.reunionemu.jreunion.game;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NpcType{
	
	private int type;
	
	private long maxHp;
	
	private int mutant;

	private int neoProgmare;
	
	private int level;
	
	private String name;
	
	private int dmgType;

	public NpcType(int type) {
		super();
		setTypeId(type);
		loadFromReference(type);
	}

	public int getTypeId() {
		return type;
	}

	public void setTypeId(int type) {
		this.type = type;
	}	
	
	public long getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(long maxHp) {
		this.maxHp = maxHp;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMutant() {
		return mutant;
	}

	public void setMutant(int mutant) {
		this.mutant = mutant;
	}

	public int getNeoProgmare() {
		return neoProgmare;
	}

	public void setNeoProgmare(int neoProgmare) {
		this.neoProgmare = neoProgmare;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getDmgType() {
		return dmgType;
	}

	public void setDmgType(int dmgType) {
		this.dmgType = dmgType;
	}
	
	public Npc<?> create(){
		
		Npc<?> npc = new Npc(this);
	
		return npc;
	}
	
	public void loadFromReference(int id) {

		ParsedItem npc = Reference.getInstance().getNpcReference().getItemById(id);

		if (npc == null) {
			// cant find Item in the reference continue to load defaults:
			setMaxHp(100);
			setName("Unknown");
		} else {
			
			if (npc.checkMembers(new String[] { "Hp" })) {
				// use member from file
				setMaxHp(Integer.parseInt(npc.getMemberValue("Hp")));
				
			} else {
				// use default
				setMaxHp(100);
			}
			
			setName(npc.getName());
		}
	}
}