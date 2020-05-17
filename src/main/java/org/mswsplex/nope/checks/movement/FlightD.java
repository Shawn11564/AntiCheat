package org.mswsplex.nope.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
public class FlightD implements Check, Listener {

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

		if (cp.hasMovementRelatedPotion())
			return;

		if (cp.isBlockNearby(Material.SCAFFOLDING))
			return;

		if (player.isInsideVehicle())
			return;

		if (player.isFlying() || cp.timeSince("wasFlying") < 5000 || player.isOnGround()
				|| cp.timeSince("lastTeleport") < 200 || cp.timeSince("lastFlightGrounded") < 500)
			return;

		if (cp.timeSince("lastLiquid") < 500)
			return;

		if (cp.timeSince("lastOnGround") < 1000)
			return;

		if (player.getLocation().getBlock().isLiquid())
			return;

		Location safe = cp.getLastSafeLocation();

		if (!safe.getWorld().equals(player.getLocation().getWorld()))
			return;

		double yDiff = safe.getY() - player.getLocation().getY();

		if (yDiff >= 0)
			return;

		double dist = safe.distanceSquared(player.getLocation());

		if (dist < 10)
			return;

		cp.flagHack(this, Math.max(Math.min((int) Math.round((dist - 10) * 10.0), 50), 10),
				"Dist: &e" + dist + "&7 >= &a10\n&7YDiff: &e" + yDiff + "&7 < 0");
	}

	@Override
	public String getCategory() {
		return "Flight";
	}

	@Override
	public String getDebugName() {
		return "Flight#4";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
