package com.example.navigation;

public class Vector3D {

    public float[] vector;

    public Vector3D() {
        vector = new float[3];
        vector[0] = 0;
        vector[1] = 0;
        vector[2] = 0;
    }

    public Vector3D(float x, float y, float z) {
        vector = new float[3];
        vector[0] = x;
        vector[1] = y;
        vector[2] = z;
    }

    public String toString() {
        String res = "";
        res += "{x: " + String.valueOf(vector[0]) + ", ";
        res += "y: " + String.valueOf(vector[1]) + ", ";
        res += "z: " + String.valueOf(vector[2]) + "}";
        return res;
    }

    public Vector3D add(Vector3D v) {
        Vector3D nv = new Vector3D();
        for (int i = 0; i < 3; i ++) {
            nv.vector[i] = vector[i] + v.vector[i];
        }
        return nv;
    }

    public Vector3D minus(Vector3D v) {
        Vector3D nv = new Vector3D();
        for (int i = 0; i < 3; i ++) {
            nv.vector[i] = vector[i] - v.vector[i];
        }
        return nv;
    }

    public Vector3D mul(float v) {
        Vector3D nv = new Vector3D();
        for (int i = 0; i < 3; i ++) {
            nv.vector[i] = vector[i] * v;
        }
        return nv;
    }

    public float dis(Vector3D v) {
        float dis = 0;
        for (int i = 0; i < 3; i ++) {
            dis += Math.pow(vector[i] - v.vector[i], 2);
        }
        return (float) Math.sqrt(dis);
    }

    public float get(int i) {
        return vector[i];
    }

}
