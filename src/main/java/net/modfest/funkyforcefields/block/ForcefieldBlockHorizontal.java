package net.modfest.funkyforcefields.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;

public class ForcefieldBlockHorizontal extends ForcefieldBlock {

	public ForcefieldBlockHorizontal(ForcefieldFluid fluid) {
		super(fluid);
	}

	private static final VoxelShape SHAPE = VoxelShapes.cuboid(0f, 0.990f, 0f, 1f, 1f, 1f);

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
}
