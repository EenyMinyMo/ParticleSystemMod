package ru.somber.particlesystem.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.opengl.ShaderProgram;
import ru.somber.clientutil.opengl.VAO;
import ru.somber.clientutil.opengl.vbo.VBODataManagerMap;
import ru.somber.clientutil.opengl.vbo.VertexAttribVBO;
import ru.somber.clientutil.textureatlas.AtlasTexture;
import ru.somber.commonutil.SomberCommonUtils;
import ru.somber.particlesystem.particle.IParticle;

import java.nio.FloatBuffer;
import java.util.List;

public abstract class AbstractShaderRenderer implements IParticleRenderer {

    /** Текстурный атлас, из которого будут браться текстуры для отрисовки частиц. */
    private AtlasTexture atlasTexture;

    /** true - шейдеры инициализированы, иначе false. */
    protected boolean isShaderInit;
    /** Объект шейдерной программы, через которую рисуются частицы. */
    protected ShaderProgram shaderProgram;

    /** Менеджер VBO и буферов данных, связанный с VBO. */
    protected VBODataManagerMap vboDataManagerMap;
    /** VAO для хранения точек привязки вершинных атрибутов с VBO, а также других модификаторов атрибутов вершин. */
    protected VAO vao;
    /** Массив объектов, связывающих VBO и вертексные атрибуты. */
    protected VertexAttribVBO[] vertexAttributes;

    /** Номер тика обновления. */
    protected int tickUpdate;

    /** Сюда перед рендером должна быть записана матрица проекции. */
    protected Matrix4f projectionMatrix;
    /** Сюда перед рендером должна быть записана матрица преобразования камеры. */
    protected Matrix4f cameraMatrix;
    /** Сюда перед рендером должна быть записана матрица проекции и матрица преобразования камеры. */
    protected Matrix4f projectionAndCameraMatrix;

    /** Служебный буфер на 16 float элементов. */
    protected FloatBuffer buffer16;

    /** Позиция камеры. */
    protected float xCamera, yCamera, zCamera;

    /** Вынесено в переменные объекта, чтобы постоянное не выделять объекты в методе. */
    protected Vector3f particleCenterPosition, particleNormalVector, particleRotationAngles;
    /** Вынесено в переменные объекта, чтобы постоянное не выделять объекты в методе. */
    protected Vector2f particleHalfSizes;


    public AbstractShaderRenderer() {
        isShaderInit = false;

        buffer16 = BufferUtils.createFloatBuffer(16);

        projectionMatrix = new Matrix4f();
        cameraMatrix = new Matrix4f();
        projectionAndCameraMatrix = new Matrix4f();

        particleCenterPosition = new Vector3f();
        particleNormalVector = new Vector3f();
        particleRotationAngles = new Vector3f();
        particleHalfSizes = new Vector2f();

        vboDataManagerMap = new VBODataManagerMap();
        tickUpdate = 0;
    }

    @Override
    public AtlasTexture getAtlasTexture() {
        return atlasTexture;
    }

    @Override
    public void setAtlasTexture(AtlasTexture atlasTexture) {
        this.atlasTexture = atlasTexture;
    }

    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        if (! isShaderInit) {
            initShaderAndBuffers();
        }

        EntityLivingBase renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
        xCamera = SomberCommonUtils.interpolateBetween((float) renderViewEntity.lastTickPosX, (float) renderViewEntity.posX, interpolationFactor);
        yCamera = SomberCommonUtils.interpolateBetween((float) renderViewEntity.lastTickPosY, (float) renderViewEntity.posY, interpolationFactor);
        zCamera = SomberCommonUtils.interpolateBetween((float) renderViewEntity.lastTickPosZ, (float) renderViewEntity.posZ, interpolationFactor);


        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendEquation(GL14.GL_FUNC_ADD);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);


        GL20.glUseProgram(shaderProgram.getShaderProgramID());


        prepareUniforms();
        prepareDataVBOs(particleList, interpolationFactor);


        VAO.bindVAO(vao);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlasTexture.getGlTextureId());
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
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(true);


        checkError(false);
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
        createShaderProgram();
        createVBOsAndVertAttribVBOs();
        createVAO();

        isShaderInit = true;
    }

    /**
     * Создает VAO с данными из массива атрибутов вершин.
     */
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

    /**
     * Производит выделение памяти VBO в зависимости от размеров переданного листа.
     */
    protected void allocateVBOs(List<IParticle> particleList) {
        //Резервиуется место под 1000 частиц,
        //чтобы на малых количествах частиц не нужно было постояноо изменять размеры памяти.
        int countParticles = Math.max(particleList.size(), 1000);

        for (VertexAttribVBO attrib : vertexAttributes) {
            attrib.allocateVBO(countParticles);
        }
    }

    /**
     * Проверяет наличие OpenGL ошибок и логирует их, если они найдены.
     * Если throwException = true, то выбрасывается исключение времени выполнения.
     */
    protected void checkError(boolean throwException) {
        int i = GL11.glGetError();

        Logger logger = LogManager.getLogger();
        String str = "shader program error";

        if (i != 0) {
            String s1 = GLU.gluErrorString(i);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + str);
            logger.error(i + ": " + s1);

            if (throwException) {
                throw new RuntimeException("########## GL ERROR ##########");
            }
        }
    }


    /**
     * Подготавливает юниформы шейдерной программы для отрисовки.
     */
    protected abstract void prepareUniforms();

    /**
     * Создает шейдерную программу для отрисовки частиц.
     */
    protected abstract void createShaderProgram();

    /**
     * Создает VBO-буферы, устанавливает данные и заполняет атрибуты вершин с этими VBO, формирует vboDataManagerMap.
     */
    protected abstract void createVBOsAndVertAttribVBOs();

    /**
     * Подготавливает VBO перед отрисовкой.
     */
    protected abstract void prepareDataVBOs(List<IParticle> particleList, float interpolationFactor);

}
