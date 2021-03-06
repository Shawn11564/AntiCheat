package org.mswsplex.nope.checks.world;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;
import org.mswsplex.nope.utils.MSG;

/**
 * Checks if block broken if liquid
 * 
 * @author imodm
 *
 */
public class FastBreakA implements Check, Listener {

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

	private final EnumSet<Material> mats = EnumSet.of(Material.RED_BED, Material.BLACK_BED, Material.BLUE_BED,
			Material.BROWN_BED, Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIME_BED,
			Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED, Material.WHITE_BED,
			Material.YELLOW_BED, Material.ACACIA_LEAVES, Material.BIRCH_LEAVES, Material.DARK_OAK_LEAVES,
			Material.JUNGLE_LEAVES, Material.OAK_LEAVES, Material.SPRUCE_LEAVES);

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		Block block = event.getClickedBlock();
		if (block == null || block.getType() == Material.AIR)
			return;
		if (mats.contains(block.getType()))
			return;
		Location loc = block.getLocation();
		cp.setTempData("targetBlockBreak", loc);
		cp.setTempData("targetBlockBreakTime",
				(double) (System.currentTimeMillis() + getDigTime(block, player) * 1000));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		CPlayer cp = plugin.getCPlayer(player);
		if (!cp.hasTempData("targetBlockBreakTime"))
			return;
		Block block = event.getBlock();
		if (mats.contains(block.getType()))
			return;
		if (!cp.getTempData("targetBlockBreak").equals(block.getLocation())) {
			cp.flagHack(this, 50, "Wrong Block");
			return;
		}
		double offset = cp.timeSince("targetBlockBreakTime");
		if (offset > -100)
			return;
		cp.flagHack(this, (int) Math.abs((offset + 25)),
				"Type: &e" + MSG.camelCase(block.getType().toString()) + "\n&7Time diff: &a"
						+ cp.timeSince("targetBlockBreakTime") + "\n&7Hardness: &e" + block.getType().getHardness());
	}

	private double getDigTime(Block block, Player player) {
		ItemStack tool = player.getInventory().getItemInMainHand();
		boolean canHarvest = !block.getDrops(tool).isEmpty();
		double seconds = block.getType().getHardness() * (canHarvest ? 1.5f : 5);

		if (canHarvest(block.getType(), tool.getType()))
			seconds /= getToolMultiplier(tool.getType());

		if (tool.containsEnchantment(Enchantment.DIG_SPEED) && canHarvest) {
			seconds -= (Math.pow(tool.getEnchantmentLevel(Enchantment.DIG_SPEED), 2) + 1);
		}

		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			seconds -= seconds * .20 * player.getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier();
		}
		if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
			switch (player.getPotionEffect(PotionEffectType.SLOW_DIGGING).getAmplifier()) {
				case 1:
					seconds += seconds * .70;
					break;
				case 2:
					seconds += seconds * .91;
					break;
				case 3:
					seconds += seconds * .9973;
					break;
				default:
					seconds += seconds * .99919;
					break;
			}
		}

		water: if (player.getLocation().clone().add(0, 1, 0).getBlock().isLiquid()) {
			ItemStack helmet = player.getInventory().getHelmet();
			if (helmet != null && helmet.getType() != Material.AIR)
				if (helmet.containsEnchantment(Enchantment.WATER_WORKER))
					break water;

			seconds *= 5;
		}
		if (!player.isOnGround())
			seconds *= 5;

		return seconds;

	}

	private double getToolMultiplier(Material tool) {
		if (tool.toString().contains("DIAMOND"))
			return 8;
		if (tool.toString().contains("IRON"))
			return 6;
		if (tool.toString().contains("STONE"))
			return 4;
		if (tool.toString().contains("WOOD"))
			return 2;
		return 1;
	}

	private boolean canHarvest(Material type, Material item) {
		switch (item) {
			case DIAMOND_AXE:
			case GOLDEN_AXE:
			case IRON_AXE:
			case STONE_AXE:
			case WOODEN_AXE:
				for (String res : new String[] { "BANNER", "FENCE", "PLANKS", "SIGN", "WOOD" }) {
					if (type.toString().contains(res))
						return true;
				}

				switch (type) {
					case BARREL:
					case BOOKSHELF:
					case BEEHIVE:
					case CAMPFIRE:
					case CARTOGRAPHY_TABLE:
					case COMPOSTER:
					case CRAFTING_TABLE:
					case DAYLIGHT_DETECTOR:
					case FLETCHING_TABLE:
					case JUKEBOX:
					case LADDER:
					case LECTERN:
					case LOOM:
					case NOTE_BLOCK:
					case SMITHING_TABLE:
						return true;
					default:
						return false;
				}
			case DIAMOND_PICKAXE:
			case GOLDEN_PICKAXE:
			case IRON_PICKAXE:
			case STONE_PICKAXE:
			case WOODEN_PICKAXE:
				for (String res : new String[] { "ICE", "ORE", "CONCRETE", "TERRACOTTA", "SLAB" }) {
					if (type.toString().contains(res))
						return true;
				}
				switch (type) {
					case ANVIL:
					case BELL:
					case REDSTONE_BLOCK:
					case BREWING_STAND:
					case CAULDRON:
					case HOPPER:
					case IRON_BARS:
					case IRON_DOOR:
					case IRON_TRAPDOOR:
					case LANTERN:
					case LIGHT_WEIGHTED_PRESSURE_PLATE:
					case HEAVY_WEIGHTED_PRESSURE_PLATE:
					case IRON_BLOCK:
					case LAPIS_BLOCK:
					case DIAMOND_BLOCK:
					case EMERALD_BLOCK:
					case GOLD_BLOCK:
					case PISTON:
					case STICKY_PISTON:
					case CONDUIT:
					case SHULKER_BOX:
					case ACTIVATOR_RAIL:
					case DETECTOR_RAIL:
					case POWERED_RAIL:
					case RAIL:
					case ANDESITE:
					case GRANITE:
					case BLAST_FURNACE:
					case COAL_BLOCK:
					case QUARTZ_BLOCK:
					case BRICKS:
					case COAL:
					case COBBLESTONE:
					case COBBLESTONE_WALL:
					case DARK_PRISMARINE:
					case DIORITE:
					case DISPENSER:
					case DROPPER:
					case ENCHANTING_TABLE:
					case END_STONE:
					case ENDER_CHEST:
					case FURNACE:
					case GRINDSTONE:
					case MOSSY_COBBLESTONE:
					case NETHER_BRICKS:
					case NETHER_BRICK:
					case NETHER_BRICK_FENCE:
					case NETHERRACK:
					case OBSERVER:
					case PRISMARINE:
					case PRISMARINE_BRICKS:
					case POLISHED_ANDESITE:
					case POLISHED_DIORITE:
					case POLISHED_GRANITE:
					case RED_SANDSTONE:
					case SANDSTONE_SLAB:
					case SMOKER:
					case SPAWNER:
					case STONECUTTER:
					case STONE:
					case STONE_BRICKS:
					case STONE_BUTTON:
					case STONE_PRESSURE_PLATE:
					case OBSIDIAN:
						return true;
					default:
						return false;
				}
			case SHEARS:
				for (String res : new String[] { "LEAVES", "WOOL" }) {
					if (type.toString().contains(res))
						return true;
				}
				switch (type) {
					case COBWEB:
						return true;
					default:
						return false;
				}
			case DIAMOND_SHOVEL:
			case GOLDEN_SHOVEL:
			case IRON_SHOVEL:
			case STONE_SHOVEL:
			case WOODEN_SHOVEL:
				for (String res : new String[] { "POWDER" }) {
					if (type.toString().contains(res))
						return true;
				}
				switch (type) {
					case CLAY:
					case DIRT:
					case COARSE_DIRT:
					case FARMLAND:
					case GRASS_BLOCK:
					case GRAVEL:
					case MYCELIUM:
					case PODZOL:
					case RED_SAND:
					case SAND:
					case SOUL_SAND:
					case SNOW_BLOCK:
					case SNOW:
						return true;
					default:
						return false;
				}
			case DIAMOND_SWORD:
			case GOLDEN_SWORD:
			case IRON_SWORD:
			case WOODEN_SWORD:
			case STONE_SWORD:
				switch (type) {
					case COBWEB:
					case BAMBOO:
						return true;
					default:
						return false;
				}
			case DIAMOND_HOE:
			case GOLDEN_HOE:
			case IRON_HOE:
			case WOODEN_HOE:
			case STONE_HOE:
				switch (type) {
					case HAY_BLOCK:
					case SPONGE:
					case WET_SPONGE:
						return true;
					default:
						return false;
				}
			default:
				break;
		}
		return false;
	}

	@Override
	public String getCategory() {
		return "FastBreak";
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
