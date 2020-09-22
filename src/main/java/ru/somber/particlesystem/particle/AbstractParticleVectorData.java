package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import ru.somber.clientutil.textureatlas.icon.AtlasIcon;
import ru.somber.commonutil.Axis;
import ru.somber.particlesystem.texture.ParticleAtlasIcon;

/**
 * Абстрактный класс частицы.
 * Данные частицы хранятся в объектах-векторах.
 */
public abstract class AbstractParticleVectorData extends AbstractParticle {

    /** Новая позиция частицы. */
    private Vector3f position;
    private Vector3f oldPosition;

    /** Половина размера частицы (x = width/2, y = height/2). */
    private Vector2f halfSizes;
    private Vector2f oldHalfSizes;

    /**
     * Вектор, содержащий локальные углы поворота частицы
     * (эти углы должны применятся после мировых преобразований частицы).
     */
    private Vector3f rotateAngles;
    private Vector3f oldRotateAngles;

    /** Коэффициенты цветов. */
    private Vector4f colorFactor;


    public AbstractParticleVectorData(Vector3f position, int maxLifeTime, AtlasIcon iconName) {
        super(maxLifeTime, iconName);

        this.position = position;
        this.oldPosition = new Vector3f(this.position);

        this.halfSizes = new Vector2f(0.5F, 0.5F);
        this.oldHalfSizes = new Vector2f(this.halfSizes);

        this.rotateAngles = new Vector3f(0, 0, 0);
        this.oldRotateAngles = new Vector3f(this.rotateAngles);

        this.colorFactor = new Vector4f(1, 1, 1, 1);
    }


    @Override
    public void getPosition(Vector3f dest) {
        dest.set(position);
    }

    @Override
    public void getOldPosition(Vector3f dest) {
        dest.set(oldPosition);
    }


    @Override
    public float getPositionX() {
        return position.getX();
    }

    @Override
    public float getPositionY() {
        return position.getY();
    }

    @Override
    public float getPositionZ() {
        return position.getZ();
    }

    @Override
    public float getOldPositionX() {
        return oldPosition.getX();
    }

    @Override
    public float getOldPositionY() {
        return oldPosition.getY();
    }

    @Override
    public float getOldPositionZ() {
        return oldPosition.getZ();
    }


    @Override
    public void getHalfSizes(Vector2f dest) {
        dest.set(halfSizes);
    }

    @Override
    public void getOldHalfSizes(Vector2f dest) {
        dest.set(oldHalfSizes);
    }


    @Override
    public float getHalfWidth() {
        return halfSizes.getX();
    }

    @Override
    public float getHalfHeight() {
        return halfSizes.getY();
    }

    @Override
    public float getOldHalfWidth() {
        return oldHalfSizes.getX();
    }

    @Override
    public float getOldHalfHeight() {
        return oldHalfSizes.getY();
    }


    @Override
    public void getRotateAngles(Vector3f dest) {
        dest.set(rotateAngles);
    }

    @Override
    public void getOldRotateAngles(Vector3f dest) {
        dest.set(oldRotateAngles);
    }


    @Override
    public float getAngleX() {
        return rotateAngles.getX();
    }

    @Override
    public float getAngleY() {
        return rotateAngles.getY();
    }

    @Override
    public float getAngleZ() {
        return rotateAngles.getZ();
    }

    @Override
    public float getOldAngleX() {
        return oldRotateAngles.getX();
    }

    @Override
    public float getOldAngleY() {
        return oldRotateAngles.getY();
    }

    @Override
    public float getOldAngleZ() {
        return oldRotateAngles.getZ();
    }


    @Override
    public void getColorFactor(Vector4f dest) {
        dest.set(colorFactor);
    }

    @Override
    public float getRedFactor() {
        return colorFactor.getX();
    }

    @Override
    public float getGreenFactor() {
        return colorFactor.getY();
    }

    @Override
    public float getBlueFactor() {
        return colorFactor.getZ();
    }

    @Override
    public float getAlphaFactor() {
        return colorFactor.getW();
    }


    @Override
    public void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, float interpolateFactor) {
        Vector3f interpolatePosition = new Vector3f();
        computeInterpolatedPosition(interpolatePosition, interpolateFactor);

        computeNormalVector(destination, xCamera, yCamera, zCamera, interpolatePosition);
    }


    @Override
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    @Override
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    @Override
    public void setOldPosition(float x, float y, float z) {
        this.oldPosition.set(x, y, z);
    }

    @Override
    public void setOldPosition(Vector3f oldPosition) {
        this.oldPosition = oldPosition;
    }

    @Override
    public void setPositionX(float x) {
        position.setX(x);
    }

    @Override
    public void setPositionY(float y) {
        position.setY(y);
    }

    @Override
    public void setPositionZ(float z) {
        position.setZ(z);
    }

    @Override
    public void setOldPositionX(float x) {
        oldPosition.setX(x);
    }

    @Override
    public void setOldPositionY(float y) {
        oldPosition.setY(y);
    }

    @Override
    public void setOldPositionZ(float z) {
        oldPosition.setZ(z);
    }


    @Override
    public void setHalfSizes(float halfWidth, float halfHeight) {
        this.halfSizes.set(halfWidth, halfHeight);
    }

    @Override
    public void setHalfSizes(Vector2f halfSizes) {
        this.halfSizes = halfSizes;
    }

    @Override
    public void setOldHalfSizes(float halfWidth, float halfHeight) {
        this.oldHalfSizes.set(halfWidth, halfHeight);
    }

    @Override
    public void setOldHalfSizes(Vector2f oldHalfSizes) {
        this.oldHalfSizes = oldHalfSizes;
    }

    @Override
    public void setHalfWidth(float halfWidth) {
        halfSizes.setX(halfWidth);
    }

    @Override
    public void setHalfHeight(float halfHeight) {
        halfSizes.setY(halfHeight);
    }

    @Override
    public void setOldHalfWidth(float halfWidth) {
        oldHalfSizes.setX(halfWidth);
    }

    @Override
    public void setOldHalfHeight(float halfHeight) {
        oldHalfSizes.setY(halfHeight);
    }


    @Override
    public void setRotateAngles(float x, float y, float z) {
        this.rotateAngles.set(x, y, z);
    }

    @Override
    public void setRotateAngles(Vector3f rotateAngles) {
        this.rotateAngles = rotateAngles;
    }

    @Override
    public void setOldRotateAngles(float x, float y, float z) {
        this.oldRotateAngles.set(x, y, z);
    }

    @Override
    public void setOldRotateAngles(Vector3f oldRotateAngles) {
        this.oldRotateAngles = oldRotateAngles;
    }

    @Override
    public void setRotateAnglesX(float x) {
        rotateAngles.setX(x);
    }

    @Override
    public void setRotateAnglesY(float y) {
        rotateAngles.setY(y);
    }

    @Override
    public void setRotateAnglesZ(float z) {
        rotateAngles.setZ(z);
    }

    @Override
    public void setOldRotateAnglesX(float x) {
        oldRotateAngles.setX(x);
    }

    @Override
    public void setOldRotateAnglesY(float y) {
        oldRotateAngles.setY(y);
    }

    @Override
    public void setOldRotateAnglesZ(float z) {
        oldRotateAngles.setZ(z);
    }


    @Override
    public void setColorFactor(float r, float g, float b, float a) {
        this.colorFactor.set(r, g, b, a);
    }

    @Override
    public void setColorFactor(Vector4f colorFactor) {
        this.colorFactor = colorFactor;
    }

    @Override
    public void setRedFactor(float r) {
        this.colorFactor.setX(r);
    }

    @Override
    public void setGreenFactor(float g) {
        this.colorFactor.setY(g);
    }

    @Override
    public void setBlueFactor(float b) {
        this.colorFactor.setZ(b);
    }

    @Override
    public void setAlphaFactor(float a) {
        this.colorFactor.setW(a);
    }


    @Override
    public void update() {
        super.update();
    }



    public abstract Axis rotateAxis();

    public abstract void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f interpolatePosition);

}
