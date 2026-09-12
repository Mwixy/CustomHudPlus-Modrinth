package com.example.hudditor.elements;

import com.example.hudditor.hud.HudElement;
import com.example.hudditor.hud.InputTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** Draws WASD, LMB/RMB (with CPS) and a spacebar, highlighting held keys. Local origin (0,0). */
public class KeystrokesElement extends HudElement {
	private static final int KEY = 22;
	private static final int GAP = 2;
	private static final int WIDTH = KEY * 3 + GAP * 2; // 70
	private static final int SPACE_H = 8;

	private static final int UP_BG = 0x64000000;
	private static final int DOWN_BG = 0xF0FFFFFF;
	private static final int UP_TEXT = 0xFFFFFFFF;
	private static final int DOWN_TEXT = 0xFF202020;

	public KeystrokesElement() {
		super("keystrokes", "Keystrokes");
	}

	@Override
	protected int contentWidth(MinecraftClient mc, boolean editor) {
		return WIDTH;
	}

	@Override
	protected int contentHeight(MinecraftClient mc, boolean editor) {
		return KEY * 3 + GAP * 3 + SPACE_H;
	}

	@Override
	protected void draw(DrawContext ctx, MinecraftClient mc, boolean editor) {
		int wide = (WIDTH - GAP) / 2; // LMB/RMB width

		// Row 1: W (center column)
		drawKey(ctx, mc, KEY + GAP, 0, KEY, KEY, "W", InputTracker.w, null);
		// Row 2: A S D
		int r2 = KEY + GAP;
		drawKey(ctx, mc, 0, r2, KEY, KEY, "A", InputTracker.a, null);
		drawKey(ctx, mc, KEY + GAP, r2, KEY, KEY, "S", InputTracker.s, null);
		drawKey(ctx, mc, 2 * (KEY + GAP), r2, KEY, KEY, "D", InputTracker.d, null);
		// Row 3: LMB / RMB with CPS
		int r3 = r2 + KEY + GAP;
		drawKey(ctx, mc, 0, r3, wide, KEY, "LMB", InputTracker.attack, InputTracker.leftCps + " CPS");
		drawKey(ctx, mc, wide + GAP, r3, wide, KEY, "RMB", InputTracker.use, InputTracker.rightCps + " CPS");
		// Row 4: spacebar
		int r4 = r3 + KEY + GAP;
		drawKey(ctx, mc, 0, r4, WIDTH, SPACE_H, "", InputTracker.jump, null);
	}

	private void drawKey(DrawContext ctx, MinecraftClient mc, int x, int y, int w, int h,
	                     String label, boolean pressed, String sub) {
		ctx.fill(x, y, x + w, y + h, pressed ? DOWN_BG : UP_BG);
		TextRenderer tr = mc.textRenderer;
		int textColor = pressed ? DOWN_TEXT : UP_TEXT;
		if (!label.isEmpty()) {
			int cx = x + w / 2;
			if (sub == null) {
				int ty = y + (h - tr.fontHeight) / 2;
				drawCentered(ctx, tr, label, cx, ty, textColor);
			} else {
				int ty = y + h / 2 - tr.fontHeight;
				drawCentered(ctx, tr, label, cx, ty, textColor);
				drawCentered(ctx, tr, sub, cx, ty + tr.fontHeight + 1, textColor);
			}
		}
	}

	/** Centered text without a drop shadow (shadow doubles up on the light pressed background). */
	private void drawCentered(DrawContext ctx, TextRenderer tr, String text, int cx, int y, int color) {
		int x = cx - tr.getWidth(text) / 2;
		ctx.drawText(tr, Text.literal(text), x, y, color, false);
	}
}
