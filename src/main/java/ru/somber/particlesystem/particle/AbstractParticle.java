package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.textureatlas.icon.AtlasIcon;
import ru.somber.commonutil.SomberCommonUtil;

/**
 * Абстрактный класс частицы, реализцющий наиболее общий функционал частиц.
 */
public abstract class AbstractParticle implements IModifiableParticle {

    /** Количество тиков, которое существует частица. */
    private final int maxLifeTime;
    /** Количество тиков, которое частица уже существует. */
    private int lifeTime;

    /** Коэффициент освещенности частицы. */
    private float lightFactor;
    /** Коэффициент смешивания частицы. */
    private float blendFactor;

    /** Иконка частицы с текстурными координатами. */
    private AtlasIcon particleIcon;

    /** Флаг для определени жива ли частица. */
    private boolean isDie;

    public AbstractParticle(int maxLifeTime, AtlasIcon particleIcon) {
        this.maxLifeTime = maxLifeTime;
        this.particleIcon = particleIcon;

        this.lightFactor = 1F;
        this.blendFactor = 0F;

        this.lifeTime = 1;
        this.isDie = false;
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
    public float getLightFactor() {
        return lightFactor;
    }

    @Override
    public float getBlendFactor() {
        return blendFactor;
    }


    @Override
    public AtlasIcon getParticleIcon() {
        return particleIcon;
    }


    @Override
    public boolean isDie() {
        return isDie;
    }

    @Override
    public void setDie(boolean die) {
        this.isDie = die;
    }


    @Override
    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    @Override
    public void setLightFactor(float lightFactor) {
        this.lightFactor = lightFactor;
    }

    @Override
    public void setBlendFactor(float blendFactor) {
        this.blendFactor = blendFactor;
    }

    @Override
    public void setParticleIcon(AtlasIcon particleIcon) {
        this.particleIcon = particleIcon;
    }


    @Override
    public void update() {
        setOldPosition(getPositionX(), getPositionY(), getPositionZ());
        setOldHalfSizes(getHalfWidth(), getHalfHeight());
        setOldRotateAngles(getAngleX(), getAngleY(), getAngleZ());

        lifeTime++;
        if (getLifeTime() >= getMaxLifeTime()) {
            setDie(true);
        }
    }


    /**
     * Вычисляет вектор нормали для частицы сферического типа (нормаль вращается по всем осям).
     *
     * @param destination вектор, куда запишутся данные нормали.
     * @param lookAtX позиция X точки, куда должна быть направлена нормаль частицы.
     * @param lookAtY позиция Y точки, куда должна быть направлена нормаль частицы.
     * @param lookAtZ позиция Z точки, куда должна быть направлена нормаль частицы.
     * @param interpolatePosition вектор с интерполированной позицией частицы.
     */
    protected final void computeNormalVectorSphericalParticle(Vector3f destination, float lookAtX, float lookAtY, float lookAtZ, Vector3f interpolatePosition) {
        destination.x = interpolatePosition.x - lookAtX;
        destination.y = interpolatePosition.y - lookAtY;
        destination.z = interpolatePosition.z - lookAtZ;
    }

    /**
     * Вычисляет вектор нормали для частицы сферического типа (нормаль вращается по всем осям).
     *
     * @param destination вектор, куда запишутся данные нормали.
     * @param lookAtX позиция X точки, куда должна быть направлена нормаль частицы.
     * @param lookAtY позиция Y точки, куда должна быть направлена нормаль частицы.
     * @param lookAtZ позиция Z точки, куда должна быть направлена нормаль частицы.
     * @param interpolationFactor коэффициент интерполяции между старой и новой позициями частицы.
     */
    protected final void computeNormalVectorSphericalParticle(Vector3f destination, float lookAtX, float lookAtY, float lookAtZ, float interpolationFactor) {
        float interpolateX = SomberCommonUtil.interpolateBetween(getOldPositionX(), getPositionX(), interpolationFactor);
        float interpolateY = SomberCommonUtil.interpolateBetween(getOldPositionY(), getPositionY(), interpolationFactor);
        float interpolateZ = SomberCommonUtil.interpolateBetween(getOldPositionZ(), getPositionZ(), interpolationFactor);

        destination.x = interpolateX - lookAtX;
        destination.y = interpolateY - lookAtY;
        destination.z = interpolateZ - lookAtZ;
    }

    /**
     * Вычисляет вектор нормали для частицы цилиндрического типа (нормаль вращается по оси Y).
     *
     * @param destination вектор, куда запишутся данные нормали.
     * @param lookAtX позиция X точки, куда должна быть направлена нормаль частицы.
     * @param lookAtZ позиция Z точки, куда должна быть направлена нормаль частицы.
     * @param interpolatePosition вектор с интерполированной позицией частицы.
     */
    protected final void computeNormalVectorCylindricalParticle(Vector3f destination, float lookAtX, float lookAtZ, Vector3f interpolatePosition) {
        destination.x = interpolatePosition.x - lookAtX;
        destination.y = 0;
        destination.z = interpolatePosition.z - lookAtZ;
    }

    /**
     * Вычисляет вектор нормали для частицы цилиндрического типа (нормаль вращается по оси Y).
     *
     * @param destination вектор, куда запишутся данные нормали.
     * @param lookAtX позиция X точки, куда должна быть направлена нормаль частицы.
     * @param lookAtZ позиция Z точки, куда должна быть направлена нормаль частицы.
     * @param interpolationFactor коэффициент интерполяции между старой и новой позициями частицы.
     */
    protected final void computeNormalVectorCylindricalParticle(Vector3f destination, float lookAtX, float lookAtZ, float interpolationFactor) {
        float interpolateX = SomberCommonUtil.interpolateBetween(getOldPositionX(), getPositionX(), interpolationFactor);
        float interpolateZ = SomberCommonUtil.interpolateBetween(getOldPositionZ(), getPositionZ(), interpolationFactor);

        destination.x = interpolateX - lookAtX;
        destination.y = 0;
        destination.z = interpolateZ - lookAtZ;
    }

    /**
     * Вычисляет вектор нормали для частицы статического типа (нормаль не вращается по осям).
     *
     * @param destination вектор, куда запишутся данные нормали.
     */
    protected final void computeNormalVectorStaticParticle(Vector3f destination) {
        destination.x = 0;
        destination.y = 0;
        destination.z = 1;
    }

}
