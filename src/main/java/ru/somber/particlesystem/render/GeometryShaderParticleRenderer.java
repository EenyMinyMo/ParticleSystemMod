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
import ru.somber.clientutil.opengl.texture.TextureCoord;
import ru.somber.commonutil.SomberUtils;
import ru.somber.particlesystem.ParticleSystemMod;
import ru.somber.particlesystem.particle.IParticle;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GeometryShaderParticleRenderer extends AbstractParticleRenderer {

    private boolean isShaderInit;

    private ResourceLocation vertexShaderCodeLocation =
            new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/geometry_shader_particle/particle_vert.glsl");
    private ResourceLocation geometryShaderCodeLocation =
            new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/geometry_shader_particle/particle_geom.glsl");
    private ResourceLocation fragmentShaderCodeLocation =
            new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/geometry_shader_particle/particle_frag.glsl");

    private ShaderProgram shaderProgram;

    private VAO vao;

    private BufferObject particleCenterPositionVBO;
    private BufferObject particleSideScalesVBO;
    private BufferObject particleNormalVectorVBO;
    private BufferObject particleLocalAnglesVBO;
    private BufferObject particleAttributeVBO;
    private BufferObject particleTextureCoordAABBVBO;

    private FloatBuffer particleCenterPositionBuffer;
    private FloatBuffer particleSideScalesBuffer;
    private FloatBuffer particleNormalVectorBuffer;
    private FloatBuffer particleLocalAnglesBuffer;
    private FloatBuffer particleAttributeBuffer;
    private FloatBuffer particleTextureCoordAABBBuffer;

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


    public GeometryShaderParticleRenderer() {
        isShaderInit = false;

        buffer8 = BufferUtils.createFloatBuffer(8);
        buffer16 = BufferUtils.createFloatBuffer(16);

        projectionMatrix = new Matrix4f();
        cameraMatrix = new Matrix4f();
        projectionAndCameraMatrix = new Matrix4f();

        particleCenterPosition = new Vector3f();
        particleNormalVector = new Vector3f();
    }

    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        if (! isShaderInit) {
            initShader();
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


        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft:dynamic/lightMap_1"));

        BufferObject.bindNone(particleCenterPositionVBO);
    }

    @Override
    public void render(List<IParticle> particleList, float interpolationFactor) {
        GL11.glDrawArrays(GL11.GL_POINTS, 0, particleList.size());
    }

    @Override
    public void postRender(List<IParticle> particleList, float interpolationFactor) {
        VAO.bindNone();


        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL20.glDisableVertexAttribArray(5);


        GL20.glUseProgram(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);


        checkError();
    }

    @Override
    public void update(List<IParticle> particleList) {
        if (! isShaderInit) {
            initShader();
        }

        allocateVBOs(particleList);
    }

    private void initShader() {
        createShaderProgram();
        createVBOs();
        createVAO();

        isShaderInit = true;
    }

    private void createShaderProgram() {
        Shader vertexShader = new Shader(GL20.GL_VERTEX_SHADER);
        Shader geomShader = new Shader(GL32.GL_GEOMETRY_SHADER);
        Shader fragmentShader = new Shader(GL20.GL_FRAGMENT_SHADER);

        try {
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

    private void createVBOs() {
        particleCenterPositionVBO = BufferObject.createVBO();
        particleSideScalesVBO = BufferObject.createVBO();
        particleNormalVectorVBO = BufferObject.createVBO();
        particleLocalAnglesVBO = BufferObject.createVBO();
        particleAttributeVBO = BufferObject.createVBO();
        particleTextureCoordAABBVBO = BufferObject.createVBO();

        BufferObject.bindBuffer(particleCenterPositionVBO);
        BufferObject.bindBuffer(particleSideScalesVBO);
        BufferObject.bindBuffer(particleNormalVectorVBO);
        BufferObject.bindBuffer(particleLocalAnglesVBO);
        BufferObject.bindBuffer(particleAttributeVBO);
        BufferObject.bindBuffer(particleTextureCoordAABBVBO);

        BufferObject.bindNone(particleCenterPositionVBO);
    }

    private void createVAO() {
        vao = VAO.createVAO();


        VAO.bindVAO(vao);


        BufferObject.bindBuffer(particleCenterPositionVBO);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleSideScalesVBO);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleNormalVectorVBO);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleLocalAnglesVBO);
        GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleAttributeVBO);
        GL20.glVertexAttribPointer(4, 2, GL11.GL_FLOAT, false, 0, 0);

        BufferObject.bindBuffer(particleTextureCoordAABBVBO);
        GL20.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, 0, 0);


        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL20.glEnableVertexAttribArray(4);
        GL20.glEnableVertexAttribArray(5);


        VAO.bindNone();

        BufferObject.bindNone(particleCenterPositionVBO);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL20.glDisableVertexAttribArray(5);
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
        int countParticles = particleList.size();
        int countPrimitivePerBuffer;


        countPrimitivePerBuffer = countParticles * 3;
        if (particleCenterPositionBuffer == null || particleCenterPositionBuffer.capacity() != countPrimitivePerBuffer) {
            particleCenterPositionBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);

            BufferObject.bindBuffer(particleCenterPositionVBO);
            BufferObject.bufferData(particleCenterPositionVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        }

        countPrimitivePerBuffer = countParticles * 2;
        if (particleSideScalesBuffer == null || particleSideScalesBuffer.capacity() != countPrimitivePerBuffer) {
            particleSideScalesBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);

            BufferObject.bindBuffer(particleSideScalesVBO);
            BufferObject.bufferData(particleSideScalesVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        }

        countPrimitivePerBuffer = countParticles * 3;
        if (particleNormalVectorBuffer == null || particleNormalVectorBuffer.capacity() != countPrimitivePerBuffer) {
            particleNormalVectorBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);

            BufferObject.bindBuffer(particleNormalVectorVBO);
            BufferObject.bufferData(particleNormalVectorVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        }

        countPrimitivePerBuffer = countParticles * 3;
        if (particleLocalAnglesBuffer == null || particleLocalAnglesBuffer.capacity() != countPrimitivePerBuffer) {
            particleLocalAnglesBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);

            BufferObject.bindBuffer(particleLocalAnglesVBO);
            BufferObject.bufferData(particleLocalAnglesVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        }

        countPrimitivePerBuffer = countParticles * 2;
        if (particleAttributeBuffer == null || particleAttributeBuffer.capacity() != countPrimitivePerBuffer) {
            particleAttributeBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);

            BufferObject.bindBuffer(particleAttributeVBO);
            BufferObject.bufferData(particleAttributeVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        }

        countPrimitivePerBuffer = countParticles * 4;
        if (particleTextureCoordAABBBuffer == null || particleTextureCoordAABBBuffer.capacity() != countPrimitivePerBuffer) {
            particleTextureCoordAABBBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);

            BufferObject.bindBuffer(particleTextureCoordAABBVBO);
            BufferObject.bufferData(particleTextureCoordAABBVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        }


        BufferObject.bindNone(particleCenterPositionVBO);
    }

    private void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor) {
        particleCenterPositionBuffer.clear();
        particleSideScalesBuffer.clear();
        particleNormalVectorBuffer.clear();
        particleLocalAnglesBuffer.clear();
        particleAttributeBuffer.clear();
        particleTextureCoordAABBBuffer.clear();


        for (IParticle particle : particleList) {
            particle.computeInterpolatedPosition(particleCenterPosition, interpolationFactor);
            particle.computeNormalVector(particleNormalVector, xCamera, yCamera, zCamera, particleCenterPosition);
            Vector2f halfSizes = particle.getHalfSizes();
            Vector3f localAngles = particle.getLocalRotateAngles();
            TextureCoord texCoord = particle.getTextureCoord();
            float alpha = particle.getAlpha();
            float light = particle.getLight();


            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());

            particleSideScalesBuffer.put(halfSizes.getX()).put(halfSizes.getY());

            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());

            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());

            particleAttributeBuffer.put(alpha).put(light);

            particleTextureCoordAABBBuffer.put(texCoord.getCoordX_0()).put(texCoord.getCoordY_0()).put(texCoord.getCoordX_2()).put(texCoord.getCoordY_2());
        }


        particleCenterPositionBuffer.flip();
        particleSideScalesBuffer.flip();
        particleNormalVectorBuffer.flip();
        particleLocalAnglesBuffer.flip();
        particleAttributeBuffer.flip();
        particleTextureCoordAABBBuffer.flip();


        BufferObject.bindBuffer(particleCenterPositionVBO);
        BufferObject.bufferSubData(particleCenterPositionVBO, 0, particleCenterPositionBuffer);

        BufferObject.bindBuffer(particleSideScalesVBO);
        BufferObject.bufferSubData(particleSideScalesVBO, 0, particleSideScalesBuffer);

        BufferObject.bindBuffer(particleNormalVectorVBO);
        BufferObject.bufferSubData(particleNormalVectorVBO, 0, particleNormalVectorBuffer);

        BufferObject.bindBuffer(particleLocalAnglesVBO);
        BufferObject.bufferSubData(particleLocalAnglesVBO, 0, particleLocalAnglesBuffer);

        BufferObject.bindBuffer(particleAttributeVBO);
        BufferObject.bufferSubData(particleAttributeVBO, 0, particleAttributeBuffer);

        BufferObject.bindBuffer(particleTextureCoordAABBVBO);
        BufferObject.bufferSubData(particleTextureCoordAABBVBO, 0, particleTextureCoordAABBBuffer);


        BufferObject.bindNone(particleCenterPositionVBO);
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

