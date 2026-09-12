package com.example.hudditor.elements;

import com.example.hudditor.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/** Shows a Totem of Undying icon with the number held across the inventory. */
public class TotemElement extends HudElement {
	private int count;

	public TotemElement() {
		super("totem", "Totem Count");
		this.enabled = false;
	}

	private int countTotems(MinecraftClient mc) {
		if (mc.player == null) return 0;
		PlayerInventory inv = mc.player.getInventory();
		int n = 0;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack s = inv.getStack(i);
			if (s.isOf(Items.TOTEM_OF_UNDYING)) n += s.getCount();
		}
		return n;
	}

	@Override
	protected int contentWidth(MinecraftClient mc, boolean editor) {
		count = countTotems(mc);
		if (count == 0 && !editor) return 0;
		int shown = editor && count == 0 ? 2 : count;
		return 16 + 3 + mc.textRenderer.getWidth("x" + shown);
	}

	@Override
	protected int contentHeight(MinecraftClient mc, boolean editor) {
		return (count == 0 && !editor) ? 0 : 16;
	}

	@Override
	protected void draw(DrawContext ctx, MinecraftClient mc, boolean editor) {
		int shown = editor && count == 0 ? 2 : count;
		ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
		ctx.drawItem(totem, 0, 0);
		ctx.drawTextWithShadow(mc.textRenderer, Text.literal("x" + shown), 19, 4, color);
	}

	@Override
	public boolean supportsColor() { return true; }
}
