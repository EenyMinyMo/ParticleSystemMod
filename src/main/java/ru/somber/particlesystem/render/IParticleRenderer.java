package ru.somber.particlesystem.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ru.somber.particlesystem.particle.IParticle;

import java.util.List;

/**
 * Интерфейс для рендерера частиц.
 * <p>
 * Определяя методы данного интерфейса, можно задавать различные способы отрисовки частиц.
 */
@SideOnly(Side.CLIENT)
public interface IParticleRenderer {

    /**
     * Метод вызывается перед непосредственно рендером частиц, здесь осуществляется подготовка к отрисовке.
     * <p>
     * В параметры методу передается подготовленный список отрисовки частиц.
     * <p>
     * Отрисовка частиц происходит в том порядке, в каком частицы идут в списке.
     * Для прозрачных частиц этот список должен быть уже отсортированным от дальнего к ближнему к началу отрисовки.
     */
    void preRender(List<IParticle> particleList);

    /**
     * Непосредственно отрисовка частиц.
     * <p>
     * Частицы отрисовываются из списка, переданного в параметрах метода {@code preRender(List<IParticle>)}.
     */
    void render( float interpolationFactor);

    /**
     * Вызывается после отрисовки частиц.
     * <p>
     * Данный метод должен подчистить данные после отрисовки частиц
     * (допустим забиндить стандартный майнкрафтовский фреймбуфер, если тот менялся и т.д.).
     */
    void postRender();

}
