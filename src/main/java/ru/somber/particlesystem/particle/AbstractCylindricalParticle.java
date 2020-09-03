package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.Axis;

/**
 * Простейшая надстройка над {@code AbstractParticle}.
 * Представляет простой пример цилиндрической частицы.
 */
public abstract class AbstractCylindricalParticle extends AbstractParticleSimpleData {

    public AbstractCylindricalParticle(float x, float y, float z, int maxLifeTime, String iconName) {
        super(x, y, z, maxLifeTime, iconName);
    }

    public AbstractCylindricalParticle(Vector3f position, int maxLifeTime, String iconName) {
        super(position.getX(), position.getY(), position.getZ(), maxLifeTime, iconName);
    }


    @Override
    public Axis rotateAxis() {
        return Axis.ORDINATE_AXIS;
    }

    @Override
    public void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f interpolatePosition) {
        destination.x = interpolatePosition.x - xCamera;
        destination.y = 0;
        destination.z = interpolatePosition.z - zCamera;
    }

    public void update() {
        super.update();
    }

}
