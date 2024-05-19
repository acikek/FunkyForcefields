package net.modfest.funkyforcefields.transport;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.math.Direction;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;

import javax.annotation.Nullable;

public interface FluidContainer {

	BlockApiLookup<FluidContainer, Direction> LOOKUP = BlockApiLookup.get(FunkyForcefields.id("fluid_container"), FluidContainer.class, Direction.class);

	float getContainerVolume();

	float getPressure();

	float getThermalDiffusivity();

	float getTemperature();

	@Nullable
	ForcefieldFluid getContainedFluid();

	void tick(FluidContainer... neighbors);
}
