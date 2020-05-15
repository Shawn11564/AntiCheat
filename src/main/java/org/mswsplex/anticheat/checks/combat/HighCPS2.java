package org.mswsplex.anticheat.checks.combat;

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
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;

/**
 * Checks CPS over slightly longer period of time
 * 
 * @author imodm
 *
 */
public class HighCPS2 implements Check, Listener {

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
				plugin.getCPlayer(target).setTempData("highCpsClicks1", 0);
			}

		}, 0, checkEvery);
	}

	private final int maxCps = 12, checkEvery = 100;

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
		cp.setTempData("highCpsClicks1", cp.getTempInteger("highCpsClicks1") + 1);

		if (cp.getTempInteger("highCpsClicks1") < (checkEvery / 20) * maxCps)
			return;

		cp.flagHack(this, (cp.getTempInteger("highCpsClicks1") - ((checkEvery / 20) * maxCps)) * 3 + 5,
				"Clicks: &e" + cp.getTempInteger("highCpsClicks1") + "&7 >= &a" + (checkEvery / 20) * maxCps
						+ "\n&7CPS: &e" + cp.getTempInteger("highCpsClicks1") / (checkEvery / 20));
	}

	@Override
	public String getCategory() {
		return "HighCPS";
	}

	@Override
	public String getDebugName() {
		return "HighCPS#2";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
