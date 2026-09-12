package com.example.hudditor.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Polls key/mouse state once per client tick so HUD elements (keystrokes, CPS, speed)
 * can read a consistent snapshot without touching input on the render thread.
 */
public final class InputTracker {
	public static boolean w, a, s, d, jump, sneak, attack, use;
	public static int leftCps, rightCps;
	public static double speedBps; // horizontal blocks per second

	private static boolean attackPrev, usePrev;
	private static final Deque<Long> leftClicks = new ArrayDeque<>();
	private static final Deque<Long> rightClicks = new ArrayDeque<>();

	private static double lastX, lastZ;
	private static boolean hasLast;

	private InputTracker() {}

	public static void tick(MinecraftClient mc) {
		GameOptions o = mc.options;
		w = o.forwardKey.isPressed();
		a = o.leftKey.isPressed();
		s = o.backKey.isPressed();
		d = o.rightKey.isPressed();
		jump = o.jumpKey.isPressed();
		sneak = o.sneakKey.isPressed();
		attack = o.attackKey.isPressed();
		use = o.useKey.isPressed();

		long now = System.currentTimeMillis();
		if (attack && !attackPrev) leftClicks.add(now);
		if (use && !usePrev) rightClicks.add(now);
		attackPrev = attack;
		usePrev = use;
		prune(leftClicks, now);
		prune(rightClicks, now);
		leftCps = leftClicks.size();
		rightCps = rightClicks.size();

		if (mc.player != null) {
			double px = mc.player.getX();
			double pz = mc.player.getZ();
			if (hasLast) {
				double dx = px - lastX;
				double dz = pz - lastZ;
				speedBps = Math.sqrt(dx * dx + dz * dz) * 20.0; // 20 ticks per second
			}
			lastX = px;
			lastZ = pz;
			hasLast = true;
		} else {
			hasLast = false;
			speedBps = 0;
		}
	}

	private static void prune(Deque<Long> q, long now) {
		while (!q.isEmpty() && now - q.peekFirst() > 1000) {
			q.pollFirst();
		}
	}
}
