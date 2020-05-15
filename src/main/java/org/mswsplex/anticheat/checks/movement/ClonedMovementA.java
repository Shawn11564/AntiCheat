package org.mswsplex.anticheat.checks.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.NOPE;

/**
 * Checks recent movements, their differences, and flags if there are too many
 * duplicate differences
 * 
 * @author imodm
 *
 */
public class ClonedMovementA implements Check, Listener {

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

	@SuppressWarnings({ "unchecked" })
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.isFlying() || player.isInsideVehicle())
			return;
		if (cp.isInClimbingBlock())
			return;
		if (cp.isBlockNearby(Material.COBWEB))
			return;
		if (cp.timeSince("lastBlockPlace") < 500)
			return;
		if (cp.timeSince("lastTeleported") < 1000)
			return;

		if (player.isOnGround())
			return;

		Location from = event.getFrom(), to = event.getTo();

		if (cp.timeSince("lastHorizontalBlockChange") > 500)
			return;

		double dist = Math.abs(to.getX() - from.getX()) + Math.abs(to.getZ() - from.getZ());

		if (dist == 0)
			return;

		if (player.getLocation().getBlock().getType() == Material.COBWEB && dist <= 0.06586018003872596)
			return;

		List<Double> distances = (ArrayList<Double>) cp.getTempData("teleportDistances");
		if (distances == null)
			distances = new ArrayList<>();

		int amo = distances.stream().filter((val) -> val == dist).collect(Collectors.toList()).size();

		distances.add(0, dist);

		for (int i = distances.size() - SIZE; i < distances.size() && i > SIZE; i++)
			distances.remove(i);

		cp.setTempData("teleportDistances", distances);

		if (amo < SIZE / 4)
			return;

		cp.flagHack(this, (amo - (SIZE / 4)) * 2 + 5,
				"Dist: &e" + dist + " \n&7Similar: &e" + amo + "&7 >= &a" + (SIZE / 4));
	}

	@Override
	public String getCategory() {
		return "ClonedMovements";
	}

	@Override
	public String getDebugName() {
		return "ClonedMovement#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
