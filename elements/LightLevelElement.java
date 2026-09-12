package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class LightLevelElement extends TextHudElement {
	public LightLevelElement() {
		super("light", "Light Level");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.player == null || mc.world == null) return editor ? List.of("Light: 15") : List.of();
		int light = mc.world.getLightLevel(mc.player.getBlockPos());
		return List.of("Light: " + light);
	}
}
