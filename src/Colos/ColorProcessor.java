package Colos;

import java.awt.Color;
import java.awt.color.ColorSpace;

public class ColorProcessor {
    private static Color color_ = null;

    private static float red_ = -1;
    private static float green_ = -1;
    private static float blue_ = -1;

    private static float minRGB_ = -1;
    private static float maxRGB_ = -1;

    private static float chroma_ = -1;

    private static int hue_ = -1;

    private static float saturationHSV_ = -1;
    private static float valueHSV_ = -1;

    private static float saturationHSL_ = -1;
    private static float lightnessHSL_ = -1;

    public static Color createFromHSV(int hue, float saturation, float value) {
        int[] rgb = HSVtoRGB(hue, saturation, value);

        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    public static Color createFromHSL(int hue, float saturation, float lightness) {
        int[] rgb = HSLtoRGB(hue, saturation, lightness);

        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /*public static Color createFromCMYK(int cyan, int magenta, int yellow, int black) {
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
            Math.round((raw[0] + m) * 255),
            Math.round((raw[1] + m) * 255),
            Math.round((raw[2] + m) * 255),
        };
    }

    public static int[] HSLtoRGB(int hue, float saturation, float lightness) {
        hue = hue % 360;
        float chroma = (1 - Math.abs(2 * lightness - 1)) * saturation;

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

        float m = lightness - 0.5f * chroma;
        
        return new int[] {
            Math.round((raw[0] + m) * 255),
            Math.round((raw[1] + m) * 255),
            Math.round((raw[2] + m) * 255),
        };
    }

    public static int getHueHSV(Color color) {
        changeColor_(color);

        if(hue_ == -1) {
            obtainRGBComponents_(color);

            float auxHue;
            if(maxRGB_ - minRGB_ == 0) {
                auxHue = -1;
            } else if(maxRGB_ == red_) {
                auxHue = mod_(((green_ - blue_) / chroma_), 6);
            } else if(maxRGB_ == green_) {
                auxHue = ((blue_ - red_) / chroma_) + 2;
            } else {
                auxHue = ((red_ - green_) / chroma_) + 4;
            }

            if(auxHue != -1) {
                hue_ = Math.round(auxHue * 60);
            } else {
                hue_ = -1;
            }
        }

        return hue_;
    }

    public static float getSaturationHSV(Color color) {
        changeColor_(color);

        if(saturationHSV_ == -1) {
            obtainRGBComponents_(color);

            if(chroma_ == 0) {
                saturationHSV_ = 0;
            } else {
                saturationHSV_ = chroma_ / getValueHSV(color);
            }
        }

        return saturationHSV_;
    }

    public static float getValueHSV(Color color) {
        changeColor_(color);

        if(valueHSV_ == -1) {
            obtainRGBComponents_(color);

            valueHSV_ = maxRGB_;
        }

        return valueHSV_;
    }

    public static int getHueHSL(Color color) {
        return getHueHSV(color);
    }

    public static float getSaturationHSL(Color color) {
        changeColor_(color);

        if(saturationHSL_ == -1) {
            obtainRGBComponents_(color);

            if(chroma_ == 0) {
                saturationHSL_ = 0;
            } else {
                saturationHSL_ = chroma_ / (1 - Math.abs(2 * getLightnessHSL(color) - 1));
            }
        }

        return saturationHSL_;
    }

    public static float getLightnessHSL(Color color) {
        changeColor_(color);

        if(lightnessHSL_ == -1) {
            obtainRGBComponents_(color);

            lightnessHSL_ = 0.5f * (maxRGB_ + minRGB_);
        }

        return lightnessHSL_;
    }

    private static void obtainRGBComponents_(Color color) {
        changeColor_(color);

        if(red_ == -1 || green_ == -1 || blue_ == -1) {
            red_ = color.getRed() / 255f;
            green_ = color.getGreen() / 255f;
            blue_ = color.getBlue() / 255f;
        }

        if(minRGB_ == -1 || maxRGB_ == -1 || chroma_ == -1) {
            maxRGB_ = Math.max(red_, Math.max(green_, blue_));
            minRGB_ = Math.min(red_, Math.min(green_, blue_));

            chroma_ = maxRGB_ - minRGB_;
        }
    }

    private static void changeColor_(Color color) {
        if(! color.equals(color_)) {
            color_ = color;

            red_ = -1;
            green_ = -1;
            blue_ = -1;

            minRGB_ = -1;
            maxRGB_ = -1;

            chroma_ = -1;

            hue_ = -1;

            saturationHSV_ = -1;
            valueHSV_ = -1;

            saturationHSL_ = -1;
            lightnessHSL_ = -1;
        }
    }

    private static float mod_(float a, float b) {
        return ((a % b) + b) % b;
    }    
}
