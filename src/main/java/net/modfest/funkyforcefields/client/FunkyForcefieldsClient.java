package net.modfest.funkyforcefields.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.block.ForcefieldBlocks;
import net.modfest.funkyforcefields.client.render.PlasmaProjectorBlockEntityRenderer;
import net.modfest.funkyforcefields.client.screen.PlasmaEjectorGuiDescription;
import net.modfest.funkyforcefields.client.screen.PlasmaEjectorScreen;

public class FunkyForcefieldsClient implements ClientModInitializer {

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ForcefieldBlocks.initClient();
		BlockEntityRendererFactories.register(FunkyForcefields.PLASMA_PROJECTOR_BLOCK_ENTITY, PlasmaProjectorBlockEntityRenderer::new);
		HandledScreens.<PlasmaEjectorGuiDescription, PlasmaEjectorScreen>register(FunkyForcefields.PLASMA_EJECTOR_SCREEN_HANDLER, (handler, playerInventory, title) -> new PlasmaEjectorScreen(handler, playerInventory.player, title));
	}
}
