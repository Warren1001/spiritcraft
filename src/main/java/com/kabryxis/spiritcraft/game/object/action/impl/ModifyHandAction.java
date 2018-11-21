package com.kabryxis.spiritcraft.game.object.action.impl;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import com.kabryxis.spiritcraft.game.player.SpiritPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ModifyHandAction extends SpiritGameObjectAction {
	
	private Map<Enchantment, Integer> addEnchants;
	private Map<Enchantment, Integer> removeEnchants;
	private Map<Enchantment, Integer> toggleEnchants;
	
	public ModifyHandAction(ConfigSection creatorData) {
		super(creatorData, "modify_hand");
		addRequiredObject("hand", ItemStack.class);
		addRequiredObject("triggerer", SpiritPlayer.class);
		handleSubCommand("addenchant", false, true, data -> {
			if(addEnchants == null) addEnchants = new HashMap<>();
			addEnchantToMap(addEnchants, data);
		});
		handleSubCommand("removeenchant", false, true, data -> {
			if(removeEnchants == null) removeEnchants = new HashMap<>();
			addEnchantToMap(removeEnchants, data);
		});
		handleSubCommand("toggleenchant", false, true, data -> {
			if(toggleEnchants == null) toggleEnchants = new HashMap<>();
			addEnchantToMap(toggleEnchants, data);
		});
	}
	
	@Override
	public void perform(ConfigSection triggerData) {
		super.perform(triggerData);
		ItemStack itemStack = triggerData.get("hand");
		ItemMeta meta = itemStack.getItemMeta();
		if(addEnchants != null) addEnchants.forEach((enchant, i) -> meta.addEnchant(enchant, i, true));
		if(removeEnchants != null) removeEnchants.forEach((enchant, i) -> {
			if(meta.getEnchantLevel(enchant) == i) meta.removeEnchant(enchant);
		});
		if(toggleEnchants != null) toggleEnchants.forEach((enchant, i) -> {
			if(meta.getEnchantLevel(enchant) == i) meta.removeEnchant(enchant);
			else meta.addEnchant(enchant, i, true);
		});
		itemStack.setItemMeta(meta);
	}
	
	private void addEnchantToMap(Map<Enchantment, Integer> map, String data) {
		String enchantName;
		int level;
		if(data.contains("-")) {
			String[] args = data.split("-", 2);
			enchantName = args[0].toUpperCase();
			level = Integer.parseInt(args[1]);
		}
		else {
			enchantName = data;
			level = 1;
		}
		map.put(Enchantment.getByName(enchantName), level);
	}
	
}
