package ru.somber.particlesystem.particle;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import ru.somber.clientutil.textureatlas.icon.AtlasIcon;
import ru.somber.commonutil.Axis;

/**
 * Абстрактный класс частицы.
 * Данные частицы хранятся в переменных-примитивах.
 */
public abstract class AbstractParticleSimpleData extends AbstractParticle {

    /** Новая позиция частицы. */
    private float x, y, z;
    private float xOld, yOld, zOld;

    /** Половина размера частицы (halfWidth = width/2, halfHeight = height/2). */
    private float halfWidth, halfHeight;
    private float halfWidthOld, halfHeightOld;

    /**
     * Вектор, содержащий локальные углы поворота частицы
     * (эти углы должны применятся после мировых преобразований частицы).
     */
    private float xAngle, yAngle, zAngle;
    private float xAngleOld, yAngleOld, zAngleOld;

    /** Коэффициенты цветов. */
    private float r, g, b, a;


    public AbstractParticleSimpleData(float x, float y, float z, int maxLifeTime, AtlasIcon particleIcon) {
        super(maxLifeTime, particleIcon);

        this.x = x;
        this.y = y;
        this.z = z;

        this.xOld = this.x;
        this.yOld = this.y;
        this.zOld = this.z;

        this.halfWidth = 0.5F;
        this.halfHeight = 0.5F;
        this.halfWidthOld = this.halfWidth;
        this.halfHeightOld = this.halfHeight;

        this.xAngle = 0;
        this.yAngle = 0;
        this.zAngle = 0;
        this.xAngleOld = this.xAngle;
        this.yAngleOld = this.yAngle;
        this.zAngleOld = this.zAngle;

        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
    }


    @Override
    public void getPosition(Vector3f dest) {
        dest.set(x, y, z);
    }

    @Override
    public void getOldPosition(Vector3f dest) {
        dest.set(xOld, yOld, zOld);
    }


    @Override
    public float getPositionX() {
        return this.x;
    }

    @Override
    public float getPositionY() {
        return this.y;
    }

    @Override
    public float getPositionZ() {
        return this.z;
    }

    @Override
    public float getOldPositionX() {
        return this.xOld;
    }

    @Override
    public float getOldPositionY() {
        return this.yOld;
    }

    @Override
    public float getOldPositionZ() {
        return this.zOld;
    }


    @Override
    public void getHalfSizes(Vector2f dest) {
        dest.set(halfWidth, halfHeight);
    }

    @Override
    public void getOldHalfSizes(Vector2f dest) {
        dest.set(halfWidthOld, halfHeightOld);
    }


    @Override
    public float getHalfWidth() {
        return halfWidth;
    }

    @Override
    public float getHalfHeight() {
        return halfHeight;
    }

    @Override
    public float getOldHalfWidth() {
        return halfWidthOld;
    }

    @Override
    public float getOldHalfHeight() {
        return halfHeightOld;
    }


    @Override
    public void getRotateAngles(Vector3f dest) {
        dest.set(xAngle, yAngle, zAngle);
    }

    @Override
    public void getOldRotateAngles(Vector3f dest) {
        dest.set(xAngleOld, yAngleOld, zAngleOld);
    }


    @Override
    public float getAngleX() {
        return xAngle;
    }

    @Override
    public float getAngleY() {
        return yAngle;
    }

    @Override
    public float getAngleZ() {
        return zAngle;
    }

    @Override
    public float getOldAngleX() {
        return xAngleOld;
    }

    @Override
    public float getOldAngleY() {
        return yAngleOld;
    }

    @Override
    public float getOldAngleZ() {
        return zAngleOld;
    }


    @Override
    public void getColorFactor(Vector4f dest) {
        dest.set(r, g, b, a);
    }

    @Override
    public float getRedFactor() {
        return r;
    }

    @Override
    public float getGreenFactor() {
        return g;
    }

    @Override
    public float getBlueFactor() {
        return b;
    }

    @Override
    public float getAlphaFactor() {
        return a;
    }


    @Override
    public void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, float interpolateFactor) {
        Vector3f interpolatePosition = new Vector3f();
        computeInterpolatedPosition(interpolatePosition, interpolateFactor);

        computeNormalVector(destination, xCamera, yCamera, zCamera, interpolatePosition);
    }


    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void setPosition(Vector3f position) {
        this.x = position.getX();
        this.y = position.getY();
        this.z = position.getZ();
    }

    @Override
    public void setOldPosition(float x, float y, float z) {
        this.xOld = x;
        this.yOld = y;
        this.zOld = z;
    }

    @Override
    public void setOldPosition(Vector3f oldPosition) {
        this.xOld = oldPosition.getX();
        this.yOld = oldPosition.getY();
        this.zOld = oldPosition.getZ();
    }

    @Override
    public void setPositionX(float x) {
        this.x = x;
    }

    @Override
    public void setPositionY(float y) {
        this.y = y;
    }

    @Override
    public void setPositionZ(float z) {
       this.z = z;
    }

    @Override
    public void setOldPositionX(float x) {
        this.xOld = x;
    }

    @Override
    public void setOldPositionY(float y) {
        this.yOld = y;
    }

    @Override
    public void setOldPositionZ(float z) {
        this.zOld = z;
    }


    @Override
    public void setHalfSizes(float halfWidth, float halfHeight) {
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }

    @Override
    public void setHalfSizes(Vector2f halfSizes) {
        this.halfWidth = halfSizes.getX();
        this.halfHeight = halfSizes.getY();
    }

    @Override
    public void setOldHalfSizes(float halfWidth, float halfHeight) {
        this.halfWidthOld = halfWidth;
        this.halfHeightOld = halfHeight;
    }

    @Override
    public void setOldHalfSizes(Vector2f oldHalfSizes) {
        this.halfWidthOld = oldHalfSizes.getX();
        this.halfHeightOld = oldHalfSizes.getY();
    }

    @Override
    public void setHalfWidth(float halfWidth) {
        this.halfWidth = halfWidth;
    }

    @Override
    public void setHalfHeight(float halfHeight) {
        this.halfHeight = halfHeight;
    }

    @Override
    public void setOldHalfWidth(float halfWidth) {
        this.halfWidthOld = halfWidth;
    }

    @Override
    public void setOldHalfHeight(float halfHeight) {
        this.halfHeightOld = halfHeight;
    }


    @Override
    public void setRotateAngles(float xAngle, float yAngle, float zAngle) {
        this.xAngle = xAngle;
        this.yAngle = yAngle;
        this.zAngle = zAngle;
    }

    @Override
    public void setRotateAngles(Vector3f rotateAngles) {
        this.xAngle = rotateAngles.getX();
        this.yAngle = rotateAngles.getY();
        this.zAngle = rotateAngles.getZ();
    }

    @Override
    public void setOldRotateAngles(float x, float y, float z) {
        this.xAngleOld = x;
        this.yAngleOld = y;
        this.zAngleOld = z;
    }

    @Override
    public void setOldRotateAngles(Vector3f oldRotateAngles) {
        this.xAngle = oldRotateAngles.getX();
        this.yAngle = oldRotateAngles.getY();
        this.zAngle = oldRotateAngles.getZ();
    }

    @Override
    public void setRotateAnglesX(float x) {
        this.xAngle = x;
    }

    @Override
    public void setRotateAnglesY(float y) {
        this.yAngle = y;
    }

    @Override
    public void setRotateAnglesZ(float z) {
        this.zAngle = z;
    }

    @Override
    public void setOldRotateAnglesX(float x) {
        this.xAngleOld = x;
    }

    @Override
    public void setOldRotateAnglesY(float y) {
        this.yAngleOld = y;
    }

    @Override
    public void setOldRotateAnglesZ(float z) {
        this.zAngleOld = z;
    }


    @Override
    public void setColorFactor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public void setColorFactor(Vector4f colorFactor) {
        this.r = colorFactor.getX();
        this.g = colorFactor.getY();
        this.b = colorFactor.getZ();
        this.a = colorFactor.getW();
    }

    @Override
    public void setRedFactor(float r) {
        this.r = r;
    }

    @Override
    public void setGreenFactor(float g) {
        this.g = g;
    }

    @Override
    public void setBlueFactor(float b) {
        this.b = b;
    }

    @Override
    public void setAlphaFactor(float a) {
        this.a = a;
    }


    @Override
    public void update() {
        super.update();
    }



    public abstract Axis rotateAxis();

    public abstract void computeNormalVector(Vector3f destination, float xCamera, float yCamera, float zCamera, Vector3f interpolatePosition);

}
