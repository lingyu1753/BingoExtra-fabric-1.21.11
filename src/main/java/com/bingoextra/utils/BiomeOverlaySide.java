package com.bingoextra.utils;

public enum BiomeOverlaySide {

	LEFT, RIGHT;

	public static BiomeOverlaySide fromString(String str) {
		if (str.equals("RIGHT")) {
			return RIGHT;
		}
		return LEFT;
	}

}