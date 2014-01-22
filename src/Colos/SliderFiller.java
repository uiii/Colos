package Colos;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.Transparency;

import java.awt.Component;
import javax.swing.SwingUtilities;

/**
 * Represents a painter that can be used to paint sliders.
 *
 * The color of the pixels painter by this painter is specified
 * by the getColor method. Filler is using the color cache.
 *
 * @see #getColor
 */
public abstract class SliderFiller implements Paint {

    protected Slider slider_;
    protected int x_offset_;
    protected int y_offset_;

    protected Color[][] cache_;
    protected int[][] cacheInfo_;

    protected int validityKey_ = 0;
    protected int validCacheX_;
    protected int validCacheY_;

    protected int cacheWidth_ = 0;
    protected int cacheHeight_ = 0;

    /**
     * SliderFiller's constructor
     */
    public SliderFiller() {
        invalidateCache();
    }

    /**
     * Sets slider object which will be painter
     *
     * @param slider
     *     Slider which will be painted
     */
    public void setSlider(Slider slider) {
        slider_ = slider;
    }

    private void initCache_() {
        int width = slider_.getWidth() + 10;
        int height = slider_.getHeight() + 10;
        if(cacheWidth_ != width || cacheHeight_ != height) {
            cache_ = new Color[width][height];
            cacheInfo_ = new int[width][height];
            cacheWidth_ = width;
            cacheHeight_ = height;
        }
    }

    /**
     * Invalidates filling color cache
     */
    public void invalidateCache() {
        validityKey_ = (validityKey_ + 1) % 1000;
    }
    
    private boolean cacheIsValid_(int x, int y) {
        return cacheInfo_[x][y] == validityKey_;
    }

    private Color getCachedColor_(int x, int y) {
        return cache_[x][y];
    }

    private void cacheColor_(int x, int y, Color color) {
        cache_[x][y] = color;
        cacheInfo_[x][y] = validityKey_;
    }

    /**
     * Gets color for the specified pixel in the slider's component coordinates
     *
     * @param x
     *     X coordinate of the pixel
     * @param y
     *     Y coordinate of the pixel
     */
    public abstract Color getColor(int x, int y);

    @Override
	public PaintContext createContext(
            ColorModel cm,
            Rectangle deviceBounds,
            Rectangle2D userBounds,
            AffineTransform xform,
            RenderingHints hints) {

        Component root = SwingUtilities.getRootPane(slider_);

        Point point = new Point(0, 0);
        point = SwingUtilities.convertPoint(slider_, point, root);

        x_offset_ = - (int)point.getX();
        y_offset_ = - (int)point.getY();
        
        initCache_();

        return new PaintContext() {
            protected final int x_offset = x_offset_;
            protected final int y_offset = y_offset_;

            public void dispose() {
            }

            public ColorModel getColorModel() {
                return ColorModel.getRGBdefault();
            }

            public Raster getRaster(int rawX, int rawY, int w, int h) {
                WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);

                int[] data = new int[w * h * 4];

                for(int j = 0; j < h; ++j) {
                    for(int i = 0; i < w; ++i) {
                        int x = (int)(rawX + x_offset + i);
                        int y = (int)(rawY + y_offset + j);

                        Color color;

                        if(cacheIsValid_(x, y)) {
                            color = getCachedColor_(x, y);
                        } else {
                            color = getColor(x, y);
                            cacheColor_(x, y, color);
                        }

                        int base = (j * w + i) * 4;

                        data[base + 0] = color.getRed();
                        data[base + 1] = color.getGreen();
                        data[base + 2] = color.getBlue();
                        data[base + 3] = color.getAlpha();
                    }
                }

                raster.setPixels(0, 0, w, h, data);

                return raster;
            }
        };
    }

    public int getTransparency() {
        return Transparency.TRANSLUCENT;
    }

}
