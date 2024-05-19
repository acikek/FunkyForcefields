package net.modfest.funkyforcefields.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.modfest.funkyforcefields.FunkyForcefields;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/entity/LivingEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;", cancellable = true)
	private static void funkyforcefields$wearInputHatch(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
		if (stack.getItem().equals(FunkyForcefields.LIQUID_INPUT_HATCH.asItem())) {
			cir.setReturnValue(EquipmentSlot.HEAD);
		}
	}
}
