package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.opengl.*;
import ru.somber.commonutil.SomberUtils;
import ru.somber.particlesystem.ParticleSystemMod;
import ru.somber.particlesystem.particle.IParticle;
import ru.somber.particlesystem.texture.ParticleAtlasTexture;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GeometryShaderParticleRenderer extends AbstractShaderRenderer {

    public GeometryShaderParticleRenderer() {
        super();
    }


    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        super.preRender(particleList, interpolationFactor);
    }

    @Override
    public void render(List<IParticle> particleList, float interpolationFactor) {
        GL11.glDrawArrays(GL11.GL_POINTS, 0, particleList.size());
    }

    @Override
    public void postRender(List<IParticle> particleList, float interpolationFactor) {
        super.postRender(particleList, interpolationFactor);
    }

    @Override
    public void update(List<IParticle> particleList) {
        super.update(particleList);
    }


    @Override
    protected void createShaderProgram() {
        Shader vertexShader = new Shader(GL20.GL_VERTEX_SHADER);
        Shader geomShader = new Shader(GL32.GL_GEOMETRY_SHADER);
        Shader fragmentShader = new Shader(GL20.GL_FRAGMENT_SHADER);

        try {
            ResourceLocation vertexShaderCodeLocation =
                    new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/geometry_shader_particle/particle_vert.glsl");
            ResourceLocation geometryShaderCodeLocation =
                    new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/geometry_shader_particle/particle_geom.glsl");
            ResourceLocation fragmentShaderCodeLocation =
                    new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/geometry_shader_particle/particle_frag.glsl");

            vertexShader.setSourceCode(OpenGLUtils.loadShaderCode(vertexShaderCodeLocation));
            geomShader.setSourceCode(OpenGLUtils.loadShaderCode(geometryShaderCodeLocation));
            fragmentShader.setSourceCode(OpenGLUtils.loadShaderCode(fragmentShaderCodeLocation));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vertexShader.compileShader();
        vertexShader.checkError();

        geomShader.compileShader();
        geomShader.checkError();

        fragmentShader.compileShader();
        fragmentShader.checkError();

        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(vertexShader);
        shaderProgram.attachShader(geomShader);
        shaderProgram.attachShader(fragmentShader);
        shaderProgram.linkProgram();
        shaderProgram.checkError();
    }

    @Override
    protected void prepareUniforms() {
        int uniformLocation;

        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, buffer16);
        projectionMatrix.load(buffer16);
        buffer16.clear();

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer16);
        cameraMatrix.load(buffer16);
        buffer16.clear();

        Matrix4f.mul(projectionMatrix, cameraMatrix, projectionAndCameraMatrix);
        projectionAndCameraMatrix.store(buffer16);
        buffer16.flip();

        uniformLocation = GL20.glGetUniformLocation(shaderProgram.getShaderProgramID(), "projectionCameraMatrix");
        GL20.glUniformMatrix4(uniformLocation, false, buffer16);
        buffer16.clear();

        uniformLocation = GL20.glGetUniformLocation(shaderProgram.getShaderProgramID(), "cameraPosition");
        GL20.glUniform3f(uniformLocation, xCamera, yCamera, zCamera);

        uniformLocation = GL20.glGetUniformLocation(shaderProgram.getShaderProgramID(), "particleTexture");
        GL20.glUniform1i(uniformLocation, 0);
    }


    @Override
    protected void createVertexAttribVBOs() {
        vertexAttributes = new VertexAttribVBO[6];

        //particle center position VBO
        vertexAttributes[0] = new VertexAttribVBO(0, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle side scales VBO
        vertexAttributes[1] = new VertexAttribVBO(1, 2, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle normal vector VBO
        vertexAttributes[2] = new VertexAttribVBO(2, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle local angles VBO
        vertexAttributes[3] = new VertexAttribVBO(3, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle color factor VBO
        vertexAttributes[4] = new VertexAttribVBO(4, 4, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle texCoord VBO
        vertexAttributes[5] = new VertexAttribVBO(5, 4, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);

        int intervalTimeUpdate = SomberUtils.timeToTick(0, 1, 0);
        float expansionFactor = 1.5F;

        for (int i = 0; i < vertexAttributes.length; i++) {
            vertexAttributes[i].addVBOInVBODataManager(intervalTimeUpdate, expansionFactor);
        }
    }

    @Override
    protected void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor) {
        FloatBuffer particleCenterPositionBuffer = vertexAttributes[0].getVboBuffer();
        FloatBuffer particleSideScalesBuffer = vertexAttributes[1].getVboBuffer();
        FloatBuffer particleNormalVectorBuffer = vertexAttributes[2].getVboBuffer();
        FloatBuffer particleLocalAnglesBuffer = vertexAttributes[3].getVboBuffer();
        FloatBuffer particleColorFactorBuffer = vertexAttributes[4].getVboBuffer();
        FloatBuffer particleTextureCoordAABBBuffer = vertexAttributes[5].getVboBuffer();

        particleCenterPositionBuffer.clear();
        particleSideScalesBuffer.clear();
        particleNormalVectorBuffer.clear();
        particleLocalAnglesBuffer.clear();
        particleColorFactorBuffer.clear();
        particleTextureCoordAABBBuffer.clear();


        for (IParticle particle : particleList) {
            particle.computeInterpolatedPosition(particleCenterPosition, interpolationFactor);
            particle.computeNormalVector(particleNormalVector, xCamera, yCamera, zCamera, particleCenterPosition);
            Vector2f halfSizes = particle.getHalfSizes();
            Vector3f localAngles = particle.getLocalRotateAngles();
            float[] colorFactor = particle.getColorFactor();
            String iconName = particle.getIconName();
            IIcon icon = getParticleTextureAtlas().getAtlasIcon(iconName);


            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());

            particleSideScalesBuffer.put(halfSizes.getX()).put(halfSizes.getY());

            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());

            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());

            particleColorFactorBuffer.put(colorFactor);

            particleTextureCoordAABBBuffer.put(icon.getMinU()).put(icon.getMinV()).put(icon.getMaxU()).put(icon.getMaxV());
        }


        particleCenterPositionBuffer.flip();
        particleSideScalesBuffer.flip();
        particleNormalVectorBuffer.flip();
        particleLocalAnglesBuffer.flip();
        particleColorFactorBuffer.flip();
        particleTextureCoordAABBBuffer.flip();


        for (int i = 0; i < vertexAttributes.length; i++) {
            vertexAttributes[i].bufferSubData(0);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}

