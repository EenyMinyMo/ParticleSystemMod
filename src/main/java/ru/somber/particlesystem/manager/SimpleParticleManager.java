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

        try {
            particleComparatorLowAccuracy.setEntityPos(Minecraft.getMinecraft().renderViewEntity, 0);
            particleContainer.sort(particleComparatorLowAccuracy);
        } catch (Exception e) {
            //обработка исключений при сортировке массива частиц.
            //вообще обрабатывать исключение мне кажется бесполезно, т.к. некоторые исключения просто сложно исправить (городить проверки и т.д.)
            //если исключени выпало, просто оставим список частиц без сортировки.
        }
    }

    @Override
    public void render(float interpolationFactor) {
        if (particleContainer == null || particleRenderer == null) {
            throw new RuntimeException("ParticleContainer or ParticleRenderer is null.");
        }

        particleRenderer.preRender(particleContainer.getParticleList(), interpolationFactor);
        particleRenderer.render(particleContainer.getParticleList(), interpolationFactor);
        particleRenderer.postRender(particleContainer.getParticleList(), interpolationFactor);
    }
}
