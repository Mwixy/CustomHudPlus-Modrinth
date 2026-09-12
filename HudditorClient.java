package com.example.hudditor;

import com.example.hudditor.editor.ElementsScreen;
import com.example.hudditor.elements.*;
import com.example.hudditor.hud.Anchor;
import com.example.hudditor.hud.HudManager;
import com.example.hudditor.hud.InputTracker;
import com.example.hudditor.hud.VanillaHud;
import com.example.hudditor.hud.VanillaHudElement;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HudditorClient implements ClientModInitializer {
	public static final HudManager HUD = new HudManager();

	private static KeyBinding openEditorKey;

	@Override
	public void onInitializeClient() {
		registerElements();
		HUD.captureDefaults(); // remember the built-in layout before user config overrides it
		HUD.load();

		openEditorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.hudditor.open_editor",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				KeyBinding.Category.MISC
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			InputTracker.tick(client);
			while (openEditorKey.wasPressed()) {
				client.setScreen(new ElementsScreen(HUD));
			}
		});

		HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
			var client = net.minecraft.client.MinecraftClient.getInstance();
			if (client.currentScreen instanceof com.example.hudditor.editor.HudEditorScreen) return; // editor draws its own copy
			HUD.renderAll(ctx, client);
		});
	}

	private void registerElements() {
		FpsElement fps = new FpsElement();
		fps.anchor = Anchor.TOP_LEFT; fps.offsetX = 4; fps.offsetY = 4;
		HUD.register(fps);

		CoordinatesElement coords = new CoordinatesElement();
		coords.anchor = Anchor.TOP_LEFT; coords.offsetX = 4; coords.offsetY = 18;
		HUD.register(coords);

		DirectionElement dir = new DirectionElement();
		dir.anchor = Anchor.TOP_LEFT; dir.offsetX = 4; dir.offsetY = 32; dir.enabled = false;
		HUD.register(dir);

		SpeedElement speed = new SpeedElement();
		speed.anchor = Anchor.TOP_LEFT; speed.offsetX = 4; speed.offsetY = 46; speed.enabled = false;
		HUD.register(speed);

		BiomeElement biome = new BiomeElement();
		biome.anchor = Anchor.TOP_LEFT; biome.offsetX = 4; biome.offsetY = 60; biome.enabled = false;
		HUD.register(biome);

		RealTimeElement clock = new RealTimeElement();
		clock.anchor = Anchor.TOP_RIGHT; clock.offsetX = -4; clock.offsetY = 4;
		HUD.register(clock);

		PotionTimersElement potions = new PotionTimersElement();
		potions.anchor = Anchor.TOP_RIGHT; potions.offsetX = -4; potions.offsetY = 20;
		HUD.register(potions);

		CpsElement cps = new CpsElement();
		cps.anchor = Anchor.BOTTOM_RIGHT; cps.offsetX = -4; cps.offsetY = -40;
		HUD.register(cps);

		KeystrokesElement keys = new KeystrokesElement();
		keys.anchor = Anchor.BOTTOM_LEFT; keys.offsetX = 10; keys.offsetY = -120;
		HUD.register(keys);

		DayCounterElement day = new DayCounterElement();
		day.anchor = Anchor.TOP_LEFT; day.offsetX = 4; day.offsetY = 74; day.enabled = false;
		HUD.register(day);

		InGameClockElement gameClock = new InGameClockElement();
		gameClock.anchor = Anchor.TOP_LEFT; gameClock.offsetX = 4; gameClock.offsetY = 88; gameClock.enabled = false;
		HUD.register(gameClock);

		LightLevelElement light = new LightLevelElement();
		light.anchor = Anchor.TOP_LEFT; light.offsetX = 4; light.offsetY = 102; light.enabled = false;
		HUD.register(light);

		HeldItemElement held = new HeldItemElement();
		held.anchor = Anchor.BOTTOM_CENTER; held.offsetX = 0; held.offsetY = -40; held.enabled = false;
		HUD.register(held);

		TargetBlockElement target = new TargetBlockElement();
		target.anchor = Anchor.TOP_CENTER; target.offsetX = 0; target.offsetY = 4; target.enabled = false;
		HUD.register(target);

		MemoryElement memory = new MemoryElement();
		memory.anchor = Anchor.TOP_RIGHT; memory.offsetX = -4; memory.offsetY = 60; memory.enabled = false;
		HUD.register(memory);

		ArmorElement armor = new ArmorElement();
		armor.anchor = Anchor.CENTER_LEFT; armor.offsetX = 4; armor.offsetY = 0; armor.enabled = false;
		HUD.register(armor);

		TotemElement totem = new TotemElement();
		totem.anchor = Anchor.TOP_CENTER; totem.offsetX = 0; totem.offsetY = 20; totem.enabled = false;
		HUD.register(totem);

		registerVanillaElements();
	}

	/**
	 * Vanilla components the InGameHud Mixin can move. The RectProvider returns the component's
	 * approximate default rectangle {x, y, w, h} so the editor can show a draggable placeholder.
	 */
	private void registerVanillaElements() {
		VanillaHud.HOTBAR = new VanillaHudElement("vanilla_hotbar", "Hotbar", mc -> {
			int sw = mc.getWindow().getScaledWidth();
			int sh = mc.getWindow().getScaledHeight();
			return new int[]{(sw - 182) / 2, sh - 22, 182, 22};
		});
		HUD.register(VanillaHud.HOTBAR);

		VanillaHud.HEALTH = new VanillaHudElement("vanilla_health", "Health", mc -> {
			int sw = mc.getWindow().getScaledWidth();
			int sh = mc.getWindow().getScaledHeight();
			return new int[]{sw / 2 - 91, sh - 49, 81, 20};
		});
		HUD.register(VanillaHud.HEALTH);

		VanillaHud.HUNGER = new VanillaHudElement("vanilla_hunger", "Hunger", mc -> {
			int sw = mc.getWindow().getScaledWidth();
			int sh = mc.getWindow().getScaledHeight();
			return new int[]{sw / 2 + 10, sh - 49, 81, 20};
		});
		HUD.register(VanillaHud.HUNGER);

		VanillaHud.XP = new VanillaHudElement("vanilla_xp", "Experience Bar", mc -> {
			int sw = mc.getWindow().getScaledWidth();
			int sh = mc.getWindow().getScaledHeight();
			return new int[]{sw / 2 - 91, sh - 32, 182, 11};
		});
		HUD.register(VanillaHud.XP);

		VanillaHud.CROSSHAIR = new VanillaHudElement("vanilla_crosshair", "Crosshair", mc -> {
			int sw = mc.getWindow().getScaledWidth();
			int sh = mc.getWindow().getScaledHeight();
			return new int[]{sw / 2 - 8, sh / 2 - 8, 16, 16};
		});
		HUD.register(VanillaHud.CROSSHAIR);

		VanillaHud.STATUS_EFFECTS = new VanillaHudElement("vanilla_status_effects", "Potion Icons (vanilla)", mc -> {
			int sw = mc.getWindow().getScaledWidth();
			return new int[]{sw - 26, 1, 25, 25};
		});
		HUD.register(VanillaHud.STATUS_EFFECTS);
	}
}
