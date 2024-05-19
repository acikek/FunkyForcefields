package net.modfest.funkyforcefields.client.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.network.C2SConfigurePlasmaEjector;

public class PlasmaEjectorGuiDescription extends SyncedGuiDescription {

	private final ScreenHandlerContext context;
	private int currLength;

	public PlasmaEjectorGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(FunkyForcefields.PLASMA_EJECTOR_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
		this.context = context;
		currLength = propertyDelegate.get(0);
		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		//root.setSize(300, 200);
		WLabel title = new WLabel(Text.translatable("block.funkyforcefields.plasma_ejector"));
		title.setHorizontalAlignment(HorizontalAlignment.CENTER);
		root.add(title, 0, 0, 9, 1);
		WSlider lengthSlider = new WSlider(1, 10, Axis.HORIZONTAL);
		lengthSlider.setValue(currLength);
		root.add(lengthSlider, 4, 1, 5, 1);
		WLabel lengthCount = new WLabel(Text.literal(Integer.toString(currLength)));
		root.add(lengthCount, 3, 1);
		WLabel lengthLabel = new WLabel(Text.translatable("block.funkyforcefields.plasma_ejector.length"));
		root.add(lengthLabel, 0, 1);
		root.add(createPlayerInventoryPanel(), 0, 3);
		lengthSlider.setValueChangeListener(val -> {
			lengthCount.setText(Text.literal(Integer.toString(val)));
			currLength = val;
		});
		root.validate(this);
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		if (world.isClient()) {
			context.run((world, blockPos) -> ClientPlayNetworking.send(new C2SConfigurePlasmaEjector(blockPos, currLength)));
		}
	}
}
