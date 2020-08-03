package ru.somber.particlesystem.container.comparator;

import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.SomberUtils;
import ru.somber.particlesystem.particle.IParticle;

import java.util.Comparator;

public class ParticleComparator implements Comparator<IParticle> {
    private Entity entity;
    private float interpolationFactor;


    public ParticleComparator(Entity entity, float interpolationFactor) {
        this.entity = entity;
        this.interpolationFactor = interpolationFactor;
    }


    @Override
    public int compare(IParticle o1, IParticle o2) {
        Vector3f pos1 = o1.getInterpolatedPosition(interpolationFactor);
        Vector3f pos2 = o2.getInterpolatedPosition(interpolationFactor);
        Vector3f entityPos = SomberUtils.interpolateMove(entity, interpolationFactor);

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
