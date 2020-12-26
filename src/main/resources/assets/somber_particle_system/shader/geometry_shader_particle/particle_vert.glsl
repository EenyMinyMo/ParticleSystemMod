#version 330

layout (location = 0) in vec3 centerPosition;
layout (location = 1) in vec3 oldCenterPosition;
layout (location = 2) in vec4 sideScalesLightBlend;
layout (location = 3) in vec4 oldSideScalesLightBlend;
layout (location = 4) in vec3 normalVector;
layout (location = 5) in vec3 oldNormalVector;
layout (location = 6) in vec3 rotateAngles;
layout (location = 7) in vec3 oldRotateAngles;
layout (location = 8) in vec4 colorFactor;
layout (location = 9) in vec4 textureCoordAABB;

//uniform vec3 cameraPosition;
//uniform mat4 cameraMatrix;
//uniform mat4 projectionMatrix;
uniform float interpolationFactor;

out vec4 particleColorFact;
out vec4 particleSScalesLightBlend;
out vec3 particleNormalVec;
out vec3 particleRotateAng;
out vec4 texCoordAABB;

void main() {
    particleColorFact = colorFactor;
    particleSScalesLightBlend = sideScalesLightBlend + (sideScalesLightBlend - oldSideScalesLightBlend) * interpolationFactor;
    particleNormalVec = normalVector + (normalVector - oldNormalVector) * interpolationFactor;
    particleRotateAng = rotateAngles + (rotateAngles - oldRotateAngles) * interpolationFactor;
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
