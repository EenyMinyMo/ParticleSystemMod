package ru.somber.particlesystem.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ParticleAtlasSprite extends TextureAtlasSprite {

    public ParticleAtlasSprite(String iconName) {
        super(iconName);
    }

    @Override
    public void initSprite(int width, int height, int originX, int originY, boolean rotated) {
        super.initSprite(width, height, originX, originY, rotated);
    }

    public void copyFrom(TextureAtlasSprite fromSprite) {
        super.copyFrom(fromSprite);
    }

    public float getInterpolatedU(double interpolationFactor) {
        return super.getInterpolatedU(interpolationFactor);
    }

    public float getInterpolatedV(double interpolationFactor) {
        return super.getInterpolatedV(interpolationFactor);
    }

}
