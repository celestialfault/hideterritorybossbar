package me.celestialfault.hideterritorybossbar.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.celestialfault.hideterritorybossbar.HideTerritoryBossbar;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin {
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;"))
	public <B extends ClientBossBar> Iterator<B> removeTerritoryBossbarFromRendered(Collection<B> instance, Operation<Iterator<B>> original) {
		Iterator<B> iterator = original.call(instance);
		UUID target = HideTerritoryBossbar.getBossbarUuid();
		if(!HideTerritoryBossbar.isOnWynn || target == null) {
			// noop if we aren't on wynn or don't have a target bossbar uuid
			return iterator;
		}

		ArrayList<B> filtered = new ArrayList<>();
		iterator.forEachRemaining(bar -> {
			if(!Objects.equals(target, bar.getUuid())) {
				filtered.add(bar);
			}
		});
		return filtered.iterator();
	}
}
