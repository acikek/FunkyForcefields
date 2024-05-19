package net.modfest.funkyforcefields.transport;

import net.minecraft.util.math.Direction;
import net.modfest.funkyforcefields.FunkyForcefields;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public interface FluidContainerComponent extends Component, AutoSyncedComponent {

	ComponentKey<FluidContainerComponent> TYPE = ComponentRegistry.getOrCreate(FunkyForcefields.id("fluid_container"), FluidContainerComponent.class);

	FluidContainer dir(Direction dir);

	FluidContainer self();
}
