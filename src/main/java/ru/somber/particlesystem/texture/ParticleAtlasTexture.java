package ru.somber.particlesystem.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Класс для представления текстурного атласа частиц.
 * Во многом скопирован с манйкрафтовского TextureMap, однако здесь добавлена документация и вырезана часть ненужного кода.
 *
 * <p> Создание атласа:
 * <p> 1. Создаем объект класса с корректными atlasPath и уровнями анизатропной фильтрации и мипмапы.
 * <p> Для atlasPath: путь до папки с тексутрами для атласа. Все текстуры атласа должны храниться в этой папке.
 * atlasPath должен быть следующего формата: "MOD_ID:путь_до_папки_с_текстурами".
 *
 * <p> 2. Все используемые для частиц спрайты зарегистрировать. Лучше делать это, используя метод с параматром-объектом готовой иконки.
 * Т.е. объект иконки создать заранее и прописать ему нужные характеристики.
 * Для использования иконок подойдут только иконки-наследники от ParticleAtlasIcon.
 * Для объекта иконок прописывать имя следующим образом: "MOD_ID + ":название_файла_частицы"".
 * В качестве названия файла указывать только само название файла! Папки до файла не нужно!
 *
 * <p> 3. Вызвать loadTextureAtlas с переданными файловым менеджером (можно юзать майнкрафтовский).
 * Этот метод загрузит текстурки частиц и сформирует текстурный атласа.
 *
 * <p> Использование атласа:
 * <p> 1. Забиндить атлас как текстуру.
 * <p> 2. Получить объекты иконок, в которых уже хранятся нужные текстурные координаты. Рисовать объекты с этими текстурными координатами.
 *
 * <p> При внесении изменений в атлас (добавление новой иконки и тд.) требуется пересоздавать атлас, иначе изменения не будут учтены.
 */
@SideOnly(Side.CLIENT)
public class ParticleAtlasTexture extends AbstractTexture implements ITickableTextureObject, IIconRegister {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Здесь хранятся иконки, которые будут загружаться.
     * Хранение в формате <Название иконки, соответствующий ParticleAtlasIcon>.
     */
    private final Map<String, ParticleAtlasIcon> mapRegisteredIcons = Maps.newHashMap();
    /**
     * Здесь хранятся иконки, вошедшие в текущий текстурный атлас. Т.е. мапа заполнена иконки, имеющися в уже готовом атласе.
     * <Название иконки, соответствующий ParticleAtlasIcon>.
     */
    private final Map<String, ParticleAtlasIcon> mapUploadedIcons = Maps.newHashMap();
    /** Список иконок, которые могут в анимацию. */
    private final List<ParticleAtlasIcon> listAnimatedIcons = Lists.newArrayList();

    /** Путь до атласа. Нужен для идетификации в менеджере ресурсов и формирования путей до ресурсов частиц. */
    private final String atlasPath;
    /** Уровень создания мипмапов для этого атласа. */
    private int mipmapLevels;
    /** Уровень анизатропной фильтрации для этого алтаса. */
    private int anisotropicFiltering = 1;

    /** Спрайт для отсутствующих текстур. */
    private final ParticleAtlasIcon missingImage = new ParticleAtlasIcon("missingno");


    /**
     * @param atlasPath путь до папки с тексутрами для атласа. Все текстуры атласа должны храниться в этой папке.
     *                  atlasPath должен быть следующего формата: "MOD_ID:путь_до_папки_с_текстурами"
     */
    public ParticleAtlasTexture(String atlasPath) {
        this(atlasPath, 1, 0);
    }

    /**
     * @param atlasPath путь до папки с тексутрами для атласа. Все текстуры атласа должны храниться в этой папке.
     *                  atlasPath должен быть следующего формата: "MOD_ID:путь_до_папки_с_текстурами"
     * @param anisotropicFiltering уровень анизатропной фильтрации.
     * @param mipmapLevels уровень формирования мипмапов.
     */
    public ParticleAtlasTexture(String atlasPath, int anisotropicFiltering, int mipmapLevels) {
        this.atlasPath = atlasPath;
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
        this.mapUploadedIcons.clear();
        this.listAnimatedIcons.clear();

        //инициализация ститчера.
        int maximumTextureSize = Minecraft.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(maximumTextureSize, maximumTextureSize, true, 0, this.mipmapLevels);

        //Здесь происходит загрузка и дальнешая подготовка всех спрайтов из mapRegisteredIcons
        loadAllIcons(resourceManager, stitcher, maximumTextureSize);
        generateMipmap();

        //подготовка текстуры-заглушки.
        this.missingImage.generateMipmaps(this.mipmapLevels);
        stitcher.addSprite(this.missingImage);

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
        HashMap<String, ParticleAtlasIcon> tempMapRegisteredIcons = Maps.newHashMap(this.mapRegisteredIcons);
        List<ParticleAtlasIcon> stichSlots = stitcher.getStichSlots();
        for (ParticleAtlasIcon atlasIcons : stichSlots) {
            String iconName = atlasIcons.getIconName();
            tempMapRegisteredIcons.remove(iconName);
            this.mapUploadedIcons.put(iconName, atlasIcons);

            try {
                //загрузить данные текстуры вместе с их мипмапами в текстуру-атлас
                //по сути это glTexSubImage2D
                TextureUtil.uploadTextureMipmap(
                        atlasIcons.getFrameTextureData(0),
                        atlasIcons.getIconWidth(),
                        atlasIcons.getIconHeight(),
                        atlasIcons.getOriginX(),
                        atlasIcons.getOriginY(),
                        false,
                        false);
            } catch (Throwable throwable) {
                CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");

                CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Texture being stitched together");

                crashreportcategory1.addCrashSection("Atlas path", this.atlasPath);
                crashreportcategory1.addCrashSection("Sprite", atlasIcons);

                throw new ReportedException(crashreport1);
            }

            atlasIcons.clearFramesTextureData();
        }

        //Для всех, не вошедших в mapRegisteredSprites спрайтов, установить атрибуты от missingImage.
        for (ParticleAtlasIcon atlasSprite : tempMapRegisteredIcons.values()) {
            atlasSprite.copyFrom(this.missingImage);
        }

        for (ParticleAtlasIcon icon : mapRegisteredIcons.values()) {
            if (icon.isAnimatedIcon()) {
                listAnimatedIcons.add((ParticleAtlasAnimatedIcon) icon);
            }
        }
    }

    /**
     * Возвращает зарегистрированный спрайт по его имени.
     */
    public ParticleAtlasIcon getRegisteredParticleAtlasIcon(String iconName) {
        return mapRegisteredIcons.get(iconName);
    }

    /**
     * Возвращает путь до папки с атласом.
     * Все текстуры атласа должны храниться в этой папке.
     */
    public String getAtlasPath() {
        return atlasPath;
    }

    /**
     * Возвращает уровень создания мипмапов.
     */
    public int getMipmapLevels() {
        return mipmapLevels;
    }

    /**
     * Возвращает уровень анизатропной фильтрации.
     */
    public int getAnisotropicFiltering() {
        return anisotropicFiltering;
    }

    /**
     * Возвращает спрайт по имени, если он входит в текущий текстурный атлас.
     * Иначе возвращается текстура-заглушка.
     */
    public ParticleAtlasIcon getAtlasIcon(String iconName) {
        ParticleAtlasIcon particleAtlasIcon = this.mapUploadedIcons.get(iconName);
        if (particleAtlasIcon == null) {
            particleAtlasIcon = this.missingImage;
        }

        return particleAtlasIcon;
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
        for (ParticleAtlasIcon particleAtlasIcon : this.listAnimatedIcons) {
            particleAtlasIcon.updateAnimation();
        }
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {}

    @Override
    public void tick() {
        this.updateAnimations();
    }

    @Override
    public IIcon registerIcon(String iconName) {
        if (iconName == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        } else if (iconName.indexOf(92) == -1) {  // Disable backslashes (\) in texture asset paths.
            ParticleAtlasIcon textureAtlasSprite = this.mapRegisteredIcons.get(iconName);

            if (textureAtlasSprite == null) {
                textureAtlasSprite = new ParticleAtlasIcon(iconName);

                this.mapRegisteredIcons.put(iconName, textureAtlasSprite);
            }

            return textureAtlasSprite;
        } else {
            throw new IllegalArgumentException("Name cannot contain slashes!");
        }
    }

    /**
     * Вносит переданную иконку в список иконок для загрузки и формирования атласа с ней.
     */
    public IIcon registerIcon(ParticleAtlasIcon icon) {
        String iconName = icon.getIconName();

        if (iconName == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        } else if (iconName.indexOf(92) == -1) {  // Disable backslashes (\) in texture asset paths.
            this.mapRegisteredIcons.put(iconName, icon);

            return icon;
        } else {
            throw new IllegalArgumentException("Name cannot contain slashes!");
        }
    }


    /**
     * Создает текстуру-заглушку для случаев, когда необходимая текстура не найдена.
     */
    private void initMissingImage() {
        int[] textureData;

        if (this.anisotropicFiltering > 1) {
            textureData = new int[1024];

            this.missingImage.setIconWidth(32);
            this.missingImage.setIconHeight(32);

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
     * Загружает иконки из mapRegisteredIcons стандартным майновским способом.
     */
    private void loadAllIcons(IResourceManager resourceManager, Stitcher stitcher, int maximumTextureSize) {
        //сохранит количество текселей самой маленькой стороны спрайта среди всех спрайтов.
        int minCountTexelForSprite = Integer.MAX_VALUE;

        //здесь происходит загрузка спрайтов.
        for (Map.Entry<String, ParticleAtlasIcon> entry : this.mapRegisteredIcons.entrySet()) {
            ResourceLocation resourcelocation = new ResourceLocation(entry.getKey());
            ParticleAtlasIcon textureAtlasSprite = entry.getValue();
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
     * В зависимости от номера мипмапа формирует путь до ресурса.
     * Путь до ресурса выглядит следующим образом:
     * <p>
     * Мипмап 0: atlasPath + "/" + iconLocation + ".png"
     * <p>
     * Мипмап не 0: atlasPath + "/" + mipmaps + "/" + iconLocation + "." + mipmapLevel + ".png"
     * <p>
     * Примеры:
     * "atlasPath/iconTexture.png", "atlasPath/mipmaps/iconTexture.2.png"
     */
    private ResourceLocation completeResourceLocation(ResourceLocation iconLocation, int mipmapLevel) {
        ResourceLocation location;

        if (mipmapLevel == 0) {
            location = new ResourceLocation(iconLocation.getResourceDomain(), String.format("%s/%s%s", this.atlasPath, iconLocation.getResourcePath(), ".png"));
        } else {
            location = new ResourceLocation(iconLocation.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.atlasPath, iconLocation.getResourcePath(), mipmapLevel, ".png"));
        }

        return location;
    }

    /**
     * Генерирование мипмапов для всех спрайтов в mapRegisteredSprites.
     */
    private void generateMipmap() {
        for (ParticleAtlasIcon particleAtlasIcon : this.mapRegisteredIcons.values()) {
            try {
                particleAtlasIcon.generateMipmaps(this.mipmapLevels);
            } catch (Throwable throwable1) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Applying mipmap");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");

                crashreportcategory.addCrashSectionCallable("Sprite name", new Callable() {
                    private static final String __OBFID = "CL_00001059";

                    public String call() {
                        return particleAtlasIcon.getIconName();
                    }
                });

                crashreportcategory.addCrashSectionCallable("Sprite size", new Callable() {
                    private static final String __OBFID = "CL_00001060";

                    public String call() {
                        return particleAtlasIcon.getIconWidth() + " x " + particleAtlasIcon.getIconHeight();
                    }
                });

                crashreportcategory.addCrashSectionCallable("Sprite frames", new Callable() {
                    private static final String __OBFID = "CL_00001061";

                    public String call() {
                        return particleAtlasIcon.getFrameCount() + " frames";
                    }
                });

                crashreportcategory.addCrashSection("Mipmap levels", this.mipmapLevels);

                throw new ReportedException(crashreport);
            }
        }
    }

}
