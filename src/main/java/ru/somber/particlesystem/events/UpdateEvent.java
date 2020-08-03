package ru.somber.particlesystem.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ru.somber.particlesystem.ParticleAPI;
import ru.somber.particlesystem.manager.IParticleManager;

import java.util.TreeMap;

public class UpdateEvent {

    private ParticleAPI particleAPI;
    private TreeMap<Integer, IParticleManager> particleManagerTreeMap;

    public UpdateEvent() {
        this.particleAPI = ParticleAPI.getInstance();
        this.particleManagerTreeMap = this.particleAPI.getParticleManagerMap();
    }

    @SubscribeEvent
    public void updateParticleSystem(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            particleManagerTreeMap.forEach((Integer priority, IParticleManager particleManager) -> {
                particleManager.update();
            });
        }
    }

}
