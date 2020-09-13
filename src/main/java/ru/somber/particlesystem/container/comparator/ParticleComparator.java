package ru.somber.particlesystem.container.comparator;

import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.SomberCommonUtils;
import ru.somber.particlesystem.particle.IParticle;

import java.util.Comparator;

public class ParticleComparator implements Comparator<IParticle> {
    private Entity entity;
    private float interpolationFactor;
    /**
     * Векторы вынесены в переменные объекта, чтобы постоянное не создавать их в методе compare.
     * Крч так лучше для скорости выполнения.
     */
    private Vector3f entityPos, pos1, pos2;


    public ParticleComparator(Entity entity, float interpolationFactor) {
        this.entity = entity;
        this.interpolationFactor = interpolationFactor;

        entityPos = new Vector3f();
        pos1 = new Vector3f();
        pos2 = new Vector3f();
    }

    public Entity getEntity() {
        return entity;
    }

    public float getInterpolationFactor() {
        return interpolationFactor;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setInterpolationFactor(float interpolationFactor) {
        this.interpolationFactor = interpolationFactor;
    }

    @Override
    public int compare(IParticle p1, IParticle p2) {
        p1.computeInterpolatedPosition(pos1, interpolationFactor);
        p2.computeInterpolatedPosition(pos2, interpolationFactor);
        SomberCommonUtils.interpolateMove(entityPos, entity, interpolationFactor);

        Vector3f.sub(entityPos, pos1, pos1);
        Vector3f.sub(entityPos, pos2, pos2);

        float len1 = pos1.lengthSquared();
        float len2 = pos2.lengthSquared();

        if (Math.abs(len1 - len2) < SomberCommonUtils.NUMBER_ERROR_8) {
            return 0;
        } else if (len1 > len2) {
            return 1;
        } else {
            return -1;
        }
    }

}
