package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class FpsElement extends TextHudElement {
	public FpsElement() {
		super("fps", "FPS Counter");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		return List.of(mc.getCurrentFps() + " FPS");
	}
}
