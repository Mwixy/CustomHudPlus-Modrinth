package com.example.hudditor.elements;

import com.example.hudditor.hud.TextHudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

import java.util.List;

public class HeldItemElement extends TextHudElement {
	public HeldItemElement() {
		super("held_item", "Held Item");
	}

	@Override
	protected List<String> getLines(MinecraftClient mc, boolean editor) {
		if (mc.player == null) return editor ? List.of("Diamond Sword") : List.of();
		ItemStack stack = mc.player.getMainHandStack();
		if (stack.isEmpty()) return editor ? List.of("(empty hand)") : List.of();
		String name = stack.getName().getString();
		if (stack.getCount() > 1) name += " x" + stack.getCount();
		return List.of(name);
	}
}
