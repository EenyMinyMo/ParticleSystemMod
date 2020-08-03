package ru.somber.particlesystem.container;

import ru.somber.particlesystem.particle.IParticle;

import java.util.Comparator;
import java.util.List;

/**
 * Интерфейс для хранилища частиц.
 * Нужен для хранения частиц для какого-либо менеджера частиц.
 * <p>
 * Данный интерфейс позволяет обновлять частицы, которые содержит (метод {@code update()}),
 * сортировать частицы (метод {@code sort(Comparator<IParticle>)}),
 * получать список всех хранящихся частиц (метод {@code getParticleList()}).
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
     * Возвращает true, если данный менеджер содержит частицы.
     */
    boolean containsParticle(IParticle particle);

    /**
     * Возвращает текущее количество обрабатываемых частиц.
     */
    int countStoredParticle();

    /**
     * Производит обновление частиц.
     */
    void update();

    /**
     * Сортирует частицы в соответствии с переданным компратором.
     * Вызывать перед получемнием списка частиц.
     */
    void sort(Comparator<IParticle> comparator);

    /**
     * Возвращает список частиц в виде листа.
     * Для получения отсортированного списка вызвать метод
     * {@code sort(Comparator<IParticle>)} из этого класса перед получением списка.
     */
    List<IParticle> getParticleList();

}
