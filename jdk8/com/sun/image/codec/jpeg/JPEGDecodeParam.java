package com.sun.image.codec.jpeg;

public interface JPEGDecodeParam extends Cloneable {
   int COLOR_ID_UNKNOWN = 0;
   int COLOR_ID_GRAY = 1;
   int COLOR_ID_RGB = 2;
   int COLOR_ID_YCbCr = 3;
   int COLOR_ID_CMYK = 4;
   int COLOR_ID_PYCC = 5;
   int COLOR_ID_RGBA = 6;
   int COLOR_ID_YCbCrA = 7;
   int COLOR_ID_RGBA_INVERTED = 8;
   int COLOR_ID_YCbCrA_INVERTED = 9;
   int COLOR_ID_PYCCA = 10;
   int COLOR_ID_YCCK = 11;
   int NUM_COLOR_ID = 12;
   int NUM_TABLES = 4;
   int DENSITY_UNIT_ASPECT_RATIO = 0;
   int DENSITY_UNIT_DOTS_INCH = 1;
   int DENSITY_UNIT_DOTS_CM = 2;
   int NUM_DENSITY_UNIT = 3;
   int APP0_MARKER = 224;
   int APP1_MARKER = 225;
   int APP2_MARKER = 226;
   int APP3_MARKER = 227;
   int APP4_MARKER = 228;
   int APP5_MARKER = 229;
   int APP6_MARKER = 230;
   int APP7_MARKER = 231;
   int APP8_MARKER = 232;
   int APP9_MARKER = 233;
   int APPA_MARKER = 234;
   int APPB_MARKER = 235;
   int APPC_MARKER = 236;
   int APPD_MARKER = 237;
   int APPE_MARKER = 238;
   int APPF_MARKER = 239;
   int COMMENT_MARKER = 254;

   Object clone();

   int getWidth();

   int getHeight();

   int getHorizontalSubsampling(int var1);

   int getVerticalSubsampling(int var1);

   JPEGQTable getQTable(int var1);

   JPEGQTable getQTableForComponent(int var1);

   JPEGHuffmanTable getDCHuffmanTable(int var1);

   JPEGHuffmanTable getDCHuffmanTableForComponent(int var1);

   JPEGHuffmanTable getACHuffmanTable(int var1);

   JPEGHuffmanTable getACHuffmanTableForComponent(int var1);

   int getDCHuffmanComponentMapping(int var1);

   int getACHuffmanComponentMapping(int var1);

   int getQTableComponentMapping(int var1);

   boolean isImageInfoValid();

   boolean isTableInfoValid();

   boolean getMarker(int var1);

   byte[][] getMarkerData(int var1);

   int getEncodedColorID();

   int getNumComponents();

   int getRestartInterval();

   int getDensityUnit();

   int getXDensity();

   int getYDensity();
}
