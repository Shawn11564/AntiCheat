package org.mswsplex.nope.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

/**
 * Checks if a player moves vertically straight up
 * 
 * @author imodm
 *
 */
public class FlightC implements Check, Listener {

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

		if (player.isFlying() || cp.isInWeirdBlock() || player.isInsideVehicle() || cp.isInClimbingBlock())
			return;

		if (cp.timeSince("lastDamageTaken") < 2000)
			return;

		if (cp.timeSince("wasFlying") < 2000)
			return;

		if (cp.timeSince("lastBlockPlace") < 1000)
			return;

		if (cp.timeSince("lastLiquid") < 1000)
			return;

		if (cp.timeSince("lastTeleport") < 1000)
			return;

		if (cp.timeSince("lastFlightGrounded") < 500)
			return;

		if (player.hasPotionEffect(PotionEffectType.LEVITATION))
			return;

		if (cp.timeSince("lastInClimbing") < 1000) {
			cp.removeTempData("lastGroundLocation");
			return;
		}

		Location safe = cp.getLastSafeLocation();

		if (event.getTo().getY() - 3 < safe.getY())
			return;

		if (event.getTo().getY() <= event.getFrom().getY())
			return;

		cp.flagHack(this, 10);
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return "Flight#3";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
