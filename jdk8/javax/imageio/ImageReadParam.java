package javax.imageio;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ImageReadParam extends IIOParam {
   protected boolean canSetSourceRenderSize = false;
   protected Dimension sourceRenderSize = null;
   protected BufferedImage destination = null;
   protected int[] destinationBands = null;
   protected int minProgressivePass = 0;
   protected int numProgressivePasses = Integer.MAX_VALUE;

   public void setDestinationType(ImageTypeSpecifier var1) {
      super.setDestinationType(var1);
      this.setDestination((BufferedImage)null);
   }

   public void setDestination(BufferedImage var1) {
      this.destination = var1;
   }

   public BufferedImage getDestination() {
      return this.destination;
   }

   public void setDestinationBands(int[] var1) {
      if (var1 == null) {
         this.destinationBands = null;
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

         this.destinationBands = (int[])((int[])var1.clone());
      }

   }

   public int[] getDestinationBands() {
      return this.destinationBands == null ? null : (int[])((int[])this.destinationBands.clone());
   }

   public boolean canSetSourceRenderSize() {
      return this.canSetSourceRenderSize;
   }

   public void setSourceRenderSize(Dimension var1) throws UnsupportedOperationException {
      if (!this.canSetSourceRenderSize()) {
         throw new UnsupportedOperationException("Can't set source render size!");
      } else {
         if (var1 == null) {
            this.sourceRenderSize = null;
         } else {
            if (var1.width <= 0 || var1.height <= 0) {
               throw new IllegalArgumentException("width or height <= 0!");
            }

            this.sourceRenderSize = (Dimension)var1.clone();
         }

      }
   }

   public Dimension getSourceRenderSize() {
      return this.sourceRenderSize == null ? null : (Dimension)this.sourceRenderSize.clone();
   }

   public void setSourceProgressivePasses(int var1, int var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("minPass < 0!");
      } else if (var2 <= 0) {
         throw new IllegalArgumentException("numPasses <= 0!");
      } else if (var2 != Integer.MAX_VALUE && (var1 + var2 - 1 & Integer.MIN_VALUE) != 0) {
         throw new IllegalArgumentException("minPass + numPasses - 1 > INTEGER.MAX_VALUE!");
      } else {
         this.minProgressivePass = var1;
         this.numProgressivePasses = var2;
      }
   }

   public int getSourceMinProgressivePass() {
      return this.minProgressivePass;
   }

   public int getSourceMaxProgressivePass() {
      return this.numProgressivePasses == Integer.MAX_VALUE ? Integer.MAX_VALUE : this.minProgressivePass + this.numProgressivePasses - 1;
   }

   public int getSourceNumProgressivePasses() {
      return this.numProgressivePasses;
   }
}
