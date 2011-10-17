package com.googlecode.reunion.jreunion.game.items.etc;

import com.googlecode.reunion.jreunion.game.Item;

public class AmplifiedMissionReceiver extends MissionReceiver{

	
	public AmplifiedMissionReceiver(int id) {
		super(id);
	}
	
	@Override
	public Item<?> create() {
		Item<?> item = super.create();
		item.setExtraStats(30);
		return item;
	}

}