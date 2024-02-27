package me.celestialfault.hideterritorybossbar.mixin;

import me.celestialfault.hideterritorybossbar.HideTerritoryBossbar;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.LiteralTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	// no intellij, this is not a redundant escape. we do in fact need to escape the ']' in '[^]]'.
	@SuppressWarnings("RegExpRedundantEscape")
	@Unique
	private static final Pattern TERRITORY_REGEX = Pattern.compile("§c[^§]+§4 \\[[^\\]]+]");

	@Unique
	private void maybeHideBossBar(UUID uuid, LiteralTextContent text) {
		// we already know this bossbar is the territory one, so don't bother re-checking if it is
		if(uuid.equals(HideTerritoryBossbar.getBossbarUuid())) return;

		if(TERRITORY_REGEX.matcher(text.string()).matches()) {
			HideTerritoryBossbar.LOGGER.info("Found territory bossbar with uuid {} and text {}", uuid, text);
			// Store the UUID of the territory bossbar for later hiding at render time; we do this instead of
			// simply canceling any packets relating to the boss bar to not break other mods that might still
			// depend on the boss bar existing.
			HideTerritoryBossbar.setBossbarUuid(uuid);
		}
	}

	@Inject(method = "onBossBar", at = @At("HEAD"))
	public void detectTerritoryBossBarUuid(BossBarS2CPacket packet, CallbackInfo ci) {
		if(!HideTerritoryBossbar.isOnWynn) return;

		if(packet.action instanceof BossBarS2CPacket.AddAction addAction && addAction.name.getContent() instanceof LiteralTextContent literalText) {
			maybeHideBossBar(packet.uuid, literalText);
		} else if(packet.action instanceof BossBarS2CPacket.UpdateNameAction updateNameAction && updateNameAction.name.getContent() instanceof LiteralTextContent literalText) {
			maybeHideBossBar(packet.uuid, literalText);
		}
	}
}
