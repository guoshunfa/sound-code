package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class RenderContext implements Cloneable {
   RenderingHints hints;
   AffineTransform usr2dev;
   Shape aoi;

   public RenderContext(AffineTransform var1, Shape var2, RenderingHints var3) {
      this.hints = var3;
      this.aoi = var2;
      this.usr2dev = (AffineTransform)var1.clone();
   }

   public RenderContext(AffineTransform var1) {
      this(var1, (Shape)null, (RenderingHints)null);
   }

   public RenderContext(AffineTransform var1, RenderingHints var2) {
      this(var1, (Shape)null, var2);
   }

   public RenderContext(AffineTransform var1, Shape var2) {
      this(var1, var2, (RenderingHints)null);
   }

   public RenderingHints getRenderingHints() {
      return this.hints;
   }

   public void setRenderingHints(RenderingHints var1) {
      this.hints = var1;
   }

   public void setTransform(AffineTransform var1) {
      this.usr2dev = (AffineTransform)var1.clone();
   }

   public void preConcatenateTransform(AffineTransform var1) {
      this.preConcetenateTransform(var1);
   }

   /** @deprecated */
   @Deprecated
   public void preConcetenateTransform(AffineTransform var1) {
      this.usr2dev.preConcatenate(var1);
   }

   public void concatenateTransform(AffineTransform var1) {
      this.concetenateTransform(var1);
   }

   /** @deprecated */
   @Deprecated
   public void concetenateTransform(AffineTransform var1) {
      this.usr2dev.concatenate(var1);
   }

   public AffineTransform getTransform() {
      return (AffineTransform)this.usr2dev.clone();
   }

   public void setAreaOfInterest(Shape var1) {
      this.aoi = var1;
   }

   public Shape getAreaOfInterest() {
      return this.aoi;
   }

   public Object clone() {
      RenderContext var1 = new RenderContext(this.usr2dev, this.aoi, this.hints);
      return var1;
   }
}
