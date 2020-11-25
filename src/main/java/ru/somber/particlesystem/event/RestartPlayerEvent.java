package ru.somber.particlesystem.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;

import ru.somber.particlesystem.ParticleAPI;
import ru.somber.particlesystem.container.IEmitterContainer;
import ru.somber.particlesystem.manager.IParticleManager;

import java.util.Map;
import java.util.Set;

public class RestartPlayerEvent {

    public RestartPlayerEvent() {}

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        clearParticleSystem();
    }

    @SubscribeEvent
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        clearParticleSystem();
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        clearParticleSystem();
    }

    @SubscribeEvent
    public void onPlayerJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.entity == Minecraft.getMinecraft().thePlayer) {
            clearParticleSystem();
        }
    }


    private void clearParticleSystem() {
        Set<IEmitterContainer> emitterContainers = ParticleAPI.getInstance().getEmitterContainerSet();
        emitterContainers.forEach((emitterContainer) -> {
            emitterContainer.getEmitterList().clear();
        });

        Map<Integer, IParticleManager> particleManagerMap = ParticleAPI.getInstance().getParticleManagerMap();
        particleManagerMap.forEach((priority, particleManager) -> {
            particleManager.getParticleContainer().getParticleList().clear();
        });
    }
}
