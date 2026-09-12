package com.example.hudditor.mixin;

import com.example.hudditor.hud.VanillaHud;
import com.example.hudditor.hud.VanillaTransform;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Moves/resizes/hides the vanilla experience bar (bar + level number) via the XP element. */
@Mixin(ExperienceBar.class)
public class ExperienceBarMixin {

	@Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
	private void hudditor$barHead(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.XP)) ci.cancel();
	}

	@Inject(method = "renderBar", at = @At("RETURN"))
	private void hudditor$barReturn(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.XP);
	}

	@Inject(method = "renderAddons", at = @At("HEAD"), cancellable = true)
	private void hudditor$addonsHead(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.XP)) ci.cancel();
	}

	@Inject(method = "renderAddons", at = @At("RETURN"))
	private void hudditor$addonsReturn(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.XP);
	}
}
