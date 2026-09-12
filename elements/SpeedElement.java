package com.example.hudditor.elements;

import com.example.hudditor.hud.InputTracker;
import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class SpeedElement extends TextHudElement {
	public SpeedElement() {
		super("speed", "Speed (blocks/s)");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.player == null) {
			return editor ? List.of("Speed: 5.6 b/s") : List.of();
		}
		return List.of(String.format("Speed: %.1f b/s", InputTracker.speedBps));
	}
}
