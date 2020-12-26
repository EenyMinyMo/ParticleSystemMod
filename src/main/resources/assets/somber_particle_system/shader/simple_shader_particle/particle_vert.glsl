#version 330

layout (location = 0) in vec2 vertPosition;
layout (location = 1) in vec4 colorFactors;

layout (location = 2) in vec4 sideScalesLightBlend;
layout (location = 3) in vec4 oldSideScalesLightBlend;

layout (location = 4) in vec3 centerPosition;
layout (location = 5) in vec3 oldCenterPosition;

layout (location = 6) in vec3 normalVector;
layout (location = 7) in vec3 oldNormalVector;

layout (location = 8) in vec3 rotateAngles;
layout (location = 9) in vec3 oldRotateAngles;

layout (location = 10) in vec2 texCoord;

uniform vec3 cameraPosition;
uniform mat4 projectionCameraMatrix;
uniform float interpolationFactor;

out vec4 colorFactor;
out vec2 textureCoord;
out float light;
out float blend;

/*
Вычисляет матрицу трансформации модели.
Матрица трансформации модели - по сути совмещенная матрица
для поворота за вектором lookAtParticle (вектор направления частицы)
и переносом в позицию частицы.
*/
mat4 computeModelTranformMat(vec3 particleCenterPos, vec3 particleNormalVec) {
    vec3 translatePosition = particleCenterPos.xyz - cameraPosition.xyz;

    vec3 forward = normalize(particleNormalVec);
    vec3 up = vec3(0, 1, 0);
    vec3 left = normalize(cross(up, forward));
    up = normalize(cross(forward, left));

    //Устанавливаем новый базис для матрицы трансформации.
    //строка здесь - столбец для матрицы на самом деле.
    mat4 modelTransformMat = mat4(
        left.x,                 left.y,                 left.z,                 0,
        up.x,                   up.y,                   up.z,                   0,
        forward.x,              forward.y,              forward.z,              0,
        translatePosition.x,    translatePosition.y,    translatePosition.z,    1
    );

    return modelTransformMat;
}

mat4 computeRotateMat(vec3 rotateAngles) {
    float sinPitch = sin(rotateAngles.x);
    float cosPitch = cos(rotateAngles.x);
    float sinYaw = sin(rotateAngles.y);
    float cosYaw = cos(rotateAngles.y);
    float sinRoll = sin(rotateAngles.z);
    float cosRoll = cos(rotateAngles.z);

    mat4 rotateMat = mat4(
        cosYaw * cosRoll,   sinPitch * sinYaw * cosRoll + cosPitch * sinRoll,   -cosPitch * sinYaw * cosRoll + sinPitch * sinRoll,  0,
        -cosYaw * sinRoll,  -sinPitch * sinYaw * sinRoll + cosPitch * cosRoll,  cosPitch * sinYaw * sinRoll + sinPitch * cosRoll,   0,
        sinYaw,             -sinPitch * cosYaw,                                 cosPitch * cosYaw,                                  0,
        0,                  0,                                                  0,                                                  1
    );

    return rotateMat;
}

void main() {
    vec4 interpolationSideScalesLightBlend = sideScalesLightBlend + (sideScalesLightBlend - oldSideScalesLightBlend) * interpolationFactor;
    vec3 interpolateCenterPosition = centerPosition + (centerPosition - oldCenterPosition) * interpolationFactor;
    vec3 interpolateNormalVector = normalVector + (normalVector - oldNormalVector) * interpolationFactor;
    vec3 interpolateRotateAngles = rotateAngles + (rotateAngles - oldRotateAngles) * interpolationFactor;

    mat4 modelTransformMat = computeModelTranformMat(interpolateCenterPosition, interpolateNormalVector);
    mat4 rotateMat = computeRotateMat(interpolateRotateAngles);

    textureCoord = texCoord;
    colorFactor = colorFactors;
    light = interpolationSideScalesLightBlend.z;
    blend = interpolationSideScalesLightBlend.w;

    gl_Position = projectionCameraMatrix * modelTransformMat * rotateMat * vec4(vertPosition * interpolationSideScalesLightBlend.xy, 0, 1);
}
