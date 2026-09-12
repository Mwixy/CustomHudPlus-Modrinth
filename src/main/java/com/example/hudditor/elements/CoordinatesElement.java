package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class CoordinatesElement extends TextHudElement {
	public CoordinatesElement() {
		super("coordinates", "Coordinates");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.player == null) {
			return editor ? List.of("XYZ: 0 / 64 / 0") : List.of();
		}
		int x = (int) Math.floor(mc.player.getX());
		int y = (int) Math.floor(mc.player.getY());
		int z = (int) Math.floor(mc.player.getZ());
		return List.of("XYZ: " + x + " / " + y + " / " + z);
	}
}
