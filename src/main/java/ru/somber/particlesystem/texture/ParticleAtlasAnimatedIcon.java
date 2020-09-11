package ru.somber.particlesystem.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Класс для представления анимированных иконок.
 * Правила прописывания имени иконки аналогичные ParticleAtlasIcon.
 *
 * <p> Для деления текстуры с анимацией на отдельные кадры используются следующие поля:
 * <p> countFrameColumn - количество колонок с кадрами.
 * <p> countFrameRow - количество рядов с кадроми.
 * <p> Общее количество кадров высчитывается так: countFrameColumn * countFrameRow.
 * <p> currentAnimationFrame - текущий кадр анимации. Значение этого поля находятся в пределах от 0 до (countFrameColumn * countFrameRow - 1)
 *
 * <p> Процесс выборки данных из анимированного атласа происходит следующим образом:
 * Кадры берутся в порядке слева направо, сверху вниз. Т.е. Левый верхний кадр - нулевой, нижний правый кадр - последний.
 */
@SideOnly(Side.CLIENT)
public class ParticleAtlasAnimatedIcon extends ParticleAtlasIcon {

    /** Количество колонок фреймов. */
    private final int countFrameColumn;
    /** Количество строк фреймов. */
    private final int countFrameRow;
    private final int countFrames;

    /** Номер текущего используемого фрейма. На основе этого номера вычисляются текстурные координаты. */
    private int currentAnimationFrame;

//    /**
//     * Координаты текстуры с учетом смещения анимационного фрейма
//     * (координаты в пределах координат анимационной текстуры,
//     * вычисляются на основе значений countFrameColumn, countFrameRow и currentAnimationFrame).
//     */
//    private float animatedMinU,
//            animatedMaxU,
//            animatedMinV,
//            animatedMaxV;

    private float[][] frameCoords;


    /**
     * @param iconName имя для иконки формировать следующим образом: "MOD_ID + ":название_файла_частицы"".
     *                 В качестве названия файла указывать только само название файла! Папки до файла не нужно!
     * @param countFrameColumn количество колонок фреймов.
     * @param countFrameRow количество строк фреймов.
     * @param isInverted нужно ли делать текстурные координаты по оси Y инвертированными (нижние координаты будут верхними и наоборот).
     *                   Использовать true, если иконка для текстуры, загруженной через майнкрафт.
     */
    public ParticleAtlasAnimatedIcon(String iconName, int countFrameColumn, int countFrameRow, boolean isInverted) {
        super(iconName, isInverted);

        this.countFrameColumn = countFrameColumn;
        this.countFrameRow = countFrameRow;
        this.countFrames = countFrameColumn * countFrameRow;

        this.currentAnimationFrame = 0;

        frameCoords = new float[countFrames][4];

//        updateUVCoord();
    }

    @Override
    public void initSprite(int width, int height, int originX, int originY, boolean rotated) {
        super.initSprite(width, height, originX, originY, rotated);

        for (int i = 0; i < countFrames; i++) {
            int currentFrameColumn = i % countFrameColumn;
            int currentFrameRow = i / countFrameColumn;

            float animatedMinU = super.getMinU() + (currentFrameColumn + 0.0F) / countFrameColumn * (super.getMaxU() - super.getMinU());
            float animatedMaxU = super.getMinU() + (currentFrameColumn + 1.0F) / countFrameColumn * (super.getMaxU() - super.getMinU());

            float animatedMinV = super.getOriginalMinV() + (currentFrameRow + 0.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
            float animatedMaxV = super.getOriginalMinV() + (currentFrameRow + 1.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());

            if (isInvertedY()) {
                animatedMinV = super.getOriginalMinV() + (currentFrameRow + 1.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
                animatedMaxV = super.getOriginalMinV() + (currentFrameRow + 0.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
            }

            frameCoords[i][0] = animatedMinU;
            frameCoords[i][1] = animatedMinV;
            frameCoords[i][2] = animatedMaxU;
            frameCoords[i][3] = animatedMaxV;
        }

    }

    @Override
    public float getMinU() {
        return frameCoords[currentAnimationFrame][0];
    }

    @Override
    public float getMaxU() {
        return frameCoords[currentAnimationFrame][2];
    }

    @Override
    public float getMinV() {
        return frameCoords[currentAnimationFrame][1];
    }

    @Override
    public float getMaxV() {
        return frameCoords[currentAnimationFrame][3];
    }

    @Override
    public float getOriginalMinV() {
        if (isInvertedY()) {
            return getMaxV();
        } else {
            return getMinV();
        }
    }

    @Override
    public float getOriginalMaxV() {
        if (isInvertedY()) {
            return getMinV();
        } else {
            return getMaxV();
        }
    }

    @Override
    public boolean isAnimatedIcon() {
        return true;
    }

    @Override
    public void updateAnimation() {
        currentAnimationFrame++;
        currentAnimationFrame %= countFrameColumn * countFrameRow;

//        updateUVCoord();
    }

    @Override
    public int getFrameCount() {
        return countFrames;
    }

    /**
     * Возвращает количество колонок фреймов.
     */
    public int getCountFrameColumn() {
        return countFrameColumn;
    }

    /**
     * Возвращает количество строк фреймов.
     */
    public int getCountFrameRow() {
        return countFrameRow;
    }

    /**
     * Возвращает номер текущего анимационного фрейма.
     */
    public int getCurrentAnimationFrame() {
        return currentAnimationFrame;
    }

    /**
     * Устанавливает номер текущего анимационного фрейма.
     */
    public void setCurrentAnimationFrame(int currentAnimationFrame) {
        this.currentAnimationFrame = currentAnimationFrame;
        this.currentAnimationFrame %= getFrameCount();
    }

    /**
     * Устанавливает номер текущего анимационного фрейма на основе переданной позиции этого фрейма в текстуре с фреймами.
     * <p> Для понимания: левый верхний кадр - нулевой, нижний правый кадр - последний.
     * <p> 0ая строка, 0ой столбец - 0ой фрейм,
     * <p> 0ая строка, 2ой столбец - 2ой фрейм и тд.
     */
    public void setCurrentAnimationFrame(int newFrameColumn, int newFrameRow) {
        newFrameColumn %= countFrameColumn;
        newFrameRow %= countFrameRow;

        this.currentAnimationFrame = countFrameColumn * newFrameRow + newFrameColumn;
    }

//    /**
//     * Обновляет текстурные координаты на основе текущего номера анимационного фрейма.
//     */
//    public void updateUVCoord() {
//        int currentFrameColumn = currentAnimationFrame % countFrameColumn;
//        int currentFrameRow = currentAnimationFrame / countFrameColumn;
//
//        animatedMinU = super.getMinU() + (currentFrameColumn + 0.0F) / countFrameColumn * (super.getMaxU() - super.getMinU());
//        animatedMaxU = super.getMinU() + (currentFrameColumn + 1.0F) / countFrameColumn * (super.getMaxU() - super.getMinU());
//
//        animatedMinV = super.getOriginalMinV() + (currentFrameRow + 0.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
//        animatedMaxV = super.getOriginalMinV() + (currentFrameRow + 1.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
//
//        if (isInvertedY()) {
//            animatedMinV = super.getOriginalMinV() + (currentFrameRow + 1.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
//            animatedMaxV = super.getOriginalMinV() + (currentFrameRow + 0.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
//        } else {
//            animatedMinV = super.getOriginalMinV() + (currentFrameRow + 0.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
//            animatedMaxV = super.getOriginalMinV() + (currentFrameRow + 1.0F) / countFrameRow * (super.getOriginalMaxV() - super.getOriginalMinV());
//        }
//    }

    /**
     * Возвращает левую текстурную координату текстуры со всеми фреймами.
     */
    public float getAllFrameMinU() {
        return super.getMinU();
    }

    /**
     * Возвращает правую текстурную координату текстуры со всеми фреймами.
     */
    public float getAllFrameMaxU() {
        return super.getMaxU();
    }

    /**
     * Возвращает нижнюю текстурную координату текстуры со всеми фреймами.
     */
    public float getAllFrameMinV() {
        return super.getMinV();
    }

    /**
     * Возвращает верхнюю текстурную координату текстуры со всеми фреймами.
     */
    public float getAllFrameMaxV() {
        return super.getMaxV();
    }

}
