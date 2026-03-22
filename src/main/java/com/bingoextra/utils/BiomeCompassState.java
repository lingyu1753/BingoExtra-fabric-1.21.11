package com.bingoextra.utils;

public enum BiomeCompassState {

	INACTIVE(0), SEARCHING(1), FOUND(2), NOT_FOUND(3);

	private int id;

	BiomeCompassState(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public static BiomeCompassState fromID(Integer id) {
		if (id == null) {
			return null;
		}
		for (BiomeCompassState state : values()) {
			if (state.getID() == id) {
				return state;
			}
		}

		return null;
	}

}