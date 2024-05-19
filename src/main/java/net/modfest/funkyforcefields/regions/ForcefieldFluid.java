package net.modfest.funkyforcefields.regions;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.modfest.funkyforcefields.FunkyForcefields;
import net.modfest.funkyforcefields.block.ForcefieldBlocks;

public interface ForcefieldFluid {

	RegistryKey<? extends Registry<ForcefieldFluid>> REGISTRY_KEY = RegistryKey.ofRegistry(FunkyForcefields.id("forcefield_fluid"));

	SimpleRegistry<ForcefieldFluid> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
		@Override
		public RegistryEntry.Reference<ForcefieldFluid> add(RegistryKey<ForcefieldFluid> key, ForcefieldFluid value, RegistryEntryInfo info) {
			ForcefieldBlocks.registerFluid(value);
			return super.add(key, value, info);
		}
	};

	boolean allowsEntity(Entity ent);

	void applyCollisionEffect(World world, BlockPos pos, Entity entity);

	Identifier getBaseIdentifier();

	default void displayTick(World world, BlockPos pos, Random random, VoxelShape shape) {
	}

	Text getFluidName();

	default boolean hasModel() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	default RenderLayer getRenderLayer() {
		return RenderLayer.getTranslucent();
	}
	// TODO: conversion from Minecraft fluids
}
