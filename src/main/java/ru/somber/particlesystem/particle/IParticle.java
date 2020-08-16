package ru.somber.particlesystem.particle;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.opengl.texture.TextureCoordAABB;
import ru.somber.commonutil.Axis;

/**
 * Интерфейс для частиц.
 */
public interface IParticle {

    /**
     * Возвращает фактическую позицию частицы
     * (позиция частицы после очередного обновления).
     */
    Vector3f getNewPosition();

    /**
     * Предыдущая позиция частицы (нужно для интерполяции).
     */
    Vector3f getOldPosition();

    /**
     * Возвращает интерполированную между oldPosition и newPosition позицию частицы по интерполяционному коэффициенту.
     */
    void computeInterpolatedPosition(Vector3f destination, float interpolationFactor);

    /**
     * Половина размера частицы по высоте и ширине.
     * <p>
     * (x = width/2, y = height/2)
     */
    Vector2f getHalfSizes();

    /**
     * Возвращает локальные углы поворота частицы.
     * Эти углы должны применятся после мировых преобразований частицы.
     */
    Vector3f getLocalRotateAngles();

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
     * Запишет в передаваемый вектор координаты нормали к частице. Направление нормали должно быть в сторону камеры.
     * Вектор нормали может быть произвиольной длины.
     * <p>
     * (нормаль частицы зависит от позиции частицы, позиции камеры (которая передается в переметрах) и вокруг какой оси частица вращается)
     */
    void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, float interpolationFactor);

    /**
     * Запишет в передаваемый вектор координаты нормали к частице. Направление нормали должно быть в сторону камеры.
     * Вектор нормали может быть произвиольной длины.
     * <p>
     * Дополнительно передается позиция этой частицы
     * (это нужно для того, чтобы не вычислять еще один раз интерполированную позицию частицы, а передать уже вычисленную).
     * <p>
     * (нормаль частицы зависит от позиции частицы, позиции камеры (которая передается в переметрах) и вокруг какой оси частица вращается)
     */
    void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f particlePosition);

    /**
     * Возвращает текстурные координаты, соответствующие частице.
     */
    TextureCoordAABB getTextureCoordAABB();

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
     * Возвращает коэффициенты цветов.
     */
    float[] getColorFactor();

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

    /**
     * Возвращает ResourceLocation c текстурой частицы.
     */
    ResourceLocation getTextureLocation();

}
