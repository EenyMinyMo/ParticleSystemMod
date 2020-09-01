package ru.somber.particlesystem.container;

import ru.somber.particlesystem.emitter.IParticleEmitter;

import java.util.List;

public interface IEmitterContainer {

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
     * Возвращает список эмиттеров в виде листа.
     */
    List<IParticleEmitter> getEmitterList();


    /**
     * Производит обновление эмиттеров.
     */
    void update();

}
