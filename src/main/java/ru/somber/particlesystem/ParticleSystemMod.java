package ru.somber.particlesystem;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ParticleSystemMod.MOD_ID, name = ParticleSystemMod.MOD_NAME, version = ParticleSystemMod.MOD_VERSION)
public class ParticleSystemMod {
    public static final String MOD_ID = "somber_particle_system";
    public static final String MOD_NAME = "Particle system mod";
    public static final String MOD_VERSION = "0.0.0";

    @SidedProxy(clientSide = "ru.somber.particlesystem.ClientProxy", serverSide = "ru.somber.particlesystem.ServerProxy")
    public static CommonProxy proxy;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
