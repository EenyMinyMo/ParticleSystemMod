package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;
import ru.somber.clientutil.opengl.OpenGLUtils;
import ru.somber.clientutil.opengl.Shader;
import ru.somber.clientutil.opengl.ShaderProgram;
import ru.somber.particlesystem.ParticleSystemMod;
import ru.somber.particlesystem.particle.IParticle;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ShaderParticleRenderer extends AbstractParticleRenderer {
    private ShaderProgram shaderProgram;


    public ShaderParticleRenderer() {
        Shader vertexShader = new Shader(GL20.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(GL20.GL_FRAGMENT_SHADER);

        ResourceLocation vertexShaderCodeLocation =
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/simple_shader_particle/particle_vert.glsl");
        ResourceLocation fragmentShaderCodeLocation =
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/simple_shader_particle/particle_frag.glsl");

        try {
            vertexShader.setSourceCode(OpenGLUtils.loadShaderCode(vertexShaderCodeLocation));
            fragmentShader.setSourceCode(OpenGLUtils.loadShaderCode(fragmentShaderCodeLocation));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vertexShader.compileShader();
        vertexShader.checkError();
        fragmentShader.compileShader();
        fragmentShader.checkError();

        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(vertexShader);
        shaderProgram.attachShader(fragmentShader);
        shaderProgram.linkProgram();
        shaderProgram.checkError();
    }


    @Override
    public void preRender(List<IParticle> particleList) {
        super.preRender(particleList);


    }

    @Override
    public void render(float interpolationFactor) {
        super.render(interpolationFactor);


    }

    @Override
    public void postRender() {
        super.postRender();


    }

}
