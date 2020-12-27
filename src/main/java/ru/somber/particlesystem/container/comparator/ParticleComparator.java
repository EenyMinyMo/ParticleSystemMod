package ru.somber.particlesystem.container.comparator;

import ru.somber.particlesystem.particle.IParticle;
import ru.somber.util.clientutil.PlayerPositionUtil;
import ru.somber.util.commonutil.SomberCommonUtil;

import java.util.Comparator;

public class ParticleComparator implements Comparator<IParticle> {
    private float xCamera, yCamera, zCamera;

    public ParticleComparator() {}

    public void updateCameraPosition() {
        PlayerPositionUtil positionUtil = PlayerPositionUtil.getInstance();
        xCamera = positionUtil.xCamera();
        yCamera = positionUtil.yCamera();
        zCamera = positionUtil.zCamera();
    }

    @Override
    public int compare(IParticle o1, IParticle o2) {
        double posX1 = xCamera - o1.getPositionX();
        double posY1 = yCamera - o1.getPositionY();
        double posZ1 = zCamera - o1.getPositionZ();

        double posX2 = xCamera - o2.getPositionX();
        double posY2 = yCamera - o2.getPositionY();
        double posZ2 = zCamera - o2.getPositionZ();

        double len1 = posX1 * posX1 + posY1 * posY1 + posZ1 * posZ1;
        double len2 = posX2 * posX2 + posY2 * posY2 + posZ2 * posZ2;

        if (Math.abs(len1 - len2) < SomberCommonUtil.NUMBER_ERROR_8) {
            return 0;
        } else if (len1 > len2) {
            return -1;
        } else {
            return 1;
        }
    }

}
