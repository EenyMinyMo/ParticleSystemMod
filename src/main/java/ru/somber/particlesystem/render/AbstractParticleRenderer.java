package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ru.somber.particlesystem.particle.IParticle;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class AbstractParticleRenderer implements IParticleRenderer {

    @Override
    public abstract void preRender(List<IParticle> particleList, float interpolationFactor);

    @Override
    public abstract void render(List<IParticle> particleList, float interpolationFactor);

    @Override
    public abstract void postRender(List<IParticle> particleList, float interpolationFactor);

    @Override
    public abstract void update(List<IParticle> particleList);

}
