package ru.somber.particlesystem.emitter;

import org.lwjgl.util.vector.Vector3f;
import ru.somber.particlesystem.container.IParticleContainer;

/**
 * Интерфейс для представления эмиттера частиц.
 * Объекты подклассов этого интерфейса для использования нужно добавлять в объекты, производные от IEmitterContainer.
 */
public interface IParticleEmitter {

    /**
     * Устанавливает позицию эмиттера.
     */
    void setPosition(float x, float y, float z);

    /**
     * Устанавливает позицию эмиттера, координаты берутся из переданного вектора.
     */
    void setPosition(Vector3f position);

    /**
     * Возращает координату Х позиции эмиттера.
     */
    float getPositionX();

    /**
     * Возращает координату Y позиции эмиттера.
     */
    float getPositionY();

    /**
     * Возращает координату Z позиции эмиттера.
     */
    float getPositionZ();

    /**
     * Записвает позицию эмиттера в переданный вектор.
     */
    void getPosition(Vector3f position);


    /**
     * Производит создание эмиттера с инициализацией внутренних данных.
     */
    void create();

    /**
     * Производит обновление эмиттера. В этом методе должны создаваться новые частицы.
     */
    void update();

    /**
     * Удаляет внутренние данные эмиттера и делает его непригодным к использованию.
     */
    void delete();


    /**
     * Возвращает true, если эмиттер полностью создан и готов работать.
     * В общем случае true должно возвращаться после вызова create().
     */
    boolean isCreated();

    /**
     * Возвращает true, если эмиттер помечен мертвым, т.е. эмиттер более не пригоден для использования.
     */
    boolean isDie();

    /**
     * Помечает эмиттер мертвым.
     */
    void setDie();

}
