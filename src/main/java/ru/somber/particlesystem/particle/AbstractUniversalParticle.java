package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.Axis;

/**
 * Простейшая надстройка над {@code AbstractParticle}.
 * Позволяет при создании объекта задавать ось, вокруг которой частца будет вращаться.
 */
public abstract class AbstractUniversalParticle extends AbstractParticle {
    /**
     * Ось, вокруг которой происходит вращение за игроком.
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
    protected Axis rotateAxis;

    public AbstractUniversalParticle(Vector3f newPosition, Axis rotateAxis, int maxLifeTime, String iconName) {
        super(newPosition, maxLifeTime, iconName);
        this.rotateAxis = rotateAxis;
    }

    public AbstractUniversalParticle(Vector3f newPosition, Vector2f halfSizes, Axis rotateAxis, int maxLifeTime, String iconName) {
        super(newPosition, halfSizes, maxLifeTime, iconName);
        this.rotateAxis = rotateAxis;
    }

    public AbstractUniversalParticle(Vector3f newPosition, Vector2f halfSizes, Vector3f localRotateAngles, Axis rotateAxis, int maxLifeTime, String iconName) {
        super(newPosition, halfSizes, localRotateAngles, maxLifeTime, iconName);
        this.rotateAxis = rotateAxis;
    }

    @Override
    public Axis rotateAxis() {
        return rotateAxis;
    }

    @Override
    public void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f particlePosition) {
        if (rotateAxis() == Axis.NONE_AXIS) {
            destination.x = 0;
            destination.y = 0;
            destination.z = 1;
        } else if (rotateAxis() == Axis.ALL_AXIS) {
            destination.x = particlePosition.x - xCamera;
            destination.y = particlePosition.y - yCamera;
            destination.z = particlePosition.z - zCamera;
        } else if (rotateAxis() == Axis.ABSCISSA_AXIS) {
            destination.x = 0;
            destination.y = particlePosition.y - yCamera;
            destination.z = particlePosition.z - zCamera;
        } else if (rotateAxis() == Axis.ORDINATE_AXIS) {
            destination.x = particlePosition.x - xCamera;
            destination.y = 0;
            destination.z = particlePosition.z - zCamera;
        } else if (rotateAxis() == Axis.APPLICATE_AXIS) {
            destination.x = particlePosition.x - xCamera;
            destination.y = particlePosition.y - yCamera;
            destination.z = 0;
        }
    }

    public void update() {
        super.update();
    }

}
