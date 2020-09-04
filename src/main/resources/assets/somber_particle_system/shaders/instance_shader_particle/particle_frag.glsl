#version 330

uniform sampler2D particleTexture;

in vec4 colorFactor;
in vec2 texCoord;
in float light;
in float blend;

out vec4 fragColor;

void main() {
    vec4 texel = texture(particleTexture, texCoord);

    float alpha = texel.a * colorFactor.a * (1 - blend);

    texel.rgb *= colorFactor.rgb;
    texel.rgb = vec3(texel.r * (light + (1 - light) * blend),
    texel.g * (light + (1 - light) * blend),
    texel.b * (light + (1 - light) * blend));

    texel.rgb *= colorFactor.a * texel.a;
    texel.a = alpha;

    fragColor = vec4(texel);
}
