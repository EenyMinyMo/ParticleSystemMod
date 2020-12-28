#version 330

layout (location = 0) in vec2 position;                         //per vertex
layout (location = 1) in vec3 centerPosition;                   //per primitive
layout (location = 2) in vec3 oldCenterPosition;                //per primitive
layout (location = 3) in vec2 sideScales;                       //per primitive
layout (location = 4) in vec2 oldSideScales;                    //per primitive
layout (location = 5) in vec3 normalVector;                     //per primitive
layout (location = 6) in vec3 oldNormalVector;                  //per primitive
layout (location = 7) in vec3 rotateAngles;                     //per primitive
layout (location = 8) in vec3 oldRotateAngles;                  //per primitive
layout (location = 9) in vec4 colorFactors;                     //per primitive
layout (location = 10) in vec2 lightAndBlendFactors;            //per primitive
layout (location = 11) in vec4 textureCoordAABB;                //per primitive


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
    vec3 interpolatedCenterPosition = centerPosition + (centerPosition - oldCenterPosition) * interpolationFactor;
    vec2 interpolatedSideScales = sideScales + (sideScales - oldSideScales) * interpolationFactor;
    vec3 interpolatedNormalVector = normalVector + (normalVector - oldNormalVector) * interpolationFactor;
    vec3 interpolatedRotateAngles = rotateAngles + (rotateAngles - oldRotateAngles) * interpolationFactor;

    mat4 modelTransformMat = computeModelTranformMat(interpolatedCenterPosition, interpolatedNormalVector, cameraPosition);
    mat4 rotateMat = computeRotateMat(interpolatedRotateAngles);
    mat4 commonTransformMat = projectionCameraMatrix * modelTransformMat * rotateMat;

    /*
    x - minU
    y - minV
    z - maxU
    w - maxV
    Для нахождения тектурных координат вершины применяется следующий алгортим:
    1. Находим центр текстуры по координатам. (min + max) / 2
    2. Находим размер стороны прямоугольника текстуры. (max - min)
    3. Вершины частицы всегда имеют координаты:
    .(-0.5, 0.5)-----.(0.5, 0.5)
    |                |
    |                |
    .(-0.5, -0.5)----.(0.5, -0.5)
    Тогда умножением размеров стороны текстуры на координаты вершины и прибавлением координат центра текстуры
    мы получим нужные координаты вершины текстуры, соответствующие вершине частицы.
    */
    vec2 texCoordCenter = vec2(textureCoordAABB.xy + textureCoordAABB.zw) / 2;
    vec2 texCoordSideSizes = vec2(textureCoordAABB.zw - textureCoordAABB.xy);
    textureCoord = vec2(texCoordCenter.xy) + vec2(position.xy * texCoordSideSizes.xy);
    colorFactor = colorFactors;
    light = lightAndBlendFactors.x;
    blend = lightAndBlendFactors.y;

    gl_Position = commonTransformMat * vec4(position * interpolatedSideScales, 0, 1);
}
