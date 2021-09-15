package java.awt.print;

import java.awt.geom.Rectangle2D;

public class Paper implements Cloneable {
   private static final int INCH = 72;
   private static final double LETTER_WIDTH = 612.0D;
   private static final double LETTER_HEIGHT = 792.0D;
   private double mHeight = 792.0D;
   private double mWidth = 612.0D;
   private Rectangle2D mImageableArea;

   public Paper() {
      this.mImageableArea = new Rectangle2D.Double(72.0D, 72.0D, this.mWidth - 144.0D, this.mHeight - 144.0D);
   }

   public Object clone() {
      Paper var1;
      try {
         var1 = (Paper)super.clone();
      } catch (CloneNotSupportedException var3) {
         var3.printStackTrace();
         var1 = null;
      }

      return var1;
   }

   public double getHeight() {
      return this.mHeight;
   }

   public void setSize(double var1, double var3) {
      this.mWidth = var1;
      this.mHeight = var3;
   }

   public double getWidth() {
      return this.mWidth;
   }

   public void setImageableArea(double var1, double var3, double var5, double var7) {
      this.mImageableArea = new Rectangle2D.Double(var1, var3, var5, var7);
   }

   public double getImageableX() {
      return this.mImageableArea.getX();
   }

   public double getImageableY() {
      return this.mImageableArea.getY();
   }

   public double getImageableWidth() {
      return this.mImageableArea.getWidth();
   }

   public double getImageableHeight() {
      return this.mImageableArea.getHeight();
   }
}
