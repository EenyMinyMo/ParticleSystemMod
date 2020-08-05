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

    public AbstractUniversalParticle(Vector3f newPosition, Axis rotateAxis, int maxLifeTime) {
        super(newPosition, maxLifeTime);
        this.rotateAxis = rotateAxis;
    }

    public AbstractUniversalParticle(Vector3f newPosition, Vector2f halfSizes, Axis rotateAxis, int maxLifeTime) {
        super(newPosition, halfSizes, maxLifeTime);
        this.rotateAxis = rotateAxis;
    }

    public AbstractUniversalParticle(Vector3f newPosition, Vector2f halfSizes, Vector3f localRotateAngles, Axis rotateAxis, int maxLifeTime) {
        super(newPosition, halfSizes, localRotateAngles, maxLifeTime);
        this.rotateAxis = rotateAxis;
    }

    @Override
    public Axis rotateAxis() {
        return rotateAxis;
    }


    public abstract void update();

}
