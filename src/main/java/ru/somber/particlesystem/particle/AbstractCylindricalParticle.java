package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.Axis;

/**
 * Простейшая надстройка над {@code AbstractParticle}.
 * Представляет простой пример цилиндрической частицы.
 */
public abstract class AbstractCylindricalParticle extends AbstractParticle {

    public AbstractCylindricalParticle(Vector3f newPosition, int maxLifeTime) {
        super(newPosition, maxLifeTime);
    }

    public AbstractCylindricalParticle(Vector3f newPosition, Vector2f halfSizes, int maxLifeTime) {
        super(newPosition, halfSizes, maxLifeTime);
    }

    public AbstractCylindricalParticle(Vector3f newPosition, Vector2f halfSizes, Vector3f localRotateAngles, int maxLifeTime) {
        super(newPosition, halfSizes, localRotateAngles, maxLifeTime);
    }

    @Override
    public Axis rotateAxis() {
        return Axis.ORDINATE_AXIS;
    }


    public abstract void update();

}
