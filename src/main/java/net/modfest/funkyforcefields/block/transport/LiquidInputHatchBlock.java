package net.modfest.funkyforcefields.block.transport;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modfest.funkyforcefields.block.entity.LiquidInputHatchBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class LiquidInputHatchBlock extends Block implements BlockEntityProvider {

	public LiquidInputHatchBlock(Settings settings) {
		super(settings);
	}

	VoxelShape SHAPE = Stream.of(
			Block.createCuboidShape(0, 0, 0, 16, 4, 16),
			Block.createCuboidShape(0, 4, 0, 4, 16, 16),
			Block.createCuboidShape(4, 4, 0, 16, 16, 4),
			Block.createCuboidShape(12, 4, 4, 16, 16, 16),
			Block.createCuboidShape(4, 4, 12, 12, 16, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new LiquidInputHatchBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return (world1, pos, state1, blockEntity) -> {
			if (blockEntity instanceof LiquidInputHatchBlockEntity inputHatch) {
				inputHatch.tick();
			}
		};
	}
}
