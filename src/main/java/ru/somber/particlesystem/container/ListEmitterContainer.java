package ru.somber.particlesystem.container;

import ru.somber.particlesystem.emitter.IParticleEmitter;

import java.util.ArrayList;
import java.util.List;

public class ListEmitterContainer implements IEmitterContainer {
    private List<IParticleEmitter> emitterList;


    public ListEmitterContainer() {
        this.emitterList = new ArrayList<>(500);
    }

    @Override
    public void addEmitter(IParticleEmitter emitter) {
        emitter.create();
        emitterList.add(emitter);
    }

    @Override
    public void removeEmitter(IParticleEmitter emitter) {
        emitterList.remove(emitter);
    }

    @Override
    public boolean containsEmitter(IParticleEmitter emitter) {
        return emitterList.contains(emitter);
    }

    @Override
    public int countStoredEmitters() {
        return emitterList.size();
    }


    @Override
    public void update() {
        emitterList.removeIf(IParticleEmitter::isDie);
        emitterList.forEach(IParticleEmitter::update);
    }

    @Override
    public List<IParticleEmitter> getEmitterList() {
        return emitterList;
    }

}
