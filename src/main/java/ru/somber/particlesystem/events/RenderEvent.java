package ru.somber.particlesystem.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import ru.somber.particlesystem.ParticleAPI;
import ru.somber.particlesystem.manager.IParticleManager;

import java.util.TreeMap;

public class RenderEvent {

    private ParticleAPI particleAPI;
    private TreeMap<Integer, IParticleManager> particleManagerTreeMap;

    public RenderEvent() {
        this.particleAPI = ParticleAPI.getInstance();
        this.particleManagerTreeMap = this.particleAPI.getParticleManagerMap();
    }

    @SubscribeEvent
    public void renderParticleSystem(RenderWorldLastEvent event) {
        float interpolationFactor = event.partialTicks;

        particleManagerTreeMap.forEach((Integer priority, IParticleManager particleManager) -> {
            particleManager.render(interpolationFactor);
        });

    }

}
