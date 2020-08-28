package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
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
    protected void assembleShaderProgram() {
        Shader vertexShader = Shader.createShaderObject(GL20.GL_VERTEX_SHADER,
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shaders/simple_shader_particle/particle_vert.glsl"));

        Shader fragmentShader = Shader.createShaderObject(GL20.GL_FRAGMENT_SHADER,
                new ResourceLocation(ParticleSystemMod.MOD_ID, "shaders/simple_shader_particle/particle_frag.glsl"));

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
        //particle color factor VBO
        vertexAttributes[1] = new VertexAttribVBO(1, 4, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle side scales VBO
        vertexAttributes[2] = new VertexAttribVBO(2, 2, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle center position VBO
        vertexAttributes[3] = new VertexAttribVBO(3, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle normal vector VBO
        vertexAttributes[4] = new VertexAttribVBO(4, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle local angles VBO
        vertexAttributes[5] = new VertexAttribVBO(5, 3, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);
        //particle texCoord VBO
        vertexAttributes[6] = new VertexAttribVBO(6, 2, GL11.GL_FLOAT, BufferObject.createVBO(), null, vboDataManager, GL15.GL_STREAM_DRAW);

        int intervalTimeUpdate = SomberUtils.timeToTick(0, 1, 0);
        float expansionFactor = 1.5F;

        for (int i = 0; i < vertexAttributes.length; i++) {
            vertexAttributes[i].addVBOInVBODataManager(intervalTimeUpdate, expansionFactor);
        }
    }

    @Override
    protected void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor) {
        FloatBuffer particlePositionBuffer = vertexAttributes[0].getVboBuffer();
        FloatBuffer particleColorFactorBuffer = vertexAttributes[1].getVboBuffer();
        FloatBuffer particleSideScalesBuffer = vertexAttributes[2].getVboBuffer();
        FloatBuffer particleCenterPositionBuffer = vertexAttributes[3].getVboBuffer();
        FloatBuffer particleNormalVectorBuffer = vertexAttributes[4].getVboBuffer();
        FloatBuffer particleLocalAnglesBuffer = vertexAttributes[5].getVboBuffer();
        FloatBuffer particleTexCoordBuffer = vertexAttributes[6].getVboBuffer();

        particlePositionBuffer.clear();
        particleColorFactorBuffer.clear();
        particleSideScalesBuffer.clear();
        particleCenterPositionBuffer.clear();
        particleNormalVectorBuffer.clear();
        particleLocalAnglesBuffer.clear();
        particleTexCoordBuffer.clear();


        for (IParticle particle : particleList) {
            particle.computeInterpolatedPosition(particleCenterPosition, interpolationFactor);
            particle.computeNormalVector(particleNormalVector, xCamera, yCamera, zCamera, particleCenterPosition);
            Vector2f halfSizes = particle.getHalfSizes();
            Vector3f localAngles = particle.getLocalRotateAngles();
            float[] colorFactor = particle.getColorFactor();
            String iconName = particle.getIconName();
            IIcon icon = getParticleTextureAtlas().getAtlasIcon(iconName);


            particlePositionBuffer.put(-0.5F).put(-0.5F);
            particlePositionBuffer.put(+0.5F).put(-0.5F);
            particlePositionBuffer.put(+0.5F).put(+0.5F);
            particlePositionBuffer.put(-0.5F).put(+0.5F);

            particleColorFactorBuffer.put(colorFactor[0]).put(colorFactor[1]).put(colorFactor[2]).put(colorFactor[3]);
            particleColorFactorBuffer.put(colorFactor[0]).put(colorFactor[1]).put(colorFactor[2]).put(colorFactor[3]);
            particleColorFactorBuffer.put(colorFactor[0]).put(colorFactor[1]).put(colorFactor[2]).put(colorFactor[3]);
            particleColorFactorBuffer.put(colorFactor[0]).put(colorFactor[1]).put(colorFactor[2]).put(colorFactor[3]);

            particleSideScalesBuffer.put(halfSizes.getX()).put(halfSizes.getY());
            particleSideScalesBuffer.put(halfSizes.getX()).put(halfSizes.getY());
            particleSideScalesBuffer.put(halfSizes.getX()).put(halfSizes.getY());
            particleSideScalesBuffer.put(halfSizes.getX()).put(halfSizes.getY());

            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());
            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());
            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());
            particleCenterPositionBuffer.put(particleCenterPosition.getX()).put(particleCenterPosition.getY()).put(particleCenterPosition.getZ());

            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());
            particleNormalVectorBuffer.put(particleNormalVector.getX()).put(particleNormalVector.getY()).put(particleNormalVector.getZ());

            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());
            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());
            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());
            particleLocalAnglesBuffer.put(localAngles.getX()).put(localAngles.getY()).put(localAngles.getZ());

            particleTexCoordBuffer.put(icon.getMinU()).put(icon.getMinV());
            particleTexCoordBuffer.put(icon.getMaxU()).put(icon.getMinV());
            particleTexCoordBuffer.put(icon.getMaxU()).put(icon.getMaxV());
            particleTexCoordBuffer.put(icon.getMinU()).put(icon.getMaxV());
        }


        particlePositionBuffer.flip();
        particleColorFactorBuffer.flip();
        particleSideScalesBuffer.flip();
        particleCenterPositionBuffer.flip();
        particleNormalVectorBuffer.flip();
        particleLocalAnglesBuffer.flip();
        particleTexCoordBuffer.flip();


        for (int i = 0; i < vertexAttributes.length; i++) {
            vertexAttributes[i].bufferSubData(0);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
