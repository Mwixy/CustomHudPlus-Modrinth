package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RealTimeElement extends TextHudElement {
	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

	public RealTimeElement() {
		super("realtime", "Real-Time Clock");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		return List.of(LocalTime.now().format(FMT));
	}
}
