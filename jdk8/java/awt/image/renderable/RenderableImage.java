package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.util.Vector;

public interface RenderableImage {
   String HINTS_OBSERVED = "HINTS_OBSERVED";

   Vector<RenderableImage> getSources();

   Object getProperty(String var1);

   String[] getPropertyNames();

   boolean isDynamic();

   float getWidth();

   float getHeight();

   float getMinX();

   float getMinY();

   RenderedImage createScaledRendering(int var1, int var2, RenderingHints var3);

   RenderedImage createDefaultRendering();

   RenderedImage createRendering(RenderContext var1);
}
