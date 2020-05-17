package org.mswsplex.nope.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.NOPE;

/**
 * TODO
 * 
 * @author imodm
 *
 */
public class GlideB implements Check, Listener {

	@SuppressWarnings("unused")
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
//		Player player = event.getPlayer();
//		CPlayer cp = plugin.getCPlayer(player);
	}

	@Override
	public String getCategory() {
		return "Glide";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#2";
	}

	@Override
	public boolean lagBack() {
		return true;
	}
}
