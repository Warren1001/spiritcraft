package com.kabryxis.spiritcraft.game.a.parse;

public interface ParseHandler {
	
	void parsed(String command, String data);
	
	void finish();
	
}
