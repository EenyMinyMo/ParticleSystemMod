#version 330

uniform sampler2D particleTexture;

in vec2 texCoord;
in float alpha;
in float light;

out vec4 fragColor;

void main() {
    vec4 texel = vec4(texture(particleTexture, texCoord).rgb * light, alpha);

    fragColor = texel;
}
