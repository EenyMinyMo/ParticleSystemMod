package ru.somber.particlesystem.particle;

import ru.somber.util.clientutil.textureatlas.icon.AtlasIcon;

/**
 * Интерфейс для частиц.
 */
public interface IParticle {

    /** Возвращает координату X позиции. */
    float getPositionX();
    /** Возвращает координату Y позиции. */
    float getPositionY();
    /** Возвращает координату Z позиции. */
    float getPositionZ();

    /** Возвращает координату X позиции на предыдущем обновлении (нужно для интерполяции позиции). */
    float getOldPositionX();
    /** Возвращает координату Y позиции на предыдущем обновлении (нужно для интерполяции позиции). */
    float getOldPositionY();
    /** Возвращает координату Z позиции на предыдущем обновлении (нужно для интерполяции позиции). */
    float getOldPositionZ();


    /** Возвращает половину ширины. */
    float getHalfWidth();
    /** Возвращает половину высоты. */
    float getHalfHeight();

    /** Возвращает половину ширины на предыдущем обновлении (нужно для интерполяции размеров). */
    float getOldHalfWidth();
    /** Возвращает половину высоты на предыдущем обновлении (нужно для интерполяции размеров). */
    float getOldHalfHeight();


    /** Возвращает локальный угол поворота по оси X. */
    float getAngleX();
    /** Возвращает локальный угол поворота по оси Y. */
    float getAngleY();
    /** Возвращает локальный угол поворота по оси Z. */
    float getAngleZ();

    /** Возвращает локальный угол поворота по оси X на предыдущем тике (нужно для интерполяции углов поворота). */
    float getOldAngleX();
    /** Возвращает локальный угол поворота по оси Y на предыдущем тике (нужно для интерполяции углов поворота). */
    float getOldAngleY();
    /** Возвращает локальный угол поворота по оси Z на предыдущем тике (нужно для интерполяции углов поворота). */
    float getOldAngleZ();


    /** Возвращает координату X вектора нормали. */
    float getNormalVectorX();
    /** Возвращает координату Y вектора нормали. */
    float getNormalVectorY();
    /** Возвращает координату Z вектора нормали. */
    float getNormalVectorZ();

    /** Возвращает координату X вектора нормали на предыдущем тике (нужно для интерполяции вектора нормали). */
    float getOldNormalVectorX();
    /** Возвращает координату Y вектора нормали на предыдущем тике (нужно для интерполяции вектора нормали). */
    float getOldNormalVectorY();
    /** Возвращает координату Z вектора нормали на предыдущем тике (нужно для интерполяции вектора нормали). */
    float getOldNormalVectorZ();


    /** Возвращает коэффициент красного цвета. */
    float getRedFactor();
    /** Возвращает коэффициент зеленого цвета. */
    float getGreenFactor();
    /** Возвращает коэффициент синего цвета. */
    float getBlueFactor();
    /** Возвращает коэффициент альфа. */
    float getAlphaFactor();

    /** Возвращает коэффициент освещенности частицы. */
    float getLightFactor();
    /** Возвращает коэффициент смешивания. */
    float getBlendFactor();

    /** Возвращает иконку частицы. */
    AtlasIcon getParticleIcon();


    /**
     * Возвращает текущее время жизни частицы.
     * (На основе этого времени может вычисляться позиция частицы.
     * Если время превышает максимальное время жизни, то частица должна помечаться мертвой.)
     */
    int getLifeTime();

    /**
     * Возвращает максимальное время жизни частицы.
     */
    int getMaxLifeTime();


    /**
     * @return true - частица помечается мертвой, false - частица помечается живой.
     */
    boolean isDie();

    /**
     * @param die true - частица помечается мертвой, false - частица помечается живой.
     */
    void setDie(boolean die);


    /**
     * Обновляет внутренние данные частицы (допустим позицию и т.д.).
     */
    void update();


//    /**
//     * Записывает интерполированную между oldPosition и position позицию частицы по интерполяционному коэффициенту
//     * в переданный вектор.
//     */
//    default void computeInterpolatedPosition(Vector3f dest, float interpolationFactor) {
//        float x = getPositionX();
//        float y = getPositionY();
//        float z = getPositionZ();
//
//        float xOld = getOldPositionX();
//        float yOld = getOldPositionY();
//        float zOld = getOldPositionZ();
//
//        float interpolatedX = xOld + (x - xOld) * interpolationFactor;
//        float interpolatedY = yOld + (y - yOld) * interpolationFactor;
//        float interpolatedZ = zOld + (z - zOld) * interpolationFactor;
//
//        dest.set(interpolatedX, interpolatedY, interpolatedZ);
//    }
//
//    /**
//     * Записывает интерполированные между oldHalfSizes и halfSizes размеры частицы по интерполяционному коэффициенту
//     * в переданный вектор.
//     */
//    default void computeInterpolatedHalfSizes(Vector2f dest, float interpolationFactor) {
//        float halfWidth = getHalfWidth();
//        float halfHeight = getHalfHeight();
//
//        float oldHalfWidth = getOldHalfWidth();
//        float oldHalfHeight = getOldHalfHeight();
//
//        float interpolatedHalfWidth = oldHalfWidth + (halfWidth - oldHalfWidth) * interpolationFactor;
//        float interpolatedHalfHeight = oldHalfHeight + (halfHeight - oldHalfHeight) * interpolationFactor;
//
//        dest.set(interpolatedHalfWidth, interpolatedHalfHeight);
//    }
//
//    /**
//     * Записывает интерполированные между oldRotateAngles и rotateAngles локальные углы поворота частицы по интерполяционному коэффициенту
//     * в переданный вектор.
//     */
//    default void computeInterpolatedRotateAngles(Vector3f dest, float interpolationFactor) {
//        float angleX = getAngleX();
//        float angleY = getAngleY();
//        float angleZ = getAngleZ();
//
//        float oldAngleX = getOldAngleX();
//        float oldAngleY = getOldAngleY();
//        float oldAngleZ = getOldAngleZ();
//
//        float interpolatedAngleX = oldAngleX + (angleX - oldAngleX) * interpolationFactor;
//        float interpolatedAngleY = oldAngleY + (angleY - oldAngleY) * interpolationFactor;
//        float interpolatedAngleZ = oldAngleZ + (angleZ - oldAngleZ) * interpolationFactor;
//
//        dest.set(interpolatedAngleX, interpolatedAngleY, interpolatedAngleZ);
//    }

    /**
     * Возвращает коэффициент соотношения текущего времени жизни к максимальному времени жизни.
     */
    default float getLifeFactor() {
        return (float) getLifeTime() / getMaxLifeTime();
    }

}
