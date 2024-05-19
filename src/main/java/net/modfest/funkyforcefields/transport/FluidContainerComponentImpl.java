package net.modfest.funkyforcefields.transport;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Direction;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.block.entity.LiquidInputHatchBlockEntity;
import net.modfest.funkyforcefields.block.entity.PipeBlockEntity;
import net.modfest.funkyforcefields.block.entity.PlasmaEjectorBlockEntity;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;

public record FluidContainerComponentImpl(FluidContainerImpl container, BlockEntity entity) implements FluidContainerComponent {

	@Override
	public FluidContainer self() {
		return container;
	}

	@Override
	public FluidContainer dir(Direction dir) {
		if (entity.getType() == FunkyForcefields.PIPE_BLOCK_ENTITY) {
			return container;
		}
		else if (entity.getType() == FunkyForcefields.LIQUID_INPUT_HATCH_BLOCK_ENTITY) {
			return dir == Direction.DOWN ? container : null;
		}
		if (entity instanceof PlasmaEjectorBlockEntity ejector) {
			return ejector.exposesComponentTo(dir) ? container : null;
		}
		return container;
	}

	@Override
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		container.setPressure(tag.getFloat("pressure"));
		container.setTemperature(tag.getFloat("temperature"));
		if (tag.getInt("containedFluid") != -1) {
			container.setContainedFluid(ForcefieldFluid.REGISTRY.get(tag.getInt("containedFluid")));
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putFloat("pressure", container.getPressure());
		tag.putFloat("temperature", container.getTemperature());
		if (container.getContainedFluid() != null) {
			tag.putInt("containedFluid", ForcefieldFluid.REGISTRY.getRawId(container.getContainedFluid()));
		}
		else {
			tag.putInt("containedFluid", -1);
		}
	}

	public static FluidContainerComponentImpl forPipe(PipeBlockEntity blockEntity) {
		return new FluidContainerComponentImpl(new FluidContainerImpl(6, 0.2f), blockEntity);
	}

	public static FluidContainerComponentImpl forEjector(PlasmaEjectorBlockEntity blockEntity) {
		return new FluidContainerComponentImpl(new FluidContainerImpl(0, 0.3f), blockEntity);
	}

	public static FluidContainerComponentImpl forInputHatch(LiquidInputHatchBlockEntity blockEntity) {
		return new FluidContainerComponentImpl(new FluidContainerImpl(10, 0.2f), blockEntity);
	}
}
