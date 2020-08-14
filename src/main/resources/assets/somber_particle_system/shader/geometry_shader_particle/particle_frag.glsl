#version 330


uniform sampler2D particleTexture;


in vec2 particleAttribute;
in vec2 textureCoord;


out vec4 fragColor;


void main() {
    float alpha = particleAttribute.x;
    float light = particleAttribute.y;

    vec4 texel = texture(particleTexture, textureCoord);

    fragColor = vec4(texel.rgb * light, texel.a * alpha);
}
