package net.modfest.funkyforcefields.block.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.block.PlasmaEjectorHorizontal;
import net.modfest.funkyforcefields.block.PlasmaEjectorVertical;
import net.modfest.funkyforcefields.client.screen.PlasmaEjectorGuiDescription;
import net.modfest.funkyforcefields.regions.ForcefieldRegionHolder;
import net.modfest.funkyforcefields.regions.ForcefieldRegionLine;
import net.modfest.funkyforcefields.regions.ForcefieldRegionManager;
import net.modfest.funkyforcefields.transport.FluidContainer;
import net.modfest.funkyforcefields.transport.FluidContainerComponent;
import net.modfest.funkyforcefields.transport.FluidContainerImpl;
import org.jetbrains.annotations.Nullable;

public class PlasmaEjectorBlockEntity extends BlockEntity implements ForcefieldRegionHolder, PropertyDelegateHolder, NamedScreenHandlerFactory {

	public int length = 3;
	private ForcefieldRegionLine region;
	private boolean queuedBlockUpdate = false;

	private BlockApiCache<FluidContainer, Direction> pointingComponent = null;
	private Direction pointingDirection;

	public PlasmaEjectorBlockEntity(BlockPos pos, BlockState state) {
		super(FunkyForcefields.PLASMA_EJECTOR_BLOCK_ENTITY, pos, state);
	}

	public void placeBlocks() {
		queuedBlockUpdate = true;
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		if (getWorld() != null && !getWorld().isClient()) {
			ForcefieldRegionManager manager = ForcefieldRegionManager.get(getWorld());
			if (manager != null) {
				manager.removeRegion(this);
				if (region != null) {
					region.cleanup(getWorld(), manager);
				}
				region = null;
			}
		}
	}

	private static final float REQUIRED_PRESSURE = 2000;
	private static final float PRESSURE_PER_TICK_PER_BLOCK = 10;
	private static final float REQUIRED_TEMP = 700;

	private ForcefieldRegionLine createRegion(FluidContainer container) {
		if (!(getCachedState().getBlock() instanceof PlasmaEjectorVertical)) {
			return new ForcefieldRegionLine(pos, length, getCachedState().get(PlasmaEjectorHorizontal.FACING), Direction.UP, container.getContainedFluid());
		}
		return switch (getCachedState().get(PlasmaEjectorVertical.POINTING)) {
			case UP -> new ForcefieldRegionLine(pos, length, Direction.UP, getCachedState().get(PlasmaEjectorVertical.FACING), container.getContainedFluid());
			case DOWN -> new ForcefieldRegionLine(pos, length, Direction.DOWN, getCachedState().get(PlasmaEjectorVertical.FACING), container.getContainedFluid());
			case SIDEWAYS -> new ForcefieldRegionLine(pos, length, getCachedState().get(PlasmaEjectorVertical.FACING), getCachedState().get(PlasmaEjectorVertical.FACING), container.getContainedFluid());
		};
	}

	private void updateRegion(boolean newState, boolean doBlockUpdates) {
		ForcefieldRegionManager manager = ForcefieldRegionManager.get(getWorld());
		FluidContainer container = FluidContainerComponent.TYPE.get(this).self();
		if (manager != null && region != null) {
			manager.removeRegion(this);
			region.cleanup(getWorld(), manager);
			region = null;
			if (newState) {
				doBlockUpdates = true;
			}
		}
		if (newState && container.getContainedFluid() != null) {
			region = createRegion(container);
			registerRegion(region, world);
		}
		if (doBlockUpdates && region != null) {
			region.placeBlocks(world);
		}
	}

	private boolean isFluidContainerValid(FluidContainer container) {
		return container.getContainedFluid() != null
				&& container.getTemperature() >= REQUIRED_TEMP
				&& container.getPressure() >= REQUIRED_PRESSURE;
	}

	private void tickFluidMagic(FluidContainer container) {
		if (isFluidContainerValid(container)) {
			((FluidContainerImpl) container).setPressure(container.getPressure() - (PRESSURE_PER_TICK_PER_BLOCK * length));
			if (region == null) {
				updateRegion(true, true);
			}
			else if (region.getForcefieldFluid() != container.getContainedFluid()) {
				updateRegion(true, true);
			}
			else if (queuedBlockUpdate) {
				region.placeBlocks(world);
			}
			queuedBlockUpdate = false;
			return;
		}
		if (region != null) {
			updateRegion(false, true);
		}
	}

	private Direction getPointingDirection() {
		if (!(getCachedState().getBlock() instanceof PlasmaEjectorVertical)) {
			return getCachedState().get(PlasmaEjectorHorizontal.FACING).getOpposite();
		}
		return switch (getCachedState().get(PlasmaEjectorVertical.POINTING)) {
			case UP -> Direction.DOWN;
			case DOWN -> Direction.UP;
			case SIDEWAYS -> getCachedState().get(PlasmaEjectorVertical.FACING).getOpposite();
		};
	}

	public void tick() {
		if (world == null || world.isClient()) {
			return;
		}
		if (pointingComponent == null && getWorld() instanceof ServerWorld serverWorld) {
			pointingDirection = getPointingDirection();
			pointingComponent = BlockApiCache.create(FluidContainer.LOOKUP, serverWorld, pos.offset(pointingDirection, 1));
		}
		FluidContainer container = FluidContainerComponent.TYPE.get(this).self();
		FluidContainer pointingContainer = pointingComponent.find(pointingDirection);
		if (pointingContainer != null) {
			container.tick(pointingContainer);
		}
		else {
			container.tick();
		}
		tickFluidMagic(container);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		length = nbt.getInt("length");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		nbt.putInt("length", length);
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

	public boolean exposesComponentTo(Direction direction) {
		BlockState state = getWorld().getBlockState(getPos());
		if (!(state.getBlock() instanceof PlasmaEjectorVertical)) {
			return state.get(PlasmaEjectorHorizontal.FACING).getOpposite() == direction;
		}
		return switch (state.get(PlasmaEjectorVertical.POINTING)) {
			case UP -> Direction.DOWN == direction;
			case DOWN -> Direction.UP == direction;
			case SIDEWAYS -> state.get(PlasmaEjectorVertical.FACING).getOpposite() == direction;
		};
	}

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return new PropertyDelegate() {

			@Override
			public int get(int index) {
				return length;
			}

			@Override
			public void set(int index, int value) {
				length = value;
				markDirty();
			}

			@Override
			public int size() {
				return 1;
			}
		};
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("block.funkyforcefields.plasma_ejector");
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new PlasmaEjectorGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (world != null && !world.isClient()) {
			updateRegion(isFluidContainerValid(FluidContainerComponent.TYPE.get(this).self()), true);
			getWorld().updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
		}
	}
}
