#version 330

layout (location = 0) in vec3 centerPosition;
layout (location = 1) in vec3 oldCenterPosition;
layout (location = 2) in vec2 sideScales;
layout (location = 3) in vec2 oldSideScales;
layout (location = 4) in vec3 normalVector;
layout (location = 5) in vec3 oldNormalVector;
layout (location = 6) in vec3 rotateAngles;
layout (location = 7) in vec3 oldRotateAngles;
layout (location = 8) in vec4 colorFactors;
layout (location = 9) in vec2 lightAndBlendFactors;
layout (location = 10) in vec4 textureCoordAABB;

uniform float interpolationFactor;

out vec2 particleSideScales;
out vec3 particleNormalVector;
out vec3 particleRotateAngels;
out vec4 particleColorFactors;
out vec2 particleLightBlendFactors;
out vec4 texCoordAABB;

void main() {
    particleSideScales = sideScales + (sideScales - oldSideScales) * interpolationFactor;
    particleNormalVector = normalVector + (normalVector - oldNormalVector) * interpolationFactor;
    particleRotateAngels = rotateAngles + (rotateAngles - oldRotateAngles) * interpolationFactor;
    particleColorFactors = colorFactors;
    particleLightBlendFactors = lightAndBlendFactors;
    /*
    texCoordAABB - текстурные координаты представлены в виде AABB:
    x - minU
    y - minV
    z - maxU
    w - maxV
    */
    texCoordAABB = textureCoordAABB;

    vec3 position = centerPosition + (centerPosition - oldCenterPosition) * interpolationFactor;
    gl_Position = vec4(position, 1);
}
