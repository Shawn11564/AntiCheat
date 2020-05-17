package org.mswsplex.nope.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

/**
 * 
 * Checks if the player's last ongrond position is too low and too far away
 * <i>conveniently</i> also checks Jesus
 * 
 * @author imodm
 * 
 */
public class FlightE implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (!player.isInsideVehicle())
			return;

		if (player.getVehicle().getType() != EntityType.BOAT)
			return;

		if (player.getLocation().getBlock().isLiquid())
			return;

		if (player.getLocation().clone().add(0, -1, 0).getBlock().isLiquid())
			return;

		if (cp.distanceToGround() < 2)
			return;

		cp.flagHack(this, 20);
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#5";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
