package org.mswsplex.nope.checks.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

public class AutoToolA implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData("lastClicked", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onSwap(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (cp.timeSince("lastClicked") != 98)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "AutoTool";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
