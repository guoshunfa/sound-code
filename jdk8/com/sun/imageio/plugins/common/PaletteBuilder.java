package com.sun.imageio.plugins.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageTypeSpecifier;

public class PaletteBuilder {
   protected static final int MAXLEVEL = 8;
   protected RenderedImage src;
   protected ColorModel srcColorModel;
   protected Raster srcRaster;
   protected int requiredSize;
   protected PaletteBuilder.ColorNode root;
   protected int numNodes;
   protected int maxNodes;
   protected int currLevel;
   protected int currSize;
   protected PaletteBuilder.ColorNode[] reduceList;
   protected PaletteBuilder.ColorNode[] palette;
   protected int transparency;
   protected PaletteBuilder.ColorNode transColor;

   public static RenderedImage createIndexedImage(RenderedImage var0) {
      PaletteBuilder var1 = new PaletteBuilder(var0);
      var1.buildPalette();
      return var1.getIndexedImage();
   }

   public static IndexColorModel createIndexColorModel(RenderedImage var0) {
      PaletteBuilder var1 = new PaletteBuilder(var0);
      var1.buildPalette();
      return var1.getIndexColorModel();
   }

   public static boolean canCreatePalette(ImageTypeSpecifier var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("type == null");
      } else {
         return true;
      }
   }

   public static boolean canCreatePalette(RenderedImage var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("image == null");
      } else {
         ImageTypeSpecifier var1 = new ImageTypeSpecifier(var0);
         return canCreatePalette(var1);
      }
   }

   protected RenderedImage getIndexedImage() {
      IndexColorModel var1 = this.getIndexColorModel();
      BufferedImage var2 = new BufferedImage(this.src.getWidth(), this.src.getHeight(), 13, var1);
      WritableRaster var3 = var2.getRaster();

      for(int var4 = 0; var4 < var2.getHeight(); ++var4) {
         for(int var5 = 0; var5 < var2.getWidth(); ++var5) {
            Color var6 = this.getSrcColor(var5, var4);
            var3.setSample(var5, var4, 0, this.findColorIndex(this.root, var6));
         }
      }

      return var2;
   }

   protected PaletteBuilder(RenderedImage var1) {
      this(var1, 256);
   }

   protected PaletteBuilder(RenderedImage var1, int var2) {
      this.src = var1;
      this.srcColorModel = var1.getColorModel();
      this.srcRaster = var1.getData();
      this.transparency = this.srcColorModel.getTransparency();
      this.requiredSize = var2;
   }

   private Color getSrcColor(int var1, int var2) {
      int var3 = this.srcColorModel.getRGB(this.srcRaster.getDataElements(var1, var2, (Object)null));
      return new Color(var3, this.transparency != 1);
   }

   protected int findColorIndex(PaletteBuilder.ColorNode var1, Color var2) {
      if (this.transparency != 1 && var2.getAlpha() != 255) {
         return 0;
      } else if (var1.isLeaf) {
         return var1.paletteIndex;
      } else {
         int var3 = this.getBranchIndex(var2, var1.level);
         return this.findColorIndex(var1.children[var3], var2);
      }
   }

   protected void buildPalette() {
      this.reduceList = new PaletteBuilder.ColorNode[9];

      int var1;
      for(var1 = 0; var1 < this.reduceList.length; ++var1) {
         this.reduceList[var1] = null;
      }

      this.numNodes = 0;
      this.maxNodes = 0;
      this.root = null;
      this.currSize = 0;
      this.currLevel = 8;
      var1 = this.src.getWidth();
      int var2 = this.src.getHeight();

      for(int var3 = 0; var3 < var2; ++var3) {
         for(int var4 = 0; var4 < var1; ++var4) {
            Color var5 = this.getSrcColor(var1 - var4 - 1, var2 - var3 - 1);
            if (this.transparency != 1 && var5.getAlpha() != 255) {
               if (this.transColor == null) {
                  --this.requiredSize;
                  this.transColor = new PaletteBuilder.ColorNode();
                  this.transColor.isLeaf = true;
               }

               this.transColor = this.insertNode(this.transColor, var5, 0);
            } else {
               this.root = this.insertNode(this.root, var5, 0);
            }

            if (this.currSize > this.requiredSize) {
               this.reduceTree();
            }
         }
      }

   }

   protected PaletteBuilder.ColorNode insertNode(PaletteBuilder.ColorNode var1, Color var2, int var3) {
      if (var1 == null) {
         var1 = new PaletteBuilder.ColorNode();
         ++this.numNodes;
         if (this.numNodes > this.maxNodes) {
            this.maxNodes = this.numNodes;
         }

         var1.level = var3;
         var1.isLeaf = var3 > 8;
         if (var1.isLeaf) {
            ++this.currSize;
         }
      }

      ++var1.colorCount;
      var1.red += (long)var2.getRed();
      var1.green += (long)var2.getGreen();
      var1.blue += (long)var2.getBlue();
      if (!var1.isLeaf) {
         int var4 = this.getBranchIndex(var2, var3);
         if (var1.children[var4] == null) {
            ++var1.childCount;
            if (var1.childCount == 2) {
               var1.nextReducible = this.reduceList[var3];
               this.reduceList[var3] = var1;
            }
         }

         var1.children[var4] = this.insertNode(var1.children[var4], var2, var3 + 1);
      }

      return var1;
   }

   protected IndexColorModel getIndexColorModel() {
      int var1 = this.currSize;
      if (this.transColor != null) {
         ++var1;
      }

      byte[] var2 = new byte[var1];
      byte[] var3 = new byte[var1];
      byte[] var4 = new byte[var1];
      int var5 = 0;
      this.palette = new PaletteBuilder.ColorNode[var1];
      if (this.transColor != null) {
         ++var5;
      }

      if (this.root != null) {
         this.findPaletteEntry(this.root, var5, var2, var3, var4);
      }

      IndexColorModel var6 = null;
      if (this.transColor != null) {
         var6 = new IndexColorModel(8, var1, var2, var3, var4, 0);
      } else {
         var6 = new IndexColorModel(8, this.currSize, var2, var3, var4);
      }

      return var6;
   }

   protected int findPaletteEntry(PaletteBuilder.ColorNode var1, int var2, byte[] var3, byte[] var4, byte[] var5) {
      if (var1.isLeaf) {
         var3[var2] = (byte)((int)(var1.red / (long)var1.colorCount));
         var4[var2] = (byte)((int)(var1.green / (long)var1.colorCount));
         var5[var2] = (byte)((int)(var1.blue / (long)var1.colorCount));
         var1.paletteIndex = var2;
         this.palette[var2] = var1;
         ++var2;
      } else {
         for(int var6 = 0; var6 < 8; ++var6) {
            if (var1.children[var6] != null) {
               var2 = this.findPaletteEntry(var1.children[var6], var2, var3, var4, var5);
            }
         }
      }

      return var2;
   }

   protected int getBranchIndex(Color var1, int var2) {
      if (var2 <= 8 && var2 >= 0) {
         int var3 = 8 - var2;
         int var4 = 1 & (255 & var1.getRed()) >> var3;
         int var5 = 1 & (255 & var1.getGreen()) >> var3;
         int var6 = 1 & (255 & var1.getBlue()) >> var3;
         int var7 = var4 << 2 | var5 << 1 | var6;
         return var7;
      } else {
         throw new IllegalArgumentException("Invalid octree node depth: " + var2);
      }
   }

   protected void reduceTree() {
      int var1;
      for(var1 = this.reduceList.length - 1; this.reduceList[var1] == null && var1 >= 0; --var1) {
      }

      PaletteBuilder.ColorNode var2 = this.reduceList[var1];
      if (var2 != null) {
         PaletteBuilder.ColorNode var3 = var2;
         int var4 = var2.colorCount;

         for(int var5 = 1; var3.nextReducible != null; ++var5) {
            if (var4 > var3.nextReducible.colorCount) {
               var2 = var3;
               var4 = var3.colorCount;
            }

            var3 = var3.nextReducible;
         }

         if (var2 == this.reduceList[var1]) {
            this.reduceList[var1] = var2.nextReducible;
         } else {
            var3 = var2.nextReducible;
            var2.nextReducible = var3.nextReducible;
            var2 = var3;
         }

         if (!var2.isLeaf) {
            int var6 = var2.getLeafChildCount();
            var2.isLeaf = true;
            this.currSize -= var6 - 1;
            int var7 = var2.level;

            for(int var8 = 0; var8 < 8; ++var8) {
               var2.children[var8] = this.freeTree(var2.children[var8]);
            }

            var2.childCount = 0;
         }
      }
   }

   protected PaletteBuilder.ColorNode freeTree(PaletteBuilder.ColorNode var1) {
      if (var1 == null) {
         return null;
      } else {
         for(int var2 = 0; var2 < 8; ++var2) {
            var1.children[var2] = this.freeTree(var1.children[var2]);
         }

         --this.numNodes;
         return null;
      }
   }

   protected class ColorNode {
      public boolean isLeaf = false;
      public int childCount = 0;
      PaletteBuilder.ColorNode[] children = new PaletteBuilder.ColorNode[8];
      public int colorCount;
      public long red;
      public long blue;
      public long green;
      public int paletteIndex;
      public int level = 0;
      PaletteBuilder.ColorNode nextReducible;

      public ColorNode() {
         for(int var2 = 0; var2 < 8; ++var2) {
            this.children[var2] = null;
         }

         this.colorCount = 0;
         this.red = this.green = this.blue = 0L;
         this.paletteIndex = 0;
      }

      public int getLeafChildCount() {
         if (this.isLeaf) {
            return 0;
         } else {
            int var1 = 0;

            for(int var2 = 0; var2 < this.children.length; ++var2) {
               if (this.children[var2] != null) {
                  if (this.children[var2].isLeaf) {
                     ++var1;
                  } else {
                     var1 += this.children[var2].getLeafChildCount();
                  }
               }
            }

            return var1;
         }
      }

      public int getRGB() {
         int var1 = (int)this.red / this.colorCount;
         int var2 = (int)this.green / this.colorCount;
         int var3 = (int)this.blue / this.colorCount;
         int var4 = -16777216 | (255 & var1) << 16 | (255 & var2) << 8 | 255 & var3;
         return var4;
      }
   }
}
