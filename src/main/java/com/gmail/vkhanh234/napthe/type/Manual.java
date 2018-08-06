package com.gmail.vkhanh234.napthe.type;

import com.gmail.vkhanh234.napthe.ChatStatus;
import com.gmail.vkhanh234.napthe.data.Card;
import org.bukkit.configuration.ConfigurationSection;


public class Manual extends Type{
	public Manual(ConfigurationSection cs) {
	}

	public Card send(ChatStatus c) {
		Card r = c.cloneCard();
		r.seen = true;
		r.setCode(201);
		return r;
	}
}
