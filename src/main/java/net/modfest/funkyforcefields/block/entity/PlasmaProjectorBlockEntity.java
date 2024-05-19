package net.modfest.funkyforcefields.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.modfest.funkyforcefields.FunkyForcefields;

public class PlasmaProjectorBlockEntity extends BlockEntity {

	public PlasmaProjectorBlockEntity(BlockPos pos, BlockState state) {
		super(FunkyForcefields.PLASMA_PROJECTOR_BLOCK_ENTITY, pos, state);
	}
}
