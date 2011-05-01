package Colos;

import java.awt.Shape;
import java.awt.Color;
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

public abstract class Filler implements Paint {

    protected Shape shape_;
    protected double x_offset_;
    protected double y_offset_;

    public Filler() {
    }

    public void setShape(Shape shape) {
        shape_ = shape;
    }
    
    public abstract Color getColor(Shape shape, int x, int y);

	public PaintContext createContext(
            ColorModel cm,
            Rectangle deviceBounds,
            Rectangle2D userBounds,
            AffineTransform xform,
            RenderingHints hints) {

        Point devicePoint = deviceBounds.getLocation();
        devicePoint.move(1, 1);

        Point userPoint = userBounds.getBounds().getLocation();

        x_offset_ = userBounds.getX() - deviceBounds.getX();
        y_offset_ = userBounds.getY() - deviceBounds.getY();

        return new PaintContext() {
            public void dispose() {
            }

            public ColorModel getColorModel() {
                return ColorModel.getRGBdefault();
            }

            public Raster getRaster(int x, int y, int w, int h) {
                /*System.out.print(x);
                System.out.print(" ");
                System.out.print(y);
                System.out.print(" ");
                System.out.print(w);
                System.out.print(" ");
                System.out.println(h);

                System.out.print(x_offset_);
                System.out.print(" ");
                System.out.println(y_offset_);*/
                WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);

                int[] data = new int[w * h * 4];

                for(int j = 0; j < h; ++j) {
                    for(int i = 0; i < w; ++i) {
                        Color color = getColor(shape_, (int)(x + x_offset_ + i), (int)(y + y_offset_ + j));
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
