package net.modfest.funkyforcefields.block.transport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.modfest.funkyforcefields.block.entity.PipeBlockEntity;
import net.modfest.funkyforcefields.transport.FluidContainer;
import net.modfest.funkyforcefields.transport.PipeConnection;
import org.jetbrains.annotations.Nullable;

public class PipeBlock extends Block implements BlockEntityProvider {

	public static final EnumProperty<PipeConnection> NORTH = EnumProperty.of("north", PipeConnection.class);
	public static final EnumProperty<PipeConnection> EAST = EnumProperty.of("east", PipeConnection.class);
	public static final EnumProperty<PipeConnection> SOUTH = EnumProperty.of("south", PipeConnection.class);
	public static final EnumProperty<PipeConnection> WEST = EnumProperty.of("west", PipeConnection.class);
	public static final EnumProperty<PipeConnection> UP = EnumProperty.of("up", PipeConnection.class);
	public static final EnumProperty<PipeConnection> DOWN = EnumProperty.of("down", PipeConnection.class);

	public PipeBlock(Settings settings) {
		super(settings);
		setDefaultState(this.getStateManager().getDefaultState()
				.with(NORTH, PipeConnection.DISCONNECTED)
				.with(EAST, PipeConnection.DISCONNECTED)
				.with(SOUTH, PipeConnection.DISCONNECTED)
				.with(WEST, PipeConnection.DISCONNECTED)
				.with(UP, PipeConnection.DISCONNECTED)
				.with(DOWN, PipeConnection.DISCONNECTED)
		);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH);
		builder.add(EAST);
		builder.add(SOUTH);
		builder.add(WEST);
		builder.add(UP);
		builder.add(DOWN);
	}

	private static Property<PipeConnection> directionToProperty(Direction dir) {
		return switch (dir) {
			case NORTH -> NORTH;
			case EAST -> EAST;
			case SOUTH -> SOUTH;
			case WEST -> WEST;
			case UP -> UP;
			case DOWN -> DOWN;
		};
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PipeBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return (world1, pos, state1, blockEntity) -> {
			if (blockEntity instanceof PipeBlockEntity pipe) {
				pipe.tick();
			}
		};
	}

	private static class CursedShapeHolder {

		private final VoxelShape connected;
		private final VoxelShape blockConnected;
		private final Property<PipeConnection> prop;

		public CursedShapeHolder(Property<PipeConnection> prop, VoxelShape connected, VoxelShape blockConnected1, VoxelShape blockConnected2) {
			this.connected = connected;
			this.blockConnected = VoxelShapes.combineAndSimplify(blockConnected1, blockConnected2, BooleanBiFunction.OR);
			this.prop = prop;
		}

		public VoxelShape get(BlockState state, VoxelShape shape) {
			return switch (state.get(prop)) {
				case CONNECTED -> VoxelShapes.union(shape, connected);
				case BLOCK_CONNECTED -> VoxelShapes.union(shape, blockConnected);
				default -> shape;
			};
		}
	}

	private static final CursedShapeHolder NORTH_SHAPE = new CursedShapeHolder(NORTH,
			Block.createCuboidShape(4, 4, 0, 12, 12, 4),
			Block.createCuboidShape(4, 4, 1, 12, 12, 4),
			Block.createCuboidShape(3, 3, 0, 13, 13, 1));

	private static final CursedShapeHolder EAST_SHAPE = new CursedShapeHolder(EAST,
			Block.createCuboidShape(12, 4, 4, 16, 12, 12),
			Block.createCuboidShape(12, 4, 4, 15, 12, 12),
			Block.createCuboidShape(15, 3, 3, 16, 13, 13));

	private static final CursedShapeHolder SOUTH_SHAPE = new CursedShapeHolder(SOUTH,
			Block.createCuboidShape(4, 4, 12, 12, 12, 16),
			Block.createCuboidShape(4, 4, 12, 12, 12, 15),
			Block.createCuboidShape(3, 3, 15, 13, 13, 16));

	private static final CursedShapeHolder WEST_SHAPE = new CursedShapeHolder(WEST,
			Block.createCuboidShape(0, 4, 4, 4, 12, 12),
			Block.createCuboidShape(1, 4, 4, 4, 12, 12),
			Block.createCuboidShape(0, 3, 3, 1, 13, 13));

	private static final CursedShapeHolder UP_SHAPE = new CursedShapeHolder(UP,
			Block.createCuboidShape(4, 12, 4, 12, 16, 12),
			Block.createCuboidShape(4, 12, 4, 12, 15, 12),
			Block.createCuboidShape(3, 15, 3, 13, 16, 13));

	private static final CursedShapeHolder DOWN_SHAPE = new CursedShapeHolder(DOWN,
			Block.createCuboidShape(4, 0, 4, 12, 4, 12),
			Block.createCuboidShape(4, 1, 4, 12, 4, 12),
			Block.createCuboidShape(3, 0, 3, 13, 1, 13));

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		VoxelShape shape = Block.createCuboidShape(4, 4, 4, 12, 12, 12);
		shape = NORTH_SHAPE.get(state, shape);
		shape = EAST_SHAPE.get(state, shape);
		shape = SOUTH_SHAPE.get(state, shape);
		shape = WEST_SHAPE.get(state, shape);
		shape = UP_SHAPE.get(state, shape);
		shape = DOWN_SHAPE.get(state, shape);
		return shape;
	}

	public BlockState modifyState(World world, Block block, BlockPos pos, BlockState state, Direction dir) {
		// TODO: check fluid, check for component rather than side solid
		if (block instanceof PipeBlock) {
			return state.with(directionToProperty(dir), PipeConnection.CONNECTED);
		}
		FluidContainer found = FluidContainer.LOOKUP.find(world, pos, dir.getOpposite());
		if (found == null) {
			return state;
		}
		return state.with(directionToProperty(dir), PipeConnection.BLOCK_CONNECTED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		BlockState state = getDefaultState();
		for (Direction dir : Direction.values()) {
			BlockPos neighborPos = pos.offset(dir);
			BlockState neighborState = world.getBlockState(neighborPos);
			state = modifyState(ctx.getWorld(), neighborState.getBlock(), neighborPos, state, dir);
		}
		return state;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (world instanceof World actualWorld) {
			state = modifyState(actualWorld, neighborState.getBlock(), pos, state, direction);
		}
		return state.with(directionToProperty(direction), PipeConnection.DISCONNECTED);
	}
}
