package net.modfest.funkyforcefields.client.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class PlasmaEjectorScreen extends CottonInventoryScreen<PlasmaEjectorGuiDescription> {

	public PlasmaEjectorScreen(PlasmaEjectorGuiDescription container, PlayerEntity player, Text title) {
		super(container, player, title);
	}
}
