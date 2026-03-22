package com.bingoextra.utils;

public enum StructureCompassOverlaySide {

	LEFT, RIGHT;

	public static StructureCompassOverlaySide fromString(String str) {
		if (str.equals("RIGHT")) {
			return RIGHT;
		}
		return LEFT;
	}

}
