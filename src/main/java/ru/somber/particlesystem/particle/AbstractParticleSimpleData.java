package ru.somber.particlesystem.particle;

import ru.somber.util.clientutil.textureatlas.icon.AtlasIcon;

/**
 * Абстрактный класс частицы.
 * Данные частицы хранятся в переменных-примитивах.
 */
public abstract class AbstractParticleSimpleData extends AbstractParticle {

    /** Позиция частицы. */
    private float x, y, z;
    private float xOld, yOld, zOld;

    /** Половина размера частицы (halfWidth = width/2, halfHeight = height/2). */
    private float halfWidth, halfHeight;
    private float oldHalfWidth, oldHalfHeight;

    /**
     * Вектор, содержащий локальные углы поворота частицы
     * (эти углы должны применяться после мировых преобразований частицы).
     */
    private float xAngle, yAngle, zAngle;
    private float xOldAngle, yOldAngle, zOldAngle;

    /** Координаты вектора нормали частицы. */
    private float xNormalVector, yNormalVector, zNormalVector;
    private float xOldNormalVector, yOldNormalVector, zOldNormalVector;

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
        this.oldHalfWidth = this.halfWidth;
        this.oldHalfHeight = this.halfHeight;

        this.xAngle = 0;
        this.yAngle = 0;
        this.zAngle = 0;
        this.xOldAngle = this.xAngle;
        this.yOldAngle = this.yAngle;
        this.zOldAngle = this.zAngle;

        this.xNormalVector = 0;
        this.yNormalVector = 0;
        this.zNormalVector = 0;
        this.xOldNormalVector = this.xNormalVector;
        this.yOldNormalVector = this.yNormalVector;
        this.zOldNormalVector = this.zNormalVector;

        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
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
    public float getHalfWidth() {
        return halfWidth;
    }

    @Override
    public float getHalfHeight() {
        return halfHeight;
    }

    @Override
    public float getOldHalfWidth() {
        return oldHalfWidth;
    }

    @Override
    public float getOldHalfHeight() {
        return oldHalfHeight;
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
        return xOldAngle;
    }

    @Override
    public float getOldAngleY() {
        return yOldAngle;
    }

    @Override
    public float getOldAngleZ() {
        return zOldAngle;
    }


    @Override
    public float getNormalVectorX() {
        return xNormalVector;
    }

    @Override
    public float getNormalVectorY() {
        return yNormalVector;
    }

    @Override
    public float getNormalVectorZ() {
        return zNormalVector;
    }

    @Override
    public float getOldNormalVectorX() {
        return xOldNormalVector;
    }

    @Override
    public float getOldNormalVectorY() {
        return yOldNormalVector;
    }

    @Override
    public float getOldNormalVectorZ() {
        return zOldNormalVector;
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
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void setOldPosition(float x, float y, float z) {
        this.xOld = x;
        this.yOld = y;
        this.zOld = z;
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
    public void addToPosition(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @Override
    public void addToOldPosition(float x, float y, float z) {
        this.xOld += x;
        this.yOld += y;
        this.zOld += z;
    }


    @Override
    public void setHalfSizes(float halfWidth, float halfHeight) {
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }

    @Override
    public void setOldHalfSizes(float halfWidth, float halfHeight) {
        this.oldHalfWidth = halfWidth;
        this.oldHalfHeight = halfHeight;
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
        this.oldHalfWidth = halfWidth;
    }

    @Override
    public void setOldHalfHeight(float halfHeight) {
        this.oldHalfHeight = halfHeight;
    }

    @Override
    public void addToHalfSizes(float halfWidth, float halfHeight) {
        this.halfWidth += halfWidth;
        this.halfHeight += halfHeight;
    }

    @Override
    public void addToOldHalfSizes(float halfWidth, float halfHeight) {
        this.oldHalfWidth += halfWidth;
        this.oldHalfHeight += halfHeight;
    }


    @Override
    public void setRotateAngles(float xAngle, float yAngle, float zAngle) {
        this.xAngle = xAngle;
        this.yAngle = yAngle;
        this.zAngle = zAngle;
    }

    @Override
    public void setOldRotateAngles(float x, float y, float z) {
        this.xOldAngle = x;
        this.yOldAngle = y;
        this.zOldAngle = z;
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
        this.xOldAngle = x;
    }

    @Override
    public void setOldRotateAnglesY(float y) {
        this.yOldAngle = y;
    }

    @Override
    public void setOldRotateAnglesZ(float z) {
        this.zOldAngle = z;
    }

    @Override
    public void addToRotateAngles(float x, float y, float z) {
        this.xAngle += x;
        this.yAngle += y;
        this.zAngle += z;
    }

    @Override
    public void addToOldRotateAngles(float x, float y, float z) {
        this.xOldAngle += x;
        this.yOldAngle += y;
        this.zOldAngle += z;
    }


    @Override
    public void setNormalVector(float x, float y, float z) {
        this.xNormalVector = x;
        this.yNormalVector = y;
        this.zNormalVector = z;
    }

    @Override
    public void setOldNormalVector(float x, float y, float z) {
        this.xOldNormalVector = x;
        this.yOldNormalVector = y;
        this.zOldNormalVector = z;
    }

    @Override
    public void setNormalVectorX(float x) {
        this.xNormalVector = x;
    }

    @Override
    public void setNormalVectorY(float y) {
        this.yNormalVector = y;
    }

    @Override
    public void setNormalVectorZ(float z) {
        this.zNormalVector = z;
    }

    @Override
    public void setOldNormalVectorX(float x) {
        this.xOldNormalVector = x;
    }

    @Override
    public void setOldNormalVectorY(float y) {
        this.yOldNormalVector = y;
    }

    @Override
    public void setOldNormalVectorZ(float z) {
        this.zOldNormalVector = z;
    }


    @Override
    public void setColorFactor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
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

}
