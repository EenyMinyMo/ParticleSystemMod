package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ru.somber.clientutil.textureatlas.AtlasTexture;
import ru.somber.particlesystem.particle.IParticle;
import ru.somber.particlesystem.texture.ParticleAtlasTexture;

import java.util.List;

/**
 * Интерфейс для рендерера частиц.
 * <p>
 * Определяя методы данного интерфейса, можно задавать различные способы отрисовки частиц.
 */
@SideOnly(Side.CLIENT)
public interface IParticleRenderer {

    /**
     * Возращает текстурный атлас, из которого будут браться текстуры для отрисовки частиц.
     */
    AtlasTexture getAtlasTexture();

    /**
     * Устанавливает текстурный атлас, из которого будут браться текстуры для отрисовки частиц.
     */
    void setAtlasTexture(AtlasTexture atlasTexture);

    /**
     * Метод вызывается перед непосредственно рендером частиц, здесь осуществляется подготовка к отрисовке.
     * <p>
     * В параметры методу передается подготовленный список отрисовки частиц.
     * <p>
     * Отрисовка частиц происходит в том порядке, в каком частицы идут в списке.
     * Для прозрачных частиц этот список должен быть уже отсортированным от дальнего к ближнему к началу отрисовки.
     */
    void preRender(List<IParticle> particleList, float interpolationFactor);

    /**
     * Непосредственно отрисовка частиц.
     * <p>
     * Частицы отрисовываются из списка, переданного в параметрах метода {@code preRender(List<IParticle>)}.
     */
    void render(List<IParticle> particleList, float interpolationFactor);

    /**
     * Вызывается после отрисовки частиц.
     * <p>
     * Данный метод должен подчистить данные после отрисовки частиц
     * (допустим забиндить стандартный майнкрафтовский фреймбуфер, если тот менялся и т.д.).
     */
    void postRender(List<IParticle> particleList, float interpolationFactor);

    /**
     * Вызывается после обновления частиц.
     * <p>
     * Может использоваться для работы с данными частиц,
     * т.к. между обновлениями данные частиц менятся не должны.
     */
    void update(List<IParticle> particleList);

}
