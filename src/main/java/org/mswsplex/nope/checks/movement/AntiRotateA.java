package org.mswsplex.nope.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

/**
 * 
 * Explanation: Slightly teleports the player if they aren't moving every so
 * ticks and sees if (after one tick) the player has in fact actually teleported
 * 
 * @author imodm
 * 
 */
public class AntiRotateA implements Check {

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	private final int CHECK_PER = 200;

	@Override
	public void register(NOPE plugin) {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				CPlayer cp = plugin.getCPlayer(player);
				if (player.getSpectatorTarget() != null)
					return;
				if (player.getOpenInventory() != null)
					return;
				if (cp.timeSince("lastMove") <= 100)
					return;
				Location modified = player.getLocation();
				float original = modified.getYaw();
				modified.setYaw(modified.getYaw() - .0001f);

				player.teleport(modified);

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
					if (player.getLocation().getYaw() != original)
						return;
					cp.flagHack(this, 50);
				}, 1);
			}
		}, CHECK_PER, CHECK_PER);
	}

	@Override
	public String getCategory() {
		return "AntiRotate";
	}

	@Override
	public String getDebugName() {
		return "AntiRotate#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
