package org.reunionemu.jreunion.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.Pet;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Quest;
import org.reunionemu.jreunion.game.Pet.PetStatus;

public class PetManager {

	private java.util.List<Pet> petList = new Vector<Pet>();
	
	public PetManager(){
		
	}
	
	public void loadPets(){
		petList = Database.getInstance().loadPets();
		if(petList != null){
			LoggerFactory.getLogger(this.getClass()).info("Loaded "+petList.size()+" pets");
		}
	}
	
	public Pet getPet(int petid){
		for(Pet pet : petList){
			if(pet.getId() == petid){
				return pet;
			}
		}
		return null;
	}
	
	public Pet getPet(Player owner){
		for(Pet pet : petList){
			if(pet.getId() == owner.getPetId()){
				return pet;
			}
		}
		return null;
	}
	
	public synchronized boolean addPet(Pet pet){
		if(pet!=null && !petList.contains(pet)){
			petList.add(pet);
			return true;
		}
		return false;
	}
	
	public synchronized boolean removePet(Pet pet){
		if(pet == null){
			return false;
		}
		while(petList.contains(pet)){
			petList.remove(pet);
		}
		return true;
	}
	
	public boolean isEmpty(){
		return petList.isEmpty();
	}
	
	public int geNumberOfPets(){
		return petList.size();
	}
	
	public int getEggType(int race){
		switch(race){
			case 0: return 614;
			case 1: return 615;
			case 2: return 616;
			case 3: return 617;
			case 4: return 1161;
			
			default: return -1;
		}
	}
	
	public Iterator<Pet> getListIterator(){
		return petList.iterator();
	}
	
	public java.util.List<Pet> getList(){
		return petList;
	}
	
	public void buyEgg(Pet pet, int tab){
		if(pet == null)
			return;
		Player player = pet.getOwner();
		Item<?> egg = Server.getInstance().getWorld().getItemManager().create(getEggType(player.getRace().value()));
		player.getPosition().getLocalMap().createEntityId(egg);
		player.pickItem(egg, tab);
		pet.sendStatus(PetStatus.STATE);
		player.setLime(player.getLime() - 1000000);
		player.setPet(pet);
		player.save();
		player.setPetId(pet.getId());
		addPet(pet);
		LoggerFactory.getLogger(this.getClass()).info("Player: "+player+" bought Pet Egg: "+pet);
	}
}
