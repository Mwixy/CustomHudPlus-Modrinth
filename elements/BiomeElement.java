package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class BiomeElement extends TextHudElement {
	public BiomeElement() {
		super("biome", "Biome");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.player == null || mc.world == null) {
			return editor ? List.of("Biome: plains") : List.of();
		}
		String biome = mc.world.getBiome(mc.player.getBlockPos())
				.getKey()
				.map(k -> k.getValue().getPath())
				.orElse("unknown");
		return List.of("Biome: " + biome);
	}
}
