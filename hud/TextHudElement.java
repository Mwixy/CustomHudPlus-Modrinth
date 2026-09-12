package com.example.hudditor.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Convenience base for elements that render one or more lines of text with an optional
 * translucent background. Subclasses only provide {@link #getLines}.
 */
public abstract class TextHudElement extends HudElement {
	protected static final int PAD = 2;

	private List<String> cached = List.of();

	protected TextHudElement(String id, String name) {
		super(id, name);
	}

	@Override
	public boolean supportsColor() { return true; }

	@Override
	public boolean supportsBackground() { return true; }

	/** Return the lines to draw. May return sample data when {@code editor} is true. */
	protected abstract List<String> getLines(MinecraftClient mc, boolean editor);

	private int lineHeight(MinecraftClient mc) {
		return mc.textRenderer.fontHeight + 1;
	}

	@Override
	protected int contentWidth(MinecraftClient mc, boolean editor) {
		cached = getLines(mc, editor);
		if (cached.isEmpty()) return 0;
		TextRenderer tr = mc.textRenderer;
		int w = 0;
		for (String s : cached) {
			w = Math.max(w, tr.getWidth(s));
		}
		return w + PAD * 2;
	}

	@Override
	protected int contentHeight(MinecraftClient mc, boolean editor) {
		if (cached.isEmpty()) return 0;
		return cached.size() * lineHeight(mc) - 1 + PAD * 2;
	}

	@Override
	protected void draw(DrawContext ctx, MinecraftClient mc, boolean editor) {
		if (cached.isEmpty()) return;
		TextRenderer tr = mc.textRenderer;
		int lineH = lineHeight(mc);
		int w = 0;
		for (String s : cached) {
			w = Math.max(w, tr.getWidth(s));
		}
		int bw = w + PAD * 2;
		int bh = cached.size() * lineH - 1 + PAD * 2;
		if (background) {
			ctx.fill(0, 0, bw, bh, 0x90000000);
		}
		int ty = PAD;
		for (String s : cached) {
			ctx.drawTextWithShadow(tr, Text.literal(s), PAD, ty, color);
			ty += lineH;
		}
	}
}
