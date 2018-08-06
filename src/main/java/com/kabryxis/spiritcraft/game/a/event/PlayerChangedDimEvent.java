package com.kabryxis.spiritcraft.game.a.event;

import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangedDimEvent extends Event {
	
	private static HandlerList handlerList = new HandlerList();
	
	private final SpiritPlayer player;
	private final DimType newDim;
	
	public PlayerChangedDimEvent(SpiritPlayer player, DimType newDim) {
		this.player = player;
		this.newDim = newDim;
	}
	
	public SpiritPlayer getPlayer() {
		return player;
	}
	
	public DimType getNewDim() {
		return newDim;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
	
	public static HandlerList getHandlerList() {
		return handlerList;
	}
	
	public enum DimType {
		
		NORMAL, SPIRIT
		
	}
	
}
