package org.mswsplex.nope.checks.render;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.nope.checks.Check;
import org.mswsplex.nope.checks.CheckType;
import org.mswsplex.nope.data.CPlayer;
import org.mswsplex.nope.NOPE;
import org.mswsplex.nope.protocols.WrapperPlayClientSettings;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class SkinBlinkerA implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	private NOPE plugin;

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			throw new OperationNotSupportedException("ProtocolLib is not enabled");
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				CPlayer cp = SkinBlinkerA.this.plugin.getCPlayer(player);
				PacketContainer packet = event.getPacket();
				WrapperPlayClientSettings wrapped = new WrapperPlayClientSettings(packet);

				int lastSkin = cp.getTempInteger("lastSkinValue");
				if (lastSkin == wrapped.getDisplayedSkinParts())
					return;
				cp.setTempData("lastSkinValue", wrapped.getDisplayedSkinParts());
				cp.setTempData("lastSettingsPacket", (double) System.currentTimeMillis());
				cp.setTempData("settingsPackets", cp.getTempInteger("settingsPackets") + 1);
			}

			@Override
			public void onPacketSending(PacketEvent event) {
			}
		};
		manager.addPacketListener(adapter);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(player);
					if (cp.timeSince("lastMove") > 500 || cp.timeSince("lastOnGround") > 500
							|| cp.timeSince("lastSettingsPacket") > 200)
						return;

					int packets = cp.getTempInteger("settingsPackets");
					cp.setTempData("settingsPackets", 0);

					if (packets <= 20)
						return;
					cp.flagHack(SkinBlinkerA.this, (packets - 8) * 10, "Packets: &e" + packets + ">&a20");
				}
			}
		}.runTaskTimer(plugin, 0, 20);

	}

	@Override
	public String getCategory() {
		return "SkinBlinker";
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
