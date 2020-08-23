package ru.somber.particlesystem.emitter;

import org.lwjgl.util.vector.Vector3f;
import ru.somber.particlesystem.container.IParticleContainer;
import ru.somber.particlesystem.particle.IParticle;

import java.util.ArrayList;
import java.util.List;

public class AbstractEmitter implements IParticleEmitter {

    /** true - эмиттер был создан через метод create и готов к работе, иначе false. */
    private boolean isCreated = false;
    /** true - эмиттер помечен мертвым и не может быть более использован. */
    private boolean isDie = false;
    /** Позиция эмиттера. */
    private float x, y, z;

    /** Связанный контейнер частиц, в который будут помещаться создаваемые частицы. */
    private IParticleContainer particleContainer;
    /** Список частиц, за которые отвечает данный эмитер. */
    private List<IParticle> emitterParticleList;
    /** Количество тиков с момента создания эмиттера, но на самом деле сюда можно записать все, что угодно. */
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
    public void create(IParticleContainer container) {
        if (isCreated() || isDie()) {
            throw new RuntimeException("Emitter cannot be created!");
        }

        this.particleContainer = container;
        this.emitterParticleList = new ArrayList<>(50);
        this.tick = 0;

        isCreated = true;
        isDie = false;
    }

    @Override
    public void update() {
        if (! isCreated() || isDie()) {
            throw new RuntimeException("Emitter cannot be used!");
        }

        tick++;
    }

    @Override
    public void delete() {
        if (! isCreated() || isDie()) {
            throw new RuntimeException("Emitter cannot be deleted!");
        }

        emitterParticleList.forEach(IParticle::setDie);

        emitterParticleList = null;
        particleContainer = null;
        tick = -1;
        isDie = true;
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


    protected IParticleContainer getParticleContainer() {
        return particleContainer;
    }

    protected List<IParticle> getEmitterParticleList() {
        return emitterParticleList;
    }

    protected int getTick() {
        return tick;
    }

    protected void setTick(int newTick) {
        this.tick = newTick;
    }

}
