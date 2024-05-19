package net.modfest.funkyforcefields.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;
import net.modfest.funkyforcefields.transport.FluidContainer;

import java.util.ArrayList;
import java.util.List;

public class GaugeItem extends Item {

	public GaugeItem(Settings settings) {
		super(settings);
	}

	private static Multimap<FluidContainer, Direction> findValidDirections(World world, BlockPos pos) {
		Multimap<FluidContainer, Direction> result = HashMultimap.create();
		List<Direction> dirs = new ArrayList<>(List.of(Direction.values()));
		//dirs.add(null);
		for (Direction dir : dirs) {
			FluidContainer found = FluidContainer.LOOKUP.find(world, pos, dir);
			if (found != null) {
				result.put(found, dir);
			}
		}
		return result;
	}

	private static void printInformation(PlayerEntity player, FluidContainer component) {
		ForcefieldFluid fluid = component.getContainedFluid();
		if (fluid == null) {
			player.sendMessage(Text.translatable("item.funkyforcefields.gauge.fluidname").append(Text.translatable("item.funkyforcefields.gauge.empty")));
		}
		else {
			player.sendMessage(Text.translatable("item.funkyforcefields.gauge.fluidname").append(fluid.getFluidName()));
		}
		player.sendMessage(Text.translatable("item.funkyforcefields.gauge.volume").append(Float.toString(component.getContainerVolume())));
		player.sendMessage(Text.translatable("item.funkyforcefields.gauge.thermal_diffusivity").append(Float.toString(component.getThermalDiffusivity())));
		player.sendMessage(Text.translatable("item.funkyforcefields.gauge.pressure").append(Float.toString(component.getPressure())));
		player.sendMessage(Text.translatable("item.funkyforcefields.gauge.temperature").append(Float.toString(component.getTemperature())));
	}

	private static void printDirectionList(PlayerEntity player, List<Direction> directions) {
		if (directions.isEmpty() || (directions.size() == 1 && directions.get(0) == null)) {
			return;
		}
		MutableText text = Text.empty();
		for (int i = 0; i < directions.size(); i++) {
			text = text.append(Text.translatable("item.funkyforcefields.gauge." + directions.get(i).asString()));
			if (i < directions.size() - 1) {
				text = text.append(",");
			}
		}
		player.sendMessage(text.append(":"));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos pos = context.getBlockPos();
		BlockState state = context.getWorld().getBlockState(pos);
		Block block = state.getBlock();
		PlayerEntity player = context.getPlayer();
		if (player == null || FluidContainer.LOOKUP.getProvider(block) == null) {
			return ActionResult.FAIL;
		}
		if (context.getWorld().isClient()) {
			return ActionResult.SUCCESS;
		}
		var dirs = findValidDirections(context.getWorld(), pos);
		if (dirs.isEmpty()) {
			return ActionResult.FAIL;
		}
		for (var entry : dirs.asMap().entrySet()) {
			if (dirs.size() > 1) {
				printDirectionList(player, entry.getValue().stream().toList());
			}
			printInformation(player, entry.getKey());
		}
		return ActionResult.SUCCESS;
	}
}
