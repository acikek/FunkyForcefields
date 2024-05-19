package net.modfest.funkyforcefields.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.modfest.funkyforcefields.block.entity.PlasmaProjectorBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PlasmaProjectorBlock extends Block implements BlockEntityProvider {

	public PlasmaProjectorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PlasmaProjectorBlockEntity(pos, state);
	}
}
