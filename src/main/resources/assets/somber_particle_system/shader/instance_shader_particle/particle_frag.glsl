#version 330

uniform sampler2D particleTexture;

in vec4 colorFactor;
in vec2 textureCoord;
in float light;
in float blend;

out vec4 fragColor;

/*
    Вычисляет цвет текселя на основе переданных цветов текселя текстуры, коэффициентов цветов, коэффициента освещенности и коэффициета смешивания.
    Данный тексель будет подготовлен для гибридного смешивания
    (в зависимости от значения blend смешивание может происходить как по альфа формуле (blend = 0), так и по аддитивной формуле (blend = 1)
    или по промежуточному состоянию (blend = (0 .. 1)))
*/
vec4 computeParticleTexel(vec4 textureTexel, vec4 colorFactor, float lightFactor, float blendFactor) {
    //применение коэффициентов цвета.
    //каждая компонента текселя умножается на соответствующий коэффициент цвета частицы.
    textureTexel.rgb *= colorFactor.rgb;

    //здесь применяется освещение частицы.
    //light - коэффициент освещенности частицы, применяется сразу на все компоненты.
    //чем сильнее blend приближается к 1 (по сути применяется аддитивновное смешивание), тем сильнее снижается влияние коэффициента light.
    //при blend = 1 выражение (light + (1 - light) * blend) всегда будет давать единицу, т.е. освещение не применяется.
    textureTexel.rgb *= (lightFactor + (1 - lightFactor) * blendFactor);

    //вычисление конечной альфа-компоненты частицы на основе альфы текселя, альфы коэффициента цветов и коэффициента смешивания.
    //эта альфа будет влиять на режим смешивания конечного текселя с фреймбуфером.
    float alpha = textureTexel.a * colorFactor.a * (1 - blendFactor);

    textureTexel.rgb *= colorFactor.a * textureTexel.a;   //изменение интерсивности цвета на основе значение альфы коэффициентов цветов и альфы текселя.
    textureTexel.a = alpha;    //присовение конечной альфы текселя (эта альфа будет влиять на способ смешивания с другими компонентами цвета фреймбуфера).

    return textureTexel;
}

void main() {
    vec4 texel = texture(particleTexture, textureCoord);

    fragColor = computeParticleTexel(texel, colorFactor, light, blend);;
}
