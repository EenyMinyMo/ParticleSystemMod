package ru.somber.particlesystem.texture;

public class ParticleAtlasAnimatedSprite extends ParticleAtlasSprite {

    private int countFrameColumn;
    private int countFrameRow;
    private int currentAnimationFrame;

    private float animatedMinU;
    private float animatedMaxU;
    private float animatedMinV;
    private float animatedMaxV;
    private float invertedAnimatedMinV;
    private float invertedAnimatedMaxV;

    public ParticleAtlasAnimatedSprite(String iconName, int countFrameColumn, int countFrameRow, boolean isInverted) {
        super(iconName, isInverted);

        this.countFrameColumn = countFrameColumn;
        this.countFrameRow = countFrameRow;
        this.currentAnimationFrame = 0;

        updateUVCoord();
    }


    @Override
    public float getMinU() {
        return animatedMinU;
    }

    @Override
    public float getMaxU() {
        return animatedMaxU;
    }

    @Override
    public float getMinV() {
        if (isInverted()) {
            return invertedAnimatedMinV;
        } else {
            return animatedMinV;
        }
    }

    @Override
    public float getMaxV() {
        if (isInverted()) {
            return invertedAnimatedMaxV;
        } else {
            return animatedMaxV;
        }
    }

    @Override
    public boolean isAnimatedSprite() {
        return true;
    }

    @Override
    public void updateAnimation() {
        currentAnimationFrame++;
        currentAnimationFrame %= countFrameColumn * countFrameRow;

        updateUVCoord();
    }


    public int getCountFrameColumn() {
        return countFrameColumn;
    }

    public int getCountFrameRow() {
        return countFrameRow;
    }

    public int getCurrentAnimationFrame() {
        return currentAnimationFrame;
    }

    public void setCurrentAnimationFrame(int currentAnimationFrame) {
        this.currentAnimationFrame = currentAnimationFrame;
        this.currentAnimationFrame %= countFrameColumn * countFrameRow;
    }

    public void updateUVCoord() {
        int currentFrameColumn = currentAnimationFrame % countFrameColumn;
        int currentFrameRow = currentAnimationFrame / countFrameColumn;

        animatedMinU = super.getMinU() + (currentFrameColumn + 0.0F) / countFrameColumn * (super.getMaxU() - super.getMinU());
        animatedMaxU = super.getMinU() + (currentFrameColumn + 1.0F) / countFrameColumn * (super.getMaxU() - super.getMinU());

        animatedMinV = super.getOriginalMinV() + (currentFrameRow + 0.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
        animatedMaxV = super.getOriginalMinV() + (currentFrameRow + 1.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());

        invertedAnimatedMinV = animatedMaxV;
        invertedAnimatedMaxV = animatedMinV;
    }

}
