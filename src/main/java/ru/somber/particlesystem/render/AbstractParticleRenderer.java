package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ru.somber.particlesystem.particle.IParticle;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class AbstractParticleRenderer implements IParticleRenderer {
    protected List<IParticle> particleList;

    @Override
    public void preRender(final List<IParticle> particleList) {
        this.particleList = particleList;

    }

    @Override
    public void render(final float interpolationFactor) {

    }

    @Override
    public void postRender() {
        this.particleList = null;

    }

}
