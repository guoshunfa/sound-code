package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class TruncatedFileException extends RuntimeException {
   private Raster ras = null;
   private BufferedImage bi = null;

   public TruncatedFileException(BufferedImage var1) {
      super("Premature end of input file");
      this.bi = var1;
      this.ras = var1.getData();
   }

   public TruncatedFileException(Raster var1) {
      super("Premature end of input file");
      this.ras = var1;
   }

   public Raster getRaster() {
      return this.ras;
   }

   public BufferedImage getBufferedImage() {
      return this.bi;
   }
}
