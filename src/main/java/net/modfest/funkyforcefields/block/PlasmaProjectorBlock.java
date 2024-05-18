package net.modfest.funkyforcefields.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.modfest.funkyforcefields.block.entity.PlasmaProjectorBlockEntity;

public class PlasmaProjectorBlock extends Block implements BlockEntityProvider {
	public PlasmaProjectorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new PlasmaProjectorBlockEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
