package net.modfest.funkyforcefields.mixin;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.modfest.funkyforcefields.util.EntityContextBypasser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityShapeContext.class)
public abstract class EntityShapeContextMixin implements EntityContextBypasser {

	private Entity underlyingEntity = null;

	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/entity/Entity;)V")
	public void funkyforcefields$onConstruction(Entity ent, CallbackInfo ci) {
		underlyingEntity = ent;
	}

	@Override
	public Entity funkyforcefields$getUnderlyingEntity() {
		return underlyingEntity;
	}
}
