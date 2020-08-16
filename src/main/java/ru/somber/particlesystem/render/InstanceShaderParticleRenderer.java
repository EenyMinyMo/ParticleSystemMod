package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
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
import ru.somber.clientutil.opengl.texture.TextureCoordAABB;
import ru.somber.commonutil.SomberUtils;
import ru.somber.particlesystem.ParticleSystemMod;
import ru.somber.particlesystem.particle.IParticle;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class InstanceShaderParticleRenderer extends AbstractParticleRenderer {

    private boolean isShaderInit;

    private ResourceLocation vertexShaderCodeLocation =
            new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/instance_shader_particle/particle_vert.glsl");
    private ResourceLocation fragmentShaderCodeLocation =
            new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/instance_shader_particle/particle_frag.glsl");

    private ShaderProgram shaderProgram;

    private VAO vao;

    private VBODataManager vboDataManager;
    private int tickUpdate;

    private BufferObject particlePositionVBO;
    private BufferObject particleCenterPositionVBO;
    private BufferObject particleNormalVectorVBO;
    private BufferObject particleLocalAnglesVBO;
    private BufferObject particleColorFactorVBO;
    private BufferObject particleTextureCoordAABBVBO;
    private BufferObject particleScaleVBO;

    private FloatBuffer particlePositionBuffer;
    private FloatBuffer particleCenterPositionBuffer;
    private FloatBuffer particleNormalVectorBuffer;
    private FloatBuffer particleLocalAnglesBuffer;
    private FloatBuffer particleColorFactorBuffer;
    private FloatBuffer particleTextureCoordAABBBuffer;
    private FloatBuffer particleScaleBuffer;

    private Matrix4f projectionMatrix;
    private Matrix4f cameraMatrix;
    private Matrix4f projectionAndCameraMatrix;

    private FloatBuffer buffer8;
    private FloatBuffer buffer16;

    private float xCamera;
    private float yCamera;
    private float zCamera;

    /** Вынесено в переменные объекта, чтобы постоянное не создавать в методе. */
    private Vector3f particleCenterPosition, particleNormalVector;


    public InstanceShaderParticleRenderer() {
        isShaderInit = false;

        buffer8 = BufferUtils.createFloatBuffer(8);
        buffer16 = BufferUtils.createFloatBuffer(16);

        projectionMatrix = new Matrix4f();
        cameraMatrix = new Matrix4f();
        projectionAndCameraMatrix = new Matrix4f();

        particleCenterPosition = new Vector3f();
        particleNormalVector = new Vector3f();

        vboDataManager = new VBODataManager();
        tickUpdate = 0;
    }

    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        if (!isShaderInit) {
            initShaderAndBuffers();
        }

        EntityLivingBase renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
        xCamera = SomberUtils.interpolateBetween((float) renderViewEntity.lastTickPosX, (float) renderViewEntity.posX, interpolationFactor);
        yCamera = SomberUtils.interpolateBetween((float) renderViewEntity.lastTickPosY, (float) renderViewEntity.posY, interpolationFactor);
        zCamera = SomberUtils.interpolateBetween((float) renderViewEntity.lastTickPosZ, (float) renderViewEntity.posZ, interpolationFactor);


        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);


        GL20.glUseProgram(shaderProgram.getShaderProgramID());


        prepareUniforms();
        prepareDataVBOs(particleList, interpolationFactor);


        VAO.bindVAO(vao);

        BufferObject.bindNone(particlePositionVBO);


        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft:dynamic/lightMap_1"));
    }

    @Override
    public void render(List<IParticle> particleList, float interpolationFactor) {
        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, particleList.size());
    }

    @Override
    public void postRender(List<IParticle> particleList, float interpolationFactor) {
        VAO.bindNone();


        GL33.glVertexAttribDivisor(1, 0);
        GL33.glVertexAttribDivisor(2, 0);
        GL33.glVertexAttribDivisor(3, 0);
        GL33.glVertexAttribDivisor(4, 0);
        GL33.glVertexAttribDivisor(6, 0);


        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL20.glDisableVertexAttribArray(5);
        GL20.glDisableVertexAttribArray(6);


        GL20.glUseProgram(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);


        checkError();
    }

    @Override
    public void update(List<IParticle> particleList) {
        if (!isShaderInit) {
            initShaderAndBuffers();
        }

        tickUpdate++;

        allocateVBOs(particleList);
    }

    private void initShaderAndBuffers() {
        createShaderProgram();
        createVBOs();
        createVAO();

        isShaderInit = true;
    }

    private void createShaderProgram() {
        Shader vertexShader = new Shader(GL20.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(GL20.GL_FRAGMENT_SHADER);

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

    private void createVBOs() {
        particlePositionVBO = BufferObject.createVBO();
        particleCenterPositionVBO = BufferObject.createVBO();
        particleNormalVectorVBO = BufferObject.createVBO();
        particleLocalAnglesVBO = BufferObject.createVBO();
        particleColorFactorVBO = BufferObject.createVBO();
        particleTextureCoordAABBVBO = BufferObject.createVBO();
        particleScaleVBO = BufferObject.createVBO();

        BufferObject.bindBuffer(particlePositionVBO);
        BufferObject.bindBuffer(particleCenterPositionVBO);
        BufferObject.bindBuffer(particleNormalVectorVBO);
        BufferObject.bindBuffer(particleLocalAnglesVBO);
        BufferObject.bindBuffer(particleColorFactorVBO);
        BufferObject.bindBuffer(particleTextureCoordAABBVBO);
        BufferObject.bindBuffer(particleScaleVBO);

        BufferObject.bindNone(particlePositionVBO);


        particlePositionBuffer = BufferUtils.createFloatBuffer(0);
        particleCenterPositionBuffer = BufferUtils.createFloatBuffer(0);
        particleNormalVectorBuffer = BufferUtils.createFloatBuffer(0);
        particleLocalAnglesBuffer = BufferUtils.createFloatBuffer(0);
        particleColorFactorBuffer = BufferUtils.createFloatBuffer(0);
        particleTextureCoordAABBBuffer = BufferUtils.createFloatBuffer(0);
        particleScaleBuffer = BufferUtils.createFloatBuffer(0);

        int intervalTimeUpdate = SomberUtils.timeToTick(0, 1, 0);
        vboDataManager.addVBO(particlePositionVBO, particlePositionBuffer, GL15.GL_STREAM_DRAW, intervalTimeUpdate, 1.5F);
        vboDataManager.addVBO(particleCenterPositionVBO, particleCenterPositionBuffer, GL15.GL_STREAM_DRAW, intervalTimeUpdate, 1.5F);
        vboDataManager.addVBO(particleNormalVectorVBO, particleNormalVectorBuffer, GL15.GL_STREAM_DRAW, intervalTimeUpdate, 1.5F);
        vboDataManager.addVBO(particleLocalAnglesVBO, particleLocalAnglesBuffer, GL15.GL_STREAM_DRAW, intervalTimeUpdate, 1.5F);
        vboDataManager.addVBO(particleColorFactorVBO, particleColorFactorBuffer, GL15.GL_STREAM_DRAW, intervalTimeUpdate, 1.5F);
        vboDataManager.addVBO(particleTextureCoordAABBVBO, particleTextureCoordAABBBuffer, GL15.GL_STREAM_DRAW, intervalTimeUpdate, 1.5F);
        vboDataManager.addVBO(particleScaleVBO, particleScaleBuffer, GL15.GL_STREAM_DRAW, intervalTimeUpdate, 1.5F);
    }

    private void createVAO() {
        vao = VAO.createVAO();


        VAO.bindVAO(vao);


        BufferObject.bindBuffer(particlePositionVBO);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleCenterPositionVBO);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleNormalVectorVBO);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleLocalAnglesVBO);
        GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleColorFactorVBO);
        GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleTextureCoordAABBVBO);
        GL20.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleScaleVBO);
        GL20.glVertexAttribPointer(6, 2, GL11.GL_FLOAT, false, 0, 0);


        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL20.glEnableVertexAttribArray(4);
        GL20.glEnableVertexAttribArray(5);
        GL20.glEnableVertexAttribArray(6);


        GL33.glVertexAttribDivisor(1, 1);
        GL33.glVertexAttribDivisor(2, 1);
        GL33.glVertexAttribDivisor(3, 1);
        GL33.glVertexAttribDivisor(4, 1);
        GL33.glVertexAttribDivisor(6, 1);


        VAO.bindNone();


        BufferObject.bindNone(particlePositionVBO);

        GL33.glVertexAttribDivisor(1, 0);
        GL33.glVertexAttribDivisor(2, 0);
        GL33.glVertexAttribDivisor(3, 0);
        GL33.glVertexAttribDivisor(4, 0);
        GL33.glVertexAttribDivisor(6, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL20.glDisableVertexAttribArray(5);
        GL20.glDisableVertexAttribArray(6);
    }

    private void prepareUniforms() {
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

    private void allocateVBOs(List<IParticle> particleList) {
        int countParticles = Math.max(particleList.size(), 1000);
        int countPrimitivePerBuffer;


        countPrimitivePerBuffer = 2 * 4;
        vboDataManager.getEntry(particlePositionVBO).updateSize(countPrimitivePerBuffer, tickUpdate);
        particlePositionBuffer = vboDataManager.getDataBuffer(particlePositionVBO);

        countPrimitivePerBuffer = countParticles * 3;
        vboDataManager.getEntry(particleCenterPositionVBO).updateSize(countPrimitivePerBuffer, tickUpdate);
        particleCenterPositionBuffer = vboDataManager.getDataBuffer(particleCenterPositionVBO);

        countPrimitivePerBuffer = countParticles * 3;
        vboDataManager.getEntry(particleNormalVectorVBO).updateSize(countPrimitivePerBuffer, tickUpdate);
        particleNormalVectorBuffer = vboDataManager.getDataBuffer(particleNormalVectorVBO);

        countPrimitivePerBuffer = countParticles * 3;
        vboDataManager.getEntry(particleLocalAnglesVBO).updateSize(countPrimitivePerBuffer, tickUpdate);
        particleLocalAnglesBuffer = vboDataManager.getDataBuffer(particleLocalAnglesVBO);

        countPrimitivePerBuffer = countParticles * 4;
        vboDataManager.getEntry(particleColorFactorVBO).updateSize(countPrimitivePerBuffer, tickUpdate);
        particleColorFactorBuffer = vboDataManager.getDataBuffer(particleColorFactorVBO);

        countPrimitivePerBuffer = countParticles * 2 * 4;
        vboDataManager.getEntry(particleTextureCoordAABBVBO).updateSize(countPrimitivePerBuffer, tickUpdate);
        particleTextureCoordAABBBuffer = vboDataManager.getDataBuffer(particleTextureCoordAABBVBO);

        countPrimitivePerBuffer = countParticles * 4
        ;
        vboDataManager.getEntry(particleTextureCoordAABBVBO).updateSize(countPrimitivePerBuffer, tickUpdate);
        particleTextureCoordAABBBuffer = vboDataManager.getDataBuffer(particleTextureCoordAABBVBO);
    }

    private void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor) {
        particlePositionBuffer.clear();
        particleCenterPositionBuffer.clear();
        particleNormalVectorBuffer.clear();
        particleLocalAnglesBuffer.clear();
        particleColorFactorBuffer.clear();
        particleScaleBuffer.clear();


        particlePositionBuffer.put(-0.5F).put(-0.5F);
        particlePositionBuffer.put(+0.5F).put(-0.5F);
        particlePositionBuffer.put(-0.5F).put(+0.5F);
        particlePositionBuffer.put(+0.5F).put(+0.5F);


        for (IParticle particle : particleList) {
            particle.computeInterpolatedPosition(particleCenterPosition, interpolationFactor);
            particle.computeNormalVector(particleNormalVector, xCamera, yCamera, zCamera, particleCenterPosition);
            Vector2f halfSizes = particle.getHalfSizes();
            Vector3f localAngles = particle.getLocalRotateAngles();
            TextureCoordAABB texCoord = particle.getTextureCoordAABB();
            float[] colorFactor = particle.getColorFactor();


            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());

            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());

            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());

            particleColorFactorBuffer.put(colorFactor);

            particleTextureCoordAABBBuffer.put(texCoord.getCoords());

            particleScaleBuffer.put(halfSizes.getX()).put(halfSizes.getY());
        }


        particlePositionBuffer.flip();
        particleCenterPositionBuffer.flip();
        particleNormalVectorBuffer.flip();
        particleLocalAnglesBuffer.flip();
        particleColorFactorBuffer.flip();
        particleTextureCoordAABBBuffer.flip();
        particleScaleBuffer.flip();


        BufferObject.bindBuffer(particlePositionVBO);
        BufferObject.bufferSubData(particlePositionVBO, 0, particlePositionBuffer);

        BufferObject.bindBuffer(particleCenterPositionVBO);
        BufferObject.bufferSubData(particleCenterPositionVBO, 0, particleCenterPositionBuffer);

        BufferObject.bindBuffer(particleNormalVectorVBO);
        BufferObject.bufferSubData(particleNormalVectorVBO, 0, particleNormalVectorBuffer);

        BufferObject.bindBuffer(particleLocalAnglesVBO);
        BufferObject.bufferSubData(particleLocalAnglesVBO, 0, particleLocalAnglesBuffer);

        BufferObject.bindBuffer(particleColorFactorVBO);
        BufferObject.bufferSubData(particleColorFactorVBO, 0, particleColorFactorBuffer);

        BufferObject.bindBuffer(particleTextureCoordAABBVBO);
        BufferObject.bufferSubData(particleTextureCoordAABBVBO, 0, particleTextureCoordAABBBuffer);

        BufferObject.bindBuffer(particleScaleVBO);
        BufferObject.bufferSubData(particleScaleVBO, 0, particleScaleBuffer);

        BufferObject.bindNone(particlePositionVBO);
    }

    private void checkError() {
        int i = GL11.glGetError();

        Logger logger = LogManager.getLogger();
        String str = "shader program error";

        if (i != 0) {
            String s1 = GLU.gluErrorString(i);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + str);
            logger.error(i + ": " + s1);

            throw new RuntimeException("########## GL ERROR ##########");
        }
    }

}
