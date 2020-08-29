package ru.somber.particlesystem.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ru.somber.particlesystem.ParticleAPI;
import ru.somber.particlesystem.manager.IParticleManager;

import java.util.TreeMap;

public class UpdateEvent {

    public UpdateEvent() {}

    @SubscribeEvent
    public void updateParticleSystem(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TreeMap<Integer, IParticleManager> particleManagerTreeMap = ParticleAPI.getInstance().getParticleManagerMap();
            particleManagerTreeMap.forEach((Integer priority, IParticleManager particleManager) -> {
                particleManager.update();
            });
        }
    }

}
