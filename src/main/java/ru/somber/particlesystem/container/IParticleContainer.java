package ru.somber.particlesystem.container;

import ru.somber.particlesystem.emitter.IParticleEmitter;
import ru.somber.particlesystem.particle.IParticle;

import java.util.Comparator;
import java.util.List;

/**
 * Интерфейс для хранилища частиц и эмиттеров частиц.
 * Нужен для хранения частиц для какого-либо менеджера частиц.
 * <p>
 * Данный интерфейс позволяет обновлять частицы и эмиттеры,
 * сортировать частицы (метод {@code sort(Comparator<IParticle>)}),
 * получать список всех хранящихся частиц и эмиттеров (а также проверять их наличие, количество и удалять).
 */
public interface IParticleContainer {

    /**
     * Добавляет частицу на обработку.
     */
    void addParticle(IParticle particle);

    /**
     * Удаляет частицу с обработки.
     */
    void removeParticle(IParticle particle);

    /**
     * Возвращает true, если данный менеджер содержит переданную частицу.
     */
    boolean containsParticle(IParticle particle);

    /**
     * Возвращает текущее количество обрабатываемых частиц.
     */
    int countStoredParticles();


    /**
     * Добавляет эмиттер на обработку.
     */
    void addEmitter(IParticleEmitter emitter);

    /**
     * Удаляет эмиттер с обработки.
     */
    void removeEmitter(IParticleEmitter emitter);

    /**
     * Возвращает true, если данный менеджер содержит переданный эмиттер.
     */
    boolean containsEmitter(IParticleEmitter emitter);

    /**
     * Возвращает текущее количество эмиттеров.
     */
    int countStoredEmitters();


    /**
     * Производит обновление частиц и эмиттеров.
     */
    void update();

    /**
     * Сортирует частицы в соответствии с переданным компаратором.
     * Вызывать перед получемнием списка частиц.
     */
    void sort(Comparator<IParticle> comparator);

    /**
     * Возвращает список частиц в виде листа.
     * Для получения отсортированного списка вызвать метод
     * {@code sort(Comparator<IParticle>)} из этого класса перед получением списка.
     */
    List<IParticle> getParticleList();

    /**
     * Возвращает список эмиттеров в виде листа.
     */
    List<IParticleEmitter> getEmitterList();

}
