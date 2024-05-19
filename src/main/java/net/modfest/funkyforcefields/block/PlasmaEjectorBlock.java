package net.modfest.funkyforcefields.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modfest.funkyforcefields.block.entity.PlasmaEjectorBlockEntity;
import net.modfest.funkyforcefields.regions.ForcefieldRegion;
import net.modfest.funkyforcefields.regions.ForcefieldRegionManager;
import org.jetbrains.annotations.Nullable;

public abstract class PlasmaEjectorBlock extends BlockWithEntity implements BlockEntityProvider {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	protected PlasmaEjectorBlock(Settings settings) {
		super(settings);
	}

	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PlasmaEjectorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return (world1, pos, state1, blockEntity) -> {
			if (blockEntity instanceof PlasmaEjectorBlockEntity plasmaEjector) {
				plasmaEjector.tick();
			}
		};
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (!world.isClient() && world.getBlockEntity(pos) instanceof PlasmaEjectorBlockEntity plasmaEjector) {
			plasmaEjector.placeBlocks();
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (!world.isClient()) {
			ForcefieldRegionManager manager = ForcefieldRegionManager.get(world);
			if (manager != null && neighborPos != null) {
				ForcefieldRegion reg = manager.queryRegion(neighborPos);
				if (reg != null) {
					if (world.getBlockState(neighborPos).isAir()) {
						reg.placeBlocks(world);
					}
				}
			}
		}
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient() || !(world.getBlockEntity(pos) instanceof PlasmaEjectorBlockEntity)) {
			return ActionResult.PASS;
		}
		player.openHandledScreen(world.getBlockState(pos).createScreenHandlerFactory(world, pos));
		return ActionResult.SUCCESS;
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return null;
	}
}
