#version 330


uniform sampler2D particleTexture;


in vec2 particleAttrib;
in vec2 texCoord;


out vec4 fragColor;


void main() {
    float alpha = particleAttrib.x;
    float light = particleAttrib.y;

    vec4 texel = texture(particleTexture, texCoord);

    fragColor = vec4(texel.rgb * light, texel.a * alpha);
}
