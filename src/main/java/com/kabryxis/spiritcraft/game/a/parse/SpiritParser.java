package com.kabryxis.spiritcraft.game.a.parse;

public class SpiritParser implements Parser {
	
	@Override
	public void parse(String parse, ParseHandler handler) {
		if(parse != null && !parse.isEmpty()) {
			if(parse.contains(";")) {
				String[] parseEntries = parse.split(";");
				for(String parseEntry : parseEntries) {
					parseSubCommand(handler, parseEntry);
				}
			}
			else parseSubCommand(handler, parse);
		}
		handler.finish();
	}
	
	private void parseSubCommand(ParseHandler handler, String subCommand) {
		if(subCommand.contains("~")) {
			String[] subArgs = subCommand.split("~", 2);
			handler.parsed(subArgs[0], subArgs[1]);
		}
		else handler.parsed(subCommand, null);
	}
	
}
