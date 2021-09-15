package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.OutputStream;

public interface JPEGImageEncoder {
   OutputStream getOutputStream();

   void setJPEGEncodeParam(JPEGEncodeParam var1);

   JPEGEncodeParam getJPEGEncodeParam();

   JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage var1) throws ImageFormatException;

   void encode(BufferedImage var1) throws IOException, ImageFormatException;

   void encode(BufferedImage var1, JPEGEncodeParam var2) throws IOException, ImageFormatException;

   int getDefaultColorId(ColorModel var1);

   JPEGEncodeParam getDefaultJPEGEncodeParam(Raster var1, int var2) throws ImageFormatException;

   JPEGEncodeParam getDefaultJPEGEncodeParam(int var1, int var2) throws ImageFormatException;

   JPEGEncodeParam getDefaultJPEGEncodeParam(JPEGDecodeParam var1) throws ImageFormatException;

   void encode(Raster var1) throws IOException, ImageFormatException;

   void encode(Raster var1, JPEGEncodeParam var2) throws IOException, ImageFormatException;
}
