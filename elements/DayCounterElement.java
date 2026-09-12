package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class DayCounterElement extends TextHudElement {
	public DayCounterElement() {
		super("day", "Day Counter");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.world == null) return editor ? List.of("Day 42") : List.of();
		long day = mc.world.getTimeOfDay() / 24000L;
		return List.of("Day " + day);
	}
}
