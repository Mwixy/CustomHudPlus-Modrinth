package com.example.hudditor.mixin;

import com.example.hudditor.hud.VanillaHud;
import com.example.hudditor.hud.VanillaTransform;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Repositions, resizes, and optionally hides vanilla HUD components by transforming the draw matrix
 * around each component's render method (see {@link VanillaTransform}). Disabled elements cancel
 * their render method.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

	// ---- Hotbar ----
	@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
	private void hudditor$hotbarHead(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.HOTBAR)) ci.cancel();
	}

	@Inject(method = "renderHotbar", at = @At("RETURN"))
	private void hudditor$hotbarReturn(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.HOTBAR);
	}

	// ---- Health (health bar + armor) ----
	@Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
	private void hudditor$healthHead(DrawContext ctx, PlayerEntity player, int x, int y, int lines,
	                                 int regeneratingHeart, float maxHealth, int lastHealth, int health,
	                                 int absorption, boolean blinking, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.HEALTH)) ci.cancel();
	}

	@Inject(method = "renderHealthBar", at = @At("RETURN"))
	private void hudditor$healthReturn(DrawContext ctx, PlayerEntity player, int x, int y, int lines,
	                                   int regeneratingHeart, float maxHealth, int lastHealth, int health,
	                                   int absorption, boolean blinking, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.HEALTH);
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private static void hudditor$armorHead(DrawContext ctx, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.HEALTH)) ci.cancel();
	}

	@Inject(method = "renderArmor", at = @At("RETURN"))
	private static void hudditor$armorReturn(DrawContext ctx, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.HEALTH);
	}

	// ---- Hunger (food + air bubbles) ----
	@Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
	private void hudditor$foodHead(DrawContext ctx, PlayerEntity player, int top, int right, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.HUNGER)) ci.cancel();
	}

	@Inject(method = "renderFood", at = @At("RETURN"))
	private void hudditor$foodReturn(DrawContext ctx, PlayerEntity player, int top, int right, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.HUNGER);
	}

	@Inject(method = "renderAirBubbles", at = @At("HEAD"), cancellable = true)
	private void hudditor$airHead(DrawContext ctx, PlayerEntity player, int i, int top, int right, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.HUNGER)) ci.cancel();
	}

	@Inject(method = "renderAirBubbles", at = @At("RETURN"))
	private void hudditor$airReturn(DrawContext ctx, PlayerEntity player, int i, int top, int right, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.HUNGER);
	}

	// ---- Crosshair ----
	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void hudditor$crosshairHead(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.CROSSHAIR)) ci.cancel();
	}

	@Inject(method = "renderCrosshair", at = @At("RETURN"))
	private void hudditor$crosshairReturn(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.CROSSHAIR);
	}

	// ---- Status effect (potion) icons ----
	@Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
	private void hudditor$effectsHead(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		if (!VanillaTransform.begin(ctx, VanillaHud.STATUS_EFFECTS)) ci.cancel();
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At("RETURN"))
	private void hudditor$effectsReturn(DrawContext ctx, RenderTickCounter counter, CallbackInfo ci) {
		VanillaTransform.end(ctx, VanillaHud.STATUS_EFFECTS);
	}
}
