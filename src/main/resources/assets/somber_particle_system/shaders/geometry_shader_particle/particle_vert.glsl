#version 330

layout (location = 0) in vec3 particleCenterPosition;
layout (location = 1) in vec2 particleSideScales;
layout (location = 2) in vec3 particleNormalVector;
layout (location = 3) in vec3 particleRotateAngles;
layout (location = 4) in vec4 particleColorFactor;
layout (location = 5) in vec4 particleTextureCoordAABB;

//uniform vec3 cameraPosition;
//uniform mat4 cameraMatrix;
//uniform mat4 projectionMatrix;

out vec4 particleColorFact;
out vec2 particleSScales;
out vec3 particleNormalVec;
out vec3 particleRotateAng;
out vec4 texCoordAABB;

void main() {
    particleColorFact = particleColorFactor;
    particleSScales = particleSideScales;
    particleNormalVec = particleNormalVector;
    particleRotateAng = particleRotateAngles;
    /*
    texCoordAABB - текстурные координаты представлены в виде AABB:
    x - minU
    y - minV
    z - maxU
    w - maxV
    */
    texCoordAABB = particleTextureCoordAABB;

    gl_Position = vec4(particleCenterPosition.xyz, 1);
}
