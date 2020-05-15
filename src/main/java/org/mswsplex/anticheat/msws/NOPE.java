package org.mswsplex.anticheat.msws;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.anticheat.checks.Banwave;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.Checks;
import org.mswsplex.anticheat.checks.Global;
import org.mswsplex.anticheat.checks.TPSChecker;
import org.mswsplex.anticheat.commands.AntiCheatCommand;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.data.PlayerManager;
import org.mswsplex.anticheat.data.Stats;
import org.mswsplex.anticheat.listeners.GUIListener;
import org.mswsplex.anticheat.listeners.LogImplementation;
import org.mswsplex.anticheat.listeners.LoginAndQuit;
import org.mswsplex.anticheat.listeners.MessageListener;
import org.mswsplex.anticheat.listeners.UpdateCheckerListener;
import org.mswsplex.anticheat.scoreboard.SBoard;
import org.mswsplex.anticheat.utils.MSG;
import org.mswsplex.anticheat.utils.Metrics;
import org.mswsplex.anticheat.utils.Metrics.CustomChart;

public class NOPE extends JavaPlugin {
	public FileConfiguration config, data, lang, gui;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml");

	private PlayerManager pManager;
	private TPSChecker tpsChecker;
	private Checks checks;
	private Banwave banwave;
	private Stats stats;

	public String serverName = "Unknown Server";

	private PluginInfo pluginInfo;

	private String newVersion = null;

	public void onEnable() {
		if (!configYml.exists())
			saveResource("config.yml", true);
		if (!langYml.exists())
			saveResource("lang.yml", true);
		if (!guiYml.exists())
			saveResource("guis.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);
		gui = YamlConfiguration.loadConfiguration(guiYml);

		MSG.plugin = this;
		pManager = new PlayerManager(this);
		tpsChecker = new TPSChecker(this);

		banwave = new Banwave(this);

		checks = new Checks(this);
		checks.registerChecks();

		serverName = "[DEPRECATED]";
		stats = new Stats(this);

		new Global(this);
		new AntiCheatCommand(this);

		new LogImplementation(this);
		new LoginAndQuit(this);
		new GUIListener(this);

		new SBoard(this);

		Metrics metrics = new Metrics(this, 7422);
		CustomChart chart = new Metrics.SingleLineChart("bans", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return stats.getAllBans();
			}
		});
		metrics.addCustomChart(chart);

		chart = new Metrics.AdvancedBarChart("checkweights", new Callable<Map<String, int[]>>() {
			@Override
			public Map<String, int[]> call() throws Exception {
				Map<String, int[]> result = new HashMap<>();
				for (Check check : checks.getAllChecks()) {
					int[] arr = new int[2];
					arr[0] = stats.getTotalVl(check);
					arr[1] = stats.getTotalTriggers(check);
					result.put(check.getDebugName(), arr);
				}
				return result;
			}
		});

		if (config.getBoolean("UpdateChecker.Enabled", true)) {
			if (config.getBoolean("UpdateChecker.InGame", true))
				new UpdateCheckerListener(this);
			pluginInfo = new PluginInfo(this, 64671);
			pluginInfo.fetch(pi -> {
				newVersion = pi.getVersion();
				String info = "";
				switch (pi.outdated()) {
					case DEVELOPER_VERSION:
						info = "You are using a developer version.";
						break;
					case OUTDATED_VERSION:
						info = "You are using an outdated version. Version &e" + newVersion
								+ " &7is now available (&bhttps://www.spigotmc.org/resources/64671&7)";
						break;
					case SAME_VERSION:
						info = "You are using an up-to-date version.";
						break;
				}
				MSG.log(info);
			});
		}

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));

		MSG.log("&aPlease report any bugs at the github. (https://github.com/MSWS/AntiCheat)");
	}

	public TPSChecker getTPSChecker() {
		return tpsChecker;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	public Stats getStats() {
		return stats;
	}

	public float getTPS() {
		return tpsChecker.getTPS();
	}

	public void onDisable() {
		stats.saveData();

//		animation.stopAnimations(); // Stop all animations BEFORE player data is cleared and saved

		for (OfflinePlayer p : pManager.getLoadedPlayers())
			pManager.removePlayer(p); // Clear all loaded player data and save to files

		/** Clean up entity related checks **/

		for (Player p : Bukkit.getOnlinePlayers())
			p.removeMetadata("lastEntityHit", this);

		for (World w : Bukkit.getWorlds()) {
			for (Entity ent : w.getEntitiesByClass(ArmorStand.class)) {
				if (ent.hasMetadata("killAuraMark") || ent.hasMetadata("antiKillAuraMark")
						|| ent.hasMetadata("lowerKillAuraMark"))
					ent.remove();
			}
		}

	}

	public void saveData() {
		try {
			data.save(dataYml);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public void saveConfig() {
		try {
			config.save(configYml);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public Checks getChecks() {
		return this.checks;
	}

	public PlayerManager getPlayerManager() {
		return pManager;
	}

	public boolean devMode() {
		return config.getBoolean("DevMode");
	}

	public boolean debugMode() {
		return config.getBoolean("DebugMode");
	}

	public CPlayer getCPlayer(OfflinePlayer off) {
		return pManager.getPlayer(off);
	}

	public Banwave getBanwave() {
		return this.banwave;
	}
}
