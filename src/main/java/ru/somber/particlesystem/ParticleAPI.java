package ru.somber.particlesystem;

import ru.somber.particlesystem.manager.IParticleManager;

import java.util.*;

public final class ParticleAPI {
    private static ParticleAPI instance;

    private TreeMap<Integer, IParticleManager> particleManagerMap;

    private ParticleAPI() {
        particleManagerMap = new TreeMap<>();
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


    public static ParticleAPI getInstance() {
        if (instance == null) {
            instance = new ParticleAPI();
        }

        return instance;
    }

}
