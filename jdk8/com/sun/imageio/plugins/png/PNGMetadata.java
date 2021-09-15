package com.sun.imageio.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class PNGMetadata extends IIOMetadata implements Cloneable {
   public static final String nativeMetadataFormatName = "javax_imageio_png_1.0";
   protected static final String nativeMetadataFormatClassName = "com.sun.imageio.plugins.png.PNGMetadataFormat";
   static final String[] IHDR_colorTypeNames = new String[]{"Grayscale", null, "RGB", "Palette", "GrayAlpha", null, "RGBAlpha"};
   static final int[] IHDR_numChannels = new int[]{1, 0, 3, 3, 2, 0, 4};
   static final String[] IHDR_bitDepths = new String[]{"1", "2", "4", "8", "16"};
   static final String[] IHDR_compressionMethodNames = new String[]{"deflate"};
   static final String[] IHDR_filterMethodNames = new String[]{"adaptive"};
   static final String[] IHDR_interlaceMethodNames = new String[]{"none", "adam7"};
   static final String[] iCCP_compressionMethodNames = new String[]{"deflate"};
   static final String[] zTXt_compressionMethodNames = new String[]{"deflate"};
   public static final int PHYS_UNIT_UNKNOWN = 0;
   public static final int PHYS_UNIT_METER = 1;
   static final String[] unitSpecifierNames = new String[]{"unknown", "meter"};
   static final String[] renderingIntentNames = new String[]{"Perceptual", "Relative colorimetric", "Saturation", "Absolute colorimetric"};
   static final String[] colorSpaceTypeNames = new String[]{"GRAY", null, "RGB", "RGB", "GRAY", null, "RGB"};
   public boolean IHDR_present;
   public int IHDR_width;
   public int IHDR_height;
   public int IHDR_bitDepth;
   public int IHDR_colorType;
   public int IHDR_compressionMethod;
   public int IHDR_filterMethod;
   public int IHDR_interlaceMethod;
   public boolean PLTE_present;
   public byte[] PLTE_red;
   public byte[] PLTE_green;
   public byte[] PLTE_blue;
   public int[] PLTE_order = null;
   public boolean bKGD_present;
   public int bKGD_colorType;
   public int bKGD_index;
   public int bKGD_gray;
   public int bKGD_red;
   public int bKGD_green;
   public int bKGD_blue;
   public boolean cHRM_present;
   public int cHRM_whitePointX;
   public int cHRM_whitePointY;
   public int cHRM_redX;
   public int cHRM_redY;
   public int cHRM_greenX;
   public int cHRM_greenY;
   public int cHRM_blueX;
   public int cHRM_blueY;
   public boolean gAMA_present;
   public int gAMA_gamma;
   public boolean hIST_present;
   public char[] hIST_histogram;
   public boolean iCCP_present;
   public String iCCP_profileName;
   public int iCCP_compressionMethod;
   public byte[] iCCP_compressedProfile;
   public ArrayList<String> iTXt_keyword = new ArrayList();
   public ArrayList<Boolean> iTXt_compressionFlag = new ArrayList();
   public ArrayList<Integer> iTXt_compressionMethod = new ArrayList();
   public ArrayList<String> iTXt_languageTag = new ArrayList();
   public ArrayList<String> iTXt_translatedKeyword = new ArrayList();
   public ArrayList<String> iTXt_text = new ArrayList();
   public boolean pHYs_present;
   public int pHYs_pixelsPerUnitXAxis;
   public int pHYs_pixelsPerUnitYAxis;
   public int pHYs_unitSpecifier;
   public boolean sBIT_present;
   public int sBIT_colorType;
   public int sBIT_grayBits;
   public int sBIT_redBits;
   public int sBIT_greenBits;
   public int sBIT_blueBits;
   public int sBIT_alphaBits;
   public boolean sPLT_present;
   public String sPLT_paletteName;
   public int sPLT_sampleDepth;
   public int[] sPLT_red;
   public int[] sPLT_green;
   public int[] sPLT_blue;
   public int[] sPLT_alpha;
   public int[] sPLT_frequency;
   public boolean sRGB_present;
   public int sRGB_renderingIntent;
   public ArrayList<String> tEXt_keyword = new ArrayList();
   public ArrayList<String> tEXt_text = new ArrayList();
   public boolean tIME_present;
   public int tIME_year;
   public int tIME_month;
   public int tIME_day;
   public int tIME_hour;
   public int tIME_minute;
   public int tIME_second;
   public boolean tRNS_present;
   public int tRNS_colorType;
   public byte[] tRNS_alpha;
   public int tRNS_gray;
   public int tRNS_red;
   public int tRNS_green;
   public int tRNS_blue;
   public ArrayList<String> zTXt_keyword = new ArrayList();
   public ArrayList<Integer> zTXt_compressionMethod = new ArrayList();
   public ArrayList<String> zTXt_text = new ArrayList();
   public ArrayList<String> unknownChunkType = new ArrayList();
   public ArrayList<byte[]> unknownChunkData = new ArrayList();

   public PNGMetadata() {
      super(true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", (String[])null, (String[])null);
   }

   public PNGMetadata(IIOMetadata var1) {
   }

   public void initialize(ImageTypeSpecifier var1, int var2) {
      ColorModel var3 = var1.getColorModel();
      SampleModel var4 = var1.getSampleModel();
      int[] var5 = var4.getSampleSize();
      int var6 = var5[0];

      for(int var7 = 1; var7 < var5.length; ++var7) {
         if (var5[var7] > var6) {
            var6 = var5[var7];
         }
      }

      if (var5.length > 1 && var6 < 8) {
         var6 = 8;
      }

      if (var6 > 2 && var6 < 4) {
         var6 = 4;
      } else if (var6 > 4 && var6 < 8) {
         var6 = 8;
      } else if (var6 > 8 && var6 < 16) {
         var6 = 16;
      } else if (var6 > 16) {
         throw new RuntimeException("bitDepth > 16!");
      }

      this.IHDR_bitDepth = var6;
      if (var3 instanceof IndexColorModel) {
         IndexColorModel var23 = (IndexColorModel)var3;
         int var8 = var23.getMapSize();
         byte[] var9 = new byte[var8];
         var23.getReds(var9);
         byte[] var10 = new byte[var8];
         var23.getGreens(var10);
         byte[] var11 = new byte[var8];
         var23.getBlues(var11);
         boolean var12 = false;
         if (!this.IHDR_present || this.IHDR_colorType != 3) {
            var12 = true;
            int var13 = 255 / ((1 << this.IHDR_bitDepth) - 1);

            for(int var14 = 0; var14 < var8; ++var14) {
               byte var15 = var9[var14];
               if (var15 != (byte)(var14 * var13) || var15 != var10[var14] || var15 != var11[var14]) {
                  var12 = false;
                  break;
               }
            }
         }

         boolean var24 = var3.hasAlpha();
         byte[] var25 = null;
         if (var24) {
            var25 = new byte[var8];
            var23.getAlphas(var25);
         }

         if (!var12 || !var24 || var6 != 8 && var6 != 16) {
            if (var12 && !var24) {
               this.IHDR_colorType = 0;
            } else {
               this.IHDR_colorType = 3;
               this.PLTE_present = true;
               this.PLTE_order = null;
               this.PLTE_red = (byte[])((byte[])var9.clone());
               this.PLTE_green = (byte[])((byte[])var10.clone());
               this.PLTE_blue = (byte[])((byte[])var11.clone());
               if (var24) {
                  this.tRNS_present = true;
                  this.tRNS_colorType = 3;
                  this.PLTE_order = new int[var25.length];
                  byte[] var26 = new byte[var25.length];
                  int var16 = 0;

                  for(int var17 = 0; var17 < var25.length; ++var17) {
                     if (var25[var17] != -1) {
                        this.PLTE_order[var17] = var16;
                        var26[var16] = var25[var17];
                        ++var16;
                     }
                  }

                  for(int var18 = 0; var18 < var25.length; ++var18) {
                     if (var25[var18] == -1) {
                        this.PLTE_order[var18] = var16++;
                     }
                  }

                  byte[] var27 = this.PLTE_red;
                  byte[] var19 = this.PLTE_green;
                  byte[] var20 = this.PLTE_blue;
                  int var21 = var27.length;
                  this.PLTE_red = new byte[var21];
                  this.PLTE_green = new byte[var21];
                  this.PLTE_blue = new byte[var21];

                  for(int var22 = 0; var22 < var21; ++var22) {
                     this.PLTE_red[this.PLTE_order[var22]] = var27[var22];
                     this.PLTE_green[this.PLTE_order[var22]] = var19[var22];
                     this.PLTE_blue[this.PLTE_order[var22]] = var20[var22];
                  }

                  this.tRNS_alpha = new byte[var16];
                  System.arraycopy(var26, 0, this.tRNS_alpha, 0, var16);
               }
            }
         } else {
            this.IHDR_colorType = 4;
         }
      } else if (var2 == 1) {
         this.IHDR_colorType = 0;
      } else if (var2 == 2) {
         this.IHDR_colorType = 4;
      } else if (var2 == 3) {
         this.IHDR_colorType = 2;
      } else {
         if (var2 != 4) {
            throw new RuntimeException("Number of bands not 1-4!");
         }

         this.IHDR_colorType = 6;
      }

      this.IHDR_present = true;
   }

   public boolean isReadOnly() {
      return false;
   }

   private ArrayList<byte[]> cloneBytesArrayList(ArrayList<byte[]> var1) {
      if (var1 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList(var1.size());
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            byte[] var4 = (byte[])var3.next();
            var2.add(var4 == null ? null : (byte[])((byte[])var4.clone()));
         }

         return var2;
      }
   }

   public Object clone() {
      PNGMetadata var1;
      try {
         var1 = (PNGMetadata)super.clone();
      } catch (CloneNotSupportedException var3) {
         return null;
      }

      var1.unknownChunkData = this.cloneBytesArrayList(this.unknownChunkData);
      return var1;
   }

   public Node getAsTree(String var1) {
      if (var1.equals("javax_imageio_png_1.0")) {
         return this.getNativeTree();
      } else if (var1.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
      }
   }

   private Node getNativeTree() {
      IIOMetadataNode var1 = null;
      IIOMetadataNode var2 = new IIOMetadataNode("javax_imageio_png_1.0");
      IIOMetadataNode var3;
      if (this.IHDR_present) {
         var3 = new IIOMetadataNode("IHDR");
         var3.setAttribute("width", Integer.toString(this.IHDR_width));
         var3.setAttribute("height", Integer.toString(this.IHDR_height));
         var3.setAttribute("bitDepth", Integer.toString(this.IHDR_bitDepth));
         var3.setAttribute("colorType", IHDR_colorTypeNames[this.IHDR_colorType]);
         var3.setAttribute("compressionMethod", IHDR_compressionMethodNames[this.IHDR_compressionMethod]);
         var3.setAttribute("filterMethod", IHDR_filterMethodNames[this.IHDR_filterMethod]);
         var3.setAttribute("interlaceMethod", IHDR_interlaceMethodNames[this.IHDR_interlaceMethod]);
         var2.appendChild(var3);
      }

      int var4;
      int var5;
      IIOMetadataNode var6;
      if (this.PLTE_present) {
         var3 = new IIOMetadataNode("PLTE");
         var4 = this.PLTE_red.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = new IIOMetadataNode("PLTEEntry");
            var6.setAttribute("index", Integer.toString(var5));
            var6.setAttribute("red", Integer.toString(this.PLTE_red[var5] & 255));
            var6.setAttribute("green", Integer.toString(this.PLTE_green[var5] & 255));
            var6.setAttribute("blue", Integer.toString(this.PLTE_blue[var5] & 255));
            var3.appendChild(var6);
         }

         var2.appendChild(var3);
      }

      if (this.bKGD_present) {
         var3 = new IIOMetadataNode("bKGD");
         if (this.bKGD_colorType == 3) {
            var1 = new IIOMetadataNode("bKGD_Palette");
            var1.setAttribute("index", Integer.toString(this.bKGD_index));
         } else if (this.bKGD_colorType == 0) {
            var1 = new IIOMetadataNode("bKGD_Grayscale");
            var1.setAttribute("gray", Integer.toString(this.bKGD_gray));
         } else if (this.bKGD_colorType == 2) {
            var1 = new IIOMetadataNode("bKGD_RGB");
            var1.setAttribute("red", Integer.toString(this.bKGD_red));
            var1.setAttribute("green", Integer.toString(this.bKGD_green));
            var1.setAttribute("blue", Integer.toString(this.bKGD_blue));
         }

         var3.appendChild(var1);
         var2.appendChild(var3);
      }

      if (this.cHRM_present) {
         var3 = new IIOMetadataNode("cHRM");
         var3.setAttribute("whitePointX", Integer.toString(this.cHRM_whitePointX));
         var3.setAttribute("whitePointY", Integer.toString(this.cHRM_whitePointY));
         var3.setAttribute("redX", Integer.toString(this.cHRM_redX));
         var3.setAttribute("redY", Integer.toString(this.cHRM_redY));
         var3.setAttribute("greenX", Integer.toString(this.cHRM_greenX));
         var3.setAttribute("greenY", Integer.toString(this.cHRM_greenY));
         var3.setAttribute("blueX", Integer.toString(this.cHRM_blueX));
         var3.setAttribute("blueY", Integer.toString(this.cHRM_blueY));
         var2.appendChild(var3);
      }

      if (this.gAMA_present) {
         var3 = new IIOMetadataNode("gAMA");
         var3.setAttribute("value", Integer.toString(this.gAMA_gamma));
         var2.appendChild(var3);
      }

      IIOMetadataNode var8;
      if (this.hIST_present) {
         var3 = new IIOMetadataNode("hIST");

         for(var4 = 0; var4 < this.hIST_histogram.length; ++var4) {
            var8 = new IIOMetadataNode("hISTEntry");
            var8.setAttribute("index", Integer.toString(var4));
            var8.setAttribute("value", Integer.toString(this.hIST_histogram[var4]));
            var3.appendChild(var8);
         }

         var2.appendChild(var3);
      }

      if (this.iCCP_present) {
         var3 = new IIOMetadataNode("iCCP");
         var3.setAttribute("profileName", this.iCCP_profileName);
         var3.setAttribute("compressionMethod", iCCP_compressionMethodNames[this.iCCP_compressionMethod]);
         Object var7 = this.iCCP_compressedProfile;
         if (var7 != null) {
            var7 = ((byte[])((byte[])var7)).clone();
         }

         var3.setUserObject(var7);
         var2.appendChild(var3);
      }

      if (this.iTXt_keyword.size() > 0) {
         var3 = new IIOMetadataNode("iTXt");

         for(var4 = 0; var4 < this.iTXt_keyword.size(); ++var4) {
            var8 = new IIOMetadataNode("iTXtEntry");
            var8.setAttribute("keyword", (String)this.iTXt_keyword.get(var4));
            var8.setAttribute("compressionFlag", (Boolean)this.iTXt_compressionFlag.get(var4) ? "TRUE" : "FALSE");
            var8.setAttribute("compressionMethod", ((Integer)this.iTXt_compressionMethod.get(var4)).toString());
            var8.setAttribute("languageTag", (String)this.iTXt_languageTag.get(var4));
            var8.setAttribute("translatedKeyword", (String)this.iTXt_translatedKeyword.get(var4));
            var8.setAttribute("text", (String)this.iTXt_text.get(var4));
            var3.appendChild(var8);
         }

         var2.appendChild(var3);
      }

      if (this.pHYs_present) {
         var3 = new IIOMetadataNode("pHYs");
         var3.setAttribute("pixelsPerUnitXAxis", Integer.toString(this.pHYs_pixelsPerUnitXAxis));
         var3.setAttribute("pixelsPerUnitYAxis", Integer.toString(this.pHYs_pixelsPerUnitYAxis));
         var3.setAttribute("unitSpecifier", unitSpecifierNames[this.pHYs_unitSpecifier]);
         var2.appendChild(var3);
      }

      if (this.sBIT_present) {
         var3 = new IIOMetadataNode("sBIT");
         if (this.sBIT_colorType == 0) {
            var1 = new IIOMetadataNode("sBIT_Grayscale");
            var1.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
         } else if (this.sBIT_colorType == 4) {
            var1 = new IIOMetadataNode("sBIT_GrayAlpha");
            var1.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
            var1.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
         } else if (this.sBIT_colorType == 2) {
            var1 = new IIOMetadataNode("sBIT_RGB");
            var1.setAttribute("red", Integer.toString(this.sBIT_redBits));
            var1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
            var1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
         } else if (this.sBIT_colorType == 6) {
            var1 = new IIOMetadataNode("sBIT_RGBAlpha");
            var1.setAttribute("red", Integer.toString(this.sBIT_redBits));
            var1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
            var1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
            var1.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
         } else if (this.sBIT_colorType == 3) {
            var1 = new IIOMetadataNode("sBIT_Palette");
            var1.setAttribute("red", Integer.toString(this.sBIT_redBits));
            var1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
            var1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
         }

         var3.appendChild(var1);
         var2.appendChild(var3);
      }

      if (this.sPLT_present) {
         var3 = new IIOMetadataNode("sPLT");
         var3.setAttribute("name", this.sPLT_paletteName);
         var3.setAttribute("sampleDepth", Integer.toString(this.sPLT_sampleDepth));
         var4 = this.sPLT_red.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = new IIOMetadataNode("sPLTEntry");
            var6.setAttribute("index", Integer.toString(var5));
            var6.setAttribute("red", Integer.toString(this.sPLT_red[var5]));
            var6.setAttribute("green", Integer.toString(this.sPLT_green[var5]));
            var6.setAttribute("blue", Integer.toString(this.sPLT_blue[var5]));
            var6.setAttribute("alpha", Integer.toString(this.sPLT_alpha[var5]));
            var6.setAttribute("frequency", Integer.toString(this.sPLT_frequency[var5]));
            var3.appendChild(var6);
         }

         var2.appendChild(var3);
      }

      if (this.sRGB_present) {
         var3 = new IIOMetadataNode("sRGB");
         var3.setAttribute("renderingIntent", renderingIntentNames[this.sRGB_renderingIntent]);
         var2.appendChild(var3);
      }

      if (this.tEXt_keyword.size() > 0) {
         var3 = new IIOMetadataNode("tEXt");

         for(var4 = 0; var4 < this.tEXt_keyword.size(); ++var4) {
            var8 = new IIOMetadataNode("tEXtEntry");
            var8.setAttribute("keyword", (String)this.tEXt_keyword.get(var4));
            var8.setAttribute("value", (String)this.tEXt_text.get(var4));
            var3.appendChild(var8);
         }

         var2.appendChild(var3);
      }

      if (this.tIME_present) {
         var3 = new IIOMetadataNode("tIME");
         var3.setAttribute("year", Integer.toString(this.tIME_year));
         var3.setAttribute("month", Integer.toString(this.tIME_month));
         var3.setAttribute("day", Integer.toString(this.tIME_day));
         var3.setAttribute("hour", Integer.toString(this.tIME_hour));
         var3.setAttribute("minute", Integer.toString(this.tIME_minute));
         var3.setAttribute("second", Integer.toString(this.tIME_second));
         var2.appendChild(var3);
      }

      if (this.tRNS_present) {
         var3 = new IIOMetadataNode("tRNS");
         if (this.tRNS_colorType == 3) {
            var1 = new IIOMetadataNode("tRNS_Palette");

            for(var4 = 0; var4 < this.tRNS_alpha.length; ++var4) {
               var8 = new IIOMetadataNode("tRNS_PaletteEntry");
               var8.setAttribute("index", Integer.toString(var4));
               var8.setAttribute("alpha", Integer.toString(this.tRNS_alpha[var4] & 255));
               var1.appendChild(var8);
            }
         } else if (this.tRNS_colorType == 0) {
            var1 = new IIOMetadataNode("tRNS_Grayscale");
            var1.setAttribute("gray", Integer.toString(this.tRNS_gray));
         } else if (this.tRNS_colorType == 2) {
            var1 = new IIOMetadataNode("tRNS_RGB");
            var1.setAttribute("red", Integer.toString(this.tRNS_red));
            var1.setAttribute("green", Integer.toString(this.tRNS_green));
            var1.setAttribute("blue", Integer.toString(this.tRNS_blue));
         }

         var3.appendChild(var1);
         var2.appendChild(var3);
      }

      if (this.zTXt_keyword.size() > 0) {
         var3 = new IIOMetadataNode("zTXt");

         for(var4 = 0; var4 < this.zTXt_keyword.size(); ++var4) {
            var8 = new IIOMetadataNode("zTXtEntry");
            var8.setAttribute("keyword", (String)this.zTXt_keyword.get(var4));
            int var9 = (Integer)this.zTXt_compressionMethod.get(var4);
            var8.setAttribute("compressionMethod", zTXt_compressionMethodNames[var9]);
            var8.setAttribute("text", (String)this.zTXt_text.get(var4));
            var3.appendChild(var8);
         }

         var2.appendChild(var3);
      }

      if (this.unknownChunkType.size() > 0) {
         var3 = new IIOMetadataNode("UnknownChunks");

         for(var4 = 0; var4 < this.unknownChunkType.size(); ++var4) {
            var8 = new IIOMetadataNode("UnknownChunk");
            var8.setAttribute("type", (String)this.unknownChunkType.get(var4));
            var8.setUserObject((byte[])((byte[])this.unknownChunkData.get(var4)));
            var3.appendChild(var8);
         }

         var2.appendChild(var3);
      }

      return var2;
   }

   private int getNumChannels() {
      int var1 = IHDR_numChannels[this.IHDR_colorType];
      if (this.IHDR_colorType == 3 && this.tRNS_present && this.tRNS_colorType == this.IHDR_colorType) {
         var1 = 4;
      }

      return var1;
   }

   public IIOMetadataNode getStandardChromaNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Chroma");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("ColorSpaceType");
      var2.setAttribute("name", colorSpaceTypeNames[this.IHDR_colorType]);
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("NumChannels");
      var2.setAttribute("value", Integer.toString(this.getNumChannels()));
      var1.appendChild(var2);
      if (this.gAMA_present) {
         var2 = new IIOMetadataNode("Gamma");
         var2.setAttribute("value", Float.toString((float)this.gAMA_gamma * 1.0E-5F));
         var1.appendChild(var2);
      }

      var2 = new IIOMetadataNode("BlackIsZero");
      var2.setAttribute("value", "TRUE");
      var1.appendChild(var2);
      int var4;
      if (this.PLTE_present) {
         boolean var3 = this.tRNS_present && this.tRNS_colorType == 3;
         var2 = new IIOMetadataNode("Palette");

         for(var4 = 0; var4 < this.PLTE_red.length; ++var4) {
            IIOMetadataNode var5 = new IIOMetadataNode("PaletteEntry");
            var5.setAttribute("index", Integer.toString(var4));
            var5.setAttribute("red", Integer.toString(this.PLTE_red[var4] & 255));
            var5.setAttribute("green", Integer.toString(this.PLTE_green[var4] & 255));
            var5.setAttribute("blue", Integer.toString(this.PLTE_blue[var4] & 255));
            if (var3) {
               int var6 = var4 < this.tRNS_alpha.length ? this.tRNS_alpha[var4] & 255 : 255;
               var5.setAttribute("alpha", Integer.toString(var6));
            }

            var2.appendChild(var5);
         }

         var1.appendChild(var2);
      }

      if (this.bKGD_present) {
         if (this.bKGD_colorType == 3) {
            var2 = new IIOMetadataNode("BackgroundIndex");
            var2.setAttribute("value", Integer.toString(this.bKGD_index));
         } else {
            var2 = new IIOMetadataNode("BackgroundColor");
            int var7;
            int var8;
            if (this.bKGD_colorType == 0) {
               var7 = var4 = var8 = this.bKGD_gray;
            } else {
               var7 = this.bKGD_red;
               var4 = this.bKGD_green;
               var8 = this.bKGD_blue;
            }

            var2.setAttribute("red", Integer.toString(var7));
            var2.setAttribute("green", Integer.toString(var4));
            var2.setAttribute("blue", Integer.toString(var8));
         }

         var1.appendChild(var2);
      }

      return var1;
   }

   public IIOMetadataNode getStandardCompressionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Compression");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("CompressionTypeName");
      var2.setAttribute("value", "deflate");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("Lossless");
      var2.setAttribute("value", "TRUE");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("NumProgressiveScans");
      var2.setAttribute("value", this.IHDR_interlaceMethod == 0 ? "1" : "7");
      var1.appendChild(var2);
      return var1;
   }

   private String repeat(String var1, int var2) {
      if (var2 == 1) {
         return var1;
      } else {
         StringBuffer var3 = new StringBuffer((var1.length() + 1) * var2 - 1);
         var3.append(var1);

         for(int var4 = 1; var4 < var2; ++var4) {
            var3.append(" ");
            var3.append(var1);
         }

         return var3.toString();
      }
   }

   public IIOMetadataNode getStandardDataNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Data");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("PlanarConfiguration");
      var2.setAttribute("value", "PixelInterleaved");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("SampleFormat");
      var2.setAttribute("value", this.IHDR_colorType == 3 ? "Index" : "UnsignedIntegral");
      var1.appendChild(var2);
      String var3 = Integer.toString(this.IHDR_bitDepth);
      var2 = new IIOMetadataNode("BitsPerSample");
      var2.setAttribute("value", this.repeat(var3, this.getNumChannels()));
      var1.appendChild(var2);
      if (this.sBIT_present) {
         var2 = new IIOMetadataNode("SignificantBitsPerSample");
         String var4;
         if (this.sBIT_colorType != 0 && this.sBIT_colorType != 4) {
            var4 = Integer.toString(this.sBIT_redBits) + " " + Integer.toString(this.sBIT_greenBits) + " " + Integer.toString(this.sBIT_blueBits);
         } else {
            var4 = Integer.toString(this.sBIT_grayBits);
         }

         if (this.sBIT_colorType == 4 || this.sBIT_colorType == 6) {
            var4 = var4 + " " + Integer.toString(this.sBIT_alphaBits);
         }

         var2.setAttribute("value", var4);
         var1.appendChild(var2);
      }

      return var1;
   }

   public IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Dimension");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("PixelAspectRatio");
      float var3 = this.pHYs_present ? (float)this.pHYs_pixelsPerUnitXAxis / (float)this.pHYs_pixelsPerUnitYAxis : 1.0F;
      var2.setAttribute("value", Float.toString(var3));
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("ImageOrientation");
      var2.setAttribute("value", "Normal");
      var1.appendChild(var2);
      if (this.pHYs_present && this.pHYs_unitSpecifier == 1) {
         var2 = new IIOMetadataNode("HorizontalPixelSize");
         var2.setAttribute("value", Float.toString(1000.0F / (float)this.pHYs_pixelsPerUnitXAxis));
         var1.appendChild(var2);
         var2 = new IIOMetadataNode("VerticalPixelSize");
         var2.setAttribute("value", Float.toString(1000.0F / (float)this.pHYs_pixelsPerUnitYAxis));
         var1.appendChild(var2);
      }

      return var1;
   }

   public IIOMetadataNode getStandardDocumentNode() {
      if (!this.tIME_present) {
         return null;
      } else {
         IIOMetadataNode var1 = new IIOMetadataNode("Document");
         IIOMetadataNode var2 = null;
         var2 = new IIOMetadataNode("ImageModificationTime");
         var2.setAttribute("year", Integer.toString(this.tIME_year));
         var2.setAttribute("month", Integer.toString(this.tIME_month));
         var2.setAttribute("day", Integer.toString(this.tIME_day));
         var2.setAttribute("hour", Integer.toString(this.tIME_hour));
         var2.setAttribute("minute", Integer.toString(this.tIME_minute));
         var2.setAttribute("second", Integer.toString(this.tIME_second));
         var1.appendChild(var2);
         return var1;
      }
   }

   public IIOMetadataNode getStandardTextNode() {
      int var1 = this.tEXt_keyword.size() + this.iTXt_keyword.size() + this.zTXt_keyword.size();
      if (var1 == 0) {
         return null;
      } else {
         IIOMetadataNode var2 = new IIOMetadataNode("Text");
         IIOMetadataNode var3 = null;

         int var4;
         for(var4 = 0; var4 < this.tEXt_keyword.size(); ++var4) {
            var3 = new IIOMetadataNode("TextEntry");
            var3.setAttribute("keyword", (String)this.tEXt_keyword.get(var4));
            var3.setAttribute("value", (String)this.tEXt_text.get(var4));
            var3.setAttribute("encoding", "ISO-8859-1");
            var3.setAttribute("compression", "none");
            var2.appendChild(var3);
         }

         for(var4 = 0; var4 < this.iTXt_keyword.size(); ++var4) {
            var3 = new IIOMetadataNode("TextEntry");
            var3.setAttribute("keyword", (String)this.iTXt_keyword.get(var4));
            var3.setAttribute("value", (String)this.iTXt_text.get(var4));
            var3.setAttribute("language", (String)this.iTXt_languageTag.get(var4));
            if ((Boolean)this.iTXt_compressionFlag.get(var4)) {
               var3.setAttribute("compression", "zip");
            } else {
               var3.setAttribute("compression", "none");
            }

            var2.appendChild(var3);
         }

         for(var4 = 0; var4 < this.zTXt_keyword.size(); ++var4) {
            var3 = new IIOMetadataNode("TextEntry");
            var3.setAttribute("keyword", (String)this.zTXt_keyword.get(var4));
            var3.setAttribute("value", (String)this.zTXt_text.get(var4));
            var3.setAttribute("compression", "zip");
            var2.appendChild(var3);
         }

         return var2;
      }
   }

   public IIOMetadataNode getStandardTransparencyNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Transparency");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("Alpha");
      boolean var3 = this.IHDR_colorType == 6 || this.IHDR_colorType == 4 || this.IHDR_colorType == 3 && this.tRNS_present && this.tRNS_colorType == this.IHDR_colorType && this.tRNS_alpha != null;
      var2.setAttribute("value", var3 ? "nonpremultipled" : "none");
      var1.appendChild(var2);
      if (this.tRNS_present) {
         var2 = new IIOMetadataNode("TransparentColor");
         if (this.tRNS_colorType == 2) {
            var2.setAttribute("value", Integer.toString(this.tRNS_red) + " " + Integer.toString(this.tRNS_green) + " " + Integer.toString(this.tRNS_blue));
         } else if (this.tRNS_colorType == 0) {
            var2.setAttribute("value", Integer.toString(this.tRNS_gray));
         }

         var1.appendChild(var2);
      }

      return var1;
   }

   private void fatal(Node var1, String var2) throws IIOInvalidTreeException {
      throw new IIOInvalidTreeException(var2, var1);
   }

   private String getStringAttribute(Node var1, String var2, String var3, boolean var4) throws IIOInvalidTreeException {
      Node var5 = var1.getAttributes().getNamedItem(var2);
      if (var5 == null) {
         if (!var4) {
            return var3;
         }

         this.fatal(var1, "Required attribute " + var2 + " not present!");
      }

      return var5.getNodeValue();
   }

   private int getIntAttribute(Node var1, String var2, int var3, boolean var4) throws IIOInvalidTreeException {
      String var5 = this.getStringAttribute(var1, var2, (String)null, var4);
      return var5 == null ? var3 : Integer.parseInt(var5);
   }

   private float getFloatAttribute(Node var1, String var2, float var3, boolean var4) throws IIOInvalidTreeException {
      String var5 = this.getStringAttribute(var1, var2, (String)null, var4);
      return var5 == null ? var3 : Float.parseFloat(var5);
   }

   private int getIntAttribute(Node var1, String var2) throws IIOInvalidTreeException {
      return this.getIntAttribute(var1, var2, -1, true);
   }

   private float getFloatAttribute(Node var1, String var2) throws IIOInvalidTreeException {
      return this.getFloatAttribute(var1, var2, -1.0F, true);
   }

   private boolean getBooleanAttribute(Node var1, String var2, boolean var3, boolean var4) throws IIOInvalidTreeException {
      Node var5 = var1.getAttributes().getNamedItem(var2);
      if (var5 == null) {
         if (!var4) {
            return var3;
         }

         this.fatal(var1, "Required attribute " + var2 + " not present!");
      }

      String var6 = var5.getNodeValue();
      if (!var6.equals("TRUE") && !var6.equals("true")) {
         if (!var6.equals("FALSE") && !var6.equals("false")) {
            this.fatal(var1, "Attribute " + var2 + " must be 'TRUE' or 'FALSE'!");
            return false;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private boolean getBooleanAttribute(Node var1, String var2) throws IIOInvalidTreeException {
      return this.getBooleanAttribute(var1, var2, false, true);
   }

   private int getEnumeratedAttribute(Node var1, String var2, String[] var3, int var4, boolean var5) throws IIOInvalidTreeException {
      Node var6 = var1.getAttributes().getNamedItem(var2);
      if (var6 == null) {
         if (!var5) {
            return var4;
         }

         this.fatal(var1, "Required attribute " + var2 + " not present!");
      }

      String var7 = var6.getNodeValue();

      for(int var8 = 0; var8 < var3.length; ++var8) {
         if (var7.equals(var3[var8])) {
            return var8;
         }
      }

      this.fatal(var1, "Illegal value for attribute " + var2 + "!");
      return -1;
   }

   private int getEnumeratedAttribute(Node var1, String var2, String[] var3) throws IIOInvalidTreeException {
      return this.getEnumeratedAttribute(var1, var2, var3, -1, true);
   }

   private String getAttribute(Node var1, String var2, String var3, boolean var4) throws IIOInvalidTreeException {
      Node var5 = var1.getAttributes().getNamedItem(var2);
      if (var5 == null) {
         if (!var4) {
            return var3;
         }

         this.fatal(var1, "Required attribute " + var2 + " not present!");
      }

      return var5.getNodeValue();
   }

   private String getAttribute(Node var1, String var2) throws IIOInvalidTreeException {
      return this.getAttribute(var1, var2, (String)null, true);
   }

   public void mergeTree(String var1, Node var2) throws IIOInvalidTreeException {
      if (var1.equals("javax_imageio_png_1.0")) {
         if (var2 == null) {
            throw new IllegalArgumentException("root == null!");
         }

         this.mergeNativeTree(var2);
      } else {
         if (!var1.equals("javax_imageio_1.0")) {
            throw new IllegalArgumentException("Not a recognized format!");
         }

         if (var2 == null) {
            throw new IllegalArgumentException("root == null!");
         }

         this.mergeStandardTree(var2);
      }

   }

   private void mergeNativeTree(Node var1) throws IIOInvalidTreeException {
      if (!var1.getNodeName().equals("javax_imageio_png_1.0")) {
         this.fatal(var1, "Root must be javax_imageio_png_1.0");
      }

      for(Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         String var3 = var2.getNodeName();
         if (var3.equals("IHDR")) {
            this.IHDR_width = this.getIntAttribute(var2, "width");
            this.IHDR_height = this.getIntAttribute(var2, "height");
            this.IHDR_bitDepth = Integer.valueOf(IHDR_bitDepths[this.getEnumeratedAttribute(var2, "bitDepth", IHDR_bitDepths)]);
            this.IHDR_colorType = this.getEnumeratedAttribute(var2, "colorType", IHDR_colorTypeNames);
            this.IHDR_compressionMethod = this.getEnumeratedAttribute(var2, "compressionMethod", IHDR_compressionMethodNames);
            this.IHDR_filterMethod = this.getEnumeratedAttribute(var2, "filterMethod", IHDR_filterMethodNames);
            this.IHDR_interlaceMethod = this.getEnumeratedAttribute(var2, "interlaceMethod", IHDR_interlaceMethodNames);
            this.IHDR_present = true;
         } else {
            Node var8;
            int var9;
            byte[] var15;
            int var19;
            if (var3.equals("PLTE")) {
               byte[] var26 = new byte[256];
               byte[] var25 = new byte[256];
               var15 = new byte[256];
               var19 = -1;
               var8 = var2.getFirstChild();
               if (var8 == null) {
                  this.fatal(var2, "Palette has no entries!");
               }

               while(var8 != null) {
                  if (!var8.getNodeName().equals("PLTEEntry")) {
                     this.fatal(var2, "Only a PLTEEntry may be a child of a PLTE!");
                  }

                  var9 = this.getIntAttribute(var8, "index");
                  if (var9 < 0 || var9 > 255) {
                     this.fatal(var2, "Bad value for PLTEEntry attribute index!");
                  }

                  if (var9 > var19) {
                     var19 = var9;
                  }

                  var26[var9] = (byte)this.getIntAttribute(var8, "red");
                  var25[var9] = (byte)this.getIntAttribute(var8, "green");
                  var15[var9] = (byte)this.getIntAttribute(var8, "blue");
                  var8 = var8.getNextSibling();
               }

               var9 = var19 + 1;
               this.PLTE_red = new byte[var9];
               this.PLTE_green = new byte[var9];
               this.PLTE_blue = new byte[var9];
               System.arraycopy(var26, 0, this.PLTE_red, 0, var9);
               System.arraycopy(var25, 0, this.PLTE_green, 0, var9);
               System.arraycopy(var15, 0, this.PLTE_blue, 0, var9);
               this.PLTE_present = true;
            } else {
               Node var4;
               String var5;
               if (var3.equals("bKGD")) {
                  this.bKGD_present = false;
                  var4 = var2.getFirstChild();
                  if (var4 == null) {
                     this.fatal(var2, "bKGD node has no children!");
                  }

                  var5 = var4.getNodeName();
                  if (var5.equals("bKGD_Palette")) {
                     this.bKGD_index = this.getIntAttribute(var4, "index");
                     this.bKGD_colorType = 3;
                  } else if (var5.equals("bKGD_Grayscale")) {
                     this.bKGD_gray = this.getIntAttribute(var4, "gray");
                     this.bKGD_colorType = 0;
                  } else if (var5.equals("bKGD_RGB")) {
                     this.bKGD_red = this.getIntAttribute(var4, "red");
                     this.bKGD_green = this.getIntAttribute(var4, "green");
                     this.bKGD_blue = this.getIntAttribute(var4, "blue");
                     this.bKGD_colorType = 2;
                  } else {
                     this.fatal(var2, "Bad child of a bKGD node!");
                  }

                  if (var4.getNextSibling() != null) {
                     this.fatal(var2, "bKGD node has more than one child!");
                  }

                  this.bKGD_present = true;
               } else if (var3.equals("cHRM")) {
                  this.cHRM_whitePointX = this.getIntAttribute(var2, "whitePointX");
                  this.cHRM_whitePointY = this.getIntAttribute(var2, "whitePointY");
                  this.cHRM_redX = this.getIntAttribute(var2, "redX");
                  this.cHRM_redY = this.getIntAttribute(var2, "redY");
                  this.cHRM_greenX = this.getIntAttribute(var2, "greenX");
                  this.cHRM_greenY = this.getIntAttribute(var2, "greenY");
                  this.cHRM_blueX = this.getIntAttribute(var2, "blueX");
                  this.cHRM_blueY = this.getIntAttribute(var2, "blueY");
                  this.cHRM_present = true;
               } else if (var3.equals("gAMA")) {
                  this.gAMA_gamma = this.getIntAttribute(var2, "value");
                  this.gAMA_present = true;
               } else if (var3.equals("hIST")) {
                  char[] var22 = new char[256];
                  int var17 = -1;
                  Node var23 = var2.getFirstChild();
                  if (var23 == null) {
                     this.fatal(var2, "hIST node has no children!");
                  }

                  while(var23 != null) {
                     if (!var23.getNodeName().equals("hISTEntry")) {
                        this.fatal(var2, "Only a hISTEntry may be a child of a hIST!");
                     }

                     var19 = this.getIntAttribute(var23, "index");
                     if (var19 < 0 || var19 > 255) {
                        this.fatal(var2, "Bad value for histEntry attribute index!");
                     }

                     if (var19 > var17) {
                        var17 = var19;
                     }

                     var22[var19] = (char)this.getIntAttribute(var23, "value");
                     var23 = var23.getNextSibling();
                  }

                  var19 = var17 + 1;
                  this.hIST_histogram = new char[var19];
                  System.arraycopy(var22, 0, this.hIST_histogram, 0, var19);
                  this.hIST_present = true;
               } else if (var3.equals("iCCP")) {
                  this.iCCP_profileName = this.getAttribute(var2, "profileName");
                  this.iCCP_compressionMethod = this.getEnumeratedAttribute(var2, "compressionMethod", iCCP_compressionMethodNames);
                  Object var20 = ((IIOMetadataNode)var2).getUserObject();
                  if (var20 == null) {
                     this.fatal(var2, "No ICCP profile present in user object!");
                  }

                  if (!(var20 instanceof byte[])) {
                     this.fatal(var2, "User object not a byte array!");
                  }

                  this.iCCP_compressedProfile = (byte[])((byte[])((byte[])((byte[])var20)).clone());
                  this.iCCP_present = true;
               } else {
                  String var7;
                  if (var3.equals("iTXt")) {
                     for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
                        if (!var4.getNodeName().equals("iTXtEntry")) {
                           this.fatal(var2, "Only an iTXtEntry may be a child of an iTXt!");
                        }

                        var5 = this.getAttribute(var4, "keyword");
                        if (this.isValidKeyword(var5)) {
                           this.iTXt_keyword.add(var5);
                           boolean var21 = this.getBooleanAttribute(var4, "compressionFlag");
                           this.iTXt_compressionFlag.add(var21);
                           var7 = this.getAttribute(var4, "compressionMethod");
                           this.iTXt_compressionMethod.add(Integer.valueOf(var7));
                           String var28 = this.getAttribute(var4, "languageTag");
                           this.iTXt_languageTag.add(var28);
                           String var29 = this.getAttribute(var4, "translatedKeyword");
                           this.iTXt_translatedKeyword.add(var29);
                           String var30 = this.getAttribute(var4, "text");
                           this.iTXt_text.add(var30);
                        }
                     }
                  } else if (var3.equals("pHYs")) {
                     this.pHYs_pixelsPerUnitXAxis = this.getIntAttribute(var2, "pixelsPerUnitXAxis");
                     this.pHYs_pixelsPerUnitYAxis = this.getIntAttribute(var2, "pixelsPerUnitYAxis");
                     this.pHYs_unitSpecifier = this.getEnumeratedAttribute(var2, "unitSpecifier", unitSpecifierNames);
                     this.pHYs_present = true;
                  } else if (var3.equals("sBIT")) {
                     this.sBIT_present = false;
                     var4 = var2.getFirstChild();
                     if (var4 == null) {
                        this.fatal(var2, "sBIT node has no children!");
                     }

                     var5 = var4.getNodeName();
                     if (var5.equals("sBIT_Grayscale")) {
                        this.sBIT_grayBits = this.getIntAttribute(var4, "gray");
                        this.sBIT_colorType = 0;
                     } else if (var5.equals("sBIT_GrayAlpha")) {
                        this.sBIT_grayBits = this.getIntAttribute(var4, "gray");
                        this.sBIT_alphaBits = this.getIntAttribute(var4, "alpha");
                        this.sBIT_colorType = 4;
                     } else if (var5.equals("sBIT_RGB")) {
                        this.sBIT_redBits = this.getIntAttribute(var4, "red");
                        this.sBIT_greenBits = this.getIntAttribute(var4, "green");
                        this.sBIT_blueBits = this.getIntAttribute(var4, "blue");
                        this.sBIT_colorType = 2;
                     } else if (var5.equals("sBIT_RGBAlpha")) {
                        this.sBIT_redBits = this.getIntAttribute(var4, "red");
                        this.sBIT_greenBits = this.getIntAttribute(var4, "green");
                        this.sBIT_blueBits = this.getIntAttribute(var4, "blue");
                        this.sBIT_alphaBits = this.getIntAttribute(var4, "alpha");
                        this.sBIT_colorType = 6;
                     } else if (var5.equals("sBIT_Palette")) {
                        this.sBIT_redBits = this.getIntAttribute(var4, "red");
                        this.sBIT_greenBits = this.getIntAttribute(var4, "green");
                        this.sBIT_blueBits = this.getIntAttribute(var4, "blue");
                        this.sBIT_colorType = 3;
                     } else {
                        this.fatal(var2, "Bad child of an sBIT node!");
                     }

                     if (var4.getNextSibling() != null) {
                        this.fatal(var2, "sBIT node has more than one child!");
                     }

                     this.sBIT_present = true;
                  } else if (var3.equals("sPLT")) {
                     this.sPLT_paletteName = this.getAttribute(var2, "name");
                     this.sPLT_sampleDepth = this.getIntAttribute(var2, "sampleDepth");
                     int[] var12 = new int[256];
                     int[] var13 = new int[256];
                     int[] var18 = new int[256];
                     int[] var24 = new int[256];
                     int[] var27 = new int[256];
                     var9 = -1;
                     Node var10 = var2.getFirstChild();
                     if (var10 == null) {
                        this.fatal(var2, "sPLT node has no children!");
                     }

                     int var11;
                     while(var10 != null) {
                        if (!var10.getNodeName().equals("sPLTEntry")) {
                           this.fatal(var2, "Only an sPLTEntry may be a child of an sPLT!");
                        }

                        var11 = this.getIntAttribute(var10, "index");
                        if (var11 < 0 || var11 > 255) {
                           this.fatal(var2, "Bad value for PLTEEntry attribute index!");
                        }

                        if (var11 > var9) {
                           var9 = var11;
                        }

                        var12[var11] = this.getIntAttribute(var10, "red");
                        var13[var11] = this.getIntAttribute(var10, "green");
                        var18[var11] = this.getIntAttribute(var10, "blue");
                        var24[var11] = this.getIntAttribute(var10, "alpha");
                        var27[var11] = this.getIntAttribute(var10, "frequency");
                        var10 = var10.getNextSibling();
                     }

                     var11 = var9 + 1;
                     this.sPLT_red = new int[var11];
                     this.sPLT_green = new int[var11];
                     this.sPLT_blue = new int[var11];
                     this.sPLT_alpha = new int[var11];
                     this.sPLT_frequency = new int[var11];
                     System.arraycopy(var12, 0, this.sPLT_red, 0, var11);
                     System.arraycopy(var13, 0, this.sPLT_green, 0, var11);
                     System.arraycopy(var18, 0, this.sPLT_blue, 0, var11);
                     System.arraycopy(var24, 0, this.sPLT_alpha, 0, var11);
                     System.arraycopy(var27, 0, this.sPLT_frequency, 0, var11);
                     this.sPLT_present = true;
                  } else if (var3.equals("sRGB")) {
                     this.sRGB_renderingIntent = this.getEnumeratedAttribute(var2, "renderingIntent", renderingIntentNames);
                     this.sRGB_present = true;
                  } else if (var3.equals("tEXt")) {
                     for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
                        if (!var4.getNodeName().equals("tEXtEntry")) {
                           this.fatal(var2, "Only an tEXtEntry may be a child of an tEXt!");
                        }

                        var5 = this.getAttribute(var4, "keyword");
                        this.tEXt_keyword.add(var5);
                        String var16 = this.getAttribute(var4, "value");
                        this.tEXt_text.add(var16);
                     }
                  } else if (var3.equals("tIME")) {
                     this.tIME_year = this.getIntAttribute(var2, "year");
                     this.tIME_month = this.getIntAttribute(var2, "month");
                     this.tIME_day = this.getIntAttribute(var2, "day");
                     this.tIME_hour = this.getIntAttribute(var2, "hour");
                     this.tIME_minute = this.getIntAttribute(var2, "minute");
                     this.tIME_second = this.getIntAttribute(var2, "second");
                     this.tIME_present = true;
                  } else if (var3.equals("tRNS")) {
                     this.tRNS_present = false;
                     var4 = var2.getFirstChild();
                     if (var4 == null) {
                        this.fatal(var2, "tRNS node has no children!");
                     }

                     var5 = var4.getNodeName();
                     if (var5.equals("tRNS_Palette")) {
                        var15 = new byte[256];
                        var19 = -1;
                        var8 = var4.getFirstChild();
                        if (var8 == null) {
                           this.fatal(var2, "tRNS_Palette node has no children!");
                        }

                        while(var8 != null) {
                           if (!var8.getNodeName().equals("tRNS_PaletteEntry")) {
                              this.fatal(var2, "Only a tRNS_PaletteEntry may be a child of a tRNS_Palette!");
                           }

                           var9 = this.getIntAttribute(var8, "index");
                           if (var9 < 0 || var9 > 255) {
                              this.fatal(var2, "Bad value for tRNS_PaletteEntry attribute index!");
                           }

                           if (var9 > var19) {
                              var19 = var9;
                           }

                           var15[var9] = (byte)this.getIntAttribute(var8, "alpha");
                           var8 = var8.getNextSibling();
                        }

                        var9 = var19 + 1;
                        this.tRNS_alpha = new byte[var9];
                        this.tRNS_colorType = 3;
                        System.arraycopy(var15, 0, this.tRNS_alpha, 0, var9);
                     } else if (var5.equals("tRNS_Grayscale")) {
                        this.tRNS_gray = this.getIntAttribute(var4, "gray");
                        this.tRNS_colorType = 0;
                     } else if (var5.equals("tRNS_RGB")) {
                        this.tRNS_red = this.getIntAttribute(var4, "red");
                        this.tRNS_green = this.getIntAttribute(var4, "green");
                        this.tRNS_blue = this.getIntAttribute(var4, "blue");
                        this.tRNS_colorType = 2;
                     } else {
                        this.fatal(var2, "Bad child of a tRNS node!");
                     }

                     if (var4.getNextSibling() != null) {
                        this.fatal(var2, "tRNS node has more than one child!");
                     }

                     this.tRNS_present = true;
                  } else if (var3.equals("zTXt")) {
                     for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
                        if (!var4.getNodeName().equals("zTXtEntry")) {
                           this.fatal(var2, "Only an zTXtEntry may be a child of an zTXt!");
                        }

                        var5 = this.getAttribute(var4, "keyword");
                        this.zTXt_keyword.add(var5);
                        int var14 = this.getEnumeratedAttribute(var4, "compressionMethod", zTXt_compressionMethodNames);
                        this.zTXt_compressionMethod.add(new Integer(var14));
                        var7 = this.getAttribute(var4, "text");
                        this.zTXt_text.add(var7);
                     }
                  } else if (var3.equals("UnknownChunks")) {
                     for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
                        if (!var4.getNodeName().equals("UnknownChunk")) {
                           this.fatal(var2, "Only an UnknownChunk may be a child of an UnknownChunks!");
                        }

                        var5 = this.getAttribute(var4, "type");
                        Object var6 = ((IIOMetadataNode)var4).getUserObject();
                        if (var5.length() != 4) {
                           this.fatal(var4, "Chunk type must be 4 characters!");
                        }

                        if (var6 == null) {
                           this.fatal(var4, "No chunk data present in user object!");
                        }

                        if (!(var6 instanceof byte[])) {
                           this.fatal(var4, "User object not a byte array!");
                        }

                        this.unknownChunkType.add(var5);
                        this.unknownChunkData.add(((byte[])((byte[])var6)).clone());
                     }
                  } else {
                     this.fatal(var2, "Unknown child of root node!");
                  }
               }
            }
         }
      }

   }

   private boolean isValidKeyword(String var1) {
      int var2 = var1.length();
      if (var2 >= 1 && var2 < 80) {
         return !var1.startsWith(" ") && !var1.endsWith(" ") && !var1.contains("  ") ? this.isISOLatin(var1, false) : false;
      } else {
         return false;
      }
   }

   private boolean isISOLatin(String var1, boolean var2) {
      int var3 = var1.length();

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var1.charAt(var4);
         if ((var5 < ' ' || var5 > 255 || var5 > '~' && var5 < 161) && (!var2 || var5 != 16)) {
            return false;
         }
      }

      return true;
   }

   private void mergeStandardTree(Node var1) throws IIOInvalidTreeException {
      if (!var1.getNodeName().equals("javax_imageio_1.0")) {
         this.fatal(var1, "Root must be javax_imageio_1.0");
      }

      for(Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         String var3 = var2.getNodeName();
         Node var10;
         Node var13;
         String var14;
         int var16;
         int var24;
         int var26;
         int var27;
         if (var3.equals("Chroma")) {
            for(var13 = var2.getFirstChild(); var13 != null; var13 = var13.getNextSibling()) {
               var14 = var13.getNodeName();
               if (var14.equals("Gamma")) {
                  float var20 = this.getFloatAttribute(var13, "value");
                  this.gAMA_present = true;
                  this.gAMA_gamma = (int)((double)(var20 * 100000.0F) + 0.5D);
               } else if (var14.equals("Palette")) {
                  byte[] var19 = new byte[256];
                  byte[] var23 = new byte[256];
                  byte[] var28 = new byte[256];
                  var26 = -1;

                  for(var10 = var13.getFirstChild(); var10 != null; var10 = var10.getNextSibling()) {
                     var27 = this.getIntAttribute(var10, "index");
                     if (var27 >= 0 && var27 <= 255) {
                        var19[var27] = (byte)this.getIntAttribute(var10, "red");
                        var23[var27] = (byte)this.getIntAttribute(var10, "green");
                        var28[var27] = (byte)this.getIntAttribute(var10, "blue");
                        if (var27 > var26) {
                           var26 = var27;
                        }
                     }
                  }

                  var27 = var26 + 1;
                  this.PLTE_red = new byte[var27];
                  this.PLTE_green = new byte[var27];
                  this.PLTE_blue = new byte[var27];
                  System.arraycopy(var19, 0, this.PLTE_red, 0, var27);
                  System.arraycopy(var23, 0, this.PLTE_green, 0, var27);
                  System.arraycopy(var28, 0, this.PLTE_blue, 0, var27);
                  this.PLTE_present = true;
               } else if (var14.equals("BackgroundIndex")) {
                  this.bKGD_present = true;
                  this.bKGD_colorType = 3;
                  this.bKGD_index = this.getIntAttribute(var13, "value");
               } else if (var14.equals("BackgroundColor")) {
                  var16 = this.getIntAttribute(var13, "red");
                  int var21 = this.getIntAttribute(var13, "green");
                  var24 = this.getIntAttribute(var13, "blue");
                  if (var16 == var21 && var16 == var24) {
                     this.bKGD_colorType = 0;
                     this.bKGD_gray = var16;
                  } else {
                     this.bKGD_red = var16;
                     this.bKGD_green = var21;
                     this.bKGD_blue = var24;
                  }

                  this.bKGD_present = true;
               }
            }
         } else if (var3.equals("Compression")) {
            for(var13 = var2.getFirstChild(); var13 != null; var13 = var13.getNextSibling()) {
               var14 = var13.getNodeName();
               if (var14.equals("NumProgressiveScans")) {
                  var16 = this.getIntAttribute(var13, "value");
                  this.IHDR_interlaceMethod = var16 > 1 ? 1 : 0;
               }
            }
         } else {
            String var15;
            if (var3.equals("Data")) {
               for(var13 = var2.getFirstChild(); var13 != null; var13 = var13.getNextSibling()) {
                  var14 = var13.getNodeName();
                  StringTokenizer var18;
                  if (!var14.equals("BitsPerSample")) {
                     if (var14.equals("SignificantBitsPerSample")) {
                        var15 = this.getAttribute(var13, "value");
                        var18 = new StringTokenizer(var15);
                        var24 = var18.countTokens();
                        if (var24 == 1) {
                           this.sBIT_colorType = 0;
                           this.sBIT_grayBits = Integer.parseInt(var18.nextToken());
                        } else if (var24 == 2) {
                           this.sBIT_colorType = 4;
                           this.sBIT_grayBits = Integer.parseInt(var18.nextToken());
                           this.sBIT_alphaBits = Integer.parseInt(var18.nextToken());
                        } else if (var24 == 3) {
                           this.sBIT_colorType = 2;
                           this.sBIT_redBits = Integer.parseInt(var18.nextToken());
                           this.sBIT_greenBits = Integer.parseInt(var18.nextToken());
                           this.sBIT_blueBits = Integer.parseInt(var18.nextToken());
                        } else if (var24 == 4) {
                           this.sBIT_colorType = 6;
                           this.sBIT_redBits = Integer.parseInt(var18.nextToken());
                           this.sBIT_greenBits = Integer.parseInt(var18.nextToken());
                           this.sBIT_blueBits = Integer.parseInt(var18.nextToken());
                           this.sBIT_alphaBits = Integer.parseInt(var18.nextToken());
                        }

                        if (var24 >= 1 && var24 <= 4) {
                           this.sBIT_present = true;
                        }
                     }
                  } else {
                     var15 = this.getAttribute(var13, "value");
                     var18 = new StringTokenizer(var15);
                     var24 = -1;

                     while(var18.hasMoreTokens()) {
                        var26 = Integer.parseInt(var18.nextToken());
                        if (var26 > var24) {
                           var24 = var26;
                        }
                     }

                     if (var24 < 1) {
                        var24 = 1;
                     }

                     if (var24 == 3) {
                        var24 = 4;
                     }

                     if (var24 > 4 || var24 < 8) {
                        var24 = 8;
                     }

                     if (var24 > 8) {
                        var24 = 16;
                     }

                     this.IHDR_bitDepth = var24;
                  }
               }
            } else if (!var3.equals("Dimension")) {
               if (var3.equals("Document")) {
                  for(var13 = var2.getFirstChild(); var13 != null; var13 = var13.getNextSibling()) {
                     var14 = var13.getNodeName();
                     if (var14.equals("ImageModificationTime")) {
                        this.tIME_present = true;
                        this.tIME_year = this.getIntAttribute(var13, "year");
                        this.tIME_month = this.getIntAttribute(var13, "month");
                        this.tIME_day = this.getIntAttribute(var13, "day");
                        this.tIME_hour = this.getIntAttribute(var13, "hour", 0, false);
                        this.tIME_minute = this.getIntAttribute(var13, "minute", 0, false);
                        this.tIME_second = this.getIntAttribute(var13, "second", 0, false);
                     }
                  }
               } else if (var3.equals("Text")) {
                  for(var13 = var2.getFirstChild(); var13 != null; var13 = var13.getNextSibling()) {
                     var14 = var13.getNodeName();
                     if (var14.equals("TextEntry")) {
                        var15 = this.getAttribute(var13, "keyword", "", false);
                        String var17 = this.getAttribute(var13, "value");
                        String var22 = this.getAttribute(var13, "language", "", false);
                        String var25 = this.getAttribute(var13, "compression", "none", false);
                        if (this.isValidKeyword(var15)) {
                           if (this.isISOLatin(var17, true)) {
                              if (var25.equals("zip")) {
                                 this.zTXt_keyword.add(var15);
                                 this.zTXt_text.add(var17);
                                 this.zTXt_compressionMethod.add(0);
                              } else {
                                 this.tEXt_keyword.add(var15);
                                 this.tEXt_text.add(var17);
                              }
                           } else {
                              this.iTXt_keyword.add(var15);
                              this.iTXt_compressionFlag.add(var25.equals("zip"));
                              this.iTXt_compressionMethod.add(0);
                              this.iTXt_languageTag.add(var22);
                              this.iTXt_translatedKeyword.add(var15);
                              this.iTXt_text.add(var17);
                           }
                        }
                     }
                  }
               }
            } else {
               boolean var4 = false;
               boolean var5 = false;
               boolean var6 = false;
               float var7 = -1.0F;
               float var8 = -1.0F;
               float var9 = -1.0F;

               for(var10 = var2.getFirstChild(); var10 != null; var10 = var10.getNextSibling()) {
                  String var11 = var10.getNodeName();
                  if (var11.equals("PixelAspectRatio")) {
                     var9 = this.getFloatAttribute(var10, "value");
                     var6 = true;
                  } else if (var11.equals("HorizontalPixelSize")) {
                     var7 = this.getFloatAttribute(var10, "value");
                     var4 = true;
                  } else if (var11.equals("VerticalPixelSize")) {
                     var8 = this.getFloatAttribute(var10, "value");
                     var5 = true;
                  }
               }

               if (var4 && var5) {
                  this.pHYs_present = true;
                  this.pHYs_unitSpecifier = 1;
                  this.pHYs_pixelsPerUnitXAxis = (int)(var7 * 1000.0F + 0.5F);
                  this.pHYs_pixelsPerUnitYAxis = (int)(var8 * 1000.0F + 0.5F);
               } else if (var6) {
                  this.pHYs_present = true;
                  this.pHYs_unitSpecifier = 0;

                  for(var27 = 1; var27 < 100; ++var27) {
                     int var12 = (int)(var9 * (float)var27);
                     if ((double)Math.abs((float)(var12 / var27) - var9) < 0.001D) {
                        break;
                     }
                  }

                  this.pHYs_pixelsPerUnitXAxis = (int)(var9 * (float)var27);
                  this.pHYs_pixelsPerUnitYAxis = var27;
               }
            }
         }
      }

   }

   public void reset() {
      this.IHDR_present = false;
      this.PLTE_present = false;
      this.bKGD_present = false;
      this.cHRM_present = false;
      this.gAMA_present = false;
      this.hIST_present = false;
      this.iCCP_present = false;
      this.iTXt_keyword = new ArrayList();
      this.iTXt_compressionFlag = new ArrayList();
      this.iTXt_compressionMethod = new ArrayList();
      this.iTXt_languageTag = new ArrayList();
      this.iTXt_translatedKeyword = new ArrayList();
      this.iTXt_text = new ArrayList();
      this.pHYs_present = false;
      this.sBIT_present = false;
      this.sPLT_present = false;
      this.sRGB_present = false;
      this.tEXt_keyword = new ArrayList();
      this.tEXt_text = new ArrayList();
      this.tIME_present = false;
      this.tRNS_present = false;
      this.zTXt_keyword = new ArrayList();
      this.zTXt_compressionMethod = new ArrayList();
      this.zTXt_text = new ArrayList();
      this.unknownChunkType = new ArrayList();
      this.unknownChunkData = new ArrayList();
   }
}
