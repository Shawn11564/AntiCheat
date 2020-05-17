package org.mswsplex.nope.checks.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

/**
 * Checks if the block that a player places a block against is liquid
 * 
 * @author imodm
 *
 */
public class IllegalBlockPlaceB implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.WORLD;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (!event.getBlockAgainst().isLiquid())
			return;

		if (event.getBlock().getType() == Material.LILY_PAD)
			return;

		cp.flagHack(this, 50);
	}

	@Override
	public String getCategory() {
		return "IllegalBlockPlace";
	}

	@Override
	public String getDebugName() {
		return "IllegalBlockPlace#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
