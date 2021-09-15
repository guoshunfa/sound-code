package com.sun.image.codec.jpeg;

public interface JPEGEncodeParam extends Cloneable, JPEGDecodeParam {
   Object clone();

   void setHorizontalSubsampling(int var1, int var2);

   void setVerticalSubsampling(int var1, int var2);

   void setQTable(int var1, JPEGQTable var2);

   void setDCHuffmanTable(int var1, JPEGHuffmanTable var2);

   void setACHuffmanTable(int var1, JPEGHuffmanTable var2);

   void setDCHuffmanComponentMapping(int var1, int var2);

   void setACHuffmanComponentMapping(int var1, int var2);

   void setQTableComponentMapping(int var1, int var2);

   void setImageInfoValid(boolean var1);

   void setTableInfoValid(boolean var1);

   void setMarkerData(int var1, byte[][] var2);

   void addMarkerData(int var1, byte[] var2);

   void setRestartInterval(int var1);

   void setDensityUnit(int var1);

   void setXDensity(int var1);

   void setYDensity(int var1);

   void setQuality(float var1, boolean var2);
}
