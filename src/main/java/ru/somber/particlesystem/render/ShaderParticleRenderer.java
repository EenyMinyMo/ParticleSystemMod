package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
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
public class ShaderParticleRenderer extends AbstractParticleRenderer {

    private ResourceLocation vertexShaderCodeLocation =
            new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/simple_shader_particle/particle_vert.glsl");
    private ResourceLocation fragmentShaderCodeLocation =
            new ResourceLocation(ParticleSystemMod.MOD_ID, "shader/simple_shader_particle/particle_frag.glsl");

    private ShaderProgram shaderProgram;

    private VAO vao;

    private BufferObject particlePositionVBO;
    private BufferObject particleLocalAnglesVBO;
    private BufferObject particleLookAtVBO;
    private BufferObject particleAttributeVBO;
    private BufferObject particleCenterPositionVBO;

    private FloatBuffer particlePositionBuffer;
    private FloatBuffer particleLocalAnglesBuffer;
    private FloatBuffer particleLookAtBuffer;
    private FloatBuffer particleAttributeBuffer;
    private FloatBuffer particleCenterPositionBuffer;

    private Matrix4f projectionMatrix;
    private Matrix4f cameraMatrix;
    private Matrix4f projectionAndCameraMatrix;

    private FloatBuffer buffer8;
    private FloatBuffer buffer16;

    private float xCamera;
    private float yCamera;
    private float zCamera;

    /** Вынесено в переменные объекта, чтобы постоянное не создавать в методе. */
    private Vector3f particlePosition, particleNormalVector;

    public ShaderParticleRenderer() {
        vao = VAO.createVAO();

        buffer8 = BufferUtils.createFloatBuffer(8);
        buffer16 = BufferUtils.createFloatBuffer(16);

        projectionMatrix = new Matrix4f();
        cameraMatrix = new Matrix4f();
        projectionAndCameraMatrix = new Matrix4f();

        particlePosition = new Vector3f();
        particleNormalVector = new Vector3f();

        createShaderProgram();
        createVBOs();
        createVAO();
    }

    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        xCamera = SomberUtils.interpolateBetween((float) Minecraft.getMinecraft().renderViewEntity.prevPosX, (float) Minecraft.getMinecraft().renderViewEntity.posX, interpolationFactor);
        yCamera = SomberUtils.interpolateBetween((float) Minecraft.getMinecraft().renderViewEntity.prevPosY, (float) Minecraft.getMinecraft().renderViewEntity.posY, interpolationFactor);
        zCamera = SomberUtils.interpolateBetween((float) Minecraft.getMinecraft().renderViewEntity.prevPosZ, (float) Minecraft.getMinecraft().renderViewEntity.posZ, interpolationFactor);

        buffer16.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, buffer16);
        projectionMatrix.load(buffer16);
        buffer16.clear();

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer16);
        cameraMatrix.load(buffer16);
        buffer16.clear();

        Matrix4f.mul(projectionMatrix, cameraMatrix, projectionAndCameraMatrix);

        GL20.glUseProgram(shaderProgram.getShaderProgramID());
        VAO.bindVAO(vao);

        prepareUniforms();

    }

    @Override
    public void render(List<IParticle> particleList, float interpolationFactor) {
        prepareDataVBOs(particleList, interpolationFactor);


    }

    @Override
    public void postRender(List<IParticle> particleList, float interpolationFactor) {
        VAO.bindNone();
        GL20.glUseProgram(0);

    }

    @Override
    public void update(List<IParticle> particleList) {
        allocateVBOs(particleList);
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
        particleLocalAnglesVBO = BufferObject.createVBO();
        particleLookAtVBO = BufferObject.createVBO();
        particleAttributeVBO = BufferObject.createVBO();
        particleCenterPositionVBO = BufferObject.createVBO();

        BufferObject.bindBuffer(particlePositionVBO);
        BufferObject.bindBuffer(particleLocalAnglesVBO);
        BufferObject.bindBuffer(particleLookAtVBO);
        BufferObject.bindBuffer(particleAttributeVBO);
        BufferObject.bindBuffer(particleCenterPositionVBO);

        BufferObject.bindNone(particlePositionVBO);
    }

    private void createVAO() {
        VAO.bindVAO(vao);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL20.glEnableVertexAttribArray(4);

        BufferObject.bindBuffer(particlePositionVBO);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        BufferObject.bindBuffer(particleLocalAnglesVBO);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);
        BufferObject.bindBuffer(particleLookAtVBO);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
        BufferObject.bindBuffer(particleAttributeVBO);
        GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, 0, 0);
        BufferObject.bindBuffer(particleCenterPositionVBO);
        GL20.glVertexAttribPointer(4, 3, GL11.GL_FLOAT, false, 0, 0);

        VAO.bindNone();

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
    }

    public void prepareUniforms() {
        int uniformLocation;

        uniformLocation = GL20.glGetUniformLocation(shaderProgram.getShaderProgramID(), "cameraPosition");
        GL20.glUniform3f(uniformLocation, xCamera, yCamera, zCamera);

        uniformLocation = GL20.glGetUniformLocation(shaderProgram.getShaderProgramID(), "projectionAndCameraMatrix");
        projectionAndCameraMatrix.store(buffer16);
        GL20.glUniformMatrix4(uniformLocation, false, buffer16);
        buffer16.clear();

        uniformLocation = GL20.glGetUniformLocation(shaderProgram.getShaderProgramID(), "particleTexture");
        GL20.glUniform1i(uniformLocation, 0);
    }

    public void allocateVBOs(List<IParticle> particleList) {
        int countParticles = particleList.size();
        int countPrimitivePerBuffer;

        BufferObject.bindBuffer(particlePositionVBO);
        //данные буду браться для отдельных вершин
        //4 позиции на частицу, у каждой позиции 3 вершины, каждая вешина занимает 4 байта
        countPrimitivePerBuffer = countParticles * 4 * 3;
        BufferObject.bufferData(particlePositionVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        if (particlePositionBuffer == null || particlePositionBuffer.capacity() != countPrimitivePerBuffer) {
            particlePositionBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);
        }

        BufferObject.bindBuffer(particleLocalAnglesVBO);
        //данные буду браться для отдельной частицы, а не для отдельных вершин
        //у вектора с углами 3 компоненты, каждый угол занимает 4 байта
        countPrimitivePerBuffer = countParticles * 3;
        BufferObject.bufferData(particleLocalAnglesVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        if (particleLocalAnglesBuffer == null || particleLocalAnglesBuffer.capacity() != countPrimitivePerBuffer) {
            particleLocalAnglesBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);
        }

        BufferObject.bindBuffer(particleLookAtVBO);
        //данные буду браться для отдельной частицы, а не для отдельных вершин
        //у вектора 3 компоненты, каждая компонента занимает 4 байта
        countPrimitivePerBuffer = countParticles * 3;
        BufferObject.bufferData(particleLookAtVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        if (particleLookAtBuffer == null || particleLookAtBuffer.capacity() != countPrimitivePerBuffer) {
            particleLookAtBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);
        }

        BufferObject.bindBuffer(particleAttributeVBO);
        //данные буду браться для отдельных вершин
        //4 атрибута на частицу, у каждого атрибута 4 компоненты, каждая компонента занимает 4 байта
        countPrimitivePerBuffer = countParticles * 4 * 4;
        BufferObject.bufferData(particleAttributeVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        if (particleAttributeBuffer == null || particleAttributeBuffer.capacity() != countPrimitivePerBuffer) {
            particleAttributeBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);
        }

        BufferObject.bindBuffer(particleCenterPositionVBO);
        //данные буду браться для отдельной частицы, а не для отдельных вершин
        //у вектора 3 компоненты, каждая компонента занимает 4 байта
        countPrimitivePerBuffer = countParticles * 3;
        BufferObject.bufferData(particleCenterPositionVBO, countPrimitivePerBuffer * 4, GL15.GL_STREAM_DRAW);
        if (particleCenterPositionBuffer == null || particleCenterPositionBuffer.capacity() != countPrimitivePerBuffer) {
            particleCenterPositionBuffer = BufferUtils.createFloatBuffer(countPrimitivePerBuffer);
        }

        BufferObject.bindNone(particlePositionVBO);
    }

    private void prepareDataVBOs(List<IParticle> particleList, float interpolateFactor) {
        particlePositionBuffer.clear();
        particleLocalAnglesBuffer.clear();
        particleLookAtBuffer.clear();
        particleAttributeBuffer.clear();
        particleCenterPositionBuffer.clear();

        for (IParticle particle : particleList) {
            particle.computeInterpolatedPosition(particlePosition, interpolateFactor);
            particle.computeNormalVector(particleNormalVector, xCamera, yCamera, zCamera, particlePosition);
            Vector2f hafSizes = particle.getHalfSizes();
            Vector3f localAngles = particle.getLocalRotateAngles();
            TextureCoord texCoord = particle.getTextureCoord();
            float alpha = particle.getAlpha();
            float light = particle.getLight();

            particlePositionBuffer.put(particlePosition.getX() - hafSizes.getX()).put(particlePosition.getY() - hafSizes.getY()).put(particlePosition.getZ());
            particlePositionBuffer.put(particlePosition.getX() + hafSizes.getX()).put(particlePosition.getY() - hafSizes.getY()).put(particlePosition.getZ());
            particlePositionBuffer.put(particlePosition.getX() + hafSizes.getX()).put(particlePosition.getY() + hafSizes.getY()).put(particlePosition.getZ());
            particlePositionBuffer.put(particlePosition.getX() - hafSizes.getX()).put(particlePosition.getY() + hafSizes.getY()).put(particlePosition.getZ());

            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());

            particleLookAtBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());

            particleAttributeBuffer.put(texCoord.getCoordX_0()).put(texCoord.getCoordY_0()).put(alpha).put(light);
            particleAttributeBuffer.put(texCoord.getCoordX_1()).put(texCoord.getCoordY_1()).put(alpha).put(light);
            particleAttributeBuffer.put(texCoord.getCoordX_2()).put(texCoord.getCoordY_2()).put(alpha).put(light);
            particleAttributeBuffer.put(texCoord.getCoordX_3()).put(texCoord.getCoordY_3()).put(alpha).put(light);

            particleCenterPositionBuffer.put(particlePosition.getX()).put(particlePosition.getY()).put(particlePosition.getZ());
        }

        particlePositionBuffer.flip();
        particleLocalAnglesBuffer.flip();
        particleLookAtBuffer.flip();
        particleAttributeBuffer.flip();
        particleCenterPositionBuffer.flip();

        BufferObject.bindBuffer(particlePositionVBO);
        BufferObject.bufferSubData(particlePositionVBO, 0, particlePositionBuffer);

        BufferObject.bindBuffer(particleLocalAnglesVBO);
        BufferObject.bufferSubData(particleLocalAnglesVBO, 0, particleLocalAnglesBuffer);

        BufferObject.bindBuffer(particleLookAtVBO);
        BufferObject.bufferSubData(particleLookAtVBO, 0, particleLookAtBuffer);

        BufferObject.bindBuffer(particleAttributeVBO);
        BufferObject.bufferSubData(particleAttributeVBO, 0, particleAttributeBuffer);

        BufferObject.bindBuffer(particleCenterPositionVBO);
        BufferObject.bufferSubData(particleCenterPositionVBO, 0, particleCenterPositionBuffer);

        BufferObject.bindNone(particlePositionVBO);
    }

}
