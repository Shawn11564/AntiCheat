package org.mswsplex.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.NOPE;

/**
 * Gets the average speed while a player is sneaking and flags if too high
 * 
 * @author imodm
 *
 */
public class FastSneakA implements Check, Listener {

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

	private final int SIZE = 20;

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;

		if (cp.timeSince("disableFlight") < 2000)
			return;

		if (!player.isSneaking())
			return;

		if (player.isSprinting())
			return;

		if (cp.timeSince("lastSprinting") < 200)
			return;

		if (cp.timeSince("lastLiquid") < 500)
			return;

		if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE"))
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() != from.getY())
			return;

		double dist = from.distanceSquared(to);

		List<Double> distances = (ArrayList<Double>) cp.getTempData("sneakDistances");
		if (distances == null)
			distances = new ArrayList<>();

		double avg = 0;
		for (double d : distances)
			avg += d;

		avg /= distances.size();

		distances.add(0, dist);

		for (int i = distances.size() - SIZE; i < distances.size() && i > SIZE; i++)
			distances.remove(i);

		cp.setTempData("sneakDistances", distances);

		if (distances.size() < SIZE)
			return;

		double min = .0136;

		if (avg < min)
			return;

		cp.flagHack(this, (int) Math.round((avg / (min - min / 10)) * 20.0) + 5,
				"Average: &e" + avg + "&7 >= &a" + min + "\n&7Size: &e" + distances.size() + "&7 >= &a" + SIZE);
	}

	@Override
	public String getCategory() {
		return "FastSneak";
	}

	@Override
	public String getDebugName() {
		return "FastSneak#1";
	}

	@Override // Don't lag back because this can cause a few false flags
	public boolean lagBack() {
		return false;
	}
}
