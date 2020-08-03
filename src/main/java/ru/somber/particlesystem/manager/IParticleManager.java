package ru.somber.particlesystem.manager;

import ru.somber.particlesystem.container.IParticleContainer;
import ru.somber.particlesystem.render.IParticleRenderer;

/**
 * Менеджер частиц.
 * <p>
 * Используется для совмещения данных самих частиц (в лице объекта класса {@code IParticleContainer})
 * и алгоритмов их отрисовки (в лице объекта класса {@code IParticleRenderer}).
 */
public interface IParticleManager {

    /**
     * Возвращает используемый контейнер частиц.
     * <p>
     * Частицы в этом контейнере будут обрабатываться данным менеджером частиц.
     */
    IParticleContainer getParticleContainer();

    /**
     * Устанавливает контейнер частиц.
     * <p>
     * Частицы в этом контейнере будут обрабатываться данным менеджером частиц.
     */
    void setParticleContainer(IParticleContainer particleContainer);

    /**
     * Возвращает используемый ренедерер частиц.
     * <p>
     * Частицы, хранимые в связанном контейнере частиц, будут отрисовываться с помощью этого объекта.
     */
    IParticleRenderer getParticleRenderer();

    /**
     * Устанавливает ренедерер частиц.
     * <p>
     * Частицы, хранимые в связанном контейнере частиц, будут отрисовываться с помощью этого объекта.
     */
    void setParticleRenderer(IParticleRenderer particleRenderer);

    /**
     * Обновляет всю логику, связанную с частицами (допустим позиции частиц и т.д.).
     * <p>
     * Рекомендуется вызывать каждый тик.
     */
    void update();

    /**
     * Производит отрисовку частиц.
     * <p>
     * Отрисовываются частицы, хранимые в связанном контейнере частиц.
     * <p>
     * Для отрисовки используется связанный ParticleRenderer.
     */
    void render(float interpolationFactor);

}
