package me.celestialfault.hideterritorybossbar;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.UUID;

public class HideTerritoryBossbar implements ClientModInitializer {

	public static final Logger LOGGER = LogUtils.getLogger();
	// I'm unsure if this is actually a static UUID or if it's simply determined based on the player's own UUID
	// (or any similar means), but it *appears* to be static in some capacity. If not, oh well, we'll still
	// pick up on it at some point from an add/update name packet.
	private static @Nullable UUID territoryBossbarUuid = UUID.fromString("d1ff1f36-d7c5-380f-9fa9-cd829c91cafe");
	public static boolean isOnWynn = false;

	public static synchronized void setBossbarUuid(UUID uuid) {
		territoryBossbarUuid = uuid;
	}

	public static synchronized @Nullable UUID getBossbarUuid() {
		return territoryBossbarUuid;
	}

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register(this::onJoin);
		ClientPlayConnectionEvents.DISCONNECT.register(this::onDisconnect);
	}

	private void onJoin(ClientPlayNetworkHandler handler, PacketSender ignored, MinecraftClient client) {
		ServerInfo info = handler.getServerInfo();
		if(info != null && info.address.endsWith(".wynncraft.com")) {
			LOGGER.info("Detected joining Wynncraft, enabling HideTerritoryBossbar");
			isOnWynn = true;
		}
	}

	private void onDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
		isOnWynn = false;
	}
}
