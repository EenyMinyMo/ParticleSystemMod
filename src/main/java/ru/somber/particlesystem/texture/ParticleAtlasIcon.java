package ru.somber.particlesystem.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Класс для представления иконок частиц. Включена поддержка инвертированных по оси Y текстур.
 * <p>
 * Для использования прописывать имя чатиц следующим образом:"MOD_ID + ":название_файла_частицы"".
 * В качестве названия файла указывать только само название файла! Папки до файла не нужно!
 */
@SideOnly(Side.CLIENT)
public class ParticleAtlasIcon extends TextureAtlasSprite {

    /**
     * Если true, то текстурные координаты по оси Y будут браться в обратном режиме.
     * Т.е. низ текстуры будет интерпритироваться как верх и наоборот.
     * Если false, то текстурные координаты берутся в стандартном режиме.
     */
    private boolean isInvertedY;


    /**
     * @param iconName имя для иконки формировать следующим образом: "MOD_ID + ":название_файла_частицы"".
     *                 В качестве названия файла указывать только само название файла! Папки до файла не нужно!
     */
    public ParticleAtlasIcon(String iconName) {
        super(iconName);

        this.isInvertedY = false;
    }

    /**
     * @param iconName имя для иконки формировать следующим образом: "MOD_ID + ":название_файла_частицы"".
     *                 В качестве названия файла указывать только само название файла! Папки до файла не нужно!
     *
     * @param isInvertedY нужно ли делать текстурные координаты по оси Y инвертированными (нижние координаты будут верхними и наоборот).
     *                    Использовать true, если иконка для текстуры, загруженной через майнкрафт.
     */
    public ParticleAtlasIcon(String iconName, boolean isInvertedY) {
        super(iconName);

        this.isInvertedY = isInvertedY;
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
        if (isInvertedY) {
            return super.getMaxV();
        } else {
            return super.getMinV();
        }
    }

    @Override
    public float getMaxV() {
        if (isInvertedY) {
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

    /**
     * true, если текстура имеет анимацию.
     * Не использовать отнаследованный от майнкрафтовского класса метод, исплользовать этот!
     * Код анимации прописывать в методе updateAnimation().
     */
    public boolean isAnimatedIcon() {
        return false;
    }

    /**
     * Вазвращает true, если нужно ли делать текстурные координаты по оси Y инвертированными
     * (нижние координаты будут верхними и наоборот).
     */
    public boolean isInvertedY() {
        return isInvertedY;
    }

    /**
     * @param invertedY нужно ли делать текстурные координаты по оси Y инвертированными (нижние координаты будут верхними и наоборот).
     * Использовать true, если иконка для текстуры, загруженной через майнкрафт (как пример).
     */
    public void setInvertedY(boolean invertedY) {
        isInvertedY = invertedY;
    }

    /**
     * Возвращает оригинальное значение нижней текстурной координаты. (не зависит от значения isInvertedY)
     */
    public float getOriginalMinV() {
        return super.getMinV();
    }

    /**
     * Возвращает оригинальное значение верхней текстурной координаты. (не зависит от значения isInvertedY)
     */
    public float getOriginalMaxV() {
        return super.getMaxV();
    }

}
