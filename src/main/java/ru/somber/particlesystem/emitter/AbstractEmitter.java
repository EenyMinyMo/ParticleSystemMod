package ru.somber.particlesystem.emitter;

import org.lwjgl.util.vector.Vector3f;

public class AbstractEmitter implements IParticleEmitter {

    /** Позиция эмиттера. */
    private float x, y, z;
    /** true - эмиттер был создан через метод create и готов к работе, иначе false. */
    private boolean isCreated;
    /** true - эмиттер помечен мертвым и не может быть более использован. */
    private boolean isDie;
    /** Количество тиков с момента создания эмиттера, но на самом деле сюда можно записать любое число. */
    private int tick;


    public AbstractEmitter(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    @Override
    public float getPositionX() {
        return x;
    }

    @Override
    public float getPositionY() {
        return y;
    }

    @Override
    public float getPositionZ() {
        return z;
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(x, y, z);
    }


    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void setPosition(Vector3f position) {
        x = position.getX();
        y = position.getY();
        z = position.getZ();
    }


    @Override
    public void create() {
        if (isCreated() || isDie()) {
            throw new RuntimeException("Emitter cannot be created!");
        }

        this.tick = 0;

        this.isCreated = true;
        this.isDie = false;
    }

    @Override
    public void update() {
        if (! isCreated() || isDie()) {
            throw new RuntimeException("Emitter cannot be used!");
        }

        this.tick++;
    }

    @Override
    public void delete() {
        if (! isCreated() || isDie()) {
            throw new RuntimeException("Emitter cannot be deleted!");
        }

        this.tick = -1;
        this.isDie = true;
    }


    @Override
    public boolean isCreated() {
        return isCreated;
    }

    @Override
    public boolean isDie() {
        return isDie;
    }

    @Override
    public void setDie() {
        delete();
    }


    @Override
    public String toString() {
        return "AbstractEmitter{" +
                "isCreated=" + isCreated +
                ", isDie=" + isDie +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }


    protected int getTick() {
        return tick;
    }

    protected void setTick(int newTick) {
        this.tick = newTick;
    }

}
