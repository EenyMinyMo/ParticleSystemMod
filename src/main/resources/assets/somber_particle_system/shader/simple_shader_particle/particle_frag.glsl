#version 330


uniform sampler2D particleTexture;


in vec4 colorFactor;
in vec2 texCoord;


out vec4 fragColor;


void main() {
    vec4 texel = texture(particleTexture, texCoord);
    texel *= colorFactor;

    fragColor = vec4(texel.rgba);
}
