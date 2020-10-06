package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import ru.somber.clientutil.textureatlas.icon.AtlasIcon;

/**
 * Интерфейс для частиц.
 */
public interface IParticle {

    /**
     * Записывает фактическую позицию частицы в переданный вектор
     * (позиция частицы после очередного обновления).
     */
    void getPosition(Vector3f dest);

    /**
     * Записывает значения позиции частицы с предыдущего обновления в переданный вектор (нужно для интерполяции).
     */
    void getOldPosition(Vector3f dest);

    float getPositionX();
    float getPositionY();
    float getPositionZ();

    float getOldPositionX();
    float getOldPositionY();
    float getOldPositionZ();


    /**
     * Записывает фактические размеры частицы по высоте и ширине в переданный вектор.
     * <p> (x = width/2, y = height/2)
     */
    void getHalfSizes(Vector2f dest);

    /**
     * Записывает значения размеров частицы по высоте и ширине с предыдущего обновления в переданный вектор.
     * <p> (x = width/2, y = height/2)
     */
    void getOldHalfSizes(Vector2f dest);

    float getHalfWidth();
    float getHalfHeight();

    float getOldHalfWidth();
    float getOldHalfHeight();


    /**
     * Записвает фактические локальные углы поворота частицы в переданный вектор.
     * Эти углы должны применяются после мировых преобразований частицы.
     */
    void getRotateAngles(Vector3f dest);

    /**
     * Записывает значения локальных углов поворота частицы с предыдущего обновления в переданный вектор.
     * Эти углы должны применяются после мировых преобразований частицы.
     */
    void getOldRotateAngles(Vector3f dest);

    float getAngleX();
    float getAngleY();
    float getAngleZ();

    float getOldAngleX();
    float getOldAngleY();
    float getOldAngleZ();


    /**
     * Записывает в переданный вектор координаты нормали частицы. Вектор нормали может быть произвольной длины.
     * В зависимости от класса частицы результат может и не учитывать параметры lookAt и тд,
     * т.е. метод может быть переопределен произвольным образом.
     *
     * @param destination вектор, куда запишутся данные нормали.
     * @param lookAtX позиция X точки, куда может быть направлена нормаль частицы.
     * @param lookAtY позиция Y точки, куда может быть направлена нормаль частицы.
     * @param lookAtZ позиция Z точки, куда может быть направлена нормаль частицы.
     * @param interpolationFactor коэффициент интерполяции между старой и новой позициями частицы.
     */
    void computeNormalVector(Vector3f destination, float lookAtX, float lookAtY, float lookAtZ, float interpolationFactor);

    /**
     * Записывает в переданные вектора координаты интерполированной позиции, размеров, углов поворота и нормали частицы.
     * Вектор нормали может быть произвольной длины.
     * В зависимости от класса частицы нормаль может и не учитывать параметры lookAt и тд,
     * т.е. метод может быть переопределен произвольным образом.
     *
     * @param destPosition вектор, куда запишутся данные интерполированной позиции.
     * @param destHalfSizes вектор, куда запишутся данные интерполированных размеров.
     * @param destRotationAngles вектор, куда запишутся данные интерполированных углов поворота.
     * @param destNormalVector вектор, куда запишутся данные нормали.
     * @param lookAtX позиция X точки, куда может быть направлена нормаль частицы.
     * @param lookAtY позиция Y точки, куда может быть направлена нормаль частицы.
     * @param lookAtZ позиция Z точки, куда может быть направлена нормаль частицы.
     * @param interpolationFactor коэффициент интерполяции между старой и новой позициями частицы.
     */
    void computeInterpolateData(Vector3f destPosition, Vector2f destHalfSizes, Vector3f destRotationAngles, Vector3f destNormalVector, float lookAtX, float lookAtY, float lookAtZ, float interpolationFactor);


    /**
     * Записывает коэффициенты цветов в переданный вектор.
     */
    void getColorFactor(Vector4f destination);

    /**
     * Возвращает коэффициент красного цвета.
     */
    float getRedFactor();

    /**
     * Возвращает коэффициент зеленого цвета.
     */
    float getGreenFactor();

    /**
     * Возвращает коэффициент синего цвета.
     */
    float getBlueFactor();

    /**
     * Возвращает коэффициент альфа.
     */
    float getAlphaFactor();

    /**
     * Возвращает коэффициент освещенности частицы.
     */
    float getLightFactor();

    /**
     * Возвращает коэффициент смешивания.
     */
    float getBlendFactor();

    /**
     * Возвращает иконку частицы.
     */
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


    /**
     * Записывает интерполированную между oldPosition и position позицию частицы по интерполяционному коэффициенту
     * в переданный вектор.
     */
    default void computeInterpolatedPosition(Vector3f dest, float interpolationFactor) {
        float x = getPositionX();
        float y = getPositionY();
        float z = getPositionZ();

        float xOld = getOldPositionX();
        float yOld = getOldPositionY();
        float zOld = getOldPositionZ();

        float interpolatedX = xOld + (x - xOld) * interpolationFactor;
        float interpolatedY = yOld + (y - yOld) * interpolationFactor;
        float interpolatedZ = zOld + (z - zOld) * interpolationFactor;

        dest.set(interpolatedX, interpolatedY, interpolatedZ);
    }

    /**
     * Записывает интерполированные между oldHalfSizes и halfSizes размеры частицы по интерполяционному коэффициенту
     * в переданный вектор.
     */
    default void computeInterpolatedHalfSizes(Vector2f dest, float interpolationFactor) {
        float halfWidth = getHalfWidth();
        float halfHeight = getHalfHeight();

        float oldHalfWidth = getOldHalfWidth();
        float oldHalfHeight = getOldHalfHeight();

        float interpolatedHalfWidth = oldHalfWidth + (halfWidth - oldHalfWidth) * interpolationFactor;
        float interpolatedHalfHeight = oldHalfHeight + (halfHeight - oldHalfHeight) * interpolationFactor;

        dest.set(interpolatedHalfWidth, interpolatedHalfHeight);
    }

    /**
     * Записывает интерполированные между oldRotateAngles и rotateAngles локальные углы поворота частицы по интерполяционному коэффициенту
     * в переданный вектор.
     */
    default void computeInterpolatedRotateAngles(Vector3f dest, float interpolationFactor) {
        float angleX = getAngleX();
        float angleY = getAngleY();
        float angleZ = getAngleZ();

        float oldAngleX = getOldAngleX();
        float oldAngleY = getOldAngleY();
        float oldAngleZ = getOldAngleZ();

        float interpolatedAngleX = oldAngleX + (angleX - oldAngleX) * interpolationFactor;
        float interpolatedAngleY = oldAngleY + (angleY - oldAngleY) * interpolationFactor;
        float interpolatedAngleZ = oldAngleZ + (angleZ - oldAngleZ) * interpolationFactor;

        dest.set(interpolatedAngleX, interpolatedAngleY, interpolatedAngleZ);
    }

    /**
     * Возвращает коэффициент соотношения текущего времени жизни к максимальному времени жизни.
     */
    default float getLifeFactor() {
        return (float) getLifeTime() / getMaxLifeTime();
    }

}
