package ru.somber.particlesystem.manager;

import net.minecraft.client.Minecraft;
import ru.somber.particlesystem.container.IParticleContainer;
import ru.somber.particlesystem.container.comparator.ParticleComparator;
import ru.somber.particlesystem.particle.IParticle;
import ru.somber.particlesystem.render.IParticleRenderer;

import java.util.List;

public class TestSimpleParticleManager implements IParticleManager {

    private ParticleComparator particleComparator;

    private IParticleContainer particleContainer;
    private IParticleRenderer particleRenderer;

    public TestSimpleParticleManager() {
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
        if (particleContainer == null) {
            throw new RuntimeException("ParticleContainer is null.");
        }

        particleContainer.update();
    }

    @Override
    public void render(float interpolationFactor) {
        if (particleContainer == null || particleRenderer == null) {
            throw new RuntimeException("ParticleContainer or ParticleRenderer is null.");
        }

        particleComparator.setInterpolationFactor(interpolationFactor);

        particleContainer.sort(particleComparator);
        List<IParticle> particleForRenderList = particleContainer.getParticleList();

        particleRenderer.preRender(particleForRenderList);
        particleRenderer.render(interpolationFactor);
        particleRenderer.postRender();
    }
}
