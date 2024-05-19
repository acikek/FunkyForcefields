package net.modfest.funkyforcefields;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.modfest.funkyforcefields.block.ForcefieldBlock;
import net.modfest.funkyforcefields.block.ForcefieldBlockVertical;
import net.modfest.funkyforcefields.block.ForcefieldBlocks;
import net.modfest.funkyforcefields.block.PlasmaEjectorHorizontal;
import net.modfest.funkyforcefields.block.PlasmaEjectorVertical;
import net.modfest.funkyforcefields.block.PlasmaProjectorBlock;
import net.modfest.funkyforcefields.block.entity.LiquidInputHatchBlockEntity;
import net.modfest.funkyforcefields.block.entity.PipeBlockEntity;
import net.modfest.funkyforcefields.block.entity.PlasmaEjectorBlockEntity;
import net.modfest.funkyforcefields.block.entity.PlasmaProjectorBlockEntity;
import net.modfest.funkyforcefields.block.transport.LiquidInputHatchBlock;
import net.modfest.funkyforcefields.block.transport.PipeBlock;
import net.modfest.funkyforcefields.client.screen.PlasmaEjectorGuiDescription;
import net.modfest.funkyforcefields.item.GaugeItem;
import net.modfest.funkyforcefields.network.C2SConfigurePlasmaEjector;
import net.modfest.funkyforcefields.regions.ForcefieldFluid;
import net.modfest.funkyforcefields.regions.ForcefieldFluids;
import net.modfest.funkyforcefields.transport.FluidContainer;
import net.modfest.funkyforcefields.transport.FluidContainerComponent;
import net.modfest.funkyforcefields.transport.FluidContainerComponentImpl;
import org.ladysnake.cca.api.v3.block.BlockComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.block.BlockComponentInitializer;
import org.ladysnake.cca.api.v3.block.BlockComponents;

import java.util.List;

public class FunkyForcefields implements ModInitializer, BlockComponentInitializer {

	public static final String ID = "funkyforcefields";

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}

	public static final Block PLASMA_EJECTOR_VERTICAL = new PlasmaEjectorVertical(AbstractBlock.Settings.create().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
	public static final Block PLASMA_EJECTOR_HORIZONTAL = new PlasmaEjectorHorizontal(AbstractBlock.Settings.copy(PLASMA_EJECTOR_VERTICAL));
	public static final Block PIPE = new PipeBlock(AbstractBlock.Settings.create().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
	public static final Block LIQUID_INPUT_HATCH = new LiquidInputHatchBlock(AbstractBlock.Settings.create().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
	public static final Block PLASMA_PROJECTOR = new PlasmaProjectorBlock(AbstractBlock.Settings.create().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));

	public static final Item GAUGE = new GaugeItem(new Item.Settings());

	public static BlockEntityType<PlasmaEjectorBlockEntity> PLASMA_EJECTOR_BLOCK_ENTITY;
	public static BlockEntityType<PipeBlockEntity> PIPE_BLOCK_ENTITY;
	public static BlockEntityType<LiquidInputHatchBlockEntity> LIQUID_INPUT_HATCH_BLOCK_ENTITY;
	public static BlockEntityType<PlasmaProjectorBlockEntity> PLASMA_PROJECTOR_BLOCK_ENTITY;

	public static final ScreenHandlerType<PlasmaEjectorGuiDescription> PLASMA_EJECTOR_SCREEN_HANDLER = new ScreenHandlerType<>(
			(syncId, inventory) -> new PlasmaEjectorGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY), FeatureFlags.VANILLA_FEATURES
	);

	public static Block FUNKY_GOO_ICON;
	public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, FunkyForcefields.id("main"));

	public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.funkyforcefields.main"))
			.icon(() -> FUNKY_GOO_ICON.asItem().getDefaultStack())
			.entries((displayContext, entries) -> {
				List<Block> blocks = List.of(PLASMA_EJECTOR_VERTICAL, PLASMA_EJECTOR_HORIZONTAL, PIPE, LIQUID_INPUT_HATCH, PLASMA_PROJECTOR);
				blocks.forEach(entries::add);
				entries.add(GAUGE);
			})
			.build();

	@Override
	public void onInitialize() {
		FabricRegistryBuilder.from(ForcefieldFluid.REGISTRY).buildAndRegister();
		ForcefieldFluids.register();
		ForcefieldBlocks.registerStandardBlockTypes();
		registerBlocks();
		registerBlockEntities();
		Registry.register(Registries.ITEM, FunkyForcefields.id("gauge"), GAUGE);
		registerItemGroup();
		Registry.register(Registries.SCREEN_HANDLER, FunkyForcefields.id("plasma_ejector"), PLASMA_EJECTOR_SCREEN_HANDLER);
		PayloadTypeRegistry.playC2S().register(C2SConfigurePlasmaEjector.PACKET_ID, C2SConfigurePlasmaEjector.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(C2SConfigurePlasmaEjector.PACKET_ID, C2SConfigurePlasmaEjector::receive);
		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			BlockState state = world.getBlockState(blockPos);
			if (state.getBlock() instanceof ForcefieldBlock) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});
	}

	public static void registerBlock(String path, Block block) {
		Identifier id = FunkyForcefields.id(path);
		Registry.register(Registries.BLOCK, id, block);
		Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
	}

	public static void registerBlocks() {
		registerBlock("plasma_ejector_vertical", PLASMA_EJECTOR_VERTICAL);
		registerBlock("plasma_ejector_horizontal", PLASMA_EJECTOR_HORIZONTAL);
		registerBlock("pipe", PIPE);
		registerBlock("liquid_input_hatch", LIQUID_INPUT_HATCH);
		registerBlock("plasma_projector", PLASMA_PROJECTOR);
	}

	public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String path, BlockEntityType<T> type) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, FunkyForcefields.id(path), type);
	}

	public static void registerBlockEntities() {
		PLASMA_EJECTOR_BLOCK_ENTITY = registerBlockEntity("plasma_ejector", BlockEntityType.Builder.create(PlasmaEjectorBlockEntity::new, PLASMA_EJECTOR_HORIZONTAL, PLASMA_EJECTOR_VERTICAL).build());
		PIPE_BLOCK_ENTITY = registerBlockEntity("pipe", BlockEntityType.Builder.create(PipeBlockEntity::new, PIPE).build());
		LIQUID_INPUT_HATCH_BLOCK_ENTITY = registerBlockEntity("liquid_input_hatch", BlockEntityType.Builder.create(LiquidInputHatchBlockEntity::new, LIQUID_INPUT_HATCH).build());
		PLASMA_PROJECTOR_BLOCK_ENTITY = registerBlockEntity("plasma_projector", BlockEntityType.Builder.create(PlasmaProjectorBlockEntity::new, PLASMA_PROJECTOR).build());
	}

	public static void registerItemGroup() {
		FUNKY_GOO_ICON = ForcefieldBlocks.getBlock(ForcefieldFluids.FUNKY_GOO, ForcefieldBlockVertical.class);
		Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_KEY.getValue(), ITEM_GROUP);
	}

	@Override
	public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
		registry.registerFor(PipeBlockEntity.class, FluidContainerComponent.TYPE, FluidContainerComponentImpl::forPipe);
		registry.registerFor(PlasmaEjectorBlockEntity.class, FluidContainerComponent.TYPE, FluidContainerComponentImpl::forEjector);
		registry.registerFor(LiquidInputHatchBlockEntity.class, FluidContainerComponent.TYPE, FluidContainerComponentImpl::forInputHatch);
		BlockComponents.exposeApi(FluidContainerComponent.TYPE, FluidContainer.LOOKUP, FluidContainerComponent::dir, PIPE_BLOCK_ENTITY, PLASMA_EJECTOR_BLOCK_ENTITY, LIQUID_INPUT_HATCH_BLOCK_ENTITY);
	}
}
