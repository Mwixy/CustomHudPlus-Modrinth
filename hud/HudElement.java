package com.example.hudditor.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

/**
 * Base class for every HUD element. Subclasses report their unscaled content size and draw
 * starting at local (0,0); this class handles anchoring, scaling, and recording screen bounds
 * so the editor can hit-test, outline, drag, and resize the element.
 */
public abstract class HudElement {
	private final String id;
	private final String name;

	public Anchor anchor = Anchor.TOP_LEFT;
	public int offsetX = 0;
	public int offsetY = 0;
	public boolean enabled = true;
	public float scale = 1f;
	public int color = 0xFFFFFFFF;
	public boolean background = true;

	/** Whether the properties panel should offer a text-color picker for this element. */
	public boolean supportsColor() { return false; }

	/** Whether the properties panel should offer a background toggle for this element. */
	public boolean supportsBackground() { return false; }

	// Last rendered bounds, in scaled GUI coordinates (already multiplied by scale).
	private int lastX, lastY, lastW, lastH;

	// Snapshot of the registered defaults, for the "Defaults" button.
	private Anchor defAnchor = Anchor.TOP_LEFT;
	private int defOffsetX, defOffsetY;
	private boolean defEnabled = true;
	private float defScale = 1f;
	private int defColor = 0xFFFFFFFF;
	private boolean defBackground = true;

	protected HudElement(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String id() { return id; }
	public String name() { return name; }

	/** Record the current layout as the default (call once, after initial setup, before loading config). */
	public void captureDefaults() {
		defAnchor = anchor;
		defOffsetX = offsetX;
		defOffsetY = offsetY;
		defEnabled = enabled;
		defScale = scale;
		defColor = color;
		defBackground = background;
	}

	/** Restore this element to its captured default layout (including enabled state). */
	public void resetToDefault() {
		anchor = defAnchor;
		offsetX = defOffsetX;
		offsetY = defOffsetY;
		enabled = defEnabled;
		scale = defScale;
		color = defColor;
		background = defBackground;
	}

	/** Restore default position, size, and style, but keep the element visible (single element). */
	public void resetLayoutToDefault() {
		anchor = defAnchor;
		offsetX = defOffsetX;
		offsetY = defOffsetY;
		scale = defScale;
		color = defColor;
		background = defBackground;
	}

	// ---- subclass contract ----

	/** Unscaled content width in pixels (may compute/cache live data). Return <=0 to draw nothing. */
	protected abstract int contentWidth(MinecraftClient mc, boolean editor);

	/** Unscaled content height in pixels. */
	protected abstract int contentHeight(MinecraftClient mc, boolean editor);

	/** Draw the element starting at local (0,0); the base applies translation and scale. */
	protected abstract void draw(DrawContext ctx, MinecraftClient mc, boolean editor);

	/** Vanilla elements are drawn by the game itself; the manager skips them during gameplay. */
	public boolean drawnByGame() { return false; }

	/** Whether scroll-to-resize applies (custom elements yes; vanilla elements no). */
	public boolean resizable() { return true; }

	// ---- render pipeline ----

	public void render(DrawContext ctx, MinecraftClient mc, boolean editor) {
		int cw = contentWidth(mc, editor);
		int ch = contentHeight(mc, editor);
		if (cw <= 0 || ch <= 0) {
			setBounds(0, 0, 0, 0);
			return;
		}
		int w = Math.max(1, Math.round(cw * scale));
		int h = Math.max(1, Math.round(ch * scale));
		int[] p = resolvePos(mc, w, h);
		int x = p[0];
		int y = p[1];
		setBounds(x, y, w, h);

		Matrix3x2fStack m = ctx.getMatrices();
		m.pushMatrix();
		m.translate((float) x, (float) y);
		m.scale(scale, scale);
		draw(ctx, mc, editor);
		m.popMatrix();
	}

	// ---- positioning helpers ----

	protected int[] resolvePos(MinecraftClient mc, int w, int h) {
		int sw = mc.getWindow().getScaledWidth();
		int sh = mc.getWindow().getScaledHeight();
		return new int[]{anchor.originX(sw, w) + offsetX, anchor.originY(sh, h) + offsetY};
	}

	protected void setBounds(int x, int y, int w, int h) {
		this.lastX = x;
		this.lastY = y;
		this.lastW = w;
		this.lastH = h;
	}

	public int getX() { return lastX; }
	public int getY() { return lastY; }
	public int getW() { return lastW; }
	public int getH() { return lastH; }

	public boolean contains(double mx, double my) {
		return mx >= lastX && mx <= lastX + lastW && my >= lastY && my <= lastY + lastH;
	}

	/** Move the element so its top-left sits at (px, py), keeping the current anchor. */
	public void setTopLeft(MinecraftClient mc, int w, int h, int px, int py) {
		int sw = mc.getWindow().getScaledWidth();
		int sh = mc.getWindow().getScaledHeight();
		offsetX = px - anchor.originX(sw, w);
		offsetY = py - anchor.originY(sh, h);
	}

	/** Re-pin to the nearest anchor based on the last rendered bounds, keeping screen position. */
	public void reanchorToNearest(MinecraftClient mc) {
		if (lastW == 0 && lastH == 0) return;
		int cx = lastX + lastW / 2;
		int cy = lastY + lastH / 2;
		int sw = mc.getWindow().getScaledWidth();
		int sh = mc.getWindow().getScaledHeight();
		this.anchor = Anchor.nearest(cx, cy, sw, sh);
		setTopLeft(mc, lastW, lastH, lastX, lastY);
	}
}
