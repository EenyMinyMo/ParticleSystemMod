package ru.somber.particlesystem.render;

import ru.somber.particlesystem.particle.IParticle;

import java.util.List;

public class TestSimpleParticleRenderer implements IParticleRenderer {
    private List<IParticle> particleList;

    public TestSimpleParticleRenderer() {

    }

    @Override
    public void preRender(List<IParticle> particleList) {
        this.particleList = particleList;

    }

    @Override
    public void render(float interpolationFactor) {

    }

    @Override
    public void postRender() {
        this.particleList = null;

    }
}
