package org.mswsplex.anticheat.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.mswsplex.anticheat.checks.combat.AntiKnockbackA;
import org.mswsplex.anticheat.checks.combat.AutoArmorA;
import org.mswsplex.anticheat.checks.combat.AutoClickerA;
import org.mswsplex.anticheat.checks.combat.FastBowA;
import org.mswsplex.anticheat.checks.combat.HighCPSA;
import org.mswsplex.anticheat.checks.combat.HighCPSB;
import org.mswsplex.anticheat.checks.combat.HighCPSC;
import org.mswsplex.anticheat.checks.combat.KillAuraB;
import org.mswsplex.anticheat.checks.combat.KillAuraE;
import org.mswsplex.anticheat.checks.combat.KillAuraF;
import org.mswsplex.anticheat.checks.combat.KillAuraG;
import org.mswsplex.anticheat.checks.movement.AntiAFKA;
import org.mswsplex.anticheat.checks.movement.AntiRotateA;
import org.mswsplex.anticheat.checks.movement.AutoWalkA;
import org.mswsplex.anticheat.checks.movement.BHopA;
import org.mswsplex.anticheat.checks.movement.ClonedMovementA;
import org.mswsplex.anticheat.checks.movement.FastClimbA;
import org.mswsplex.anticheat.checks.movement.FastSneakA;
import org.mswsplex.anticheat.checks.movement.FlightA;
import org.mswsplex.anticheat.checks.movement.FlightB;
import org.mswsplex.anticheat.checks.movement.FlightC;
import org.mswsplex.anticheat.checks.movement.FlightD;
import org.mswsplex.anticheat.checks.movement.FlightE;
import org.mswsplex.anticheat.checks.movement.FlightF;
import org.mswsplex.anticheat.checks.movement.GlideA;
import org.mswsplex.anticheat.checks.movement.InventoryMoveA;
import org.mswsplex.anticheat.checks.movement.JesusA;
import org.mswsplex.anticheat.checks.movement.NoSlowDownA;
import org.mswsplex.anticheat.checks.movement.NoSlowDownB;
import org.mswsplex.anticheat.checks.movement.NoSlowDownC;
import org.mswsplex.anticheat.checks.movement.NoSlowDownD;
import org.mswsplex.anticheat.checks.movement.NoSlowDownE;
import org.mswsplex.anticheat.checks.movement.NoWebA;
import org.mswsplex.anticheat.checks.movement.SpeedA;
import org.mswsplex.anticheat.checks.movement.SpeedB;
import org.mswsplex.anticheat.checks.movement.SpeedC;
import org.mswsplex.anticheat.checks.movement.SpiderA;
import org.mswsplex.anticheat.checks.movement.StepA;
import org.mswsplex.anticheat.checks.player.AntiFireA;
import org.mswsplex.anticheat.checks.player.AutoSwitchA;
import org.mswsplex.anticheat.checks.player.AutoToolA;
import org.mswsplex.anticheat.checks.player.ChestStealerA;
import org.mswsplex.anticheat.checks.player.FastEatA;
import org.mswsplex.anticheat.checks.player.GhostHandB;
import org.mswsplex.anticheat.checks.player.NoFallA;
import org.mswsplex.anticheat.checks.player.SafeWalkA;
import org.mswsplex.anticheat.checks.player.SelfHarmA;
import org.mswsplex.anticheat.checks.player.ZootA;
import org.mswsplex.anticheat.checks.render.AutoSneakA;
import org.mswsplex.anticheat.checks.render.InvalidMovementA;
import org.mswsplex.anticheat.checks.render.SkinBlinkerA;
import org.mswsplex.anticheat.checks.render.SpinbotA;
import org.mswsplex.anticheat.checks.tick.TimerA;
import org.mswsplex.anticheat.checks.tick.TimerB;
import org.mswsplex.anticheat.checks.world.FastBreakA;
import org.mswsplex.anticheat.checks.world.IllegalBlockBreakA;
import org.mswsplex.anticheat.checks.world.IllegalBlockPlaceB;
import org.mswsplex.anticheat.checks.world.ScaffoldA;
import org.mswsplex.anticheat.checks.world.ScaffoldB;
import org.mswsplex.anticheat.checks.world.ScaffoldC;
import org.mswsplex.anticheat.NOPE;
import org.mswsplex.anticheat.utils.MSG;

import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
public class Checks {
	private NOPE plugin;
	private List<Check> activeChecks;

	private Set<Check> checkList = new HashSet<>();

	public Checks(NOPE plugin) {
		this.plugin = plugin;
		activeChecks = new ArrayList<Check>();

		checkList.addAll(Sets.newHashSet(new FlightA(), new FlightB(), new FlightC(), new FlightD(), new FlightE(),
				new FlightF(), new SpeedA(), new SpeedB(), new SpeedC(), new ClonedMovementA(), new TimerA(),
				new TimerB(), new StepA(), new NoFallA(), new ScaffoldA(), new ScaffoldB(), new ScaffoldC(),
				new FastClimbA(), new JesusA(), new FastBowA(), new FastSneakA(), new InvalidMovementA(),
				new SpinbotA(), new IllegalBlockBreakA(), new IllegalBlockPlaceB(), new NoWebA(), new AutoWalkA(),
				new AutoClickerA(), new HighCPSA(), new HighCPSB(), new HighCPSC(), new AntiAFKA(), new AutoSneakA(),
				new InventoryMoveA(), new KillAuraB(), new KillAuraF(), new AntiRotateA(), new NoSlowDownA(),
				new NoSlowDownB(), new NoSlowDownC(), new NoSlowDownD(), new FastEatA(), new ChestStealerA(),
				new AntiFireA(), new SelfHarmA(), new AntiKnockbackA(), new ZootA(), new AutoArmorA(), new SafeWalkA(),
				new AutoToolA(), new AutoSwitchA(), new FastBreakA(), new SpiderA(), new KillAuraG(), new GlideA(),
				new BHopA(), new GhostHandB()));

		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			checkList.addAll(Sets.newHashSet(new NoSlowDownE(), new KillAuraE(), new SkinBlinkerA()));
		} else {
			MSG.warn("ProtocolLib is not enabled, certain checks will not work.");
		}
	}

	public void registerChecks() {
		for (Check check : checkList) {
			Result result = registerCheck(check);
			if (result != Result.SUCCESS)
				MSG.log("&cRegistration for " + check.getDebugName() + " is disabled (&e" + result.toString() + "&c)");
		}
	}

	public Check getCheckByDebug(String debugName) {
		for (Check check : activeChecks) {
			if (check.getDebugName().equals(debugName))
				return check;
		}
		return null;
	}

	public Set<Check> getAllChecks() {
		return checkList;
	}

	public boolean isCheckEnabled(Check check) {
		return activeChecks.contains(check);
	}

	public List<CheckType> getCheckTypes() {
		return Arrays.asList(CheckType.values());
	}

	public List<Check> getChecksWithType(CheckType type) {
		return getAllChecks().stream().filter((check) -> check.getType() == type).collect(Collectors.toList());
	}

	public List<Check> getChecksByCategory(String category) {
		return getAllChecks().stream().filter((check) -> check.getCategory().equals(category))
				.collect(Collectors.toList());
	}

	public Result registerCheck(Check check) {
		if (activeChecks.contains(check))
			return Result.ALREADY_REGISTERED;
		if (!plugin.config.getBoolean("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled"))
			return Result.DISABLED_NAME;
		if (!plugin.config
				.getBoolean("Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + ".Enabled"))
			return Result.DISABLED_CATEGORY;
		if (!plugin.config.getBoolean("Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + "."
				+ check.getDebugName() + ".Enabled"))
			return Result.DISABLED_DEBUG;
		try {
			check.register(plugin);
		} catch (OperationNotSupportedException e) {
//			e.printStackTrace();
			return Result.MISSING_DEPENDENCY;
		}
		activeChecks.add(check);
		return Result.SUCCESS;
	}

	enum Result {
		ALREADY_REGISTERED, DISABLED_NAME, DISABLED_CATEGORY, DISABLED_DEBUG, NOT_SUPPORTED, MISSING_DEPENDENCY,
		WRONG_VERSION, DEPRECATED, SUCCESS;

		@Override
		public String toString() {
			return MSG.camelCase(super.toString());
		}
	}
}
