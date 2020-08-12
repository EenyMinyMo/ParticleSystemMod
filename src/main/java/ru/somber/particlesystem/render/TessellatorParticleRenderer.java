package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import ru.somber.clientutil.opengl.texture.TextureCoord;
import ru.somber.commonutil.Axis;
import ru.somber.commonutil.SomberUtils;
import ru.somber.particlesystem.particle.IParticle;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TessellatorParticleRenderer extends AbstractParticleRenderer {

    /** Вынесено в переменные объекта, чтобы постоянное не создавать в методе. */
    private Vector3f particleToCamera;

    public TessellatorParticleRenderer() {
        particleToCamera = new Vector3f();
    }

    @Override
    public void preRender(List<IParticle> particleList, float interpolationFactor) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    @Override
    public void render(List<IParticle> particleList, float interpolationFactor) {
        particleList.forEach(particle -> this.renderParticle(particle, interpolationFactor));
    }

    @Override
    public void postRender(List<IParticle> particleList, float interpolationFactor) {
        GL11.glPopAttrib();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    public void update(List<IParticle> particleList) {

    }

    private void renderParticle(IParticle particle, float interpolationFactor) {
        Tessellator tessellator = Tessellator.instance;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;

        particle.computeInterpolatedPosition(particleToCamera, interpolationFactor);
        particleToCamera.translate(-SomberUtils.interpolateMoveX((Entity)player, interpolationFactor), -SomberUtils.interpolateMoveY((Entity)player, interpolationFactor), -SomberUtils.interpolateMoveZ((Entity)player, interpolationFactor));

        final Vector2f halfSizes = particle.getHalfSizes();
        final TextureCoord textureCoord = particle.getTextureCoord();

        GL11.glPushMatrix();
        GL11.glTranslatef(particleToCamera.getX(), particleToCamera.getY(), particleToCamera.getZ());

        this.applyParticleTransform(particle, particleToCamera);

        minecraft.renderEngine.bindTexture(particle.getTextureLocation());

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, particle.getAlpha());
        tessellator.setBrightness((int) (particle.getLight() * 240.0f));

        tessellator.addVertexWithUV(- (0.5F) * halfSizes.getX(), - (0.5F) * halfSizes.getY(), 0.0, textureCoord.getCoordX_0(), textureCoord.getCoordY_0());
        tessellator.addVertexWithUV((0.5F) * halfSizes.getX(), - (0.5F) * halfSizes.getY(), 0.0, textureCoord.getCoordX_1(), textureCoord.getCoordY_1());
        tessellator.addVertexWithUV((0.5F) * halfSizes.getX(), (0.5F) * halfSizes.getY(), 0.0, textureCoord.getCoordX_2(), textureCoord.getCoordY_2());
        tessellator.addVertexWithUV(- (0.5F) * halfSizes.getX(), (0.5F) * halfSizes.getY(), 0.0, textureCoord.getCoordX_3(), textureCoord.getCoordY_3());

        tessellator.draw();

        GL11.glPopMatrix();
    }

    private void applyParticleTransform(IParticle particle, Vector3f particleToCamera) {
        particleToCamera.negate();
        Vector3f upAux = new Vector3f(0.0f, 0.0f, 1.0f);

        if (particle.rotateAxis() != Axis.NONE_AXIS) {

            if (particle.rotateAxis() == Axis.ALL_AXIS) {
                Vector3f particleToCameraProj = new Vector3f(particleToCamera);
                particleToCameraProj.setY(0.0f);

                particleToCamera.normalise();
                particleToCameraProj.normalise();

                float angleCosine = Vector3f.dot(upAux, particleToCameraProj);
                Vector3f.cross(upAux, particleToCameraProj, upAux);

                if (angleCosine < 0.9999 && angleCosine > -0.9999) {
                    GL11.glRotatef((float)(Math.acos(angleCosine) * 180.0 / 3.141592653589793), upAux.getX(), upAux.getY(), upAux.getZ());
                }

                angleCosine = Vector3f.dot(particleToCameraProj, particleToCamera);
                if (angleCosine < 0.9999 && angleCosine > -0.9999) {
                    if (particleToCamera.getY() < 0.0f) {
                        GL11.glRotatef((float) (Math.acos(angleCosine) * 180.0 / 3.141592653589793), 1.0f, 0.0f, 0.0f);
                    } else {
                        GL11.glRotatef((float) (Math.acos(angleCosine) * 180.0 / 3.141592653589793), -1.0f, 0.0f, 0.0f);
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
                    GL11.glRotatef((float) (Math.acos(angleCosine2) * 180.0 / 3.141592653589793), upAux.getX(), upAux.getY(), upAux.getZ());
                }
            }
        }

        Vector3f rotateAngles = particle.getLocalRotateAngles();
        GL11.glRotatef(rotateAngles.getX(), 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(rotateAngles.getY(), 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(rotateAngles.getZ(), 0.0f, 0.0f, 1.0f);
    }

}
