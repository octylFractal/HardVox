package me.kenzierocks.hardvox.config;

import static com.google.common.base.Preconditions.checkArgument;

public class Color {

    public final int r;
    public final int g;
    public final int b;
    public final int a;

    public Color(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        checkArgument(hex.length() % 2 == 0, "hex must be in pairs");
        int[] colors = { 0, 0, 0, 255 };
        for (int i = 0; i < 4; i++) {
            int strI = i * 2;
            if ((strI + 2) > hex.length()) {
                break;
            }
            colors[i] = Integer.parseInt(hex.substring(strI, strI + 2), 16);
        }
        this.r = colors[0];
        this.g = colors[1];
        this.b = colors[2];
        this.a = colors[3];
    }

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + a;
        result = prime * result + b;
        result = prime * result + g;
        result = prime * result + r;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Color)) {
            return false;
        }
        Color other = (Color) obj;
        if (a != other.a) {
            return false;
        }
        if (b != other.b) {
            return false;
        }
        if (g != other.g) {
            return false;
        }
        if (r != other.r) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("#%02x%02x%02x", r, g, b, a);
    }

}
