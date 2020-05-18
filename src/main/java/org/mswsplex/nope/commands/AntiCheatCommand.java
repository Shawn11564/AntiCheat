package org.mswsplex.nope.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;
import org.mswsplex.nope.PluginInfo;
import org.mswsplex.nope.PluginInfo.Stats;
import org.mswsplex.nope.utils.MSG;

@CommandAlias("nope")
public class AntiCheatCommand extends BaseCommand implements TabCompleter {

	private NOPE plugin;

	public AntiCheatCommand(NOPE plugin) {
		this.plugin = plugin;
	}

	@Default
	@CatchUnknown
	public void onDefault(CommandSender sender) {
		MSG.sendHelp(sender, 0, "default");
	}

	@Subcommand("clear")
	@CommandPermission("nope.command.clear")
	public void onClear(CommandSender sender, String[] args) {
		if (args.length < 3) {
			MSG.sendHelp(sender, 0, "default");
			return;
		}

		CPlayer cp;

		String target = "", hack = "";

		if (args[1].equalsIgnoreCase("all")) {
			target = "everyone's";

			for (OfflinePlayer p : plugin.getPlayerManager().getLoadedPlayers()) {
				cp = plugin.getCPlayer(p);
				if (args[2].equalsIgnoreCase("all")) {
					cp.clearVls();
					MSG.sendPluginMessage(null, "clearvl:" + p.getName());
					hack = "all hacks";
				} else {
					boolean found = false;
					for (Check h : plugin.getChecks().getAllChecks()) {
						if (args[2].equalsIgnoreCase(h.getCategory())) {
							cp.setSaveData("vls." + h.getCategory(), 0);
							MSG.sendPluginMessage(null, "setvl:" + p.getName() + " " + h + " 0");
							hack = h.getCategory();
							found = true;
							break;
						}
					}
					if (!found) {
						MSG.tell(sender, "&7Unable to find specified hack: &e" + args[2] + "&7.");
						return;
					}
				}
			}
		} else {
			cp = plugin.getCPlayer(Bukkit.getOfflinePlayer(args[1]));
			target = cp.getPlayer().getName() + "'"
					+ (cp.getPlayer().getName().toLowerCase().endsWith("s") ? "" : "s");

			if (args[2].equalsIgnoreCase("all")) {
				cp.clearVls();
				MSG.sendPluginMessage(null, "clearvl:" + cp.getPlayer().getName());
				hack = "all hacks";
			} else {
				boolean found = false;
				for (Check h : plugin.getChecks().getAllChecks()) {
					if (args[2].equalsIgnoreCase(h.getCategory())) {
						cp.setSaveData("vls." + h.getCategory(), 0);
						MSG.sendPluginMessage(null, "setvl:" + cp.getPlayer().getName() + " " + h + " 0");
						hack = h.getCategory();
						found = true;
						break;
					}
				}
				if (!found) {
					MSG.tell(sender, "&7Unable to find specified hack: &e" + args[2] + "&7.");
					return;
				}
			}
		}

		MSG.tell(sender, "&7You cleared &e" + target + "&7 VLs for &c" + hack);
	}

	@Subcommand("reload|rl")
	@CommandPermission("nope.command.reload")
	public void onReload(CommandSender sender) {
		plugin.configYml = new File(plugin.getDataFolder(), "config.yml");
		plugin.config = YamlConfiguration.loadConfiguration(plugin.configYml);
		plugin.langYml = new File(plugin.getDataFolder(), "lang.yml");
		plugin.lang = YamlConfiguration.loadConfiguration(plugin.langYml);
		plugin.guiYml = new File(plugin.getDataFolder(), "guis.yml");
		MSG.tell(sender, MSG.getString("Reloaded", "Successfully reloaded."));
	}

	@Subcommand("toggle")
	@CommandPermission("nope.command.toggle")
	public void onToggle(CommandSender sender, String[] args) {

		CPlayer cp;

		if (args.length < 2) {
			MSG.sendHelp(sender, 0, "default");
			return;
		}
		switch (args[1].toLowerCase()) {
			case "dev":
				if (!sender.hasPermission("nope.command.toggle.dev")) {
					MSG.noPerm(sender, "nope.command.toggle.dev");
					return;
				}
				plugin.config.set("DevMode", !plugin.devMode());
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(plugin.devMode()))
								.replace("%name%", "Developer Mode"));
				plugin.saveConfig();
				break;
			case "debug":
				if (!sender.hasPermission("nope.command.toggle.debug")) {
					MSG.noPerm(sender, "nope.command.toggle.debug");
					return;
				}
				plugin.config.set("DebugMode", !plugin.debugMode());
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(plugin.debugMode()))
								.replace("%name%", "Debug Mode"));
				plugin.saveConfig();
				break;
			case "lagback":
			case "setback":
			case "cancel":
				if (!sender.hasPermission("nope.command.toggle.cancel")) {
					MSG.noPerm(sender, "nope.command.toggle.cancel");
					return;
				}
				plugin.config.set("SetBack", !plugin.config.getBoolean("SetBack"));
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(plugin.config.getBoolean("SetBack")))
								.replace("%name%", "Setbacks"));
				plugin.saveConfig();
				break;
			case "logs":
				if (!sender.hasPermission("nope.command.toggle.logs")) {
					MSG.noPerm(sender, "nope.command.toggle.logs");
					return;
				}
				plugin.config.set("Log", !plugin.config.getBoolean("Log"));
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(plugin.config.getBoolean("Log")))
								.replace("%name%", "Logs"));
				plugin.saveConfig();
				break;
			case "global":
				if (!sender.hasPermission("nope.command.toggle.global")) {
					MSG.noPerm(sender, "nope.command.toggle.global");
					return;
				}
				plugin.config.set("Global", !plugin.config.getBoolean("Global"));
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(plugin.config.getBoolean("Global")))
								.replace("%name%", "Global"));
				plugin.saveConfig();
				break;
			case "globalscoreboard":
				if (!sender.hasPermission("nope.command.toggle.globalscoreboard")) {
					MSG.noPerm(sender, "nope.command.toggle.globalscoreboard");
					return;
				}
				plugin.config.set("Scoreboard", !plugin.config.getBoolean("Scoreboard"));
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%", enabledDisable(plugin.config.getBoolean("Scoreboard")))
								.replace("%name%", "Global Scoreboard"));
				plugin.saveConfig();
				break;
			case "scoreboard":
				if (!sender.hasPermission("nope.command.toggle.scoreboard")) {
					MSG.noPerm(sender, "nope.command.toggle.scoreboard");
					return;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "no scoreboard 4 u");
					return;
				}
				cp = plugin.getCPlayer(((Player) sender));
				cp.setSaveData("scoreboard",
						cp.hasSaveData("scoreboard") ? !cp.getSaveData("scoreboard", Boolean.class) : true);
				MSG.tell(sender,
						MSG.getString("Toggle", "you %status% %name%")
								.replace("%status%",
										enabledDisable(cp.getSaveData("scoreboard", Boolean.class)))
								.replace("%name%", "your Scoreboard"));
				((Player) sender).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				break;
		}
	}

	@Subcommand("reset")
	@CommandPermission("nope.command.reset")
	public void onReset(CommandSender sender) {
		plugin.saveResource("config.yml", true);
		plugin.saveResource("lang.yml", true);
		plugin.saveResource("guis.yml", true);
		plugin.configYml = new File(plugin.getDataFolder(), "config.yml");
		plugin.langYml = new File(plugin.getDataFolder(), "lang.yml");
		plugin.config = YamlConfiguration.loadConfiguration(plugin.configYml);
		plugin.lang = YamlConfiguration.loadConfiguration(plugin.langYml);
		plugin.guiYml = new File(plugin.getDataFolder(), "guis.yml");
		MSG.tell(sender, "Succesfully reset.");
	}

	@Subcommand("time")
	@CommandPermission("nope.command.time")
	public void onTime(CommandSender sender) {
		MSG.tell(sender, "&4&l[&c&lNOPE&4&l] &7Next banwave: &e"
				+ MSG.getTime((double) plugin.getBanwave().timeToNextBanwave()));
	}

	@Subcommand("banwave")
	@CommandPermission("nope.command.banwave")
	public void onBanwave(CommandSender sender, String[] args) {

		OfflinePlayer off;
		CPlayer cp;

		if (args.length > 1) {
			if (!sender.hasPermission("nope.command.banwave.addplayer")) {
				MSG.noPerm(sender, "nope.command.banwave.addplayer");
				return;
			}
			off = Bukkit.getOfflinePlayer(args[1]);
			cp = plugin.getCPlayer(off);
			cp.setSaveData("isBanwaved", "Manual [" + sender.getName() + "]");
			MSG.sendPluginMessage(null, "banwave:" + off.getName() + " Manual");
			MSG.tell(sender, "Added " + off.getName() + " to the banwave.");
			return;
		}
		MSG.sendPluginMessage(null, "banwave");
		plugin.getBanwave().runBanwave(true).run();
		MSG.tell(sender, "&cSuccessfully initiated banwave.");
	}

	@Subcommand("removebanwave")
	@CommandPermission("nope.command.removebanwave")
	public void onRemoveBanwave(CommandSender sender, String[] args) {

		OfflinePlayer off;
		CPlayer cp;

		if (args.length < 2) {
			MSG.tell(sender, "You must specify a player");
			return;
		}
		off = Bukkit.getOfflinePlayer(args[1]);

		cp = plugin.getCPlayer(off);
		if (!cp.hasSaveData("isBanwaved")) {
			MSG.tell(sender, off.getName() + " is not banwaved.");
			return;
		}
		MSG.sendPluginMessage(null, "removebanwave:" + off.getName());
		cp.removeSaveData("isBanwaved");
		MSG.tell(sender, "Removed " + off.getName() + " from the banwave.");
	}

	@Subcommand("warn|flag")
	@CommandPermission("nope.command.warn")
	public void onWarn(CommandSender sender, String[] args) {

		CPlayer cp;

		if (args.length < 4) {
			MSG.sendHelp(sender, 0, "default");
			return;
		}
		OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
		cp = plugin.getCPlayer(t);

		String hackName = "", stringVl = "", current = "";
		for (int i = 2; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("h:")) {
				current = "hack";
				hackName += arg.substring(2);
				continue;
			} else if (arg.startsWith("v:")) {
				current = "vl";
				stringVl += arg.substring(2);
				continue;
			}

			if (current.equals("hack")) {
				hackName += " " + arg;
			} else if (current.equals("vl")) {
				stringVl += " " + arg;
			}
		}

		final String hackNameFinal = hackName;

		cp.flagHack(new Check() {
			@Override
			public boolean lagBack() {
				return true;
			}

			@Override
			public CheckType getType() {
				return CheckType.MISC;
			}

			@Override
			public String getDebugName() {
				return "ManuallyIssued";
			}

			@Override
			public String getCategory() {
				return hackNameFinal;
			}

			@Override
			public void register(NOPE plugin) {
			}

		}, Integer.parseInt(stringVl));

		MSG.tell(sender, "Warned " + t.getName() + " for " + hackName + " (vl: " + stringVl + ")");
	}

	@Subcommand("checks")
	@CommandPermission("nope.command.checks")
	public void onChecks(CommandSender sender) {
		for (CheckType type : plugin.getChecks().getCheckTypes()) {
			HashMap<String, Integer> checks = new HashMap<>();
			for (Check check : plugin.getChecks().getChecksWithType(type))
				checks.put(check.getCategory(),
						checks.containsKey(check.getCategory()) ? checks.get(check.getCategory()) + 1 : 1);

			if (checks.isEmpty())
				continue;
			MSG.tell(sender, " ");

			StringBuilder builder = new StringBuilder();

			String[] colors = { "&b", "&a" };

			for (int i = 0; i < checks.keySet().size(); i++) {
				builder.append(colors[i % colors.length] + checks.keySet().toArray()[i] + " "
						+ checks.values().toArray()[i] + " ");
			}

			MSG.tell(sender, "&6&l" + MSG.camelCase(type.toString()) + " &7(&e&l"
					+ plugin.getChecks().getChecksWithType(type).size() + "&7) " + type.getDescription());
			MSG.tell(sender, builder.toString());
		}

		MSG.tell(sender, "&c&lTotal Checks: &4" + plugin.getChecks().getAllChecks().size());
	}

	@Subcommand("stats")
	@CommandPermission("nope.command.stats")
	public void onStats(Player player) {

		CPlayer cp;

		cp = plugin.getCPlayer(player);
		player.openInventory(plugin.getStats().getInventory());
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
		cp.setTempData("openInventory", "stats");
	}

	@Subcommand("enablechecks")
	@CommandPermission("nope.command.enablechecks")
	public void onEnablechecks(CommandSender sender) {
		plugin.config.set("Checks", null);
		for (Check check : plugin.getChecks().getAllChecks()) {
			plugin.config.set("Checks." + MSG.camelCase(check.getType() + "") + ".Enabled", true);
			plugin.config.set(
					"Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + ".Enabled",
					true);
			plugin.config.set("Checks." + MSG.camelCase(check.getType() + "") + "." + check.getCategory() + "."
					+ check.getDebugName() + ".Enabled", true);
		}
		plugin.saveConfig();
		MSG.tell(sender, MSG.getString("AllChecksEnabled", "&aSuccessfully enabled all checks."));
	}

	@Subcommand("online")
	@CommandPermission("nope.command.online")
	public void onOnline(CommandSender sender, String[] args) {
		if (plugin.getPluginInfo() == null) {
			MSG.tell(sender, "&4[NOPE] &7Unable to grab plugin info at this time.");
			return;
		}
		PluginInfo info = plugin.getPluginInfo();
		Stats stats = info.getStats();
		MSG.tell(sender, String.format(
				"&4[NOPE] &7NOPE has reached &b%d&7 downloads, &a%d&7 reviews, and is averaging about &e%.2f&7.",
				stats.getDownloads(), stats.getReviews(), stats.getRating()));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1) {
			for (String res : new String[] { "clear", "vl", "toggle", "reset", "flag", "checks", "banwave",
					"removebanwave", "time", "stats", "enablechecks", "online" }) {
				if (res.toLowerCase().startsWith(args[0].toLowerCase()) && sender.hasPermission("nope.command." + res))
					result.add(res);
			}
		}

		if (args.length >= 2 && args.length <= 3) {
			if (args[0].matches("(?i)(clear|removebanwave|banwave|flag)")) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (target.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
						result.add(target.getName());
				}
			}
			if (args[0].equalsIgnoreCase("clear")) {
				if ("all".startsWith(args[args.length - 1].toLowerCase())) {
					result.add("all");
				}
				for (Check c : plugin.getChecks().getAllChecks()) {
					if (c.getCategory().toLowerCase().startsWith(args[args.length - 1])
							&& !result.contains(c.getCategory()))
						result.add(c.getCategory());
				}
			}
		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("toggle")) {
				for (String res : new String[] { "cancel", "dev", "debug", "logs", "global", "globalscoreboard",
						"scoreboard" }) {
					if (sender.hasPermission("nope.command.toggle." + res)
							&& res.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(res);
				}
			}
		}
		return result;
	}

	private String formatVls(OfflinePlayer player) {
		CPlayer cp = plugin.getPlayerManager().getPlayer(player);
		HashMap<String, Integer> vls = new HashMap<>();
		ConfigurationSection vlSection = cp.getDataFile().getConfigurationSection("vls");
		if (vlSection == null)
			return "";

		for (String hack : vlSection.getKeys(false)) {
			if (vlSection.getInt(hack) > 0)
				vls.put(MSG.camelCase(hack), vlSection.getInt(hack));
		}

		String result = "";
		for (String hack : vls.keySet()) {
			result += MSG.getVlColor(vls.get(hack)) + hack + " " + vls.get(hack) + "&7, ";
		}
		result = result.substring(0, Math.max(result.length() - 2, 0));
		return result;
	}

	private String enabledDisable(boolean toggle) {
		return toggle ? "&aenabled" : "&cdisabled";
	}
}
