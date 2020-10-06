package ru.somber.particlesystem.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ru.somber.particlesystem.ParticleAPI;
import ru.somber.particlesystem.container.IEmitterContainer;
import ru.somber.particlesystem.manager.IParticleManager;

import java.util.Set;
import java.util.TreeMap;

public class UpdateEvent {

    public UpdateEvent() {}

    @SubscribeEvent
    public void updateParticleSystem(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Set<IEmitterContainer> emitterContainerSet = ParticleAPI.getInstance().getEmitterContainerSet();
            emitterContainerSet.forEach(IEmitterContainer::update);

            TreeMap<Integer, IParticleManager> particleManagerTreeMap = ParticleAPI.getInstance().getParticleManagerMap();
            particleManagerTreeMap.forEach((Integer priority, IParticleManager particleManager) -> {
                particleManager.update();
            });
        }
    }

}
