package ru.somber.particlesystem.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.opengl.ShaderProgram;
import ru.somber.clientutil.opengl.VAO;
import ru.somber.clientutil.opengl.VBODataManager;
import ru.somber.clientutil.opengl.VertexAttribVBO;
import ru.somber.commonutil.SomberUtils;
import ru.somber.particlesystem.particle.IParticle;
import ru.somber.particlesystem.texture.ParticleAtlasTexture;

import java.nio.FloatBuffer;
import java.util.List;

public abstract class AbstractShaderRenderer implements IParticleRenderer {

    private ParticleAtlasTexture textureAtlas;

    protected boolean isShaderInit;
    protected ShaderProgram shaderProgram;

    protected VBODataManager vboDataManager;
    protected VAO vao;
    protected VertexAttribVBO[] vertexAttributes;

    protected int tickUpdate;

    protected Matrix4f projectionMatrix;
    protected Matrix4f cameraMatrix;
    protected Matrix4f projectionAndCameraMatrix;

    protected FloatBuffer buffer16;

    protected float xCamera;
    protected float yCamera;
    protected float zCamera;

    /** Вынесено в переменные объекта, чтобы постоянное не создавать в методе. */
    protected Vector3f particleCenterPosition, particleNormalVector;


    public AbstractShaderRenderer() {
        isShaderInit = false;

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
    public ParticleAtlasTexture getParticleTextureAtlas() {
        return textureAtlas;
    }

    @Override
    public void setParticleTextureAtlas(ParticleAtlasTexture textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        if (! isShaderInit) {
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

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureAtlas.getGlTextureId());
    }

    @Override
    public abstract void render(List<IParticle> particleList, float interpolationFactor);

    @Override
    public void postRender(List<IParticle> particleList, float interpolationFactor) {
        VAO.bindNone();


        for (VertexAttribVBO attrib : vertexAttributes) {
            attrib.disableVertexAttribArray();
            attrib.disableVertexAttribDivisor();
        }


        GL20.glUseProgram(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);


        checkError();
    }

    @Override
    public void update(List<IParticle> particleList) {
        if (! isShaderInit) {
            initShaderAndBuffers();
        }

        tickUpdate++;

        allocateVBOs(particleList);
    }


    protected void initShaderAndBuffers() {
        assembleShaderProgram();
        createVertexAttribVBOs();
        createVAO();

        isShaderInit = true;
    }

    protected void createVAO() {
        vao = VAO.createVAO();

        VAO.bindVAO(vao);

        for (VertexAttribVBO attrib : vertexAttributes) {
            attrib.enableVertexAttribArray();
            attrib.enableVertexAttribDivisor();
            attrib.setVertexAttribPointerVBO();
        }

        VAO.bindNone();


        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        for (VertexAttribVBO attrib : vertexAttributes) {
            attrib.disableVertexAttribArray();
            attrib.disableVertexAttribDivisor();
        }
    }

    protected void allocateVBOs(List<IParticle> particleList) {
        //Резервиуется место под 1000 частиц,
        //чтобы на малых количествах частиц не нужно было постояноо изменять размеры памяти.
        int countParticles = Math.max(particleList.size(), 1000);

        for (VertexAttribVBO attrib : vertexAttributes) {
            attrib.allocateVBO(countParticles);
        }
    }

    protected void checkError() {
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


    protected abstract void prepareUniforms();

    protected abstract void assembleShaderProgram();

    protected abstract void createVertexAttribVBOs();

    protected abstract void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor);

}
