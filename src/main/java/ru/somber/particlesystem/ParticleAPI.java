package ru.somber.particlesystem;

import ru.somber.particlesystem.container.IEmitterContainer;
import ru.somber.particlesystem.manager.IParticleManager;

import java.util.*;

public final class ParticleAPI {
    private static ParticleAPI instance;

    private TreeMap<Integer, IParticleManager> particleManagerMap;
    private Set<IEmitterContainer> emitterContainerSet;

    private ParticleAPI() {
        particleManagerMap = new TreeMap<>();
        emitterContainerSet = new HashSet<>(500);
    }

    public void addParticleManager(int priority, IParticleManager particleManager) {
        particleManagerMap.put(priority, particleManager);
    }

    public void removeParticleManager(int priority) {
        particleManagerMap.remove(priority);
    }

    public IParticleManager getParticleManager(int priority) {
        return particleManagerMap.get(priority);
    }

    public boolean containsPriority(int priority) {
        return particleManagerMap.containsKey(priority);
    }

    public boolean containsParticleManager(IParticleManager particleManager) {
        return particleManagerMap.containsValue(particleManager);
    }

    public TreeMap<Integer, IParticleManager> getParticleManagerMap() {
        return particleManagerMap;
    }


    public void addEmitterContainer(IEmitterContainer emitterContainer) {
        emitterContainerSet.add(emitterContainer);
    }

    public void removeEmitterContainer(IEmitterContainer emitterContainer) {
        emitterContainerSet.remove(emitterContainer);
    }

    public boolean containsEmitterContainer(IEmitterContainer emitterContainer) {
        return emitterContainerSet.contains(emitterContainer);
    }

    public Set<IEmitterContainer> getEmitterContainerSet() {
        return emitterContainerSet;
    }


    public static ParticleAPI getInstance() {
        if (instance == null) {
            instance = new ParticleAPI();
        }

        return instance;
    }

}
