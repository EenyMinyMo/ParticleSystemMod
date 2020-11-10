package ru.somber.particlesystem.container.comparator;

import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.util.commonutil.SomberCommonUtil;
import ru.somber.particlesystem.particle.IParticle;

import java.util.Comparator;

public class ParticleComparatorLowAccuracy implements Comparator<IParticle> {
    /**
     * Векторы вынесены в переменные объекта, чтобы постоянное не создавать их в методе compare.
     * Крч так лучше для скорости выполнения.
     */
    private Vector3f entityPos;
    private Vector3f particlePos1;
    private Vector3f particlePos2;


    public ParticleComparatorLowAccuracy() {
        this.entityPos = new Vector3f();
        this.particlePos1 = new Vector3f();
        this.particlePos2 = new Vector3f();
    }


    public void setEntityPos(Entity entity, float interpolationFactor) {
        SomberCommonUtil.interpolateMove(entityPos, entity, interpolationFactor);
    }

    @Override
    public int compare(IParticle o1, IParticle o2) {
        o1.getPosition(particlePos1);
        o2.getPosition(particlePos2);

        Vector3f.sub(entityPos, particlePos1, particlePos1);
        Vector3f.sub(entityPos, particlePos2, particlePos2);

        float len1 = particlePos1.lengthSquared();
        float len2 = particlePos2.lengthSquared();

        if (Math.abs(len1 - len2) < SomberCommonUtil.NUMBER_ERROR_8) {
            return 0;
        } else if (len1 > len2) {
            return -1;
        } else {
            return 1;
        }
    }

}
