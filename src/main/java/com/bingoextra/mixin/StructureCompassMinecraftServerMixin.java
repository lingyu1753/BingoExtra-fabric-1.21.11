package com.bingoextra.mixin;

import com.bingoextra.worker.StructureCompassWorldWorkerManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class StructureCompassMinecraftServerMixin {

	@Inject(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "HEAD"))
	private void startTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		StructureCompassWorldWorkerManager.tick(true);
	}
	
	@Inject(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "TAIL"))
	private void endTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		StructureCompassWorldWorkerManager.tick(false);
	}
	
	@Inject(method = "stopServer()V", at = @At(value = "TAIL"))
	private void onShutdown(CallbackInfo ci) {
		StructureCompassWorldWorkerManager.clear();
	}
	
}