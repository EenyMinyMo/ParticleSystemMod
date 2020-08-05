package ru.somber.particlesystem.particle;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.opengl.texture.TextureCoord;
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

    /** Коэффициент альфа прозрачности частицы. */
    protected float alpha;
    /** Коэффициент освещенности частицы. */
    protected float light;

    /** Текстурные координаты. */
    protected TextureCoord textureCoord;
    /** ResourceLocation c текстурой частицы. */
    protected ResourceLocation textureLocation;


    public AbstractParticle(Vector3f newPosition, int maxLifeTime) {
        this.newPosition = newPosition;
        this.oldPosition = new Vector3f(newPosition);
        this.maxLifeTime = maxLifeTime;

        this.lifeTime = 0;
        this.halfSizes = new Vector2f(0.5f, 0.5f);
        this.localRotateAngles = new Vector3f(0.0f, 0.0f, 0.0f);
        this.textureCoord = new TextureCoord();
        this.alpha = 1.0f;
        this.light = 1.0f;
    }

    public AbstractParticle(Vector3f newPosition, Vector2f halfSizes, int maxLifeTime) {
        this.newPosition = newPosition;
        this.oldPosition = new Vector3f(newPosition);
        this.halfSizes = halfSizes;
        this.maxLifeTime = maxLifeTime;

        this.lifeTime = 0;
        this.localRotateAngles = new Vector3f(0.0f, 0.0f, 0.0f);
        this.textureCoord = new TextureCoord();
        this.alpha = 1.0f;
        this.light = 1.0f;
    }

    public AbstractParticle(Vector3f newPosition, Vector2f halfSizes, Vector3f localRotateAngles, int maxLifeTime) {
        this.newPosition = newPosition;
        this.oldPosition = new Vector3f(newPosition);
        this.halfSizes = halfSizes;
        this.localRotateAngles = localRotateAngles;
        this.maxLifeTime = maxLifeTime;

        this.lifeTime = 0;
        this.textureCoord = new TextureCoord();
        this.alpha = 1.0f;
        this.light = 1.0f;
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

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public void setTextureCoord(TextureCoord textureCoord) {
        this.textureCoord = textureCoord;
    }

    public void setTextureLocation(ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
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
    public float getAlpha() {
        return alpha;
    }

    @Override
    public float getLight() {
        return light;
    }

    @Override
    public TextureCoord getTextureCoord() {
        return textureCoord;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    @Override
    public boolean isDie() {
        return getLifeTime() >= getMaxLifeTime();
    }


    public abstract Axis rotateAxis();

    public abstract void update();

}
