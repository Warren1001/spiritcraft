package com.kabryxis.spiritcraft.game.object.type.ability.action;

import com.kabryxis.kabutils.data.file.yaml.ConfigSection;
import com.kabryxis.kabutils.spigot.plugin.particleapi.ParticleInfo;
import com.kabryxis.kabutils.spigot.version.custom.arrow.trailing.TrailingArrow;
import com.kabryxis.spiritcraft.game.object.TriggerType;
import com.kabryxis.spiritcraft.game.object.Triggers;
import com.kabryxis.spiritcraft.game.object.action.SpiritGameObjectAction;
import org.bukkit.Location;
import org.bukkit.Particle;

public class TrailingArrowAction extends SpiritGameObjectAction {

    protected double velmod = 1.0;
    protected int speed = 2;

    public TrailingArrowAction(ConfigSection creatorData) {
        super(creatorData, "trailing_arrow", TriggerType.LEFT_CLICK, TriggerType.RIGHT_CLICK, TriggerType.LOOKING);
        handleSubCommand("velmod", false, double.class, d -> velmod = d);
        handleSubCommand("speed", false, int.class, i -> speed = i);
    }

    @Override
    public void perform(ConfigSection triggerData) {
        super.perform(triggerData);
        Location loc = Triggers.getOptimalLocation(triggerData);
        TrailingArrow.spawn(loc, loc.getDirection().multiply(velmod), speed, game.getTaskManager(), new ParticleInfo(Particle.CRIT));
    }

}
