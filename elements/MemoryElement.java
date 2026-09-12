package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class MemoryElement extends TextHudElement {
	public MemoryElement() {
		super("memory", "Memory Usage");
		this.enabled = false;
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		Runtime rt = Runtime.getRuntime();
		long max = rt.maxMemory();
		long used = rt.totalMemory() - rt.freeMemory();
		int pct = (int) (used * 100L / Math.max(1L, max));
		return List.of(String.format("Mem: %d%% %d/%d MB", pct, used >> 20, max >> 20));
	}
}
