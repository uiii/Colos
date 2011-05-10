package Colos;

import java.awt.color.ColorSpace;

public class Color extends java.awt.Color {
    public Color(java.awt.Color color) {
        super(color.getRGB());
    }

    public Color(ColorSpace cspace, float[] components, float alpha) {
        super(cspace, components, alpha);
    }

    public Color(float r, float g, float b) {
        super(r, g, b);
    }

    public Color(float r, float g, float b, float a) {
    	super(r, g, b, a);
    }

    public Color(int rgb) {
    	super(rgb);
    }

    public Color(int rgba, boolean hasalpha) {
    	super(rgba, hasalpha);
    }

    public Color(int r, int g, int b) {
    	super(r, g, b);
    }

    public Color(int r, int g, int b, int a) {
    	super(r, g, b, a);
    }

    public static Color createFromHSV(int hue, float saturation, float value) {
        int[] rgb = HSVtoRGB(hue, saturation, value);

        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /*public static Color createFromHSL(int hue, float saturation, float lightness) {
        return new Color();
    }

    public static Color createFromCMYK(int cyan, int magenta, int yellow, int black) {
        return new Color();
    }*/

    public static int[] HSVtoRGB(int hue, float saturation, float value) {
        hue = hue % 360;
        float chroma = value * saturation;

        float auxHue = hue / 60f;

        float x = chroma * (1 - Math.abs((auxHue % 2) - 1));

        float[] raw;
        if(auxHue < 1) {
            raw = new float[] { chroma, x, 0 };
        } else if(auxHue < 2) {
            raw = new float[] { x, chroma, 0 };
        } else if(auxHue < 3) {
            raw = new float[] { 0, chroma, x };
        } else if(auxHue < 4) {
            raw = new float[] { 0, x, chroma };
        } else if(auxHue < 5) {
            raw = new float[] { x, 0, chroma };
        } else {
            raw = new float[] { chroma, 0, x };
        }

        float m = value - chroma;
        
        return new int[] {
            (int) ((raw[0] + m) * 255),
            (int) ((raw[1] + m) * 255),
            (int) ((raw[2] + m) * 255),
        };
    }

    public int getHueHSV() {
        float red = getRed() / 255f;
        float green = getGreen() / 255f;
        float blue = getBlue() / 255f;

        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));

        float chroma = max - min;

        float auxHue;
        if(max - min == 0) {
            auxHue = 0;
        } else if(max == red) {
            auxHue = ((green - blue) / chroma) % 6;
        } else if(max == green) {
            auxHue = ((blue - red) / chroma) + 2;
        } else {
            auxHue = ((red - green) / chroma) + 4;
        }

        return (int) (auxHue * 60);
    }

    public float getSaturationHSV() {
        return 0.0;
    }

    public float getValueHSV() {
        return 0.0;
    }

    public int getHueHSL() {
        return 0;
    }

    public float getSaturationHSL() {
        return 0.0;
    }

    public float getValueHSL() {
        return 0.0;
    }
}
