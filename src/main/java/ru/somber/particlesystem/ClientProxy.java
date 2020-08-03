package ru.somber.particlesystem;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import ru.somber.particlesystem.events.RenderEvent;
import ru.somber.particlesystem.events.UpdateEvent;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private UpdateEvent updateEvent;
    private RenderEvent renderEvent;

    public ClientProxy() {
        super();

        updateEvent = new UpdateEvent();
        renderEvent = new RenderEvent();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        FMLCommonHandler.instance().bus().register(updateEvent);
        MinecraftForge.EVENT_BUS.register(renderEvent);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

    }

}
