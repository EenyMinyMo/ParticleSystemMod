package ru.somber.particlesystem.container.comparator;

import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.SomberUtils;
import ru.somber.particlesystem.particle.IParticle;

import java.util.Comparator;

public class ParticleComparatorLowAccuracy implements Comparator<IParticle> {
    private Entity entity;
    /**
     * Векторы вынесены в переменные объекта, чтобы постоянное не создавать их в методе compare.
     * Крч так лучше для скорости выполнения.
     */
    private Vector3f entityPos;

    public ParticleComparatorLowAccuracy(Entity entity) {
        this.entity = entity;
        this.entityPos = new Vector3f();
    }

    @Override
    public int compare(IParticle o1, IParticle o2) {
        Vector3f pos1 = o1.getNewPosition();
        Vector3f pos2 = o2.getNewPosition();
        entityPos.set((float) entity.posX, (float) entity.posY, (float) entity.posZ);

        pos1 = Vector3f.sub(entityPos, pos1, pos1);
        pos2 = Vector3f.sub(entityPos, pos2, pos2);

        float len1 = pos1.lengthSquared();
        float len2 = pos2.lengthSquared();

        if (Math.abs(len1 - len2) < SomberUtils.NUMBER_ERROR_8) {
            return 0;
        } else if (len1 > len2) {
            return 1;
        } else {
            return -1;
        }
    }

}
