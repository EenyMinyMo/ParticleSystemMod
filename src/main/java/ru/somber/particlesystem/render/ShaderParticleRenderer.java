package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import ru.somber.util.clientutil.opengl.Shader;
import ru.somber.util.clientutil.opengl.ShaderProgram;
import ru.somber.util.clientutil.opengl.vbo.VBO;
import ru.somber.util.clientutil.opengl.vbo.VBODataManager;
import ru.somber.util.clientutil.opengl.vbo.VertexAttribVBO;
import ru.somber.util.clientutil.textureatlas.icon.AtlasIcon;
import ru.somber.util.commonutil.SomberCommonUtil;
import ru.somber.particlesystem.ParticleSystemMod;
import ru.somber.particlesystem.particle.IParticle;

import java.nio.FloatBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ShaderParticleRenderer extends AbstractShaderRenderer {

    public ShaderParticleRenderer() {
        super();
    }


    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        super.preRender(particleList, interpolationFactor);
    }

    @Override
    public void render(List<IParticle> particleList, float interpolationFactor) {
        GL11.glDrawArrays(GL11.GL_QUADS, 0, 4 * particleList.size());
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
        Shader vertexShader = Shader.createShaderObject(GL20.GL_VERTEX_SHADER,
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/simple_shader_particle/particle_vert.glsl"));

        Shader fragmentShader = Shader.createShaderObject(GL20.GL_FRAGMENT_SHADER,
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/simple_shader_particle/particle_frag.glsl"));

        shaderProgram = ShaderProgram.createShaderProgram(vertexShader, fragmentShader);
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
    protected void allocateVBOs(List<IParticle> particleList) {
        //Резервиуется место под 1000 частиц,
        //чтобы на малых количествах частиц не нужно было постояноо изменять размеры памяти.
        int countParticles = Math.max(particleList.size(), 1000);

        for (VertexAttribVBO attrib : vertexAttributes) {
            attrib.allocateVBO(countParticles * 4);
        }
    }

    @Override
    protected void createVBOsAndVertAttribVBOs() {
        vertexAttributes = new VertexAttribVBO[7];

        int intervalTimeUpdate = SomberCommonUtil.timeToTick(0, 1, 0);
        float expansionFactor = 1.5F;

        VBO positionVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager positionVBOManager = new VBODataManager(positionVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO colorFactorVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager colorFactorVBOManager = new VBODataManager(colorFactorVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO sideScalesAndLightBlendFactorVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager sideScalesAndLightBlendFactorVBOManager = new VBODataManager(sideScalesAndLightBlendFactorVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO centerPositionVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager centerPositionVBOManager = new VBODataManager(centerPositionVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO normalVectorVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager normalVectorVBOManager = new VBODataManager(normalVectorVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO rotationAnglesVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager rotationAnglesVBOManager = new VBODataManager(rotationAnglesVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO textureCoordVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager textureCoordVBOManager = new VBODataManager(textureCoordVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);



        //particle position VBO
        vertexAttributes[0] = new VertexAttribVBO(positionVBOManager, 0, 2);
        //particle color factor VBO
        vertexAttributes[1] = new VertexAttribVBO(colorFactorVBOManager, 1, 4);
        //particle side scales and light & blend VBO
        vertexAttributes[2] = new VertexAttribVBO(sideScalesAndLightBlendFactorVBOManager, 2, 4);
        //particle center position VBO
        vertexAttributes[3] = new VertexAttribVBO(centerPositionVBOManager, 3, 3);
        //particle normal vector VBO
        vertexAttributes[4] = new VertexAttribVBO(normalVectorVBOManager, 4, 3);
        //particle rotated angles VBO
        vertexAttributes[5] = new VertexAttribVBO(rotationAnglesVBOManager, 5, 3);
        //particle texture coord VBO
        vertexAttributes[6] = new VertexAttribVBO(textureCoordVBOManager, 6, 2);


        vboDataManagerMap.addVBODataManager(positionVBOManager);
        vboDataManagerMap.addVBODataManager(centerPositionVBOManager);
        vboDataManagerMap.addVBODataManager(normalVectorVBOManager);
        vboDataManagerMap.addVBODataManager(rotationAnglesVBOManager);
        vboDataManagerMap.addVBODataManager(colorFactorVBOManager);
        vboDataManagerMap.addVBODataManager(textureCoordVBOManager);
        vboDataManagerMap.addVBODataManager(sideScalesAndLightBlendFactorVBOManager);
    }

    @Override
    protected void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor) {
        FloatBuffer particlePositionBuffer = vertexAttributes[0].getVboDataManager().getDataBuffer();
        FloatBuffer particleColorFactorBuffer = vertexAttributes[1].getVboDataManager().getDataBuffer();
        FloatBuffer particleSideScalesAndLightBlendBuffer = vertexAttributes[2].getVboDataManager().getDataBuffer();
        FloatBuffer particleCenterPositionBuffer = vertexAttributes[3].getVboDataManager().getDataBuffer();
        FloatBuffer particleNormalVectorBuffer = vertexAttributes[4].getVboDataManager().getDataBuffer();
        FloatBuffer particleLocalAnglesBuffer = vertexAttributes[5].getVboDataManager().getDataBuffer();
        FloatBuffer particleTexCoordBuffer = vertexAttributes[6].getVboDataManager().getDataBuffer();

        particlePositionBuffer.clear();
        particleColorFactorBuffer.clear();
        particleSideScalesAndLightBlendBuffer.clear();
        particleCenterPositionBuffer.clear();
        particleNormalVectorBuffer.clear();
        particleLocalAnglesBuffer.clear();
        particleTexCoordBuffer.clear();


        for (IParticle particle : particleList) {
            particle.computeInterpolatedPosition(particleCenterPosition, interpolationFactor);
            particle.computeNormalVector(particleNormalVector,  interpolationFactor);
            particle.computeInterpolatedHalfSizes(particleHalfSizes, interpolationFactor);
            particle.computeInterpolatedRotateAngles(particleRotationAngles, interpolationFactor);
            AtlasIcon icon = particle.getParticleIcon();
            float light = particle.getLightFactor();
            float blend = particle.getBlendFactor();


            particlePositionBuffer.put(-0.5F).put(-0.5F);
            particlePositionBuffer.put(+0.5F).put(-0.5F);
            particlePositionBuffer.put(+0.5F).put(+0.5F);
            particlePositionBuffer.put(-0.5F).put(+0.5F);

            particleColorFactorBuffer.put(particle.getRedFactor()).put(particle.getGreenFactor()).put(particle.getBlueFactor()).put(particle.getAlphaFactor());
            particleColorFactorBuffer.put(particle.getRedFactor()).put(particle.getGreenFactor()).put(particle.getBlueFactor()).put(particle.getAlphaFactor());
            particleColorFactorBuffer.put(particle.getRedFactor()).put(particle.getGreenFactor()).put(particle.getBlueFactor()).put(particle.getAlphaFactor());
            particleColorFactorBuffer.put(particle.getRedFactor()).put(particle.getGreenFactor()).put(particle.getBlueFactor()).put(particle.getAlphaFactor());

            particleSideScalesAndLightBlendBuffer.put(particleHalfSizes.getX()).put(particleHalfSizes.getY()).put(light).put(blend);
            particleSideScalesAndLightBlendBuffer.put(particleHalfSizes.getX()).put(particleHalfSizes.getY()).put(light).put(blend);
            particleSideScalesAndLightBlendBuffer.put(particleHalfSizes.getX()).put(particleHalfSizes.getY()).put(light).put(blend);
            particleSideScalesAndLightBlendBuffer.put(particleHalfSizes.getX()).put(particleHalfSizes.getY()).put(light).put(blend);

            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());
            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());
            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());
            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());

            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());

            particleLocalAnglesBuffer.put(particleRotationAngles.getX()).put(particleRotationAngles.getY()).put(particleRotationAngles.getZ());
            particleLocalAnglesBuffer.put(particleRotationAngles.getX()).put(particleRotationAngles.getY()).put(particleRotationAngles.getZ());
            particleLocalAnglesBuffer.put(particleRotationAngles.getX()).put(particleRotationAngles.getY()).put(particleRotationAngles.getZ());
            particleLocalAnglesBuffer.put(particleRotationAngles.getX()).put(particleRotationAngles.getY()).put(particleRotationAngles.getZ());

            particleTexCoordBuffer.put(icon.getMinU()).put(icon.getMinV());
            particleTexCoordBuffer.put(icon.getMaxU()).put(icon.getMinV());
            particleTexCoordBuffer.put(icon.getMaxU()).put(icon.getMaxV());
            particleTexCoordBuffer.put(icon.getMinU()).put(icon.getMaxV());
        }


        particlePositionBuffer.flip();
        particleColorFactorBuffer.flip();
        particleSideScalesAndLightBlendBuffer.flip();
        particleCenterPositionBuffer.flip();
        particleNormalVectorBuffer.flip();
        particleLocalAnglesBuffer.flip();
        particleTexCoordBuffer.flip();


        for (int i = 0; i < vertexAttributes.length; i++) {
            VBODataManager vboManager = vertexAttributes[i].getVboDataManager();
            VBO vbo = vboManager.getVbo();
            FloatBuffer buffer = vboManager.getDataBuffer();

            vbo.bindBuffer();
            vbo.bufferSubData(0, buffer);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
