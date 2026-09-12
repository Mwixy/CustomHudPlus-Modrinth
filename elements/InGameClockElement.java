package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

/** In-game time of day as a 24h clock (tick 0 = 06:00). */
public class InGameClockElement extends TextHudElement {
	public InGameClockElement() {
		super("ingame_clock", "In-Game Time");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.world == null) return editor ? List.of("Time 08:00") : List.of();
		long t = mc.world.getTimeOfDay() % 24000L;
		int hour = (int) ((t / 1000L + 6L) % 24L);
		int minute = (int) ((t % 1000L) * 60L / 1000L);
		return List.of(String.format("Time %02d:%02d", hour, minute));
	}
}
