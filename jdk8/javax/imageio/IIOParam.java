package javax.imageio;

import java.awt.Point;
import java.awt.Rectangle;

public abstract class IIOParam {
   protected Rectangle sourceRegion = null;
   protected int sourceXSubsampling = 1;
   protected int sourceYSubsampling = 1;
   protected int subsamplingXOffset = 0;
   protected int subsamplingYOffset = 0;
   protected int[] sourceBands = null;
   protected ImageTypeSpecifier destinationType = null;
   protected Point destinationOffset = new Point(0, 0);
   protected IIOParamController defaultController = null;
   protected IIOParamController controller = null;

   protected IIOParam() {
      this.controller = this.defaultController;
   }

   public void setSourceRegion(Rectangle var1) {
      if (var1 == null) {
         this.sourceRegion = null;
      } else if (var1.x < 0) {
         throw new IllegalArgumentException("sourceRegion.x < 0!");
      } else if (var1.y < 0) {
         throw new IllegalArgumentException("sourceRegion.y < 0!");
      } else if (var1.width <= 0) {
         throw new IllegalArgumentException("sourceRegion.width <= 0!");
      } else if (var1.height <= 0) {
         throw new IllegalArgumentException("sourceRegion.height <= 0!");
      } else if (var1.width <= this.subsamplingXOffset) {
         throw new IllegalStateException("sourceRegion.width <= subsamplingXOffset!");
      } else if (var1.height <= this.subsamplingYOffset) {
         throw new IllegalStateException("sourceRegion.height <= subsamplingYOffset!");
      } else {
         this.sourceRegion = (Rectangle)var1.clone();
      }
   }

   public Rectangle getSourceRegion() {
      return this.sourceRegion == null ? null : (Rectangle)this.sourceRegion.clone();
   }

   public void setSourceSubsampling(int var1, int var2, int var3, int var4) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("sourceXSubsampling <= 0!");
      } else if (var2 <= 0) {
         throw new IllegalArgumentException("sourceYSubsampling <= 0!");
      } else if (var3 >= 0 && var3 < var1) {
         if (var4 >= 0 && var4 < var2) {
            if (this.sourceRegion == null || var3 < this.sourceRegion.width && var4 < this.sourceRegion.height) {
               this.sourceXSubsampling = var1;
               this.sourceYSubsampling = var2;
               this.subsamplingXOffset = var3;
               this.subsamplingYOffset = var4;
            } else {
               throw new IllegalStateException("region contains no pixels!");
            }
         } else {
            throw new IllegalArgumentException("subsamplingYOffset out of range!");
         }
      } else {
         throw new IllegalArgumentException("subsamplingXOffset out of range!");
      }
   }

   public int getSourceXSubsampling() {
      return this.sourceXSubsampling;
   }

   public int getSourceYSubsampling() {
      return this.sourceYSubsampling;
   }

   public int getSubsamplingXOffset() {
      return this.subsamplingXOffset;
   }

   public int getSubsamplingYOffset() {
      return this.subsamplingYOffset;
   }

   public void setSourceBands(int[] var1) {
      if (var1 == null) {
         this.sourceBands = null;
      } else {
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var1[var3];
            if (var4 < 0) {
               throw new IllegalArgumentException("Band value < 0!");
            }

            for(int var5 = var3 + 1; var5 < var2; ++var5) {
               if (var4 == var1[var5]) {
                  throw new IllegalArgumentException("Duplicate band value!");
               }
            }
         }

         this.sourceBands = (int[])((int[])var1.clone());
      }

   }

   public int[] getSourceBands() {
      return this.sourceBands == null ? null : (int[])((int[])this.sourceBands.clone());
   }

   public void setDestinationType(ImageTypeSpecifier var1) {
      this.destinationType = var1;
   }

   public ImageTypeSpecifier getDestinationType() {
      return this.destinationType;
   }

   public void setDestinationOffset(Point var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("destinationOffset == null!");
      } else {
         this.destinationOffset = (Point)var1.clone();
      }
   }

   public Point getDestinationOffset() {
      return (Point)this.destinationOffset.clone();
   }

   public void setController(IIOParamController var1) {
      this.controller = var1;
   }

   public IIOParamController getController() {
      return this.controller;
   }

   public IIOParamController getDefaultController() {
      return this.defaultController;
   }

   public boolean hasController() {
      return this.controller != null;
   }

   public boolean activateController() {
      if (!this.hasController()) {
         throw new IllegalStateException("hasController() == false!");
      } else {
         return this.getController().activate(this);
      }
   }
}
