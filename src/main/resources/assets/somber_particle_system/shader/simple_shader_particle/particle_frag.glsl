#version 330

uniform sampler2D particleTexture;

in vec2 texCoord;
in float alpha;
in float light;

out vec4 fragColor;

void main() {
    vec4 texel = texture(particleTexture, texCoord);

//    fragColor = vec4(1, 1, 1, 1);
    fragColor = vec4(texel.rgb * light, texel.a * alpha);
}
