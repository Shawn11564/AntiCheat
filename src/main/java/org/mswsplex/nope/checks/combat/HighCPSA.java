package org.mswsplex.nope.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;

/**
 * Checks CPS over small period of time
 * 
 * @author imodm
 *
 */
public class HighCPSA implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.COMBAT;
	}

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for (Player target : Bukkit.getOnlinePlayers()) {
				plugin.getCPlayer(target).setTempData("highCpsClicks", 0);
			}

		}, 0, checkEvery);
	}

	private final int maxCps = 14, checkEvery = 20;

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		Block block = event.getClickedBlock();

		ItemStack hand = player.getInventory().getItemInMainHand();

		if (hand != null && hand.containsEnchantment(Enchantment.DIG_SPEED))
			return;

		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING))
			return;

		if (event.getAction() == Action.LEFT_CLICK_BLOCK)
			return;

		if (block != null && (!block.getType().isSolid() || block.getType() == Material.SLIME_BLOCK))
			return;
		cp.setTempData("highCpsClicks", cp.getTempInteger("highCpsClicks") + 1);

		if (cp.getTempInteger("highCpsClicks") < (checkEvery / 20) * maxCps)
			return;

		cp.flagHack(this, (cp.getTempInteger("highCpsClicks") - ((checkEvery / 20) * maxCps)) * 3 + 5,
				"Clicks: &e" + cp.getTempInteger("highCpsClicks") + "&7 >= &a" + (checkEvery / 20) * maxCps
						+ "\n&7CPS: &e" + cp.getTempInteger("highCpsClicks") / (checkEvery / 20));
	}

	@Override
	public String getCategory() {
		return "HighCPS";
	}

	@Override
	public String getDebugName() {
		return "HighCPS#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
