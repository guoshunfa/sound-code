package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.Vector;

public class RenderableImageOp implements RenderableImage {
   ParameterBlock paramBlock;
   ContextualRenderedImageFactory myCRIF;
   Rectangle2D boundingBox;

   public RenderableImageOp(ContextualRenderedImageFactory var1, ParameterBlock var2) {
      this.myCRIF = var1;
      this.paramBlock = (ParameterBlock)var2.clone();
   }

   public Vector<RenderableImage> getSources() {
      return this.getRenderableSources();
   }

   private Vector getRenderableSources() {
      Vector var1 = null;
      if (this.paramBlock.getNumSources() > 0) {
         var1 = new Vector();

         for(int var2 = 0; var2 < this.paramBlock.getNumSources(); ++var2) {
            Object var3 = this.paramBlock.getSource(var2);
            if (!(var3 instanceof RenderableImage)) {
               break;
            }

            var1.add((RenderableImage)var3);
         }
      }

      return var1;
   }

   public Object getProperty(String var1) {
      return this.myCRIF.getProperty(this.paramBlock, var1);
   }

   public String[] getPropertyNames() {
      return this.myCRIF.getPropertyNames();
   }

   public boolean isDynamic() {
      return this.myCRIF.isDynamic();
   }

   public float getWidth() {
      if (this.boundingBox == null) {
         this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock);
      }

      return (float)this.boundingBox.getWidth();
   }

   public float getHeight() {
      if (this.boundingBox == null) {
         this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock);
      }

      return (float)this.boundingBox.getHeight();
   }

   public float getMinX() {
      if (this.boundingBox == null) {
         this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock);
      }

      return (float)this.boundingBox.getMinX();
   }

   public float getMinY() {
      if (this.boundingBox == null) {
         this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock);
      }

      return (float)this.boundingBox.getMinY();
   }

   public ParameterBlock setParameterBlock(ParameterBlock var1) {
      ParameterBlock var2 = this.paramBlock;
      this.paramBlock = (ParameterBlock)var1.clone();
      return var2;
   }

   public ParameterBlock getParameterBlock() {
      return this.paramBlock;
   }

   public RenderedImage createScaledRendering(int var1, int var2, RenderingHints var3) {
      double var4 = (double)var1 / (double)this.getWidth();
      double var6 = (double)var2 / (double)this.getHeight();
      if (Math.abs(var4 / var6 - 1.0D) < 0.01D) {
         var4 = var6;
      }

      AffineTransform var8 = AffineTransform.getScaleInstance(var4, var6);
      RenderContext var9 = new RenderContext(var8, var3);
      return this.createRendering(var9);
   }

   public RenderedImage createDefaultRendering() {
      AffineTransform var1 = new AffineTransform();
      RenderContext var2 = new RenderContext(var1);
      return this.createRendering(var2);
   }

   public RenderedImage createRendering(RenderContext var1) {
      Object var2 = null;
      RenderContext var3 = null;
      ParameterBlock var4 = (ParameterBlock)this.paramBlock.clone();
      Vector var5 = this.getRenderableSources();

      try {
         if (var5 != null) {
            Vector var6 = new Vector();

            for(int var7 = 0; var7 < var5.size(); ++var7) {
               var3 = this.myCRIF.mapRenderContext(var7, var1, this.paramBlock, this);
               RenderedImage var8 = ((RenderableImage)var5.elementAt(var7)).createRendering(var3);
               if (var8 == null) {
                  return null;
               }

               var6.addElement(var8);
            }

            if (var6.size() > 0) {
               var4.setSources(var6);
            }
         }

         return this.myCRIF.create(var1, var4);
      } catch (ArrayIndexOutOfBoundsException var9) {
         return null;
      }
   }
}
