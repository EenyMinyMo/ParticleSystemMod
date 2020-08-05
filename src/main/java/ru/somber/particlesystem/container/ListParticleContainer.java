package ru.somber.particlesystem.container;

import ru.somber.particlesystem.particle.IParticle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListParticleContainer implements IParticleContainer {
    private List<IParticle> particleList;


    public ListParticleContainer() {
        this.particleList = new ArrayList<>(500);
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
    public int countStoredParticle() {
        return particleList.size();
    }

    @Override
    public void update() {
        particleList.forEach(IParticle::update);
        particleList.removeIf(IParticle::isDie);
    }

    @Override
    public List<IParticle> getParticleList() {
        return new ArrayList<>(particleList);
    }

    @Override
    public void sort(Comparator<IParticle> comparator) {
        particleList.sort(comparator);
    }

}
