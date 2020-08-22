package ru.somber.particlesystem.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ParticleTextureAtlas extends AbstractTexture implements ITickableTextureObject, IIconRegister {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Здесь хранятся спрайты, которые будут загружаться.
     * Хранение в формате <Название текстуры, соответствующий ParticleTextureAtlas>.
     */
    private final Map<String, ParticleAtlasSprite> mapRegisteredSprites = Maps.newHashMap();
    /**
     * Здесь хранятся спрайты, вошедшие в текущий текстурный атлас. Т.е. мапа заполнена спрайтами, имеющися в уже готовом атласе.
     * <Название текстуры, соответствующий ParticleTextureAtlas>.
     */
    private final Map<String, ParticleAtlasSprite> mapUploadedSprites = Maps.newHashMap();
    /** Список спрайтов, которые могут в анимацию. (майнкрафтовская штукенция) */
    private final List<ParticleAtlasAnimatedSprite> listAnimatedSprites = Lists.newArrayList();

    /** Путь до атласа. Нужен для идетификации в менеджере ресурсов и формирования путей до ресурсов частиц. */
    private final String atlasPath;
    /** Уровень создания мипмапов для этого атласа. */
    private int mipmapLevels;
    /** Уровень анизатропной фильтрации для этого алтаса. */
    private int anisotropicFiltering = 1;
    /** Нужно ли пропускать загрузку частиц при первом вызове loadTextureAtlas. */
    private boolean skipFirst;

    /** Спрайт для отсутствующих текстур. */
    private final ParticleAtlasSprite missingImage = new ParticleAtlasSprite("missingno");


    public ParticleTextureAtlas(String atlasPath) {
        this(atlasPath, 1, 0, false);
    }

    public ParticleTextureAtlas(String atlasPath, boolean skipFirst) {
        this(atlasPath, 1, 0, skipFirst);
    }

    public ParticleTextureAtlas(String atlasPath, int anisotropicFiltering, int mipmapLevels, boolean skipFirst) {
        this.atlasPath = atlasPath;
        this.skipFirst = skipFirst;
        this.anisotropicFiltering = anisotropicFiltering;
        this.mipmapLevels = mipmapLevels;

        initMissingImage();
        Minecraft.getMinecraft().renderEngine.loadTickableTexture(new ResourceLocation(atlasPath), this);
    }

    /**
     * Загружает частицы и формирует текстурный атлас частиц.
     */
    public void loadTextureAtlas(IResourceManager resourceManager) {
        this.deleteGlTexture();

        //они будут заполнены далее.
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();

        //инициализация ститчера.
        int maximumTextureSize = Minecraft.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(maximumTextureSize, maximumTextureSize, true, 0, this.mipmapLevels);

        //Выполнить только если не пропускаем первую инициализацию.
        //Здесь происходит подготовка всех спрайтов из mapRegisteredSprites
        if (! skipFirst) {
            loadSprites(resourceManager, stitcher, maximumTextureSize);
            generateMipmap();
        }

        //подготовка текстуры-заглушки.
        this.missingImage.generateMipmaps(this.mipmapLevels);
        stitcher.addSprite(this.missingImage);
        skipFirst = false;

        //Сшиваем текстуру.
        stitcher.doStitch();

        //Выделяем место под текстуру-атлас в OGL. Данные в эту текстуру загружаем ниже.
        logger.info("Created: {}x{} {}-atlas", stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.atlasPath);
        TextureUtil.allocateTextureImpl(
                this.getGlTextureId(),
                this.mipmapLevels,
                stitcher.getCurrentWidth(),
                stitcher.getCurrentHeight(),
                this.anisotropicFiltering);

        //Все спрайты в mapRegisteredSprites, которые вошли в stitcher, загрузить в текстуру openGL вместе с их мипмапами.
        HashMap<String, ParticleAtlasSprite> tempMapRegisteredSprites = Maps.newHashMap(this.mapRegisteredSprites);
        List<ParticleAtlasSprite> stichSlots = stitcher.getStichSlots();
        for (ParticleAtlasSprite atlasSprite : stichSlots) {
            String iconName = atlasSprite.getIconName();
            tempMapRegisteredSprites.remove(iconName);
            this.mapUploadedSprites.put(iconName, atlasSprite);

            try {
                //загрузить данные текстуры вместе с их мипмапами в текстуру-атлас
                //по сути это glTexSubImage2D
                TextureUtil.uploadTextureMipmap(
                        atlasSprite.getFrameTextureData(0),
                        atlasSprite.getIconWidth(),
                        atlasSprite.getIconHeight(),
                        atlasSprite.getOriginX(),
                        atlasSprite.getOriginY(),
                        false,
                        false);
            } catch (Throwable throwable) {
                CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");

                CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Texture being stitched together");

                crashreportcategory1.addCrashSection("Atlas path", this.atlasPath);
                crashreportcategory1.addCrashSection("Sprite", atlasSprite);

                throw new ReportedException(crashreport1);
            }

            atlasSprite.clearFramesTextureData();
        }

        //Для всех, не вошедших в mapRegisteredSprites спрайтов, установить атрибуты от missingImage.
        for (TextureAtlasSprite atlasSprite : tempMapRegisteredSprites.values()) {
            atlasSprite.copyFrom(this.missingImage);
        }

        for (ParticleAtlasSprite sprite : mapRegisteredSprites.values()) {
            if (sprite.isAnimatedSprite()) {
                listAnimatedSprites.add((ParticleAtlasAnimatedSprite) sprite);
            }
        }
    }

    /**
     * В зависимости от номера мипмапа формирует путь до ресурса.
     * Путь до ресурса выглядит следующим образом:
     * <p>
     * Мипмап 0: atlasPath + "/" + spriteLocation + ".png"
     * <p>
     * Мипмап не 0: atlasPath + "/" + mipmaps + "/" + spriteLocation + "." + mipmapLevel + ".png"
     * <p>
     * Примеры:
     * "atlasPath/spriteTexture.png", "atlasPath/mipmaps/spriteTexture.2.png"
     */
    private ResourceLocation completeResourceLocation(ResourceLocation spriteLocation, int mipmapLevel) {
        ResourceLocation location;

        if (mipmapLevel == 0) {
            location = new ResourceLocation(spriteLocation.getResourceDomain(), String.format("%s/%s%s", this.atlasPath, spriteLocation.getResourcePath(), ".png"));
        } else {
            location = new ResourceLocation(spriteLocation.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.atlasPath, spriteLocation.getResourcePath(), mipmapLevel, ".png"));
        }

        return location;
    }

    /**
     * Регистрация спрайта для загруки в атлас.
     * При следующем формировании атласа переданный спрайт будет загружен в атлас.
     */
    public void registerSpriteTexture(String spriteName, ParticleAtlasSprite particleAtlasSprite) {
        mapRegisteredSprites.put(spriteName, particleAtlasSprite);
    }

    /**
     * Возвращает зарегистрированный спрайт по его имени.
     */
    public ParticleAtlasSprite getRegisteredTextureAtlasSprite(String spriteName) {
        return mapRegisteredSprites.get(spriteName);
    }

    public String getAtlasPath() {
        return atlasPath;
    }

    public int getMipmapLevels() {
        return mipmapLevels;
    }

    public int getAnisotropicFiltering() {
        return anisotropicFiltering;
    }

    /**
     * Возвращает спрайт по имени, если он входит в текущий текстурный атлас.
     * Иначе возвращается текстура-заглушка.
     */
    public ParticleAtlasSprite getAtlasSprite(String spriteName) {
        ParticleAtlasSprite particleAtlasSprite = this.mapUploadedSprites.get(spriteName);
        if (particleAtlasSprite == null) {
            particleAtlasSprite = this.missingImage;
        }

        return particleAtlasSprite;
    }

    /**
     * Устанавливается уровень формирования мипмаов для атласа.
     * Атлас с этим уровнем мипмапа будет создан только после вызова loadTextureAtlas().
     */
    public void setMipmapLevels(int newMipmapLevel) {
        this.mipmapLevels = newMipmapLevel;
    }

    /**
     * Устанавливается уровень анизатропной фильтрации для атласа.
     * Атлас с этим уровнем анизатропной фильтрации будет создан только после вызова loadTextureAtlas().
     */
    public void setAnisotropicFiltering(int newAnisotropicFiltering) {
        this.anisotropicFiltering = newAnisotropicFiltering;
    }

    /**
     * Обновляется анимация?
     * Короче майнкрафтовская штукенция для анимированных текстур. Лучше не юзать.
     */
    public void updateAnimations() {
        for (TextureAtlasSprite textureAtlasSprite : this.listAnimatedSprites) {
            textureAtlasSprite.updateAnimation();
        }
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {}

    @Override
    public IIcon registerIcon(String iconName) {
        if (iconName == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        } else if (iconName.indexOf(92) == -1) {  // Disable backslashes (\) in texture asset paths.
            ParticleAtlasSprite textureAtlasSprite = this.mapRegisteredSprites.get(iconName);

            if (textureAtlasSprite == null) {
                textureAtlasSprite = new ParticleAtlasSprite(iconName);

                this.mapRegisteredSprites.put(iconName, textureAtlasSprite);
            }

            return textureAtlasSprite;
        } else {
            throw new IllegalArgumentException("Name cannot contain slashes!");
        }
    }

    public IIcon registerIcon(ParticleAtlasSprite sprite) {
        String iconName = sprite.getIconName();

        if (iconName == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        } else if (iconName.indexOf(92) == -1) {  // Disable backslashes (\) in texture asset paths.
            this.mapRegisteredSprites.put(iconName, sprite);

            return sprite;
        } else {
            throw new IllegalArgumentException("Name cannot contain slashes!");
        }
    }

    @Override
    public void tick() {
        this.updateAnimations();
    }


    /**
     * Создает текстуру-заглушку для случаев, когда необходимая текстура не найдена.
     */
    private void initMissingImage() {
        int[] textureData;

        if (this.anisotropicFiltering > 1) {
            this.missingImage.setIconWidth(32);
            this.missingImage.setIconHeight(32);
            textureData = new int[1024];
            System.arraycopy(TextureUtil.missingTextureData, 0, textureData, 0, TextureUtil.missingTextureData.length);
            TextureUtil.prepareAnisotropicData(textureData, 16, 16, 8);
        } else {
            textureData = TextureUtil.missingTextureData;
            this.missingImage.setIconWidth(16);
            this.missingImage.setIconHeight(16);
        }

        int[][] textureDataMipmap = new int[this.mipmapLevels + 1][];
        textureDataMipmap[0] = textureData;
        this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][] {textureDataMipmap}));
    }

    /**
     * Загружает спрайты из mapRegisteredSprites стандартным майновским способом.
     */
    private void loadSprites(IResourceManager resourceManager, Stitcher stitcher, int maximumTextureSize) {
        //сохранит количество текселей самой маленькой стороны спрайта среди всех спрайтов.
        int minCountTexelForSprite = Integer.MAX_VALUE;

        //здесь происходит загрузка спрайтов.
        for (Map.Entry<String, ParticleAtlasSprite> entry : this.mapRegisteredSprites.entrySet()) {
            ResourceLocation resourcelocation = new ResourceLocation(entry.getKey());
            TextureAtlasSprite textureAtlasSprite = entry.getValue();
            ResourceLocation completeResourceLocation = this.completeResourceLocation(resourcelocation, 0);

            if (textureAtlasSprite.hasCustomLoader(resourceManager, resourcelocation)) {    //типо если есть кастомный загрузчик
                if (!textureAtlasSprite.load(resourceManager, resourcelocation)) {
                    maximumTextureSize = Math.min(maximumTextureSize, Math.min(textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight()));
                    stitcher.addSprite(textureAtlasSprite);
                }
            } else {    //здесь происходит стандартная майновская загрузка.
                try {
                    IResource textureImageResource = resourceManager.getResource(completeResourceLocation);
                    BufferedImage[] buffImageMipmapData = new BufferedImage[1 + this.mipmapLevels];
                    buffImageMipmapData[0] = ImageIO.read(textureImageResource.getInputStream());
                    TextureMetadataSection textureMetadataSection = (TextureMetadataSection) textureImageResource.getMetadata("texture");

                    if (textureMetadataSection != null) {   //здесь происходит загрузка мипмапов, если они есть (майн как то по особому их грузит, хз)
                        List<Integer> listMipmaps = textureMetadataSection.getListMipmaps();

                        if (!listMipmaps.isEmpty()) {
                            int width = buffImageMipmapData[0].getWidth();
                            int height = buffImageMipmapData[0].getHeight();

                            if (MathHelper.roundUpToPowerOfTwo(width) != width || MathHelper.roundUpToPowerOfTwo(height) != height) {
                                throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                            }
                        }

                        for (Integer currentMipmapLevel : listMipmaps) {
                            if (currentMipmapLevel > 0 && currentMipmapLevel < buffImageMipmapData.length - 1 && buffImageMipmapData[currentMipmapLevel] == null) {
                                ResourceLocation resourceLocationMipmap = this.completeResourceLocation(resourcelocation, currentMipmapLevel);

                                try {
                                    buffImageMipmapData[currentMipmapLevel] = ImageIO.read(resourceManager.getResource(resourceLocationMipmap).getInputStream());
                                } catch (IOException ioexception) {
                                    logger.error("Unable to load miplevel {} from: {}", currentMipmapLevel, resourceLocationMipmap, ioexception);
                                }
                            }
                        }
                    }

                    textureAtlasSprite.loadSprite(buffImageMipmapData, null, this.anisotropicFiltering > 1);
                } catch (RuntimeException runtimeexception) {
                    //logger.error("Unable to parse metadata from " + completeResourceLocation, runtimeexception);
                    cpw.mods.fml.client.FMLClientHandler.instance().trackBrokenTexture(completeResourceLocation, runtimeexception.getMessage());
                    continue;
                } catch (IOException ioexception1) {
                    //logger.error("Using missing texture, unable to load " + completeResourceLocation, ioexception1);
                    cpw.mods.fml.client.FMLClientHandler.instance().trackMissingTexture(completeResourceLocation);
                    continue;
                }

                minCountTexelForSprite = Math.min(minCountTexelForSprite, Math.min(textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight()));
                stitcher.addSprite(textureAtlasSprite);
            }
        }

        //Количество возможных мипмапов. Если это количество меньше количества мипмапов для текстуры атласа, то мы уменьшаем количество мипмапов для текстуры атласа.
        int numberOfPossibleMipmaps = MathHelper.calculateLogBaseTwo(minCountTexelForSprite);
        if (numberOfPossibleMipmaps < this.mipmapLevels) {
            logger.debug("{}: dropping miplevel from {} to {}, because of minTexel: {}", this.atlasPath, this.mipmapLevels, numberOfPossibleMipmaps, minCountTexelForSprite);
            this.mipmapLevels = numberOfPossibleMipmaps;
        }
    }

    /**
     * Генерирование мипмапов для всех спрайтов в mapRegisteredSprites.
     */
    private void generateMipmap() {
        for (TextureAtlasSprite textureAtlasSprite : this.mapRegisteredSprites.values()) {
            try {
                textureAtlasSprite.generateMipmaps(this.mipmapLevels);
            } catch (Throwable throwable1) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Applying mipmap");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");

                crashreportcategory.addCrashSectionCallable("Sprite name", new Callable() {
                    private static final String __OBFID = "CL_00001059";

                    public String call() {
                        return textureAtlasSprite.getIconName();
                    }
                });

                crashreportcategory.addCrashSectionCallable("Sprite size", new Callable() {
                    private static final String __OBFID = "CL_00001060";

                    public String call() {
                        return textureAtlasSprite.getIconWidth() + " x " + textureAtlasSprite.getIconHeight();
                    }
                });

                crashreportcategory.addCrashSectionCallable("Sprite frames", new Callable() {
                    private static final String __OBFID = "CL_00001061";

                    public String call() {
                        return textureAtlasSprite.getFrameCount() + " frames";
                    }
                });

                crashreportcategory.addCrashSection("Mipmap levels", this.mipmapLevels);

                throw new ReportedException(crashreport);
            }
        }
    }

}
