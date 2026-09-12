package com.example.hudditor.elements;

import com.example.hudditor.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/** Lists active potion effects with their real sprite icon, name + level, and remaining time. */
public class PotionTimersElement extends HudElement {
	private static final String[] ROMAN = {"", "", " II", " III", " IV", " V", " VI"};
	private static final int ICON = 18;
	private static final int GAP = 3;
	private static final int ROW = 20;

	private record Row(Identifier sprite, String name, String time) {}

	private final List<Row> cached = new ArrayList<>();

	public PotionTimersElement() {
		super("potions", "Potion Timers");
	}

	@Override public boolean supportsColor() { return true; }
	@Override public boolean supportsBackground() { return true; }

	private void rebuild(MinecraftClient mc, boolean editor) {
		cached.clear();
		if (mc.player == null || mc.player.getStatusEffects().isEmpty()) {
			if (editor) {
				cached.add(new Row(Identifier.ofVanilla("mob_effect/speed"), "Speed II", "0:30"));
				cached.add(new Row(Identifier.ofVanilla("mob_effect/haste"), "Haste", "1:45"));
			}
			return;
		}
		for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
			String name = effect.getEffectType().value().getName().getString();
			int amp = effect.getAmplifier();
			String level = amp >= 0 && amp < ROMAN.length ? ROMAN[amp] : " " + (amp + 1);
			String time = effect.isInfinite() ? "∞" : formatTicks(effect.getDuration());
			Identifier id = effect.getEffectType().getKey()
					.map(k -> k.getValue().withPrefixedPath("mob_effect/"))
					.orElse(null);
			cached.add(new Row(id, name + level, time));
		}
	}

	@Override
	protected int contentWidth(MinecraftClient mc, boolean editor) {
		rebuild(mc, editor);
		if (cached.isEmpty()) return 0;
		TextRenderer tr = mc.textRenderer;
		int textW = 0;
		for (Row r : cached) {
			textW = Math.max(textW, Math.max(tr.getWidth(r.name()), tr.getWidth(r.time())));
		}
		return ICON + GAP + textW + PADX * 2;
	}

	@Override
	protected int contentHeight(MinecraftClient mc, boolean editor) {
		if (cached.isEmpty()) return 0;
		return cached.size() * ROW + PADY * 2;
	}

	private static final int PADX = 3;
	private static final int PADY = 2;

	@Override
	protected void draw(DrawContext ctx, MinecraftClient mc, boolean editor) {
		if (cached.isEmpty()) return;
		TextRenderer tr = mc.textRenderer;
		int bw = contentWidth(mc, editor);
		int bh = contentHeight(mc, editor);
		if (background) ctx.fill(0, 0, bw, bh, 0x90000000);

		int y = PADY;
		for (Row r : cached) {
			int iconX = PADX;
			if (r.sprite() != null) {
				try {
					ctx.drawGuiTexture(RenderPipelines.GUI_TEXTURED, r.sprite(), iconX, y, ICON, ICON);
				} catch (Exception ignored) {
					ctx.fill(iconX, y, iconX + ICON, y + ICON, 0x60FFFFFF);
				}
			} else {
				ctx.fill(iconX, y, iconX + ICON, y + ICON, 0x60FFFFFF);
			}
			int textX = PADX + ICON + GAP;
			ctx.drawTextWithShadow(tr, Text.literal(r.name()), textX, y + 1, color);
			ctx.drawTextWithShadow(tr, Text.literal(r.time()), textX, y + 1 + tr.fontHeight, 0xFFB0B0B0);
			y += ROW;
		}
	}

	private static String formatTicks(int ticks) {
		int total = ticks / 20;
		return (total / 60) + ":" + String.format("%02d", total % 60);
	}
}
