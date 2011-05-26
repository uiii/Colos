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

public abstract class SliderFiller implements Paint {

    protected Slider slider_;
    protected int x_offset_;
    protected int y_offset_;

    protected Color[][] cache_; // TODO cache cely slider (JPanel)
    protected int validCacheX_;
    protected int validCacheY_;

    public SliderFiller() {
        invalidateCache();
    }

    public void setSlider(Slider slider) {
        slider_ = slider;

        cache_ = new Color[slider_.getWidth()][slider_.getHeight()];
        cache_[0][0] = Color.gray;
    }

    public void invalidateCache() {
        validCacheX_ = 0;
        validCacheY_ = 0;
    }
    
    public boolean cacheIsValid(int x, int y) {
        return x <= validCacheX_ && y <= validCacheY_;
    }

    public Color getCachedColor(int x, int y) {
        return cache_[x][y];
    }

    public void cacheColor(int x, int y, Color color) {
        //cache_[x][y] = color;
        validCacheX_ = x;
        validCacheY_ = y;
    }

    public abstract Color getColor(int x, int y);

	public PaintContext createContext(
            ColorModel cm,
            Rectangle deviceBounds,
            Rectangle2D userBounds,
            AffineTransform xform,
            RenderingHints hints) {

        /*Point devicePoint = deviceBounds.getLocation();
        devicePoint.translate(1, 1);

        Point userPoint = userBounds.getBounds().getLocation();*/

        Component root = SwingUtilities.getRootPane(slider_);
        /*Point point = new Point(
                (int) userBounds.getX(),
                (int) userBounds.getY()
        );*/
        Point point = new Point(0, 0);

        point = SwingUtilities.convertPoint(slider_, point, root);

        x_offset_ = - (int)point.getX();
        y_offset_ = - (int)point.getY();
        
        //System.out.printf("create \n\t%s\n\t%s\n\t%s\n\n", userBounds, deviceBounds, point);

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
                        //System.out.printf("<%s, %s>{%s, %s} - ", x, y, x_offset, y_offset);
                        Color color;

                        if(cacheIsValid(x, y) && false) {
                            color = getCachedColor(0, 0);
                        } else {
                            color = getColor(x, y);
                            //color = Color.gray;
                            cacheColor(x, y, color);
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
        return Transparency.OPAQUE;//TRANSLUCENT;
    }

}