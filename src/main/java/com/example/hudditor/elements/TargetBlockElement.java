package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.List;

/** The block the crosshair is pointing at. */
public class TargetBlockElement extends TextHudElement {
	public TargetBlockElement() {
		super("target_block", "Target Block");
		this.enabled = false;
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.world == null || mc.crosshairTarget == null) {
			return editor ? List.of("Looking at: Stone") : List.of();
		}
		if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK && mc.crosshairTarget instanceof BlockHitResult hit) {
			String name = mc.world.getBlockState(hit.getBlockPos()).getBlock().getName().getString();
			return List.of("Looking at: " + name);
		}
		return editor ? List.of("Looking at: -") : List.of();
	}
}
