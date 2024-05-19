package net.modfest.funkyforcefields.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PlasmaEjectorHorizontal extends PlasmaEjectorBlock {

	public PlasmaEjectorHorizontal(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection());
	}

	private static final VoxelShape NORTH = VoxelShapes.combineAndSimplify(
			Block.createCuboidShape(0, 0, 3, 16, 16, 16),
			Block.createCuboidShape(0, 12, 0, 16, 16, 3), BooleanBiFunction.OR);
	private static final VoxelShape EAST = VoxelShapes.combineAndSimplify(
			Block.createCuboidShape(0, 0, 0, 13, 16, 16),
			Block.createCuboidShape(13, 12, 0, 16, 16, 16), BooleanBiFunction.OR);
	private static final VoxelShape SOUTH = VoxelShapes.combineAndSimplify(
			Block.createCuboidShape(0, 0, 0, 16, 16, 13),
			Block.createCuboidShape(0, 12, 13, 16, 16, 16), BooleanBiFunction.OR);
	private static final VoxelShape WEST = VoxelShapes.combineAndSimplify(
			Block.createCuboidShape(3, 0, 0, 16, 16, 16),
			Block.createCuboidShape(0, 12, 0, 3, 16, 16), BooleanBiFunction.OR);

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return switch (state.get(FACING)) {
			case NORTH -> NORTH;
			case SOUTH -> SOUTH;
			case EAST -> EAST;
			case WEST -> WEST;
			default -> VoxelShapes.fullCube();
		};
	}
}
