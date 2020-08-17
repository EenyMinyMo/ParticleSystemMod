#version 330

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;


uniform vec3 cameraPosition;
uniform mat4 projectionCameraMatrix;


in vec4 particleColorFact[];
in vec2 particleSScales[];
in vec3 particleNormalVec[];
in vec3 particleLocalAng[];
in vec4 texCoordAABB[];


out vec4 colorFactor;
out vec2 textureCoord;


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
    vec3 pos = gl_in[0].gl_Position.xyz;
    vec2 halfSideScales = particleSScales[0] * 0.5;

    mat4 modelTransformMat = computeModelTranformMat(pos, particleNormalVec[0], cameraPosition);
    mat4 rotateMat = computeLocalRotateMat(particleLocalAng[0]);
    mat4 commonTransformMat = projectionCameraMatrix * modelTransformMat * rotateMat;

    colorFactor = vec4(particleColorFact[0]);

    textureCoord = vec2(texCoordAABB[0].x - texCoordAABB[0].z, texCoordAABB[0].y - texCoordAABB[0].w);
    gl_Position = commonTransformMat * vec4(- halfSideScales.x, - halfSideScales.y, 0, 1);
    EmitVertex();

    textureCoord = vec2(texCoordAABB[0].x + texCoordAABB[0].z, texCoordAABB[0].y - texCoordAABB[0].w);
    gl_Position = commonTransformMat * vec4(halfSideScales.x, - halfSideScales.y, 0, 1);
    EmitVertex();

    textureCoord = vec2(texCoordAABB[0].x - texCoordAABB[0].z, texCoordAABB[0].y + texCoordAABB[0].w);
    gl_Position = commonTransformMat * vec4(- halfSideScales.x, halfSideScales.y, 0, 1);
    EmitVertex();

    textureCoord = vec2(texCoordAABB[0].x + texCoordAABB[0].z, texCoordAABB[0].y + texCoordAABB[0].w);
    gl_Position = commonTransformMat * vec4(halfSideScales.x, halfSideScales.y, 0, 1);
    EmitVertex();

    EndPrimitive();
}
