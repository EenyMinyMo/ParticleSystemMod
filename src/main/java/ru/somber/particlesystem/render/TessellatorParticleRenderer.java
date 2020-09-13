package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.commonutil.Axis;
import ru.somber.commonutil.SomberCommonUtils;
import ru.somber.particlesystem.particle.IParticle;
import ru.somber.particlesystem.texture.ParticleAtlasIcon;
import ru.somber.particlesystem.texture.ParticleAtlasTexture;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TessellatorParticleRenderer implements IParticleRenderer {

    /** Вынесено в переменные объекта, чтобы постоянное не создавать в методе. */
    private Vector3f interpolatedCenterPosition;
    private Vector3f interpolatedNormalVector;
    private Vector3f interpolatedRotatedAngles;
    private Vector2f interpolatedHalfSizes;

    private float xCamera;
    private float yCamera;
    private float zCamera;

    private Vector3f upAux;

    private ParticleAtlasTexture textureAtlas;


    public TessellatorParticleRenderer() {
        interpolatedCenterPosition = new Vector3f();
        interpolatedNormalVector = new Vector3f();
        interpolatedRotatedAngles = new Vector3f();
        interpolatedHalfSizes = new Vector2f();

        upAux = new Vector3f();
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
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureAtlas.getGlTextureId());

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        xCamera = SomberCommonUtils.interpolateMoveX(player, interpolationFactor);
        yCamera = SomberCommonUtils.interpolateMoveY(player, interpolationFactor);
        zCamera = SomberCommonUtils.interpolateMoveZ(player, interpolationFactor);

        GL11.glPushMatrix();
        GL11.glTranslatef(-xCamera, -yCamera, -zCamera);
    }

    @Override
    public void render(List<IParticle> particleList, float interpolationFactor) {
        particleList.forEach((IParticle particle) -> {
            this.renderParticle(particle, interpolationFactor);
        });
    }

    @Override
    public void postRender(List<IParticle> particleList, float interpolationFactor) {
        GL11.glPopMatrix();

        GL11.glPopAttrib();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    public void update(List<IParticle> particleList) {}

    private void renderParticle(IParticle particle, float interpolationFactor) {
        Tessellator tessellator = Tessellator.instance;

        particle.computeInterpolatedPosition(interpolatedCenterPosition, interpolationFactor);
        particle.computeInterpolatedHalfSizes(interpolatedHalfSizes, interpolationFactor);
        particle.computeInterpolatedRotateAngles(interpolatedRotatedAngles, interpolationFactor);
        ParticleAtlasIcon icon = particle.getParticleIcon();

        GL11.glPushMatrix();
        GL11.glTranslatef(interpolatedCenterPosition.getX(), interpolatedCenterPosition.getY(), interpolatedCenterPosition.getZ());
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        this.applyParticleTransform(particle, interpolatedCenterPosition);

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(particle.getRedFactor(), particle.getGreenFactor(), particle.getBlueFactor(), particle.getAlphaFactor());
//        tessellator.setBrightness(240);

        tessellator.addVertexWithUV(-interpolatedHalfSizes.getX(), -interpolatedHalfSizes.getY(), 0.0, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(interpolatedHalfSizes.getX(),  -interpolatedHalfSizes.getY(), 0.0, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(interpolatedHalfSizes.getX(),  interpolatedHalfSizes.getY(),  0.0, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(-interpolatedHalfSizes.getX(), interpolatedHalfSizes.getY(),  0.0, icon.getMinU(), icon.getMaxV());

        tessellator.draw();

        GL11.glPopMatrix();
    }

    private void applyParticleTransform(IParticle particle, Vector3f particleToCamera) {
        particleToCamera.set(xCamera - interpolatedCenterPosition.getX(), yCamera - interpolatedCenterPosition.getY(), zCamera - interpolatedCenterPosition.getZ());
        upAux.set(0.0f, 0.0f, 1.0f);

        if (particle.rotateAxis() != Axis.NONE_AXIS) {

            if (particle.rotateAxis() == Axis.ALL_AXIS) {
                Vector3f particleToCameraProj = new Vector3f(particleToCamera);
                particleToCameraProj.setY(0.0f);

                particleToCamera.normalise();
                particleToCameraProj.normalise();

                float angleCosine = Vector3f.dot(upAux, particleToCameraProj);
                Vector3f.cross(upAux, particleToCameraProj, upAux);

                if (angleCosine < 0.9999 && angleCosine > -0.9999) {
                    GL11.glRotatef((float) (Math.acos(angleCosine) * 180.0 / Math.PI), upAux.getX(), upAux.getY(), upAux.getZ());
                }

                angleCosine = Vector3f.dot(particleToCameraProj, particleToCamera);
                if (angleCosine < 0.9999 && angleCosine > -0.9999) {
                    if (particleToCamera.getY() < 0.0f) {
                        GL11.glRotatef((float) (Math.acos(angleCosine) * 180.0 / Math.PI), 1.0f, 0.0f, 0.0f);
                    } else {
                        GL11.glRotatef((float) (Math.acos(angleCosine) * 180.0 / Math.PI), -1.0f, 0.0f, 0.0f);
                    }
                }
            } else {
                float angleCosine2 = 0.0f;

                if (particle.rotateAxis() == Axis.ABSCISSA_AXIS) {
                    particleToCamera.setX(0.0f);
                    particleToCamera.normalise();

                    angleCosine2 = Vector3f.dot(upAux, particleToCamera);
                    Vector3f.cross(upAux, particleToCamera, upAux);
                } else if (particle.rotateAxis() == Axis.ORDINATE_AXIS) {
                    particleToCamera.setY(0.0f);
                    particleToCamera.normalise();

                    angleCosine2 = Vector3f.dot(upAux, particleToCamera);
                    Vector3f.cross(upAux, particleToCamera, upAux);
                } else {
                    particleToCamera.setZ(0.0f);
                    particleToCamera.normalise();

                    angleCosine2 = Vector3f.dot(upAux, particleToCamera);
                    Vector3f.cross(upAux, particleToCamera, upAux);
                }

                if (angleCosine2 < 0.9999 && angleCosine2 > -0.9999) {
                    GL11.glRotatef((float) (Math.acos(angleCosine2) * 180.0 / Math.PI), upAux.getX(), upAux.getY(), upAux.getZ());
                }
            }
        }

        GL11.glRotatef((float) Math.toDegrees(interpolatedRotatedAngles.getX()), 1.0f, 0.0f, 0.0f);
        GL11.glRotatef((float) Math.toDegrees(interpolatedRotatedAngles.getY()), 0.0f, 1.0f, 0.0f);
        GL11.glRotatef((float) Math.toDegrees(interpolatedRotatedAngles.getZ()), 0.0f, 0.0f, 1.0f);
    }

}
