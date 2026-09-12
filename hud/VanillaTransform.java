package com.example.hudditor.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

/** Shared matrix transform used by the vanilla-HUD Mixins: scale about the component's default
 * centre, then apply the element's pixel offset. */
public final class VanillaTransform {
	private VanillaTransform() {}

	/** Push the transform; returns false if the element should be hidden (caller cancels). */
	public static boolean begin(DrawContext ctx, VanillaHudElement el) {
		if (el == null) return true;
		if (!el.enabled) return false;
		int[] r = el.defaultRect(MinecraftClient.getInstance());
		float dcx = r[0] + r[2] / 2f;
		float dcy = r[1] + r[3] / 2f;
		float s = el.scale;
		Matrix3x2fStack m = ctx.getMatrices();
		m.pushMatrix();
		m.translate(el.offsetX + dcx * (1 - s), el.offsetY + dcy * (1 - s));
		m.scale(s, s);
		return true;
	}

	/** Pop the transform pushed by begin(); only pops when begin() actually pushed. */
	public static void end(DrawContext ctx, VanillaHudElement el) {
		if (el == null || !el.enabled) return;
		ctx.getMatrices().popMatrix();
	}
}
