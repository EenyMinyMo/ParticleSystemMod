package ru.somber.particlesystem.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ParticleAtlasSprite extends TextureAtlasSprite {

    private boolean isInverted;

    public ParticleAtlasSprite(String iconName) {
        super(iconName);

        this.isInverted = false;
    }

    public ParticleAtlasSprite(String iconName, boolean isInverted) {
        super(iconName);

        this.isInverted = isInverted;
    }


    /**
     * @param width - ширина в пикселях
     * @param height - высота в пикселях
     * @param originX - началная позиция по оси Х в пикселях (типо xMin, но в пискелях)
     * @param originY - началная позиция по оси У в пикселях (типо уMin, но в пискелях)
     * @param rotated - нужно ли повенуть текстуру
     */
    @Override
    public void initSprite(int width, int height, int originX, int originY, boolean rotated) {
        super.initSprite(width, height, originX, originY, rotated);
    }

    @Override
    public void copyFrom(TextureAtlasSprite fromSprite) {
        super.copyFrom(fromSprite);
    }

    @Override
    public float getMinU() {
        return super.getMinU();
    }

    @Override
    public float getMaxU() {
        return super.getMaxU();
    }

    @Override
    public float getMinV() {
        if (isInverted) {
            return super.getMaxV();
        } else {
            return super.getMinV();
        }
    }

    @Override
    public float getMaxV() {
        if (isInverted) {
            return super.getMinV();
        } else {
            return super.getMaxV();
        }
    }

    @Override
    public float getInterpolatedU(double interpolatedFactor) {
        return getMinU() + (getMaxU() - getMinU()) * ((float) interpolatedFactor / 16.0F);
    }

    @Override
    public float getInterpolatedV(double interpolatedFactor) {
        return getMinV() + (getMaxV() - getMinV()) * ((float) interpolatedFactor / 16.0F);
    }

    @Override
    public void updateAnimation() {}

    public boolean isAnimatedSprite() {
        return false;
    }

    public boolean isInverted() {
        return isInverted;
    }

    public void setInverted(boolean inverted) {
        isInverted = inverted;
    }

    public float getOriginalMinV() {
        return super.getMinV();
    }

    public float getOriginalMaxV() {
        return super.getMaxV();
    }

}
