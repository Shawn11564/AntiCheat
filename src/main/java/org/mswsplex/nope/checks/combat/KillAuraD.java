package org.mswsplex.nope.checks.combat;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

/**
 * Creates a mini armorstand below the player, this stand SHOULD NOT be hit
 * (legimiately)
 * 
 * @author imodm
 * 
 * @deprecated
 *
 */
public class KillAuraD implements Check, Listener {

	private NOPE plugin;

	private HashMap<Player, Entity> stands;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;

		stands = new HashMap<>();
	}

	private final double CHECK_EVERY = 10000;
	private final int TICKS_TO_WAIT = 40;

	@EventHandler
	public void onEntityDamgedByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getEntity().hasMetadata("lowerKillAuraMark")) {
			if (!event.getEntity().getMetadata("lowerKillAuraMark").get(0).asString()
					.equals(player.getUniqueId().toString())) {
				event.setCancelled(true);
			} else {
				cp.flagHack(this, 10);
				cp.setTempData("lastKillAuraCheck2", (double) System.currentTimeMillis() - CHECK_EVERY);
				stands.remove(player);
				event.getEntity().remove();
			}
		}

		if (stands.containsKey(player))
			return;

		if (cp.timeSince("lastKillAuraCheck2") < CHECK_EVERY)
			return;

		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		if (rnd.nextDouble() < .20)
			return;

		if (event.getEntity().hasMetadata("lowerKillAuraMark"))
			return;

		ArmorStand stand = (ArmorStand) player.getWorld()
				.spawnEntity(cp.getLastSafeLocation().clone().subtract(0, .1, 0), EntityType.ARMOR_STAND);

		stand.setGravity(false);
		stand.setVisible(false);
		stand.setSmall(true);
		stand.setBasePlate(false);

		stand.setMetadata("lowerKillAuraMark", new FixedMetadataValue(plugin, player.getUniqueId().toString()));

		stands.put(player, stand);

//		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
//				teleportArmorStand(player, stand, event.getEntity()), 0, 1);
		BukkitTask task = new BukkitRunnable() {

			@Override
			public void run() {
				teleportArmorStand(player, stand, event.getEntity());
			}
		}.runTaskTimer(plugin, 0, 1);

		new BukkitRunnable() {
			@Override
			public void run() {
				task.cancel();
				if (stands.get(player) == null) {
					stands.remove(player);
					return;
				}
				stands.get(player).remove();
				stands.remove(player);
			}
		}.runTaskLater(plugin, TICKS_TO_WAIT);

		/*
		 * Bukkit.getScheduler().runTaskLater(plugin, () -> { //
		 * Bukkit.getScheduler().cancelTask(id); task.cancel(); if (stands.get(player)
		 * == null) { stands.remove(player); return; } stands.get(player).remove();
		 * stands.remove(player); }, TICKS_TO_WAIT);
		 */

		cp.setTempData("lastKillAuraCheck2", (double) System.currentTimeMillis());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		cp.setTempData("lastKillAuraCheck2", (double) System.currentTimeMillis() - 20000);
	}

	@Override
	public String getCategory() {
		return "KillAura";
	}

	@Override
	public String getDebugName() {
		return "KillAura#4";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

	public BukkitRunnable teleportArmorStand(Player player, Entity stand, Entity target) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (target == null || target.isDead() || !target.isValid() || (stand.getNearbyEntities(5, 5, 5).stream()
						.filter((entity) -> entity instanceof Projectile).collect(Collectors.toList()).size() > 0))
					stand.remove();
				CPlayer cp = plugin.getCPlayer(player);
				stand.teleport(cp.getLastSafeLocation());
			}
		};
	}
}
