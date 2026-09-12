package com.example.hudditor.elements;

import com.example.hudditor.hud.InputTracker;
import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class CpsElement extends TextHudElement {
	public CpsElement() {
		super("cps", "CPS (clicks/sec)");
		this.enabled = false;
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		return List.of("L: " + InputTracker.leftCps + " CPS", "R: " + InputTracker.rightCps + " CPS");
	}
}
