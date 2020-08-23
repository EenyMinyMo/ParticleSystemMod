package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.Axis;

/**
 * Абстрактный класс частицы, реализцющий наиболее общий функционал частиц.
 */
public abstract class AbstractParticle implements IParticle {
    /** Количество тиков, которое существует частица. */
    protected final int maxLifeTime;
    /** Количество тиков, которое частица уже существует. */
    protected int lifeTime;

    /** Новая позиция частицы. */
    protected Vector3f newPosition;
    /** Старая позиция частицы. */
    protected Vector3f oldPosition;
    /** Половина размера частицы (x = width/2, y = height/2). */
    protected Vector2f halfSizes;
    /**
     * Вектор, содержащий локальные углы поворота частицы
     * (эти углы должны применятся после мировых преобразований частицы).
     */
    protected Vector3f localRotateAngles;

    /** Коэффициенты цветов. */
    protected float[] colorFactor;

    /** Здесь текстурные координаты. */
    protected String particleIconName;

    private boolean isDie;


    public AbstractParticle(Vector3f newPosition, int maxLifeTime, String iconName) {
        this(newPosition, new Vector2f(0.5f, 0.5f), new Vector3f(0.0f, 0.0f, 0.0f), maxLifeTime, iconName);
    }

    public AbstractParticle(Vector3f newPosition, Vector2f halfSizes, int maxLifeTime, String iconName) {
        this(newPosition, halfSizes, new Vector3f(0.0f, 0.0f, 0.0f), maxLifeTime, iconName);
    }

    public AbstractParticle(Vector3f newPosition, Vector2f halfSizes, Vector3f localRotateAngles, int maxLifeTime, String iconName) {
        this.newPosition = newPosition;
        this.oldPosition = new Vector3f(newPosition);
        this.halfSizes = halfSizes;
        this.localRotateAngles = localRotateAngles;
        this.maxLifeTime = maxLifeTime;

        this.lifeTime = 0;
        this.particleIconName = iconName;
        this.colorFactor = new float[] {1, 1, 1, 1};
        this.isDie = false;
    }


    public void setHalfSizes(float halfWidth, float halfHeight) {
        this.halfSizes.set(halfWidth, halfHeight);
    }

    public void setHalfSizes(Vector2f halfSizes) {
        this.halfSizes = halfSizes;
    }

    public void setLocalRotateAngles(float x, float y, float z) {
        this.localRotateAngles.set(x, y, z);
    }

    public void setLocalRotateAngles(Vector3f localRotateAngles) {
        this.localRotateAngles = localRotateAngles;
    }

    public void setColorFactor(float r, float g, float b, float a) {
        colorFactor[0] = r;
        colorFactor[1] = g;
        colorFactor[2] = b;
        colorFactor[3] = a;
    }

    public void setParticleIconName(String particleIconName) {
        this.particleIconName = particleIconName;
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
    public void computeInterpolatedPosition(Vector3f destination, float interpolationFactor) {
        float x = oldPosition.getX() + (newPosition.getX() - oldPosition.getX()) * interpolationFactor;
        float y = oldPosition.getY() + (newPosition.getY() - oldPosition.getY()) * interpolationFactor;
        float z = oldPosition.getZ() + (newPosition.getZ() - oldPosition.getZ()) * interpolationFactor;

        destination.set(x, y, z);
    }

    @Override
    public Vector3f getLocalRotateAngles() {
        return this.localRotateAngles;
    }

    @Override
    public Vector2f getHalfSizes() {
        return halfSizes;
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
    public float[] getColorFactor() {
        return colorFactor;
    }

    @Override
    public String getIconName() {
        return particleIconName;
    }

    @Override
    public void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, float interpolateFactor) {
        Vector3f interpolatePosition = new Vector3f();
        computeInterpolatedPosition(interpolatePosition, interpolateFactor);

        computeNormalVector(destination, xCamera, yCamera, zCamera, interpolatePosition);
    }

    @Override
    public boolean isDie() {
        return isDie;
    }

    @Override
    public void setDie() {
        this.isDie = true;
    }

    public void update() {
        if (getLifeTime() >= getMaxLifeTime()) {
            setDie();
        }
    }


    public abstract Axis rotateAxis();

    public abstract void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f particlePosition);

}
