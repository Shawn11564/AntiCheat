package org.mswsplex.anticheat.checks.combat;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.NOPE;
import org.mswsplex.anticheat.utils.MSG;

/**
 * Gets the average velocities of arrows shot within 100 ticks and compares them
 * to a regular max value
 * 
 * @author imodm
 *
 */
public class FastBow1 implements Check, Listener {

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
			for (Player player : Bukkit.getOnlinePlayers()) {
				CPlayer cp = plugin.getCPlayer(player);
				cp.setTempData("fastBowVelocities", 0.0);
			}
		}, 0, 100);
	}

	@EventHandler
	public void onShoot(ProjectileLaunchEvent event) {
		if (event.getEntity() == null || event.getEntity().getShooter() == null
				|| !(event.getEntity().getShooter() instanceof Player))
			return;
		if (event.getEntityType() != EntityType.ARROW)
			return;

		Player player = (Player) event.getEntity().getShooter();
		CPlayer cp = plugin.getCPlayer(player);

		if (cp.getTempDouble("fastBowVelocities") > 50) {
			if (plugin.devMode())
				MSG.tell(player, "&bvelocities: " + cp.getTempDouble("fastBowVelocities"));
			cp.flagHack(this, (int) (cp.getTempDouble("fastBowVelocities") - 50) * 3);
		}

		double vel = event.getEntity().getVelocity().lengthSquared();

		cp.setTempData("fastBowVelocities", cp.getTempDouble("fastBowVelocities") + vel);
	}

	@Override
	public String getCategory() {
		return "FastBow";
	}

	@Override
	public String getDebugName() {
		return "FastBow#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
