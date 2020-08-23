package ru.somber.particlesystem.container;

import ru.somber.particlesystem.emitter.IParticleEmitter;
import ru.somber.particlesystem.particle.IParticle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListParticleContainer implements IParticleContainer {
    private List<IParticle> particleList;
    private List<IParticleEmitter> emitterList;


    public ListParticleContainer() {
        this.particleList = new ArrayList<>(5_000);
        this.emitterList = new ArrayList<>(50);
    }

    @Override
    public void addParticle(IParticle particle) {
        particleList.add(particle);
    }

    @Override
    public void removeParticle(IParticle particle) {
        particleList.remove(particle);
    }

    @Override
    public boolean containsParticle(IParticle particle) {
        return particleList.contains(particle);
    }

    @Override
    public int countStoredParticles() {
        return particleList.size();
    }


    @Override
    public void addEmitter(IParticleEmitter emitter) {
        emitter.create(this);
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


        particleList.removeIf(IParticle::isDie);
        particleList.forEach(IParticle::update);
    }

    @Override
    public void sort(Comparator<IParticle> comparator) {
        particleList.sort(comparator);
    }

    @Override
    public List<IParticle> getParticleList() {
        return particleList;
    }

    @Override
    public List<IParticleEmitter> getEmitterList() {
        return emitterList;
    }

}
