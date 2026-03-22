package com.bingoextra.utils;

public enum StructureCompassState {

	INACTIVE(0), SEARCHING(1), FOUND(2), NOT_FOUND(3);

	private int id;

	StructureCompassState(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public static StructureCompassState fromID(Integer id) {
		if (id == null) {
			return null;
		}
		for (StructureCompassState state : values()) {
			if (state.getID() == id) {
				return state;
			}
		}

		return null;
	}

}
