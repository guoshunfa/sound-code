package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageTypeSpecifier;

class ImageTypeProducer {
   private ImageTypeSpecifier type = null;
   boolean failed = false;
   private int csCode;
   private static final ImageTypeProducer[] defaultTypes = new ImageTypeProducer[12];

   public ImageTypeProducer(int var1) {
      this.csCode = var1;
   }

   public ImageTypeProducer() {
      this.csCode = -1;
   }

   public synchronized ImageTypeSpecifier getType() {
      if (!this.failed && this.type == null) {
         try {
            this.type = this.produce();
         } catch (Throwable var2) {
            this.failed = true;
         }
      }

      return this.type;
   }

   public static synchronized ImageTypeProducer getTypeProducer(int var0) {
      if (var0 >= 0 && var0 < 12) {
         if (defaultTypes[var0] == null) {
            defaultTypes[var0] = new ImageTypeProducer(var0);
         }

         return defaultTypes[var0];
      } else {
         return null;
      }
   }

   protected ImageTypeSpecifier produce() {
      switch(this.csCode) {
      case 1:
         return ImageTypeSpecifier.createFromBufferedImageType(10);
      case 2:
         return ImageTypeSpecifier.createInterleaved(JPEG.JCS.sRGB, JPEG.bOffsRGB, 0, false, false);
      case 3:
      case 4:
      case 7:
      case 8:
      case 9:
      default:
         return null;
      case 5:
         if (JPEG.JCS.getYCC() != null) {
            return ImageTypeSpecifier.createInterleaved(JPEG.JCS.getYCC(), JPEG.bandOffsets[2], 0, false, false);
         }

         return null;
      case 6:
         return ImageTypeSpecifier.createPacked(JPEG.JCS.sRGB, -16777216, 16711680, 65280, 255, 3, false);
      case 10:
         return JPEG.JCS.getYCC() != null ? ImageTypeSpecifier.createInterleaved(JPEG.JCS.getYCC(), JPEG.bandOffsets[3], 0, true, false) : null;
      }
   }
}
