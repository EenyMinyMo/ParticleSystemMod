package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.opengl.BufferObject;
import ru.somber.clientutil.opengl.Shader;
import ru.somber.clientutil.opengl.ShaderProgram;
import ru.somber.clientutil.opengl.VertexAttribVBO;
import ru.somber.commonutil.SomberUtils;
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
    }


    @Override
    protected void assembleShaderProgram() {
        Shader vertexShader = Shader.createShaderObject(GL20.GL_VERTEX_SHADER,
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shaders/instance_shader_particle/particle_vert.glsl"));

        Shader fragmentShader = Shader.createShaderObject(GL20.GL_FRAGMENT_SHADER,
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shaders/instance_shader_particle/particle_frag.glsl"));

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
    protected void createVertexAttribVBOs() {
        vertexAttributes = new VertexAttribVBO[7];

        //particle position VBO
        vertexAttributes[0] = new VertexAttribVBO(0, 2, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        vertexAttributes[0].setVertexAttribDivisor(0);
        //particle center position VBO
        vertexAttributes[1] = new VertexAttribVBO(1, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        vertexAttributes[1].setVertexAttribDivisor(1);
        //particle normal vector VBO
        vertexAttributes[2] = new VertexAttribVBO(2, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        vertexAttributes[2].setVertexAttribDivisor(1);
        //particle local angles VBO
        vertexAttributes[3] = new VertexAttribVBO(3, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        vertexAttributes[3].setVertexAttribDivisor(1);
        //particle color factor VBO
        vertexAttributes[4] = new VertexAttribVBO(4, 4, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        vertexAttributes[4].setVertexAttribDivisor(1);
        //particle texCoord VBO
        vertexAttributes[5] = new VertexAttribVBO(5, 4, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        vertexAttributes[5].setVertexAttribDivisor(1);
        //particle side scales VBO
        vertexAttributes[6] = new VertexAttribVBO(6, 2, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        vertexAttributes[6].setVertexAttribDivisor(1);

        int intervalTimeUpdate = SomberUtils.timeToTick(0, 1, 0);
        float expansionFactor = 1.5F;

        for (int i = 0; i < vertexAttributes.length; i++) {
            vertexAttributes[i].addVBOInVBODataManager(intervalTimeUpdate, expansionFactor);
        }
    }

    @Override
    protected void allocateVBOs(List<IParticle> particleList) {
        //Резервиуется место под 1000 частиц,
        //чтобы на малых количествах частиц не нужно было постояноо изменять размеры памяти.
        int countParticles = Math.max(particleList.size(), 1000);

        vertexAttributes[0].allocateVBO(4);
        for (int i = 1; i < vertexAttributes.length; i++) {
            vertexAttributes[i].allocateVBO(countParticles);
        }
    }

    @Override
    protected void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor) {
        FloatBuffer particlePositionBuffer = vertexAttributes[0].getVboBuffer();
        FloatBuffer particleCenterPositionBuffer = vertexAttributes[1].getVboBuffer();
        FloatBuffer particleNormalVectorBuffer = vertexAttributes[2].getVboBuffer();
        FloatBuffer particleLocalAnglesBuffer = vertexAttributes[3].getVboBuffer();
        FloatBuffer particleColorFactorBuffer = vertexAttributes[4].getVboBuffer();
        FloatBuffer particleTextureCoordAABBBuffer = vertexAttributes[5].getVboBuffer();
        FloatBuffer particleScaleBuffer = vertexAttributes[6].getVboBuffer();

        particlePositionBuffer.clear();
        particleCenterPositionBuffer.clear();
        particleNormalVectorBuffer.clear();
        particleLocalAnglesBuffer.clear();
        particleColorFactorBuffer.clear();
        particleTextureCoordAABBBuffer.clear();
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
            float[] colorFactor = particle.getColorFactor();
            String iconName = particle.getIconName();
            IIcon icon = getParticleTextureAtlas().getAtlasIcon(iconName);


            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());

            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());

            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());

            particleColorFactorBuffer.put(colorFactor);

            particleTextureCoordAABBBuffer.put(icon.getMinU()).put(icon.getMinV()).put(icon.getMaxU()).put(icon.getMaxV());

            particleScaleBuffer.put(halfSizes.getX()).put(halfSizes.getY());
        }


        particlePositionBuffer.flip();
        particleCenterPositionBuffer.flip();
        particleNormalVectorBuffer.flip();
        particleLocalAnglesBuffer.flip();
        particleColorFactorBuffer.flip();
        particleTextureCoordAABBBuffer.flip();
        particleScaleBuffer.flip();


        for (int i = 0; i < vertexAttributes.length; i++) {
            vertexAttributes[i].bufferSubData(0);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
