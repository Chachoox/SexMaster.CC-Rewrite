package me.pignol.swift.api.util;

public class MathUtil {

    public static float rad(float angle) {
        return (float) (angle * Math.PI / 180);
    }

    public static double square(double in) {
        return in * in;
    }

    public static float square(float in) {
        return in * in;
    }

    public static int square(int in) {
        return in * in;
    }

}
