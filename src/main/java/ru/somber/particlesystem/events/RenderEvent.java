package ru.somber.particlesystem.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import ru.somber.particlesystem.ParticleAPI;
import ru.somber.particlesystem.manager.IParticleManager;

import java.util.TreeMap;

@SideOnly(Side.CLIENT)
public class RenderEvent {

    public RenderEvent() {}

    @SubscribeEvent
    public void renderParticleSystem(RenderWorldLastEvent event) {
        float interpolationFactor = event.partialTicks;

        TreeMap<Integer, IParticleManager> particleManagerTreeMap = ParticleAPI.getInstance().getParticleManagerMap();
        particleManagerTreeMap.forEach((Integer priority, IParticleManager particleManager) -> {
            particleManager.render(interpolationFactor);
        });

    }

}
