package net.modfest.funkyforcefields.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;
import net.modfest.funkyforcefields.regions.ForcefieldRegion;
import net.modfest.funkyforcefields.regions.ForcefieldRegionManager;
import net.modfest.funkyforcefields.util.EntityContextBypasser;

public abstract class ForcefieldBlock extends Block {

	private final ForcefieldFluid fluid;

	public ForcefieldBlock(ForcefieldFluid fluid) {
		super(Settings.create().nonOpaque().strength(-1.0F, 3600000.0F).dropsNothing());
		this.fluid = fluid;
	}

	public ForcefieldFluid getFluid() {
		return fluid;
	}

	@Environment(EnvType.CLIENT)
	void initRenderLayer() {
		if (getFluid().getRenderLayer() != null) {
			BlockRenderLayerMap.INSTANCE.putBlock(this, getFluid().getRenderLayer());
		}
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Entity ent = context instanceof EntityContextBypasser ? ((EntityContextBypasser) context).funkyforcefields$getUnderlyingEntity() : null;
		if (ent != null) {
			if (getFluid().allowsEntity(ent)) {
				return VoxelShapes.empty();
			}
		}
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		fluid.applyCollisionEffect(world, pos, entity);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		fluid.displayTick(world, pos, random, getOutlineShape(state, world, pos, ShapeContext.absent()));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return fluid.hasModel() ? super.getRenderType(state) : BlockRenderType.INVISIBLE;
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (!world.isClient) {
			ForcefieldRegionManager manager = ForcefieldRegionManager.get(world);
			if (manager != null) {
				ForcefieldRegion reg = manager.queryRegion(pos);
				if (reg == null) {
					world.removeBlock(pos, false);
				}
				else {
					if (!reg.isValidBlock(state)) {
						reg.revalidateBlock(world, pos);
					}
					if (neighborPos != null && reg.containsCoordinate(neighborPos)) {
						BlockState bs = world.getBlockState(neighborPos);
						if (bs.isAir()) {
							reg.placeBlocks(world);
						}
					}
				}
			}
		}
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
	}
}
