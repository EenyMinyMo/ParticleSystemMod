package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector3f;

import ru.somber.clientutil.opengl.texture.TextureCoord;
import ru.somber.commonutil.Axis;

public class TestSimpleParticle implements IParticle {
    /** Количество тиков, которое существует частица. */
    protected final int maxLifeTime;
    /** Количество тиков, которое частица уже существует. */
    protected int lifeTime;

    /** Новая позиция частицы. */
    protected Vector3f newPosition;
    /** Старая позиция частицы. */
    protected Vector3f oldPosition;

    /** Половина размера частицы (x = width/2, y = height/2, z = depth/2). */
    protected Vector3f halfSizes;

    /**
     * Ось, вокруг которой происходит вращение.
     * <p></\p>
     * AXIS_X - вращение только вокруг оси X.
     * <p></\p>
     * AXIS_Y - вращение только вокруг оси Y - классический цилиндрический партикль.
     * <p></\p>
     * AXIS_Z - вращение только вокруг оси Z.
     * <p></\p>
     * NONE_AXIS - партикль не ограничен никакими осями - сферический партикль.
     */
    protected Axis limitedAxis;

    /** Текстурные координаты. */
    protected TextureCoord textureCoord;


    public TestSimpleParticle(Vector3f newPosition, Vector3f halfSizes, Axis limitedAxis, int maxLifeTime) {
        this.newPosition = newPosition;
        this.oldPosition = new Vector3f(newPosition);
        this.halfSizes = halfSizes;
        this.limitedAxis = limitedAxis;
        this.maxLifeTime = maxLifeTime;

        this.lifeTime = 0;
        this.textureCoord = new TextureCoord();
    }

    @Override
    public Vector3f getNewPosition() {
        return newPosition;
    }

    @Override
    public Vector3f getOldPosition() {
        return oldPosition;
    }

    @Override
    public Vector3f getInterpolatedPosition(float interpolationFactor) {
        float x = oldPosition.getX() + (newPosition.getX() - oldPosition.getX()) * interpolationFactor;
        float y = oldPosition.getY() + (newPosition.getY() - oldPosition.getY()) * interpolationFactor;
        float z = oldPosition.getZ() + (newPosition.getZ() - oldPosition.getZ()) * interpolationFactor;

        return new Vector3f(x, y, z);
    }

    @Override
    public Vector3f getHalfSizes() {
        return halfSizes;
    }

    @Override
    public Axis limitedAxis() {
        return limitedAxis;
    }

    @Override
    public TextureCoord getTextureCoord() {
        return textureCoord;
    }

    @Override
    public int getLifeTime() {
        return lifeTime;
    }

    @Override
    public int getMaxLifeTime() {
        return maxLifeTime;
    }

    @Override
    public boolean isDie() {
        return maxLifeTime >= lifeTime;
    }

    @Override
    public void update() {
        oldPosition.set(newPosition);


    }

}
