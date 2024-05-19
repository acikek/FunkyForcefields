package net.modfest.funkyforcefields.block.entity;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;
import net.modfest.funkyforcefields.transport.FluidContainer;
import net.modfest.funkyforcefields.transport.FluidContainerComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipeBlockEntity extends BlockEntity {

	private Map<Direction, BlockApiCache<FluidContainer, Direction>> neighborComponents = null;

	public PipeBlockEntity(BlockPos pos, BlockState state) {
		super(FunkyForcefields.PIPE_BLOCK_ENTITY, pos, state);
		if (getWorld() instanceof ServerWorld serverWorld) {
			neighborComponents = new HashMap<>();
			for (Direction dir : Direction.values()) {
				neighborComponents.put(dir, BlockApiCache.create(FluidContainer.LOOKUP, serverWorld, pos.offset(dir, 1)));
			}
		}
	}

	private record FireFluidContainer(ForcefieldFluid fluid, float getPressure) implements FluidContainer {

		@Override
		public float getContainerVolume() {
			return 0;
		}

		@Override
		public float getThermalDiffusivity() {
			return 0;
		}

		@Override
		public float getTemperature() {
			return 1000;
		}

		@Override
		public void tick(FluidContainer... neighbors) {
		}

		@Override
		public ForcefieldFluid getContainedFluid() {
			return fluid;
		}
	}

	public void tick() {
		if (world == null || world.isClient()) {
			return;
		}
		// TODO: store directions with connected components
		FluidContainer container = FluidContainerComponent.TYPE.get(this).self();
		List<FluidContainer> neighbors = new ArrayList<>();
		for (var entry : neighborComponents.entrySet()) {
			FluidContainer neighborComponent = entry.getValue().find(entry.getKey().getOpposite());
			if (neighborComponent != null) {
				neighbors.add(neighborComponent);
			}
		}
		if (world.getBlockState(pos.offset(Direction.DOWN)).getBlock() instanceof FireBlock) {
			// TODO: make this not affect the neighbor pressure somehow?
			neighbors.add(new FireFluidContainer(container.getContainedFluid(), container.getPressure()));
		}
		container.tick(neighbors.toArray(new FluidContainer[0]));
	}
}
