package com.example.hudditor.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Represents a vanilla HUD component (hotbar, health, hunger, crosshair, potion icons). The game
 * still draws it; a Mixin reads this element's offset and scale and transforms the draw around the
 * component's default center. In the editor we draw a labelled placeholder so it can be dragged.
 */
public class VanillaHudElement extends HudElement {

	/** Supplies the component's approximate default rectangle {x, y, w, h} for the current screen. */
	public interface RectProvider {
		int[] get(MinecraftClient mc);
	}

	private final RectProvider rect;

	public VanillaHudElement(String id, String name, RectProvider rect) {
		super(id, name);
		this.rect = rect;
	}

	@Override
	public boolean drawnByGame() { return true; }

	// Unused: rendering goes through the overridden render() below.
	@Override protected int contentWidth(MinecraftClient mc, boolean editor) { return 0; }
	@Override protected int contentHeight(MinecraftClient mc, boolean editor) { return 0; }
	@Override protected void draw(DrawContext ctx, MinecraftClient mc, boolean editor) {}

	/** The component's default rectangle {x, y, w, h}; read by both the editor and the Mixin. */
	public int[] defaultRect(MinecraftClient mc) {
		return rect.get(mc);
	}

	@Override
	public void render(DrawContext ctx, MinecraftClient mc, boolean editor) {
		if (!editor) {
			setBounds(0, 0, 0, 0); // the game draws this during normal play
			return;
		}
		int[] r = rect.get(mc);
		float dcx = r[0] + r[2] / 2f;
		float dcy = r[1] + r[3] / 2f;
		int w = Math.max(1, Math.round(r[2] * scale));
		int h = Math.max(1, Math.round(r[3] * scale));
		int x = Math.round(dcx + offsetX - w / 2f);
		int y = Math.round(dcy + offsetY - h / 2f);
		setBounds(x, y, w, h);

		ctx.fill(x, y, x + w, y + h, enabled ? 0x400080FF : 0x40FF3030);
		int cx = x + w / 2;
		int ty = y + Math.max(0, (h - mc.textRenderer.fontHeight) / 2);
		ctx.drawCenteredTextWithShadow(mc.textRenderer, Text.literal(name()), cx, ty,
				enabled ? 0xFFFFFFFF : 0xFFFFAAAA);
	}

	@Override
	public void setTopLeft(MinecraftClient mc, int w, int h, int px, int py) {
		int[] r = rect.get(mc);
		float dcx = r[0] + r[2] / 2f;
		float dcy = r[1] + r[3] / 2f;
		offsetX = Math.round(px - dcx + w / 2f);
		offsetY = Math.round(py - dcy + h / 2f);
	}

	@Override
	public void reanchorToNearest(MinecraftClient mc) {
		// Vanilla elements keep a raw pixel offset from their default position; no re-anchoring.
	}
}
