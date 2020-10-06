package ru.somber.particlesystem.manager;

import net.minecraft.client.Minecraft;
import ru.somber.particlesystem.container.IParticleContainer;
import ru.somber.particlesystem.container.comparator.ParticleComparatorLowAccuracy;
import ru.somber.particlesystem.render.IParticleRenderer;

public class SimpleParticleManager implements IParticleManager {

    private ParticleComparatorLowAccuracy particleComparatorLowAccuracy;

    private IParticleContainer particleContainer;
    private IParticleRenderer particleRenderer;

    public SimpleParticleManager() {
        particleComparatorLowAccuracy = new ParticleComparatorLowAccuracy();
    }

    @Override
    public IParticleContainer getParticleContainer() {
        return particleContainer;
    }

    @Override
    public void setParticleContainer(IParticleContainer particleContainer) {
        this.particleContainer = particleContainer;
    }

    @Override
    public IParticleRenderer getParticleRenderer() {
        return particleRenderer;
    }

    @Override
    public void setParticleRenderer(IParticleRenderer particleRenderer) {
        this.particleRenderer = particleRenderer;
    }

    @Override
    public void update() {
        if (particleContainer == null || particleRenderer == null) {
            throw new RuntimeException("ParticleContainer or ParticleRenderer is null.");
        }

        particleContainer.update();
        particleRenderer.update(particleContainer.getParticleList());

        particleComparatorLowAccuracy.setEntityPos(Minecraft.getMinecraft().renderViewEntity, 0);
        try {
            particleContainer.sort(particleComparatorLowAccuracy);
        } catch (IllegalArgumentException e) {}
    }

    @Override
    public void render(float interpolationFactor) {
        if (particleContainer == null || particleRenderer == null) {
            throw new RuntimeException("ParticleContainer or ParticleRenderer is null.");
        }

//        particleComparatorLowAccuracy.setEntityPos(Minecraft.getMinecraft().renderViewEntity, interpolationFactor);
//        try {
//            particleContainer.sort(particleComparatorLowAccuracy);
//        } catch (IllegalArgumentException e) {}

        particleRenderer.preRender(particleContainer.getParticleList(), interpolationFactor);
        particleRenderer.render(particleContainer.getParticleList(), interpolationFactor);
        particleRenderer.postRender(particleContainer.getParticleList(), interpolationFactor);
    }
}
