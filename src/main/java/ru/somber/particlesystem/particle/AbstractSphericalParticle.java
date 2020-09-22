package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.textureatlas.icon.AtlasIcon;
import ru.somber.commonutil.Axis;

/**
 * Простейшая надстройка над {@code AbstractParticle}.
 * Представляет простой пример сферической частицы.
 */
public abstract class AbstractSphericalParticle extends AbstractParticleSimpleData {

    public AbstractSphericalParticle(float x, float y, float z, int maxLifeTime, AtlasIcon icon) {
        super(x, y, z, maxLifeTime, icon);
    }

    public AbstractSphericalParticle(Vector3f position, int maxLifeTime, AtlasIcon icon) {
        super(position.getX(), position.getY(), position.getZ(), maxLifeTime, icon);
    }


    @Override
    public Axis rotateAxis() {
        return Axis.ALL_AXIS;
    }

    @Override
    public void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f interpolatePosition) {
        destination.x = interpolatePosition.x - xCamera;
        destination.y = interpolatePosition.y - yCamera;
        destination.z = interpolatePosition.z - zCamera;
    }

    public void update() {
        super.update();
    }

}
