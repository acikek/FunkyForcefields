package net.modfest.funkyforcefields.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.block.entity.PlasmaEjectorBlockEntity;

public record C2SConfigurePlasmaEjector(BlockPos pos, int length) implements CustomPayload {

	public static final Id<C2SConfigurePlasmaEjector> PACKET_ID = new Id<>(FunkyForcefields.id("configure_plasma_ejector"));

	public static final PacketCodec<RegistryByteBuf, C2SConfigurePlasmaEjector> PACKET_CODEC = PacketCodec.tuple(
			BlockPos.PACKET_CODEC, C2SConfigurePlasmaEjector::pos,
			PacketCodecs.INTEGER, C2SConfigurePlasmaEjector::length,
			C2SConfigurePlasmaEjector::new
	);

	public static void receive(C2SConfigurePlasmaEjector payload, ServerPlayNetworking.Context context) {
		context.player().server.execute(() -> {
			if (context.player().getWorld().canSetBlock(payload.pos())) {
				if (context.player().getWorld().getBlockEntity(payload.pos()) instanceof PlasmaEjectorBlockEntity blockEntity) {
					blockEntity.length = payload.length();
					blockEntity.markDirty();
				}
			}
		});
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return null;
	}
}
