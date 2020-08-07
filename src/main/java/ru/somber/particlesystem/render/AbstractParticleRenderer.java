package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ru.somber.particlesystem.particle.IParticle;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class AbstractParticleRenderer implements IParticleRenderer {
    private List<IParticle> particleList;

    @Override
    public void preRender(List<IParticle> particleList) {
        this.particleList = particleList;

    }

    public abstract void render(float interpolationFactor);

    @Override
    public void postRender() {
        this.particleList = null;

    }

    public abstract void update(List<IParticle> particleList);

    public List<IParticle> getParticleList() {
        return particleList;
    }

}
