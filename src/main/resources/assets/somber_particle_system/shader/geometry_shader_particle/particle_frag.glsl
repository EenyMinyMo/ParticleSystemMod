#version 330


uniform sampler2D particleTexture;


in vec2 particleAttribute;
in vec2 textureCoord;


out vec4 fragColor;


void main() {
    float alpha = particleAttribute.x;
    float light = particleAttribute.y;

    vec4 texel = vec4(texture(particleTexture, textureCoord).rgb * light, alpha);

    fragColor = texel;
}
