package org.mswsplex.nope.checks.movement;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

/**
 * Checks if a player moves completely horizontally without being on the ground
 * 
 * @author imodm
 *
 */
public class FlightA implements Check, Listener {

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

		if (player.isOnGround())
			return;
		if (player.isFlying() || cp.isInClimbingBlock() || player.isInsideVehicle())
			return;
		if (cp.timeSince("lastLiquid") < 400)
			return;

		if (player.getNearbyEntities(1, 2, 1).stream().filter((entity) -> entity instanceof Boat)
				.collect(Collectors.toList()).size() > 0)
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() != from.getY())
			return;

		if (player.getLocation().getBlock().getType() == Material.COBWEB)
			return;

		if (cp.timeSince("lastOnGround") <= 300)
			return;
		if (cp.timeSince("lastBlockPlace") < 1500)
			return;
		if (cp.timeSince("lastDamageTaken") < 500)
			return;

		if (player.getVelocity().getY() > 0)
			return;

		cp.flagHack(this, 30);
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return "Flight#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
