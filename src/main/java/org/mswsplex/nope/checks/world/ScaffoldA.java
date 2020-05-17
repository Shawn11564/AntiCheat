package org.mswsplex.nope.checks.world;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

/**
 * Checks how many blocks a player places right below themselves
 * 
 * @author imodm
 *
 */
public class ScaffoldA implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				CPlayer cp = plugin.getCPlayer(p);
				cp.setTempData("scaffoldBlocksPlaced", 0);
			}
		}, 0, 40);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying())
			return;

		Block placed = event.getBlockPlaced();

		if (!player.getLocation().subtract(0, 1, 0).getBlock().equals(placed))
			return;

		int blocksPlaced = cp.getTempInteger("scaffoldBlocksPlaced");
		cp.setTempData("scaffoldBlocksPlaced", blocksPlaced + 1);

		if (blocksPlaced <= 5)
			return;

		cp.flagHack(this, (blocksPlaced - 5) * 10, "Blocks: &e" + blocksPlaced + "&7 > &a5");
	}

	@Override
	public String getCategory() {
		return "Scaffold";
	}

	@Override
	public String getDebugName() {
		return "Scaffold#1";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
