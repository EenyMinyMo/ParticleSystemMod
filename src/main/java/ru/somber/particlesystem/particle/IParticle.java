package ru.somber.particlesystem.particle;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.opengl.texture.TextureCoord;
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
    Vector3f getInterpolatedPosition(float interpolationFactor);

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
     * Возвращает коэффициент альфа-прозрачности.
     */
    float getAlpha();

    /**
     * Возвращает коэффициент освещенности.
     */
    float getLight();

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
