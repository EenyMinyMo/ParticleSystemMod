package ru.somber.particlesystem.container;

import ru.somber.particlesystem.particle.IParticle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListParticleContainer implements IParticleContainer {
    private List<IParticle> particleList;


    public ListParticleContainer() {
        this.particleList = new ArrayList<>(5_000);
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
    public List<IParticle> getParticleList() {
        return particleList;
    }


    @Override
    public void update() {
        particleList.removeIf(IParticle::isDie);
        particleList.forEach(IParticle::update);
    }

    @Override
    public void sort(Comparator<IParticle> comparator) {
        particleList.sort(comparator);
    }

}
