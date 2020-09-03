package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import ru.somber.commonutil.Axis;

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

    /**
     * Записывает интерполированную между oldPosition и position позицию частицы по интерполяционному коэффициенту
     * в переданный вектор.
     */
    void computeInterpolatedPosition(Vector3f destination, float interpolationFactor);

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

    /**
     * Записывает интерполированные между oldHalfSizes и halfSizes размеры частицы по интерполяционному коэффициенту
     * в переданный вектор.
     */
    void computeInterpolatedHalfSizes(Vector2f destination, float interpolationFactor);

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

    /**
     * Записывает интерполированные между oldRotateAngles и rotateAngles локальные углы поворота частицы по интерполяционному коэффициенту
     * в переданный вектор.
     */
    void computeInterpolatedRotateAngles(Vector3f destination, float interpolationFactor);

    float getAngleX();
    float getAngleY();
    float getAngleZ();

    float getOldAngleX();
    float getOldAngleY();
    float getOldAngleZ();


    /**
     * Возвращает ось, вокруг которой происходит вращение за игроком.
     * <p>
     * ABSCISSA_AXIS - вращение только вокруг оси X.
     * <p>
     * ORDINATE_AXIS - вращение только вокруг оси Y - классическая цилиндрическая частица.
     * <p>
     * APPLICATE_AXIS - вращение только вокруг оси Z.
     * <p>
     * ALL_AXIS - частица вращается вокруг всех осей - сферическая частица.
     * <p>
     * NONE_AXIS - частица не вращается за игроком - статическая частица.
     */
    Axis rotateAxis();

    /**
     * Записывает в переданный вектор координаты нормали к частице. Вектор нормали может быть произвиольной длины.
     * <p>
     * (нормаль частицы зависит от позиции частицы, позиции камеры (которая передается в переметрах) и вокруг какой оси частица вращается)
     */
    void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, float interpolationFactor);


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
     * Возвращает название иконки частицы.
     */
    String getIconName();


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
     * true - частица помечена мертвой.
     */
    boolean isDie();

    /**
     * Помечает частицу мертвой.
     */
    void setDie();


    /**
     * Обновляет внутренние данные частицы
     * (допустим позицию и т.д.).
     */
    void update();

}
