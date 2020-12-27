package ru.somber.particlesystem.particle;

import ru.somber.util.clientutil.textureatlas.icon.AtlasIcon;

/**
 * Интерфейс для частиц, данные которых в открытую доступны для модификации.
 * Сеттеры этого интерфейса аналогичны геттерам интефейса IParticle.
 */
public interface IModifiableParticle extends IParticle {

    void setPosition(float x, float y, float z);

    void setOldPosition(float x, float y, float z);

    void setPositionX(float x);

    void setPositionY(float y);

    void setPositionZ(float z);

    void setOldPositionX(float x);

    void setOldPositionY(float y);

    void setOldPositionZ(float z);

    void addToPosition(float x, float y, float z);

    void addToOldPosition(float x, float y, float z);


    void setHalfSizes(float halfWidth, float halfHeight);

    void setOldHalfSizes(float halfWidth, float halfHeight);

    void setHalfWidth(float halfWidth);

    void setHalfHeight(float halfHeight);

    void setOldHalfWidth(float halfWidth);

    void setOldHalfHeight(float halfHeight);

    void addToHalfSizes(float halfWidth, float halfHeight);

    void addToOldHalfSizes(float halfWidth, float halfHeight);


    void setRotateAngles(float x, float y, float z);

    void setOldRotateAngles(float x, float y, float z);

    void setRotateAnglesX(float x);

    void setRotateAnglesY(float y);

    void setRotateAnglesZ(float z);

    void setOldRotateAnglesX(float x);

    void setOldRotateAnglesY(float y);

    void setOldRotateAnglesZ(float z);

    void addToRotateAngles(float x, float y, float z);

    void addToOldRotateAngles(float x, float y, float z);


    void setNormalVector(float x, float y, float z);

    void setOldNormalVector(float x, float y, float z);

    void setNormalVectorX(float x);

    void setNormalVectorY(float y);

    void setNormalVectorZ(float z);

    void setOldNormalVectorX(float x);

    void setOldNormalVectorY(float y);

    void setOldNormalVectorZ(float z);


    void setColorFactor(float r, float g, float b, float a);

    void setRedFactor(float r);

    void setGreenFactor(float g);

    void setBlueFactor(float b);

    void setAlphaFactor(float a);

    void setLightFactor(float lightFactor);

    void setBlendFactor(float blendFactor);


    void setParticleIcon(AtlasIcon particleIcon);


    void setLifeTime(int lifeTime);

}
