#version 330

layout (location = 0) in vec3 particlePosition;
layout (location = 1) in vec3 particleLocalAngles;
layout (location = 2) in vec3 particleLookAt;
layout (location = 3) in vec4 particleAttribute;
layout (location = 4) in vec3 particleCenterPosition;

//uniform mat4 projectionMatrix;
//uniform mat4 cameraMatrix;
//uniform mat4 modelMatrix;
uniform mat4 projectionAndCameraMatrix;
uniform vec3 cameraPosition;

out vec2 texCoord;
out float alpha;
out float light;

mat4 modelTranformMat;

/*
Вычисляет матрицу трансформации модели.
Матрица трансформации модели - по сути совмещенная матрица
для поворота за вектором lookAtParticle (вектор направления частицы)
и переносом в позицию частицы.
*/
void computeModelTranformMat() {
    vec3 translatePosition = particleCenterPosition - cameraPosition;

    vec3 forward = normalize(particleLookAt);
    vec3 up = vec3(0, 1, 0);
    vec3 left = cross(up, forward);
    up = cross(forward, left);

    //Устанавливаем новый базис для матрицы трансформации.
    //строка здесь - столбец для матрицы на самом деле.
    modelTranformMat = mat4(
    left.x,                 left.y,                 left.z,                 0,
    up.x,                   up.y,                   up.z,                   0,
    forward.x,              forward.y,              forward.z,              0,
    -translatePosition.x,   -translatePosition.y,   -translatePosition.z,   1
    );
}

void main() {
    /*
    particleAttribute совмещает в себе текстурные координаты частицы (0 и 1 компонента),
    коэффициент альфы частицы (3я компонента),
    коэффициент освещенности (4ая компонента).
    Так сделано для уменьшения количества входных типов данных вершины.
    */
    texCoord = particleAttribute.xy;
    alpha = particleAttribute.z;
    light = particleAttribute.w;

    gl_Position = projectionAndCameraMatrix * modelTranformMat * vec4(particlePosition, 1);
}
