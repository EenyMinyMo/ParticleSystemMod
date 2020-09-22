package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.textureatlas.icon.AtlasIcon;
import ru.somber.commonutil.Axis;

/**
 * Простейшая надстройка над {@code AbstractParticle}.
 * Представляет простой пример статической частицы.
 */
public abstract class AbstractStaticParticle extends AbstractParticleSimpleData {

    public AbstractStaticParticle(float x, float y, float z, int maxLifeTime, AtlasIcon icon) {
        super(x, y, z, maxLifeTime, icon);
    }

    public AbstractStaticParticle(Vector3f position, int maxLifeTime, AtlasIcon icon) {
        super(position.getX(), position.getY(), position.getZ(), maxLifeTime, icon);
    }


    @Override
    public Axis rotateAxis() {
        return Axis.NONE_AXIS;
    }

    @Override
    public void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f interpolatePosition) {
        destination.x = 0;
        destination.y = 0;
        destination.z = 1;
    }

    public void update() {
        super.update();
    }

}
