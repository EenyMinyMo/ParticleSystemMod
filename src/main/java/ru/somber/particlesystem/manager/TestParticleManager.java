package ru.somber.particlesystem.manager;

import net.minecraft.client.Minecraft;
import ru.somber.particlesystem.container.IParticleContainer;
import ru.somber.particlesystem.container.comparator.ParticleComparator;
import ru.somber.particlesystem.particle.IParticle;
import ru.somber.particlesystem.render.IParticleRenderer;

import java.util.List;

public class TestParticleManager implements IParticleManager {

    private ParticleComparator particleComparator;

    private IParticleContainer particleContainer;
    private IParticleRenderer particleRenderer;

    public TestParticleManager() {
        particleComparator = new ParticleComparator(Minecraft.getMinecraft().thePlayer, 0);
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
    }

    @Override
    public void render(float interpolationFactor) {
        if (particleContainer == null || particleRenderer == null) {
            throw new RuntimeException("ParticleContainer or ParticleRenderer is null.");
        }

        particleComparator.setEntity(Minecraft.getMinecraft().thePlayer);
        particleComparator.setInterpolationFactor(interpolationFactor);

        particleContainer.sort(particleComparator);

        particleRenderer.preRender(particleContainer.getParticleList(), interpolationFactor);
        particleRenderer.render(particleContainer.getParticleList(), interpolationFactor);
        particleRenderer.postRender(particleContainer.getParticleList(), interpolationFactor);
    }
}
