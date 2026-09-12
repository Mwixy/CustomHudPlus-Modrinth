package com.example.hudditor.editor;

import com.example.hudditor.hud.HudElement;
import com.example.hudditor.hud.HudManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple element list: every HUD element with an ENABLED/DISABLED toggle and an OPTIONS button.
 * "Edit HUD Layout" opens the drag editor. Kept intentionally minimal.
 */
public class ElementsScreen extends Screen {
	private static final int[] PALETTE = {
			0xFFFFFFFF, 0xFFBFBFBF, 0xFFFF5555, 0xFFFFA030, 0xFFFFEE55, 0xFF55DD55,
			0xFF33BB88, 0xFF55DDEE, 0xFF5588FF, 0xFFAA66FF, 0xFFFF77CC, 0xFF202020,
	};

	private static final int HEADER_H = 24;
	private static final int FOOTER_H = 24;
	private static final int ROW_H = 16;
	private static final int PADX = 14;

	private final HudManager manager;
	private int scroll;
	private HudElement optionsFor;
	private double mouseX, mouseY;

	private final List<int[]> spots = new ArrayList<>();
	private final List<Runnable> actions = new ArrayList<>();

	public ElementsScreen(HudManager manager) {
		super(Text.literal("HudEditor"));
		this.manager = manager;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private void add(int x, int y, int w, int h, Runnable r) {
		spots.add(new int[]{x, y, w, h});
		actions.add(r);
	}

	private boolean hovering(int x, int y, int w, int h) {
		return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
	}

	@Override
	public void render(DrawContext ctx, int mx, int my, float delta) {
		this.mouseX = mx;
		this.mouseY = my;
		spots.clear();
		actions.clear();
		ctx.fill(0, 0, this.width, this.height, 0xE60A0A0D);

		List<HudElement> list = manager.elements();
		int rowW = this.width - PADX * 2;
		int top = HEADER_H + 4;
		int viewBottom = this.height - FOOTER_H;
		int maxScroll = Math.max(0, list.size() * ROW_H - (viewBottom - top));
		scroll = Math.max(0, Math.min(scroll, maxScroll));
		boolean overlay = optionsFor != null;

		ctx.enableScissor(0, top, this.width, viewBottom);
		for (int i = 0; i < list.size(); i++) {
			int y = top + i * ROW_H - scroll;
			if (y + ROW_H < top || y > viewBottom) continue;
			boolean active = !overlay && y >= top - 1 && y + ROW_H <= viewBottom + 1;
			drawRow(ctx, list.get(i), PADX, y, rowW, active);
		}
		ctx.disableScissor();

		if (maxScroll > 0) {
			int trackH = viewBottom - top;
			int thumbH = Math.max(20, trackH * trackH / (list.size() * ROW_H));
			int thumbY = top + (trackH - thumbH) * scroll / maxScroll;
			ctx.fill(this.width - 5, top, this.width - 2, viewBottom, 0x30FFFFFF);
			ctx.fill(this.width - 5, thumbY, this.width - 2, thumbY + thumbH, 0xFF5A6072);
		}

		drawHeader(ctx, !overlay);
		drawFooter(ctx, !overlay);
		if (overlay) drawOptions(ctx);
	}

	private void drawRow(DrawContext ctx, HudElement e, int x, int y, int w, boolean active) {
		boolean hover = active && hovering(x, y, w, ROW_H - 2);
		ctx.fill(x, y, x + w, y + ROW_H - 2, hover ? 0xFF20222B : 0xFF16171C);
		ctx.fill(x, y + ROW_H - 2, x + w, y + ROW_H - 1, 0xFF0E0F13);

		// Colour dot + name.
		int accent = e.drawnByGame() ? 0xFF3E76C0 : 0xFF2F9A72;
		ctx.fill(x + 5, y + 4, x + 11, y + 10, accent);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal(e.name()), x + 16, y + 3, 0xFFE6E8EC);

		// Toggle (right).
		int tw = 58, th = ROW_H - 6;
		int tx = x + w - tw - 4, ty = y + 3;
		int bg = e.enabled ? 0xFF2FA850 : 0xFFB53049;
		ctx.fill(tx, ty, tx + tw, ty + th, bg);
		String st = e.enabled ? "ENABLED" : "DISABLED";
		ctx.drawTextWithShadow(this.textRenderer, Text.literal(st), tx + (tw - this.textRenderer.getWidth(st)) / 2, ty + 1, 0xFFFFFFFF);
		if (active) add(tx, ty, tw, th, () -> { e.enabled = !e.enabled; manager.save(); });

		// Options (left of toggle).
		int ow = 46, ox = tx - ow - 6;
		boolean oHover = active && hovering(ox, ty, ow, th);
		ctx.fill(ox, ty, ox + ow, ty + th, oHover ? 0xFF33353F : 0xFF25272E);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal("OPTIONS"), ox + (ow - this.textRenderer.getWidth("OPTIONS")) / 2, ty + 1, 0xFFAFB4BC);
		if (active) add(ox, ty, ow, th, () -> optionsFor = e);
	}

	private void drawHeader(DrawContext ctx, boolean active) {
		ctx.fill(0, 0, this.width, HEADER_H, 0xF0101216);
		ctx.fill(0, HEADER_H, this.width, HEADER_H + 1, 0xFF2C2E36);
		ctx.fill(10, 7, 12, 17, 0xFF4C8DFF);
		int wCustom = this.textRenderer.getWidth("CUSTOM ");
		ctx.drawTextWithShadow(this.textRenderer, Text.literal("CUSTOM "), 16, 8, 0xFF9AA3AE);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal("HUD+"), 16 + wCustom, 8, 0xFFFFFFFF);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal("Click ENABLED / DISABLED to add or remove an element"), 120, 8, 0xFF70767E);

		int cxb = this.width - 22;
		ctx.fill(cxb, 5, cxb + 16, 20, 0xFF23252C);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal("x"), cxb + 6, 8, 0xFFFF8080);
		if (active) add(cxb, 5, 16, 16, this::close);
	}

	private void drawFooter(DrawContext ctx, boolean active) {
		int fy = this.height - FOOTER_H;
		ctx.fill(0, fy, this.width, this.height, 0xF0101216);
		ctx.fill(0, fy, this.width, fy + 1, 0xFF2C2E36);
		int bw = 116, bx = 14, by = fy + 5, bh = 14;
		boolean hover = active && hovering(bx, by, bw, bh);
		ctx.fill(bx, by, bx + bw, by + bh, hover ? 0xFF3B82E6 : 0xFF2F6FD0);
		String t = "EDIT HUD LAYOUT";
		ctx.drawTextWithShadow(this.textRenderer, Text.literal(t), bx + (bw - this.textRenderer.getWidth(t)) / 2, by + 3, 0xFFFFFFFF);
		if (active) add(bx, by, bw, bh, () -> MinecraftClient.getInstance().setScreen(new HudEditorScreen(manager)));

		String hint = "Right Shift toggles this menu";
		ctx.drawTextWithShadow(this.textRenderer, Text.literal(hint), this.width - this.textRenderer.getWidth(hint) - 14, by + 3, 0xFF70767E);
	}

	private void drawOptions(DrawContext ctx) {
		HudElement e = optionsFor;
		ctx.fill(0, 0, this.width, this.height, 0xC0000000);

		int pw = 292;
		int ph = 40 + (e.supportsColor() ? 44 : 0) + (e.supportsBackground() ? 20 : 0) + 24 + 30;
		int px = (this.width - pw) / 2, py = (this.height - ph) / 2;

		ctx.fill(px, py, px + pw, py + ph, 0xFF161820);
		outline(ctx, px, py, pw, ph, 0xFF3A7BD5);

		// Title bar.
		ctx.fill(px, py, px + pw, py + 22, 0xFF1E2130);
		int accent = e.drawnByGame() ? 0xFF3E76C0 : 0xFF2F9A72;
		ctx.fill(px + 10, py + 8, px + 16, py + 14, accent);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal(e.name()), px + 20, py + 7, 0xFFFFFFFF);
		int close = px + pw - 20;
		boolean cHover = hovering(close, py + 5, 14, 14);
		ctx.fill(close, py + 5, close + 14, py + 19, cHover ? 0xFF44464F : 0xFF2A2C33);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal("x"), close + 5, py + 8, 0xFFFF8080);
		add(close, py + 5, 14, 14, () -> optionsFor = null);

		int y = py + 32;
		int cx = px + 14;

		if (e.supportsColor()) {
			ctx.drawTextWithShadow(this.textRenderer, Text.literal("TEXT COLOUR"), cx, y, 0xFF7F8894);
			int sw = 16, gap = 5, sy = y + 12;
			for (int i = 0; i < PALETTE.length; i++) {
				int col = PALETTE[i];
				int rx = cx + i * (sw + gap);
				ctx.fill(rx, sy, rx + sw, sy + sw, col);
				outline(ctx, rx, sy, sw, sw, 0x60000000);
				if ((e.color | 0xFF000000) == col) outline(ctx, rx - 1, sy - 1, sw + 2, sw + 2, 0xFFFFEE55);
				int fc = col;
				add(rx, sy, sw, sw, () -> { e.color = fc; manager.save(); });
			}
			y = sy + sw + 12;
		}

		if (e.supportsBackground()) {
			ctx.drawTextWithShadow(this.textRenderer, Text.literal("BACKGROUND"), cx, y, 0xFF7F8894);
			int bx = px + pw - 60, bw = 46, bh = 11;
			int bcol = e.background ? 0xFF2FA850 : 0xFF6A2A38;
			ctx.fill(bx, y - 2, bx + bw, y - 2 + bh, bcol);
			String bt = e.background ? "ON" : "OFF";
			ctx.drawTextWithShadow(this.textRenderer, Text.literal(bt), bx + (bw - this.textRenderer.getWidth(bt)) / 2, y, 0xFFFFFFFF);
			add(bx, y - 2, bw, bh, () -> { e.background = !e.background; manager.save(); });
			y += 20;
		}

		// Size row.
		ctx.drawTextWithShadow(this.textRenderer, Text.literal("SIZE"), cx, y, 0xFF7F8894);
		String sc = String.format("%.2fx", e.scale);
		int minus = px + pw - 84, val = px + pw - 66, plus = px + pw - 24;
		btn(ctx, minus, y - 3, 14, () -> { e.scale = clamp(Math.round((e.scale - 0.05f) * 100) / 100f, 0.25f, 4f); manager.save(); }, "-");
		ctx.drawTextWithShadow(this.textRenderer, Text.literal(sc), val + (36 - this.textRenderer.getWidth(sc)) / 2, y, 0xFFE6E8EC);
		btn(ctx, plus, y - 3, 14, () -> { e.scale = clamp(Math.round((e.scale + 0.05f) * 100) / 100f, 0.25f, 4f); manager.save(); }, "+");
		y += 20;

		// Reset (full width).
		boolean rHover = hovering(cx, y, pw - 28, 16);
		ctx.fill(cx, y, px + pw - 14, y + 16, rHover ? 0xFF3B82E6 : 0xFF2F6FD0);
		String rt = "Reset position & size";
		ctx.drawTextWithShadow(this.textRenderer, Text.literal(rt), px + (pw - this.textRenderer.getWidth(rt)) / 2, y + 4, 0xFFFFFFFF);
		add(cx, y, pw - 28, 16, () -> { e.resetLayoutToDefault(); manager.save(); });
	}

	private void btn(DrawContext ctx, int x, int y, int size, Runnable r, String label) {
		boolean h = hovering(x, y, size, 12);
		ctx.fill(x, y, x + size, y + 12, h ? 0xFF3A3D47 : 0xFF2A2C33);
		ctx.drawTextWithShadow(this.textRenderer, Text.literal(label), x + (size - this.textRenderer.getWidth(label)) / 2, y + 2, 0xFFFFFFFF);
		add(x, y, size, 12, r);
	}

	private void outline(DrawContext ctx, int x, int y, int w, int h, int color) {
		ctx.fill(x, y, x + w, y + 1, color);
		ctx.fill(x, y + h - 1, x + w, y + h, color);
		ctx.fill(x, y, x + 1, y + h, color);
		ctx.fill(x + w - 1, y, x + w, y + h, color);
	}

	private static float clamp(float v, float lo, float hi) {
		return Math.max(lo, Math.min(hi, v));
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		double cx = click.x(), cy = click.y();
		for (int i = 0; i < spots.size(); i++) {
			int[] r = spots.get(i);
			if (cx >= r[0] && cx <= r[0] + r[2] && cy >= r[1] && cy <= r[1] + r[3]) {
				actions.get(i).run();
				return true;
			}
		}
		if (optionsFor != null) optionsFor = null;
		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double h, double v) {
		if (optionsFor == null) scroll -= (int) (v * ROW_H * 2);
		return true;
	}

	@Override
	public boolean keyPressed(KeyInput input) {
		if (input.getKeycode() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && optionsFor != null) {
			optionsFor = null;
			return true;
		}
		return super.keyPressed(input);
	}

	@Override
	public void close() {
		manager.save();
		super.close();
	}
}
