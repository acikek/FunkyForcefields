package net.modfest.funkyforcefields.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.modfest.funkyforcefields.util.CursedPointingDirection;

import java.util.stream.Stream;

public class PlasmaEjectorVertical extends PlasmaEjectorBlock {

	public static final EnumProperty<CursedPointingDirection> POINTING = EnumProperty.of("pointing", CursedPointingDirection.class);

	public PlasmaEjectorVertical(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState()
				.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
				.with(POINTING, CursedPointingDirection.SIDEWAYS));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
		stateManager.add(POINTING);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		CursedPointingDirection pointy = CursedPointingDirection.of(ctx.getPlayerLookDirection().getOpposite());
		Direction facey = ctx.getPlayerLookDirection();
		if (pointy != CursedPointingDirection.SIDEWAYS) {
			facey = facey.rotateYCounterclockwise();
		}
		return getDefaultState().with(FACING, facey).with(POINTING, pointy);
	}
	// oh no

	private static final VoxelShape NORTH_SIDE = VoxelShapes.union(
			VoxelShapes.cuboid(0f, 0f, 0.1875f, 1f, 1f, 1f),
			VoxelShapes.cuboid(0.375f, 0f, 0f, 0.625f, 1f, 1f)
	);
	private static final VoxelShape SOUTH_SIDE = VoxelShapes.union(
			VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.8125f),
			VoxelShapes.cuboid(0.375f, 0f, 0f, 0.625f, 1f, 1f)
	);
	private static final VoxelShape EAST_SIDE = VoxelShapes.union(
			VoxelShapes.cuboid(0f, 0f, 0f, 0.8125f, 1f, 1f),
			VoxelShapes.cuboid(0f, 0f, 0.375f, 1f, 1f, 0.625f)
	);
	private static final VoxelShape WEST_SIDE = VoxelShapes.union(
			VoxelShapes.cuboid(0.1875f, 0f, 0f, 1f, 1f, 1f),
			VoxelShapes.cuboid(0f, 0f, 0.375f, 1f, 1f, 0.625f)
	);
	private static final VoxelShape NORTH_UP = Stream.of(
			Block.createCuboidShape(11, 13, 0, 16, 16, 16),
			Block.createCuboidShape(0, 13, 0, 5, 16, 16),
			Block.createCuboidShape(0, 0, 0, 16, 13, 16),
			Block.createCuboidShape(6, 13, 0, 10, 16, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape EAST_UP = Stream.of(
			Block.createCuboidShape(0, 13, 11, 16, 16, 16),
			Block.createCuboidShape(0, 13, 0, 16, 16, 5),
			Block.createCuboidShape(0, 0, 0, 16, 13, 16),
			Block.createCuboidShape(0, 13, 6, 16, 16, 10)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape SOUTH_UP = Stream.of(
			Block.createCuboidShape(0, 13, 0, 5, 16, 16),
			Block.createCuboidShape(11, 13, 0, 16, 16, 16),
			Block.createCuboidShape(0, 0, 0, 16, 13, 16),
			Block.createCuboidShape(6, 13, 0, 10, 16, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape WEST_UP = Stream.of(
			Block.createCuboidShape(0, 13, 0, 16, 16, 5),
			Block.createCuboidShape(0, 13, 11, 16, 16, 16),
			Block.createCuboidShape(0, 0, 0, 16, 13, 16),
			Block.createCuboidShape(0, 13, 6, 16, 16, 10)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape NORTH_DOWN = Stream.of(
			Block.createCuboidShape(0, 3, 0, 16, 16, 16),
			Block.createCuboidShape(6, 0, 0, 10, 3, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape EAST_DOWN = Stream.of(
			Block.createCuboidShape(0, 3, 0, 16, 16, 16),
			Block.createCuboidShape(0, 0, 6, 16, 3, 10)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape SOUTH_DOWN = Stream.of(
			Block.createCuboidShape(0, 3, 0, 16, 16, 16),
			Block.createCuboidShape(6, 0, 0, 10, 3, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape WEST_DOWN = Stream.of(
			Block.createCuboidShape(0, 3, 0, 16, 16, 16),
			Block.createCuboidShape(0, 0, 6, 16, 3, 10)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		Direction dir = state.get(FACING);
		return switch (state.get(POINTING)) {
			case UP -> switch (dir) {
				case NORTH -> NORTH_UP;
				case SOUTH -> SOUTH_UP;
				case EAST -> EAST_UP;
				case WEST -> WEST_UP;
				default -> VoxelShapes.fullCube();
			};
			case DOWN -> switch (dir) {
				case NORTH -> NORTH_DOWN;
				case SOUTH -> SOUTH_DOWN;
				case EAST -> EAST_DOWN;
				case WEST -> WEST_DOWN;
				default -> VoxelShapes.fullCube();
			};
			case SIDEWAYS -> switch (dir) {
				case NORTH -> NORTH_SIDE;
				case SOUTH -> SOUTH_SIDE;
				case EAST -> EAST_SIDE;
				case WEST -> WEST_SIDE;
				default -> VoxelShapes.fullCube();
			};
		};
	}
}
