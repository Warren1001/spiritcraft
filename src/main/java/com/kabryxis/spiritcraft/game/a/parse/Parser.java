package com.kabryxis.spiritcraft.game.a.parse;

public interface Parser {
	
	void parse(String parse, ParseHandler handler);
	
	String[] splitData(String data);
	
}
