package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.Axis;

/**
 * Простейшая надстройка над {@code AbstractParticle}.
 * Представляет простой пример цилиндрической частицы.
 */
public abstract class AbstractCylindricalParticle extends AbstractParticle {

    public AbstractCylindricalParticle(Vector3f newPosition, int maxLifeTime, String iconName) {
        super(newPosition, maxLifeTime, iconName);
    }

    public AbstractCylindricalParticle(Vector3f newPosition, Vector2f halfSizes, int maxLifeTime, String iconName) {
        super(newPosition, halfSizes, maxLifeTime, iconName);
    }

    public AbstractCylindricalParticle(Vector3f newPosition, Vector2f halfSizes, Vector3f localRotateAngles, int maxLifeTime, String iconName) {
        super(newPosition, halfSizes, localRotateAngles, maxLifeTime, iconName);
    }

    @Override
    public Axis rotateAxis() {
        return Axis.ORDINATE_AXIS;
    }

    @Override
    public void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f particlePosition) {
        destination.x = particlePosition.x - xCamera;
        destination.y = 0;
        destination.z = particlePosition.z - zCamera;
    }

    public void update() {
        super.update();
    }

}
