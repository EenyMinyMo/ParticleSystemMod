#version 330


layout (location = 0) in vec2 particlePosition;             //per vertex
layout (location = 1) in vec3 particleCenterPosition;       //per primitive
layout (location = 2) in vec3 particleNormalVector;         //per primitive
layout (location = 3) in vec3 particleLocalAngles;          //per primitive
layout (location = 4) in vec2 particleAttribute;            //per primitive
layout (location = 5) in vec2 particleTextureCoord;         //per vertex


uniform vec3 cameraPosition;
uniform mat4 projectionCameraMatrix;


out vec2 particleAttrib;
out vec2 texCoord;


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
    //                1,                      0,                      0,                      0,
    //                0,                      1,                      0,                      0,
    //                0,                      0,                      1,                      0,
    //                translatePosition.x,    translatePosition.y,    translatePosition.z,    1
    //    );

    return modelTransformMat;
}


mat4 computeLocalRotateMat(vec3 localAngles) {
    float pitch = localAngles.x;
    float yaw = localAngles.y;
    float roll = localAngles.z;

    mat4 rotateMat = mat4(
    cos(yaw) * cos(roll),   sin(pitch) * sin(yaw) * cos(roll) + cos(pitch) * sin(roll),     - cos(pitch) * sin(yaw) * cos(roll) + sin(pitch) * sin(roll),   0,
    - cos(yaw) * sin(roll), - sin(pitch) * sin(yaw) * sin(roll) + cos(pitch) * cos(roll),   cos(pitch) * sin(yaw) * sin(roll) + sin(pitch) * cos(roll),     0,
    sin(yaw),               - sin(pitch) * cos(yaw),                                        cos(pitch) * cos(yaw),                                          0,
    0,                      0,                                                              0,                                                              1
    );

    return rotateMat;
}


void main() {
    mat4 modelTransformMat = computeModelTranformMat(particleCenterPosition, particleNormalVector, cameraPosition);
    mat4 rotateMat = computeLocalRotateMat(particleLocalAngles);
    mat4 commonTransformMat = projectionCameraMatrix * modelTransformMat * rotateMat;

    /*
    particleAttribute совмещает в себе коэффициент альфы частицы (0я компонента),
    коэффициент освещенности (1ая компонента).
    Так сделано для уменьшения количества входных типов данных вершины.
    */
    particleAttrib = particleAttribute;
    texCoord = particleTextureCoord;

    gl_Position = commonTransformMat * vec4(particlePosition, 0, 1);
}
