package com.example.hudditor.hud;

/**
 * Screen anchor point. An element stores its position as (anchor + offset), so it stays
 * pinned to the same corner/edge when the window is resized or the GUI scale changes.
 */
public enum Anchor {
	TOP_LEFT(0f, 0f), TOP_CENTER(0.5f, 0f), TOP_RIGHT(1f, 0f),
	CENTER_LEFT(0f, 0.5f), CENTER(0.5f, 0.5f), CENTER_RIGHT(1f, 0.5f),
	BOTTOM_LEFT(0f, 1f), BOTTOM_CENTER(0.5f, 1f), BOTTOM_RIGHT(1f, 1f);

	public final float fx;
	public final float fy;

	Anchor(float fx, float fy) {
		this.fx = fx;
		this.fy = fy;
	}

	public int originX(int screenW, int elemW) {
		return Math.round((screenW - elemW) * fx);
	}

	public int originY(int screenH, int elemH) {
		return Math.round((screenH - elemH) * fy);
	}

	/** Pick the anchor whose region contains the given point (screen split into thirds). */
	public static Anchor nearest(int cx, int cy, int screenW, int screenH) {
		int col = cx < screenW / 3 ? 0 : (cx < 2 * screenW / 3 ? 1 : 2);
		int row = cy < screenH / 3 ? 0 : (cy < 2 * screenH / 3 ? 1 : 2);
		return values()[row * 3 + col];
	}
}
