package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Entity;

import org.reunionemu.jreunion.game.*;

//@Entity
//@DiscriminatorValue(value = null)
public class ExchangeItemImpl extends ExchangeItem {

	private static final long serialVersionUID = 1L;
	
	public ExchangeItemImpl() {
	}
	public ExchangeItemImpl(Item<?> item, ExchangePosition position, Player player) {
		super(item, position, player);
	}

}
