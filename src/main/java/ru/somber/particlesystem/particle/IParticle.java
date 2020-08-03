package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector3f;

import ru.somber.clientutil.opengl.texture.TextureCoord;
import ru.somber.commonutil.Axis;

/**
 * Интерфейс для частиц.
 */
public interface IParticle {

    /**
     * Фактическая позиция частицы
     * (позиция частицы после очередного обновления).
     */
    Vector3f getNewPosition();

    /**
     * Предыдущая позиция частицы
     * (нужно для интерполяции).
     */
    Vector3f getOldPosition();

    /**
     * Интерполированная позиция частицы между oldPosition и newPosition по интерполяционному коэффициенту.
     */
    Vector3f getInterpolatedPosition(float interpolationFactor);

    /**
     * Половина размера частицы
     * <p>
     * (x = width/2, y = height/2, z = depth/2 - вообще не должно использоваться).
     */
    Vector3f getHalfSizes();

    /**
     * Ось, вокруг которой происходит вращение.
     * <p>
     * AXIS_X - вращение только вокруг оси X.
     * <p>
     * AXIS_Y - вращение только вокруг оси Y - классический цилиндрический партикль.
     * <p>
     * AXIS_Z - вращение только вокруг оси Z.
     * <p>
     * NONE_AXIS - частица не ограничен никакими осями - сферический партикль.
     */
    Axis limitedAxis();

    /**
     * Возвращает текстурные координаты, соответствующие частице.
     */
    TextureCoord getTextureCoord();

    /**
     * Текущее время жизни частицы.
     * (На основе этого времени может вычисляться позиция частицы.
     * Если время превышает максимальное время жизни, то частица должна помечаться мертвой.)
     */
    int getLifeTime();

    /**
     * Максимальное время жизни частицы.
     */
    int getMaxLifeTime();

    /**
     * Обновляет внутренние данные частицы
     * (допустим позицию и т.д.).
     */
    void update();

    /**
     * true - частица помечена мертвой.
     */
    default boolean isDie() {
        return getLifeTime() >= getMaxLifeTime();
    }

}
