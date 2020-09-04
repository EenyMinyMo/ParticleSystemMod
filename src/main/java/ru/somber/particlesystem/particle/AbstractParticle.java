package ru.somber.particlesystem.particle;

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

    /** Здесь название иконки частицы. */
    private String particleIconName;

    /** Флаг для определени жива ли частица. */
    private boolean isDie;

    public AbstractParticle(int maxLifeTime, String iconName) {
        this.maxLifeTime = maxLifeTime;
        this.particleIconName = iconName;

        this.lightFactor = 1;
        this.blendFactor = 1;

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
    public String getIconName() {
        return particleIconName;
    }


    @Override
    public boolean isDie() {
        return isDie;
    }

    @Override
    public void setDie() {
        this.isDie = true;
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
    public void setParticleIconName(String particleIconName) {
        this.particleIconName = particleIconName;
    }


    @Override
    public void update() {
        setOldPosition(getPositionX(), getPositionY(), getPositionZ());
        setOldHalfSizes(getHalfWidth(), getHalfHeight());
        setOldRotateAngles(getAngleX(), getAngleY(), getAngleZ());

        lifeTime++;
        if (getLifeTime() >= getMaxLifeTime()) {
            setDie();
        }
    }

}
