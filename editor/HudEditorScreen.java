package com.example.hudditor.editor;

import com.example.hudditor.hud.HudElement;
import com.example.hudditor.hud.HudManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Clean drag-and-drop layout editor: left-drag moves, scroll resizes (Shift = fine), right-click
 * hides, R resets the selected element, Esc saves. Enabling elements and per-element options live
 * in {@link ElementsScreen}; this screen is only about placement.
 */
public class HudEditorScreen extends Screen {
	private static final int SNAP = 6;
	private static final int MARGIN = 4;

	private final HudManager manager;
	private HudElement selected;
	private boolean dragging;
	private int grabX, grabY;
	private boolean hintVisible = true;

	public HudEditorScreen(HudManager manager) {
		super(Text.literal("HUD Layout"));
		this.manager = manager;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void init() {
		addDrawableChild(ButtonWidget.builder(Text.literal("< Menu"), b ->
				MinecraftClient.getInstance().setScreen(new ElementsScreen(manager))
		).dimensions(10, this.height - 26, 70, 18).build());

		addDrawableChild(ButtonWidget.builder(Text.literal("?"), b -> {
			hintVisible = !hintVisible;
			clearAndInit();
		}).dimensions(84, this.height - 26, 18, 18).build());
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		MinecraftClient mc = MinecraftClient.getInstance();

		for (HudElement e : manager.elements()) {
			if (e.enabled) e.render(ctx, mc, true);
		}
		for (HudElement e : manager.elements()) {
			if (!e.enabled) continue;
			int color = e == selected ? 0xFFFFEE55 : (e.drawnByGame() ? 0xFF55AAFF : 0xFF55CC55);
			outline(ctx, e.getX() - 1, e.getY() - 1, e.getW() + 2, e.getH() + 2, color);
		}

		// Top-centre instruction pill (toggle with the "?" button; off the bottom HUD).
		if (hintVisible) {
			String hint = "Drag move  ·  Scroll resize (Shift: fine)  ·  Right-click hide  ·  R reset  ·  Esc save";
			int w = this.textRenderer.getWidth(hint);
			int x = (this.width - w) / 2;
			int y = 8;
			ctx.fill(x - 8, y - 4, x + w + 8, y + this.textRenderer.fontHeight + 3, 0xC8101216);
			ctx.drawTextWithShadow(this.textRenderer, Text.literal(hint), x, y, 0xFFD8DEE6);
		}

		// Selected tag (top-right).
		if (selected != null) {
			String info = String.format("%s  ·  %.2fx", selected.name(), selected.scale);
			int iw = this.textRenderer.getWidth(info);
			int ix = this.width - iw - 10;
			ctx.fill(ix - 4, 6, this.width - 4, 6 + this.textRenderer.fontHeight + 5, 0xC8101216);
			ctx.fill(ix - 4, 6, ix - 3, 6 + this.textRenderer.fontHeight + 5, 0xFFFFEE55);
			ctx.drawTextWithShadow(this.textRenderer, Text.literal(info), ix, 9, 0xFFFFEE55);
		}
	}

	private void outline(DrawContext ctx, int x, int y, int w, int h, int color) {
		if (w <= 0 || h <= 0) return;
		ctx.fill(x, y, x + w, y + 1, color);
		ctx.fill(x, y + h - 1, x + w, y + h, color);
		ctx.fill(x, y, x + 1, y + h, color);
		ctx.fill(x + w - 1, y, x + w, y + h, color);
	}

	private HudElement topmostAt(double mx, double my) {
		List<HudElement> list = manager.elements();
		for (int i = list.size() - 1; i >= 0; i--) {
			HudElement e = list.get(i);
			if (e.enabled && e.getW() > 0 && e.contains(mx, my)) return e;
		}
		return null;
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		if (super.mouseClicked(click, doubled)) return true;
		double mx = click.x();
		double my = click.y();
		int button = click.button();
		HudElement hit = topmostAt(mx, my);
		if (hit == null) {
			selected = null;
			return false;
		}
		if (button == 1) {
			hit.enabled = false;
			if (selected == hit) selected = null;
			manager.save();
			return true;
		}
		if (button == 0) {
			selected = hit;
			dragging = true;
			grabX = (int) mx - hit.getX();
			grabY = (int) my - hit.getY();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		if (dragging && selected != null) {
			MinecraftClient mc = MinecraftClient.getInstance();
			int w = selected.getW();
			int h = selected.getH();
			int nx = snap((int) click.x() - grabX, w, this.width);
			int ny = snap((int) click.y() - grabY, h, this.height);
			selected.setTopLeft(mc, w, h, nx, ny);
			return true;
		}
		return super.mouseDragged(click, offsetX, offsetY);
	}

	private int snap(int pos, int size, int screen) {
		if (Math.abs(pos - MARGIN) <= SNAP) return MARGIN;
		if (Math.abs(pos + size - (screen - MARGIN)) <= SNAP) return screen - MARGIN - size;
		int center = (screen - size) / 2;
		if (Math.abs(pos - center) <= SNAP) return center;
		return pos;
	}

	@Override
	public boolean mouseReleased(Click click) {
		if (dragging && selected != null) {
			dragging = false;
			selected.reanchorToNearest(MinecraftClient.getInstance());
			manager.save();
			return true;
		}
		return super.mouseReleased(click);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double h, double v) {
		HudElement target = selected != null ? selected : topmostAt(mx, my);
		if (target != null && target.resizable()) {
			selected = target;
			float mag = isShiftDown() ? 0.01f : 0.05f;
			target.scale = Math.round(clamp(target.scale + mag * (v > 0 ? 1 : -1), 0.25f, 4.0f) * 100f) / 100f;
			if (!target.drawnByGame()) target.reanchorToNearest(MinecraftClient.getInstance());
			manager.save();
			return true;
		}
		return super.mouseScrolled(mx, my, h, v);
	}

	private static float clamp(float v, float lo, float hi) {
		return Math.max(lo, Math.min(hi, v));
	}

	private static boolean isShiftDown() {
		net.minecraft.client.util.Window win = MinecraftClient.getInstance().getWindow();
		return net.minecraft.client.util.InputUtil.isKeyPressed(win, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT)
				|| net.minecraft.client.util.InputUtil.isKeyPressed(win, org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	@Override
	public boolean keyPressed(KeyInput input) {
		if (selected != null && input.getKeycode() == org.lwjgl.glfw.GLFW.GLFW_KEY_R) {
			selected.resetLayoutToDefault();
			manager.save();
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
