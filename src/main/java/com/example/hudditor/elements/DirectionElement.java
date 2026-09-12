package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Direction;

import java.util.List;

public class DirectionElement extends TextHudElement {
	public DirectionElement() {
		super("direction", "Facing Direction");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.player == null) {
			return editor ? List.of("Facing: North (-Z)") : List.of();
		}
		Direction dir = mc.player.getHorizontalFacing();
		String axis = switch (dir) {
			case NORTH -> "-Z";
			case SOUTH -> "+Z";
			case WEST -> "-X";
			case EAST -> "+X";
			default -> "";
		};
		String raw = dir.asString();
		String name = raw.substring(0, 1).toUpperCase() + raw.substring(1);
		return List.of("Facing: " + name + " (" + axis + ")");
	}
}
