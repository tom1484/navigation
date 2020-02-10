package com.example.navigation;

import android.util.Log;

public class Matrix {
    
    // return n-by-n identity matrix I
    public float[][] identity(int n) {
        float[][] a = new float[n][n];
        for (int i = 0; i < n; i++)
            a[i][i] = 1f;
        return a;
    }

    // return x^T y
    public float dot(float[] x, float[] y) {
        if (x.length != y.length) throw new RuntimeException("Illegal vector dimensions.");
        float sum = 0.0f;
        for (int i = 0; i < x.length; i++)
            sum += x[i] * y[i];
        return sum;
    }

    // return B = A^T
    public float[][] transpose(float[][] a) {
        int m = a.length;
        int n = a[0].length;
        float[][] b = new float[n][m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                b[j][i] = a[i][j];
        return b;
    }

    // return c = a + b
    public float[][] add(float[][] a, float[][] b) {
        int m = a.length;
        int n = a[0].length;
        float[][] c = new float[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                c[i][j] = a[i][j] + b[i][j];
        return c;
    }

    // return c = a - b
    public float[][] subtract(float[][] a, float[][] b) {
        int m = a.length;
        int n = a[0].length;
        float[][] c = new float[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                c[i][j] = a[i][j] - b[i][j];
        return c;
    }

    // return c = a * b
    public float[][] multiply(float[][] a, float[][] b) {
        int m1 = a.length;
        int n1 = a[0].length;
        int m2 = b.length;
        int n2 = b[0].length;
        if (n1 != m2) throw new RuntimeException("Illegal matrix dimensions.");
        float[][] c = new float[m1][n2];
        for (int i = 0; i < m1; i++)
            for (int j = 0; j < n2; j++)
                for (int k = 0; k < n1; k++)
                    c[i][j] += a[i][k] * b[k][j];
        return c;
    }

    // matrix-vector multiplication (y = A * x)
    public float[] multiply(float[][] a, float[] x) {
        int m = a.length;
        int n = a[0].length;

        float[] y = new float[m];
        for (int i = 0; i < m; i ++)
            for (int j = 0; j < n; j ++)
                y[i] += a[i][j] * x[j];
        return y;
    }

    // vector-matrix multiplication (y = x^T A)
    public float[] multiply(float[] x, float[][] a) {
        int m = a.length;
        int n = a[0].length;
        if (x.length != m) throw new RuntimeException("Illegal matrix dimensions.");
        float[] y = new float[n];
        for (int j = 0; j < n; j++)
            for (int i = 0; i < m; i++)
                y[j] += a[i][j] * x[i];
        return y;
    }

    public float[][] translate(float[][] a, float sx, float sy) {
        return multiply(getTranslateMatrix(sx, sy), a);
    }

    public float[][] getTranslateMatrix(float sx, float sy) {
        return new float[][] {
                {1f, 0f, sx},
                {0f, 1f, sy},
                {0f, 0f, 1f}
        };
    }

    public float[][] scale(float[][] a, float sx, float sy) {
        return multiply(getScaleMatrix(sx, sy), a);
    }

    public float[][] getScaleMatrix(float sx, float sy) {
        return new float[][] {
                {sx, 0f, 0f},
                {0f, sy, 0f},
                {0f, 0f, 1f}
        };
    }

    public String toString(float[][] a) {
        String string = "{";
        for (int i = 0; i < a.length; i ++) {
            for (int j = 0; j < a[0].length; j ++)
                string += String.valueOf(a[i][j]) + ", ";
        }
        return string + "}";
    }
}