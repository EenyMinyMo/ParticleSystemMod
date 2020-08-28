#version 330

layout (location = 0) in vec3 particleCenterPosition;
layout (location = 1) in vec2 particleSideScales;
layout (location = 2) in vec3 particleNormalVector;
layout (location = 3) in vec3 particleLocalAngles;
layout (location = 4) in vec4 particleColorFactor;
layout (location = 5) in vec4 particleTextureCoordAABB;


//uniform vec3 cameraPosition;
//uniform mat4 cameraMatrix;
//uniform mat4 projectionMatrix;


out vec4 particleColorFact;
out vec2 particleSScales;
out vec3 particleNormalVec;
out vec3 particleLocalAng;
out vec4 texCoordAABB;


void main() {
    /*
    particleAttribute совмещает в себе коэффициент альфы частицы (0я компонента),
    коэффициент освещенности (1ая компонента).
    Так сделано для уменьшения количества входных типов данных вершины.
    */
    particleColorFact = particleColorFactor;
    particleSScales = particleSideScales;
    particleNormalVec = particleNormalVector;
    particleLocalAng = particleLocalAngles;
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
