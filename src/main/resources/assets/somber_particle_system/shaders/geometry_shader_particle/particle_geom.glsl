#version 330

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

uniform vec3 cameraPosition;
uniform mat4 projectionCameraMatrix;

in vec4 particleColorFact[];
in vec4 particleSScalesLightBlend[];
in vec3 particleNormalVec[];
in vec3 particleRotateAng[];
in vec4 texCoordAABB[];

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
mat4 computeModelTranformMat(vec3 particleCenterPos, vec3 particleNormalVec, vec3 cameraPos) {
    vec3 translatePosition = particleCenterPos - cameraPos;

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

//    mat4 modelTransformMat = mat4(
//        1,                      0,                      0,                      0,
//        0,                      1,                      0,                      0,
//        0,                      0,                      1,                      0,
//        translatePosition.x,    translatePosition.y,    translatePosition.z,    1
//    );

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
    vec3 pos = gl_in[0].gl_Position.xyz;
    vec2 halfSideScales = vec2(particleSScalesLightBlend[0].xy) * 0.5;

    mat4 modelTransformMat = computeModelTranformMat(pos, particleNormalVec[0], cameraPosition);
    mat4 rotateMat = computeRotateMat(particleRotateAng[0]);
    mat4 commonTransformMat = projectionCameraMatrix * modelTransformMat * rotateMat;

    colorFactor = vec4(particleColorFact[0]);
    light = particleSScalesLightBlend[0].z;
    blend = particleSScalesLightBlend[0].w;

    textureCoord = vec2(texCoordAABB[0].x, texCoordAABB[0].y);
    gl_Position = commonTransformMat * vec4(-halfSideScales.x, -halfSideScales.y, 0, 1);
    EmitVertex();

    textureCoord = vec2(texCoordAABB[0].z, texCoordAABB[0].y);
    gl_Position = commonTransformMat * vec4(halfSideScales.x, -halfSideScales.y, 0, 1);
    EmitVertex();

    textureCoord = vec2(texCoordAABB[0].x, texCoordAABB[0].w);
    gl_Position = commonTransformMat * vec4(-halfSideScales.x, halfSideScales.y, 0, 1);
    EmitVertex();

    textureCoord = vec2(texCoordAABB[0].z, texCoordAABB[0].w);
    gl_Position = commonTransformMat * vec4(halfSideScales.x, halfSideScales.y, 0, 1);
    EmitVertex();

    EndPrimitive();
}
