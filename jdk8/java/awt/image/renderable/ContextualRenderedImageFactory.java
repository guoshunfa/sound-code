package java.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

public interface ContextualRenderedImageFactory extends RenderedImageFactory {
   RenderContext mapRenderContext(int var1, RenderContext var2, ParameterBlock var3, RenderableImage var4);

   RenderedImage create(RenderContext var1, ParameterBlock var2);

   Rectangle2D getBounds2D(ParameterBlock var1);

   Object getProperty(ParameterBlock var1, String var2);

   String[] getPropertyNames();

   boolean isDynamic();
}
