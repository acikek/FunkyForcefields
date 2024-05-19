package net.modfest.funkyforcefields.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;

public class ForcefieldBlockVertical extends ForcefieldBlock {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public ForcefieldBlockVertical(ForcefieldFluid fluid) {
		super(fluid);
		setDefaultState(stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
	}

	private static final VoxelShape NS = VoxelShapes.cuboid(0.495f, 0f, 0f, 0.505f, 1f, 1f);
	private static final VoxelShape EW = VoxelShapes.cuboid(0f, 0f, 0.495f, 1f, 1f, 0.505f);

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		Direction dir = state.get(FACING);
		return switch (dir) {
			case NORTH, SOUTH -> NS;
			case EAST, WEST -> EW;
			default -> VoxelShapes.fullCube();
		};
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection());
	}
}
