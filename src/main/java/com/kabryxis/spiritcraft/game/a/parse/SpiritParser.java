package com.kabryxis.spiritcraft.game.a.parse;

public class SpiritParser implements Parser {
	
	@Override
	public void parse(String parse, ParseHandler handler) {
		if(parse != null && !parse.isEmpty()) {
			if(parse.contains(",")) {
				int lastIndexOfEnd = -1;
				int lastIndexOfStart = -1;
				for(int index = 0; index < parse.length(); index++) {
					char c = parse.charAt(index);
					if(c == ',') {
						if(lastIndexOfEnd > lastIndexOfStart || lastIndexOfStart == -1) {
							parseSubCommand(handler, parse.substring(lastIndexOfEnd + 1, index));
							lastIndexOfEnd = index;
						}
					}
					else if(c == '~') lastIndexOfStart = index;
					else if(c == ';') {
						if(lastIndexOfStart > lastIndexOfEnd) parseSubCommand(handler, parse.substring(lastIndexOfEnd + 1, index));
						lastIndexOfEnd = index;
					}
					if(index == parse.length() - 1) parseSubCommand(handler, parse.substring(lastIndexOfEnd + 1));
				}
			}
			else parseSubCommand(handler, parse);
		}
		handler.finish();
	}
	
	private void parseSubCommand(ParseHandler handler, String subCommand) {
		if(subCommand.contains(":")) {
			String[] subArgs = subCommand.split(":", 2);
			handler.parsed(subArgs[0], subArgs[1]);
		}
		else handler.parsed(subCommand, null);
	}
	
	@Override
	public String[] splitData(String data) {
		return data.contains("~") ? data.split("~", 2) : new String[] { data, null };
	}
	
}
