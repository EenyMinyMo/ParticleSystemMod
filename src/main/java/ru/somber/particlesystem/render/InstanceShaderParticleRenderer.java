package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import ru.somber.util.clientutil.PlayerPositionUtil;
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
public class InstanceShaderParticleRenderer extends AbstractShaderRenderer {

    public InstanceShaderParticleRenderer() {
        super();
    }


    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        super.preRender(particleList, interpolationFactor);
    }

    @Override
    public void render(List<IParticle> particleList, float interpolationFactor) {
        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, particleList.size());
    }

    @Override
    public void postRender(List<IParticle> particleList, float interpolationFactor) {
        super.postRender(particleList, interpolationFactor);
    }

    @Override
    public void update(List<IParticle> particleList) {
        super.update(particleList);

        FloatBuffer positionBuffer = vertexAttributes[0].getVboDataManager().getDataBuffer();
        FloatBuffer centerPositionBuffer = vertexAttributes[1].getVboDataManager().getDataBuffer();
        FloatBuffer oldCenterPositionBuffer = vertexAttributes[2].getVboDataManager().getDataBuffer();
        FloatBuffer sideScaleBuffer = vertexAttributes[3].getVboDataManager().getDataBuffer();
        FloatBuffer oldSideScaleBuffer = vertexAttributes[4].getVboDataManager().getDataBuffer();
        FloatBuffer normalVectorBuffer = vertexAttributes[5].getVboDataManager().getDataBuffer();
        FloatBuffer oldNormalVectorBuffer = vertexAttributes[6].getVboDataManager().getDataBuffer();
        FloatBuffer anglesBuffer = vertexAttributes[7].getVboDataManager().getDataBuffer();
        FloatBuffer oldAnglesBuffer = vertexAttributes[8].getVboDataManager().getDataBuffer();
        FloatBuffer colorFactorBuffer = vertexAttributes[9].getVboDataManager().getDataBuffer();
        FloatBuffer textureCoordAABBBuffer = vertexAttributes[10].getVboDataManager().getDataBuffer();

        positionBuffer.clear();
        centerPositionBuffer.clear();
        oldCenterPositionBuffer.clear();
        sideScaleBuffer.clear();
        oldSideScaleBuffer.clear();
        normalVectorBuffer.clear();
        oldNormalVectorBuffer.clear();
        anglesBuffer.clear();
        oldAnglesBuffer.clear();
        colorFactorBuffer.clear();
        textureCoordAABBBuffer.clear();

        positionBuffer.put(-0.5F).put(-0.5F);
        positionBuffer.put(+0.5F).put(-0.5F);
        positionBuffer.put(-0.5F).put(+0.5F);
        positionBuffer.put(+0.5F).put(+0.5F);

        for (IParticle particle : particleList) {
            particle.computeNormalVector(particleNormalVector,  0);
            AtlasIcon icon = particle.getParticleIcon();

            centerPositionBuffer.put(particle.getPositionX()).put(particle.getPositionY()).put(particle.getPositionZ());
            oldCenterPositionBuffer.put(particle.getOldPositionX()).put(particle.getOldPositionY()).put(particle.getOldPositionZ());
            sideScaleBuffer.put(particle.getHalfWidth()).put(particle.getHalfHeight()).put(particle.getLightFactor()).put(particle.getBlendFactor());
            oldSideScaleBuffer.put(particle.getOldHalfWidth()).put(particle.getOldHalfHeight()).put(particle.getLightFactor()).put(particle.getBlendFactor());
            normalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
            oldNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
            anglesBuffer.put(particle.getAngleX()).put(particle.getAngleY()).put(particle.getAngleZ());
            oldAnglesBuffer.put(particle.getOldAngleX()).put(particle.getOldAngleY()).put(particle.getOldAngleZ());
            colorFactorBuffer.put(particle.getRedFactor()).put(particle.getGreenFactor()).put(particle.getBlueFactor()).put(particle.getAlphaFactor());
            textureCoordAABBBuffer.put(icon.getMinU()).put(icon.getMinV()).put(icon.getMaxU()).put(icon.getMaxV());
        }

        positionBuffer.flip();
        centerPositionBuffer.flip();
        oldCenterPositionBuffer.flip();
        sideScaleBuffer.flip();
        oldSideScaleBuffer.flip();
        normalVectorBuffer.flip();
        oldNormalVectorBuffer.flip();
        anglesBuffer.flip();
        oldAnglesBuffer.flip();
        colorFactorBuffer.flip();
        textureCoordAABBBuffer.flip();


        for (int i = 0; i < vertexAttributes.length; i++) {
            VBODataManager vboManager = vertexAttributes[i].getVboDataManager();
            VBO vbo = vboManager.getVbo();
            FloatBuffer buffer = vboManager.getDataBuffer();

            vbo.bindBuffer();
            vbo.bufferSubData(0, buffer);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

    }


    @Override
    protected void createShaderProgram() {
        Shader vertexShader = Shader.createShaderObject(GL20.GL_VERTEX_SHADER,
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/instance_shader_particle/particle_vert.glsl"));

        Shader fragmentShader = Shader.createShaderObject(GL20.GL_FRAGMENT_SHADER,
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/instance_shader_particle/particle_frag.glsl"));

        shaderProgram = ShaderProgram.createShaderProgram(vertexShader, fragmentShader);
    }

    @Override
    protected void prepareUniforms(float interpolationFactor) {
        float xCamera = PlayerPositionUtil.getInstance().xPlayer();
        float yCamera = PlayerPositionUtil.getInstance().yPlayer();
        float zCamera = PlayerPositionUtil.getInstance().zPlayer();

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

        uniformLocation = GL20.glGetUniformLocation(shaderProgram.getShaderProgramID(), "interpolationFactor");
        GL20.glUniform1f(uniformLocation, interpolationFactor);
    }

    @Override
    protected void createVBOsAndVertAttribVBOs() {
        vertexAttributes = new VertexAttribVBO[11];

        int intervalTimeUpdate = SomberCommonUtil.timeToTick(0, 5, 0);
        float expansionFactor = 2F;

        VBO positionVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager positionVBOManager = new VBODataManager(positionVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO centerPositionVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager centerPositionVBOManager = new VBODataManager(centerPositionVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO oldCenterPositionVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager oldCenterPositionVBOManager = new VBODataManager(oldCenterPositionVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO sideScalesAndLightBlendFactorVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager sideScalesAndLightBlendFactorVBOManager = new VBODataManager(sideScalesAndLightBlendFactorVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO oldSideScalesAndLightBlendFactorVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager oldSideScalesAndLightBlendFactorVBOManager = new VBODataManager(oldSideScalesAndLightBlendFactorVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO normalVectorVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager normalVectorVBOManager = new VBODataManager(normalVectorVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO oldNormalVectorVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager oldNormalVectorVBOManager = new VBODataManager(oldNormalVectorVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO rotationAnglesVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager rotationAnglesVBOManager = new VBODataManager(rotationAnglesVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO oldRotationAnglesVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager oldRotationAnglesVBOManager = new VBODataManager(oldRotationAnglesVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO colorFactorVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager colorFactorVBOManager = new VBODataManager(colorFactorVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        VBO textureCoordVBO = VBO.createVBO(GL15.GL_STREAM_DRAW);
        VBODataManager textureCoordVBOManager = new VBODataManager(textureCoordVBO, BufferUtils.createFloatBuffer(0), intervalTimeUpdate, expansionFactor);

        //particle position VBO
        vertexAttributes[0] = new VertexAttribVBO(positionVBOManager, 0, 2);
        vertexAttributes[0].setVertexAttribDivisor(0);
        //particle center position VBO
        vertexAttributes[1] = new VertexAttribVBO(centerPositionVBOManager, 1, 3);
        vertexAttributes[1].setVertexAttribDivisor(1);
        vertexAttributes[2] = new VertexAttribVBO(oldCenterPositionVBOManager, 2, 3);
        vertexAttributes[2].setVertexAttribDivisor(1);
        //particle side scales and light & blend VBO
        vertexAttributes[3] = new VertexAttribVBO(sideScalesAndLightBlendFactorVBOManager, 3, 4);
        vertexAttributes[3].setVertexAttribDivisor(1);
        vertexAttributes[4] = new VertexAttribVBO(oldSideScalesAndLightBlendFactorVBOManager, 4, 4);
        vertexAttributes[4].setVertexAttribDivisor(1);
        //particle normal vector VBO
        vertexAttributes[5] = new VertexAttribVBO(normalVectorVBOManager, 5, 3);
        vertexAttributes[5].setVertexAttribDivisor(1);
        vertexAttributes[6] = new VertexAttribVBO(oldNormalVectorVBOManager, 6, 3);
        vertexAttributes[6].setVertexAttribDivisor(1);
        //particle rotation angles VBO
        vertexAttributes[7] = new VertexAttribVBO(rotationAnglesVBOManager, 7, 3);
        vertexAttributes[7].setVertexAttribDivisor(1);
        vertexAttributes[8] = new VertexAttribVBO(oldRotationAnglesVBOManager, 8, 3);
        vertexAttributes[8].setVertexAttribDivisor(1);
        //particle color factor VBO
        vertexAttributes[9] = new VertexAttribVBO(colorFactorVBOManager, 9, 4);
        vertexAttributes[9].setVertexAttribDivisor(1);
        //particle texture coord VBO
        vertexAttributes[10] = new VertexAttribVBO(textureCoordVBOManager, 10, 4);
        vertexAttributes[10].setVertexAttribDivisor(1);
    }

    @Override
    protected void allocateVBOs(List<IParticle> particleList) {
        //Резервиуется место под 2500 частиц,
        //чтобы на малых количествах частиц не нужно было постояноо изменять размеры памяти.
        int countParticles = Math.max(particleList.size(), 2500);

        vertexAttributes[0].allocateVBO(4);
        for (int i = 1; i < vertexAttributes.length; i++) {
            vertexAttributes[i].allocateVBO(countParticles);
        }
    }

    @Override
    protected void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor) {
//        FloatBuffer positionBuffer = vertexAttributes[0].getVboDataManager().getDataBuffer();
//        FloatBuffer centerPositionBuffer = vertexAttributes[1].getVboDataManager().getDataBuffer();
//        FloatBuffer oldCenterPositionBuffer = vertexAttributes[2].getVboDataManager().getDataBuffer();
//        FloatBuffer sideScaleBuffer = vertexAttributes[3].getVboDataManager().getDataBuffer();
//        FloatBuffer oldSideScaleBuffer = vertexAttributes[4].getVboDataManager().getDataBuffer();
//        FloatBuffer normalVectorBuffer = vertexAttributes[5].getVboDataManager().getDataBuffer();
//        FloatBuffer oldNormalVectorBuffer = vertexAttributes[6].getVboDataManager().getDataBuffer();
//        FloatBuffer anglesBuffer = vertexAttributes[7].getVboDataManager().getDataBuffer();
//        FloatBuffer oldAnglesBuffer = vertexAttributes[8].getVboDataManager().getDataBuffer();
//        FloatBuffer colorFactorBuffer = vertexAttributes[9].getVboDataManager().getDataBuffer();
//        FloatBuffer textureCoordAABBBuffer = vertexAttributes[10].getVboDataManager().getDataBuffer();
//
//        positionBuffer.clear();
//        centerPositionBuffer.clear();
//        oldCenterPositionBuffer.clear();
//        sideScaleBuffer.clear();
//        oldSideScaleBuffer.clear();
//        normalVectorBuffer.clear();
//        oldNormalVectorBuffer.clear();
//        anglesBuffer.clear();
//        oldAnglesBuffer.clear();
//        colorFactorBuffer.clear();
//        textureCoordAABBBuffer.clear();
//
//        positionBuffer.put(-0.5F).put(-0.5F);
//        positionBuffer.put(+0.5F).put(-0.5F);
//        positionBuffer.put(-0.5F).put(+0.5F);
//        positionBuffer.put(+0.5F).put(+0.5F);
//
//        for (IParticle particle : particleList) {
//            particle.computeNormalVector(particleNormalVector,  interpolationFactor);
//            AtlasIcon icon = particle.getParticleIcon();
//
//            centerPositionBuffer.put(particle.getPositionX()).put(particle.getPositionY()).put(particle.getPositionZ());
//            oldCenterPositionBuffer.put(particle.getOldPositionX()).put(particle.getOldPositionY()).put(particle.getOldPositionZ());
//            sideScaleBuffer.put(particle.getHalfWidth()).put(particle.getHalfHeight()).put(particle.getLightFactor()).put(particle.getBlendFactor());
//            oldSideScaleBuffer.put(particle.getOldHalfWidth()).put(particle.getOldHalfHeight()).put(particle.getLightFactor()).put(particle.getBlendFactor());
//            normalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
//            oldNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
//            anglesBuffer.put(particle.getAngleX()).put(particle.getAngleY()).put(particle.getAngleZ());
//            oldAnglesBuffer.put(particle.getOldAngleX()).put(particle.getOldAngleY()).put(particle.getOldAngleZ());
//            colorFactorBuffer.put(particle.getRedFactor()).put(particle.getGreenFactor()).put(particle.getBlueFactor()).put(particle.getAlphaFactor());
//            textureCoordAABBBuffer.put(icon.getMinU()).put(icon.getMinV()).put(icon.getMaxU()).put(icon.getMaxV());
//        }
//
//        positionBuffer.flip();
//        centerPositionBuffer.flip();
//        oldCenterPositionBuffer.flip();
//        sideScaleBuffer.flip();
//        oldSideScaleBuffer.flip();
//        normalVectorBuffer.flip();
//        oldNormalVectorBuffer.flip();
//        anglesBuffer.flip();
//        oldAnglesBuffer.flip();
//        colorFactorBuffer.flip();
//        textureCoordAABBBuffer.flip();
//
//
//        for (int i = 0; i < vertexAttributes.length; i++) {
//            VBODataManager vboManager = vertexAttributes[i].getVboDataManager();
//            VBO vbo = vboManager.getVbo();
//            FloatBuffer buffer = vboManager.getDataBuffer();
//
//            vbo.bindBuffer();
//            vbo.bufferSubData(0, buffer);
//        }
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
