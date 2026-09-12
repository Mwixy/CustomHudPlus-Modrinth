package com.example.hudditor.elements;

import com.example.hudditor.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Shows the four worn armor pieces as item icons with durability bars, stacked vertically. */
public class ArmorElement extends HudElement {
	private static final EquipmentSlot[] SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};
	private static final int SLOT = 18;

	public ArmorElement() {
		super("armor", "Armor Preview");
		this.enabled = false;
	}

	@Override
	protected int contentWidth(MinecraftClient mc, boolean editor) {
		return (mc.player != null || editor) ? SLOT : 0;
	}

	@Override
	protected int contentHeight(MinecraftClient mc, boolean editor) {
		return (mc.player != null || editor) ? SLOTS.length * SLOT : 0;
	}

	@Override
	protected void draw(DrawContext ctx, MinecraftClient mc, boolean editor) {
		for (int i = 0; i < SLOTS.length; i++) {
			int y = i * SLOT;
			ctx.fill(0, y, 16, y + 16, 0x50000000);
			ItemStack stack = mc.player != null ? mc.player.getEquippedStack(SLOTS[i]) : ItemStack.EMPTY;
			if (!stack.isEmpty()) {
				ctx.drawItem(stack, 0, y);
				ctx.drawStackOverlay(mc.textRenderer, stack, 0, y);
			}
		}
	}
}
