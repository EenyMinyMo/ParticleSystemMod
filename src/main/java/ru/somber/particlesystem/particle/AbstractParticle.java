package ru.somber.particlesystem.particle;

import ru.somber.particlesystem.texture.ParticleAtlasIcon;

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
    private ParticleAtlasIcon particleIcon;

    /** Флаг для определени жива ли частица. */
    private boolean isDie;

    public AbstractParticle(int maxLifeTime, ParticleAtlasIcon particleIcon) {
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
    public ParticleAtlasIcon getParticleIcon() {
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
    public void setParticleIcon(ParticleAtlasIcon particleIcon) {
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

}
