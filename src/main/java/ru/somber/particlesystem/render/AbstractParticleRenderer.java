package ru.somber.particlesystem.render;

import ru.somber.particlesystem.particle.IParticle;

import java.util.List;

public abstract class AbstractParticleRenderer implements IParticleRenderer {
    private List<IParticle> particleList;

    @Override
    public void preRender(final List<IParticle> particleList) {
        this.particleList = particleList;

    }

    @Override
    public void render(final float interpolationFactor) {

    }

    @Override
    public void postRender() {
        this.particleList = null;

    }

}
