package net.modfest.funkyforcefields.util;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum CursedPointingDirection implements StringIdentifiable {
	UP("up"),
	DOWN("down"),
	SIDEWAYS("sideways");

	private final String name;

	CursedPointingDirection(String name) {
		this.name = name;
	}

	@Override
	public String asString() {
		return name;
	}

	public static CursedPointingDirection of(Direction dir) {
		return switch (dir) {
			case UP -> CursedPointingDirection.UP;
			case DOWN -> CursedPointingDirection.DOWN;
			default -> CursedPointingDirection.SIDEWAYS;
		};
	}
}
