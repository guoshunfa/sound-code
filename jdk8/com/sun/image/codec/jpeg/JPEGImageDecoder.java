package com.sun.image.codec.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;

public interface JPEGImageDecoder {
   JPEGDecodeParam getJPEGDecodeParam();

   void setJPEGDecodeParam(JPEGDecodeParam var1);

   InputStream getInputStream();

   Raster decodeAsRaster() throws IOException, ImageFormatException;

   BufferedImage decodeAsBufferedImage() throws IOException, ImageFormatException;
}
