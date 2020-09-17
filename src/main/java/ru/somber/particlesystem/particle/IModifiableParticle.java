package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import ru.somber.particlesystem.texture.ParticleAtlasIcon;

/**
 * Интерфейс для частиц, данные которых в открытую доступны для модификации.
 * Сеттеры этого интерфейса аналогичны геттерам интефейса IParticle.
 */
public interface IModifiableParticle extends IParticle {

    void setLifeTime(int lifeTime);

    void setPosition(float x, float y, float z);

    void setPosition(Vector3f position);

    void setOldPosition(float x, float y, float z);

    void setOldPosition(Vector3f oldPosition);

    void setPositionX(float x);

    void setPositionY(float y);

    void setPositionZ(float z);

    void setOldPositionX(float x);

    void setOldPositionY(float y);

    void setOldPositionZ(float z);


    void setHalfSizes(float halfWidth, float halfHeight);

    void setHalfSizes(Vector2f halfSizes);

    void setOldHalfSizes(float halfWidth, float halfHeight);

    void setOldHalfSizes(Vector2f oldHalfSizes);

    void setHalfWidth(float halfWidth);

    void setHalfHeight(float halfHeight);

    void setOldHalfWidth(float halfWidth);

    void setOldHalfHeight(float halfHeight);


    void setRotateAngles(float x, float y, float z);

    void setRotateAngles(Vector3f rotateAngles);

    void setOldRotateAngles(float x, float y, float z);

    void setOldRotateAngles(Vector3f oldRotateAngles);

    void setRotateAnglesX(float x);

    void setRotateAnglesY(float y);

    void setRotateAnglesZ(float z);

    void setOldRotateAnglesX(float x);

    void setOldRotateAnglesY(float y);

    void setOldRotateAnglesZ(float z);


    void setColorFactor(float r, float g, float b, float a);

    void setColorFactor(Vector4f colorFactor);

    void setRedFactor(float r);

    void setGreenFactor(float g);

    void setBlueFactor(float b);

    void setAlphaFactor(float a);

    void setLightFactor(float lightFactor);

    void setBlendFactor(float blendFactor);


    void setParticleIcon(ParticleAtlasIcon particleIcon);

}
