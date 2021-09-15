package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.InputStream;
import java.io.OutputStream;
import sun.awt.image.codec.JPEGImageDecoderImpl;
import sun.awt.image.codec.JPEGImageEncoderImpl;
import sun.awt.image.codec.JPEGParam;

public class JPEGCodec {
   private JPEGCodec() {
   }

   public static JPEGImageDecoder createJPEGDecoder(InputStream var0) {
      return new JPEGImageDecoderImpl(var0);
   }

   public static JPEGImageDecoder createJPEGDecoder(InputStream var0, JPEGDecodeParam var1) {
      return new JPEGImageDecoderImpl(var0, var1);
   }

   public static JPEGImageEncoder createJPEGEncoder(OutputStream var0) {
      return new JPEGImageEncoderImpl(var0);
   }

   public static JPEGImageEncoder createJPEGEncoder(OutputStream var0, JPEGEncodeParam var1) {
      return new JPEGImageEncoderImpl(var0, var1);
   }

   public static JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage var0) {
      int var1 = JPEGParam.getDefaultColorId(var0.getColorModel());
      return getDefaultJPEGEncodeParam(var0.getRaster(), var1);
   }

   public static JPEGEncodeParam getDefaultJPEGEncodeParam(Raster var0, int var1) {
      JPEGParam var2 = new JPEGParam(var1, var0.getNumBands());
      var2.setWidth(var0.getWidth());
      var2.setHeight(var0.getHeight());
      return var2;
   }

   public static JPEGEncodeParam getDefaultJPEGEncodeParam(int var0, int var1) throws ImageFormatException {
      return new JPEGParam(var1, var0);
   }

   public static JPEGEncodeParam getDefaultJPEGEncodeParam(JPEGDecodeParam var0) throws ImageFormatException {
      return new JPEGParam(var0);
   }
}
