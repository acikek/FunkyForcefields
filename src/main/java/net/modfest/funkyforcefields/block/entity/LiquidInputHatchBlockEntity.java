package net.modfest.funkyforcefields.block.entity;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;
import net.modfest.funkyforcefields.regions.ForcefieldFluids;
import net.modfest.funkyforcefields.transport.FluidContainer;
import net.modfest.funkyforcefields.transport.FluidContainerComponent;
import net.modfest.funkyforcefields.transport.FluidContainerImpl;
import net.modfest.funkyforcefields.transport.TransportUtilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LiquidInputHatchBlockEntity extends BlockEntity {

	private ForcefieldFluid currentFluid = null;
	private int fluidTicksRemaining = -1;
	private BlockApiCache<FluidContainer, Direction> belowComponent = null;

	public LiquidInputHatchBlockEntity(BlockPos pos, BlockState state) {
		super(FunkyForcefields.LIQUID_INPUT_HATCH_BLOCK_ENTITY, pos, state);
		if (getWorld() instanceof ServerWorld serverWorld) {
			belowComponent = BlockApiCache.create(FluidContainer.LOOKUP, serverWorld, pos.offset(Direction.DOWN, 1));
		}
	}

	public ForcefieldFluid getNewCurrentFluid() {
		assert world != null;
		// TODO: other fluids?
		BlockState bs = world.getBlockState(pos.offset(Direction.UP));
		if (bs.getFluidState().getFluid().matchesType(Fluids.WATER)) {
			return ForcefieldFluids.WATER;
		}
		if (bs.getFluidState().getFluid().matchesType(Fluids.LAVA)) {
			return ForcefieldFluids.LAVA;
		}
		if (bs.getBlock() == Blocks.GLASS) {
			return ForcefieldFluids.GLASS;
		}
		// TODO: make event driven?
		List<ItemEntity> ents = world.getEntitiesByType(EntityType.ITEM, new Box(pos), item ->
				item.getStack() != null && (item.getStack().getItem().equals(Items.WATER_BUCKET) || item.getStack().getItem().equals(Items.LAVA_BUCKET) || item.getStack().getItem().equals(Items.ROTTEN_FLESH)));
		if (!ents.isEmpty()) {
			if (ents.getFirst().getStack().getItem().equals(Items.WATER_BUCKET)) {
				return ForcefieldFluids.WATER;
			}
			else if (ents.getFirst().getStack().getItem().equals(Items.ROTTEN_FLESH)) {
				return ForcefieldFluids.FUNKY_GOO;
			}
			else {
				return ForcefieldFluids.LAVA;
			}
		}
		return null;
	}

	public ForcefieldFluid removeCurrentFluid() {
		assert world != null;
		BlockState bs = world.getBlockState(pos.offset(Direction.UP));
		if (bs.getBlock() instanceof FluidDrainable drainable) {
			ItemStack stack = drainable.tryDrainFluid(null, world, pos.offset(Direction.UP), bs);
			if (stack.isOf(Items.WATER_BUCKET)) {
				return ForcefieldFluids.WATER;
			}
			if (stack.isOf(Items.LAVA_BUCKET)) {
				return ForcefieldFluids.LAVA;
			}
		}
		else if (bs.getBlock() == Blocks.GLASS) {
			if (world.removeBlock(pos.offset(Direction.UP), false)) {
				return ForcefieldFluids.GLASS;
			}
			else {
				return null;
			}
		}
		List<ItemEntity> ents = world.getEntitiesByType(EntityType.ITEM, new Box(pos), item ->
				item.getStack() != null && (item.getStack().getItem().equals(Items.WATER_BUCKET) || item.getStack().getItem().equals(Items.LAVA_BUCKET) || item.getStack().getItem().equals(Items.ROTTEN_FLESH)));
		if (!ents.isEmpty()) {
			ItemEntity ent = ents.getFirst();
			if (ent.isAlive()) {
				ent.kill();
				if (ent.getStack().getItem().equals(Items.ROTTEN_FLESH)) {
					return ForcefieldFluids.FUNKY_GOO;
				}
				ItemEntity emptyBucket = new ItemEntity(world, ent.getX(), ent.getY(), ent.getZ(), new ItemStack(Items.BUCKET));
				emptyBucket.setVelocity(new Vec3d(ent.getVelocity().getX(), 1, ent.getVelocity().getZ()));
				world.spawnEntity(emptyBucket);
				if (ent.getStack().getItem().equals(Items.WATER_BUCKET)) {
					return ForcefieldFluids.WATER;
				}
				return ForcefieldFluids.LAVA;
			}
		}
		return null;
	}

	public void tick() {
		if (getWorld() == null || getWorld().isClient()) {
			return;
		}
		FluidContainer container = FluidContainerComponent.TYPE.get(this).self();
		FluidContainer below = belowComponent.find(Direction.UP);
		if (below != null) {
			container.tick(below);
		}
		else {
			container.tick();
		}
		if (fluidTicksRemaining > -1 && currentFluid != null) {
			fluidTicksRemaining--;
			if (container instanceof FluidContainerImpl mutable) {
				if (mutable.getContainedFluid() == null) {
					mutable.setContainedFluid(currentFluid);
				}
				if (currentFluid != null && currentFluid.equals(mutable.getContainedFluid())) {
					mutable.setPressure(mutable.getPressure() + 1000);
					mutable.setTemperature(TransportUtilities.NOMINAL_TEMPERATURE);
				}
			}
		}
		else {
			currentFluid = null;
		}
		ForcefieldFluid newCurrentFluid = getNewCurrentFluid();
		if (currentFluid == null || currentFluid.equals(newCurrentFluid)) {
			ForcefieldFluid testFluid = removeCurrentFluid();
			if (testFluid != null && (currentFluid == null || testFluid.equals(currentFluid))) {
				currentFluid = testFluid;
				fluidTicksRemaining += 5;
			}
		}
	}

	@Override
	protected void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(tag, registryLookup);
		if (tag.getInt("bufferedFluid") != -1) {
			currentFluid = ForcefieldFluid.REGISTRY.get(tag.getInt("bufferedFluid"));
		}
		fluidTicksRemaining = tag.getInt("fluidTicksRemaining");
	}

	@Override
	protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(tag, registryLookup);
		if (currentFluid != null) {
			tag.putInt("bufferedFluid", ForcefieldFluid.REGISTRY.getRawId(currentFluid));
		}
		else {
			tag.putInt("bufferedFluid", -1);
		}
		tag.putInt("fluidTicksRemaining", fluidTicksRemaining);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return createNbt(registryLookup);
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
}
