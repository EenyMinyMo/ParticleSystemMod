package ru.somber.particlesystem.manager;

import ru.somber.particlesystem.container.IParticleContainer;
import ru.somber.particlesystem.container.comparator.ParticleComparator;
import ru.somber.particlesystem.render.IParticleRenderer;

public class SimpleParticleManager implements IParticleManager {

    private ParticleComparator particleComparator;

    private IParticleContainer particleContainer;
    private IParticleRenderer particleRenderer;

    public SimpleParticleManager() {
        particleComparator = new ParticleComparator();
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

        try {
            particleComparator.updateCameraPosition();
            particleContainer.sort(particleComparator);
        } catch (Exception e) {
            //обработка исключений при сортировке массива частиц.
            //вообще обрабатывать исключение мне кажется бесполезно, т.к. некоторые исключения просто сложно исправить (городить проверки и т.д.)
            //если исключени выпало, просто оставим список частиц без сортировки.
        }

        particleRenderer.update(particleContainer.getParticleList());
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
