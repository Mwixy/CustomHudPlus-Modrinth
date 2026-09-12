package com.example.hudditor.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the list of HUD elements, renders the enabled ones each frame, and persists
 * per-element layout (anchor, offset, enabled, scale) to config/hudditor.json.
 */
public final class HudManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("hudditor");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final List<HudElement> elements = new ArrayList<>();
	private final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("hudditor.json");

	public void register(HudElement element) {
		elements.add(element);
	}

	public List<HudElement> elements() {
		return elements;
	}

	/** Snapshot every element's current layout as its default (call before {@link #load}). */
	public void captureDefaults() {
		for (HudElement e : elements) e.captureDefaults();
	}

	/** Restore all elements to their default layout and persist. */
	public void resetAll() {
		for (HudElement e : elements) e.resetToDefault();
		save();
	}

	/** Render enabled elements during normal gameplay (skipped while the editor is open). */
	public void renderAll(DrawContext ctx, MinecraftClient mc) {
		if (mc.options.hudHidden) return;
		for (HudElement e : elements) {
			if (e.enabled && !e.drawnByGame()) {
				e.render(ctx, mc, false);
			}
		}
	}

	// ---- persistence ----

	public void load() {
		try {
			if (!Files.exists(configPath)) return;
			JsonObject root = JsonParser.parseString(Files.readString(configPath)).getAsJsonObject();
			for (HudElement e : elements) {
				if (!root.has(e.id())) continue;
				JsonObject o = root.getAsJsonObject(e.id());
				if (o.has("anchor")) e.anchor = Anchor.valueOf(o.get("anchor").getAsString());
				if (o.has("offsetX")) e.offsetX = o.get("offsetX").getAsInt();
				if (o.has("offsetY")) e.offsetY = o.get("offsetY").getAsInt();
				if (o.has("enabled")) e.enabled = o.get("enabled").getAsBoolean();
				if (o.has("scale")) e.scale = o.get("scale").getAsFloat();
				if (o.has("color")) e.color = o.get("color").getAsInt();
				if (o.has("background")) e.background = o.get("background").getAsBoolean();
			}
		} catch (Exception ex) {
			LOGGER.warn("[hudditor] Failed to load config", ex);
		}
	}

	public void save() {
		try {
			JsonObject root = new JsonObject();
			for (HudElement e : elements) {
				JsonObject o = new JsonObject();
				o.addProperty("anchor", e.anchor.name());
				o.addProperty("offsetX", e.offsetX);
				o.addProperty("offsetY", e.offsetY);
				o.addProperty("enabled", e.enabled);
				o.addProperty("scale", e.scale);
				o.addProperty("color", e.color);
				o.addProperty("background", e.background);
				root.add(e.id(), o);
			}
			Files.createDirectories(configPath.getParent());
			Files.writeString(configPath, GSON.toJson(root));
		} catch (Exception ex) {
			LOGGER.warn("[hudditor] Failed to save config", ex);
		}
	}
}
