package com.sun.imageio.plugins.bmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class BMPMetadata extends IIOMetadata implements BMPConstants {
   public static final String nativeMetadataFormatName = "javax_imageio_bmp_1.0";
   public String bmpVersion;
   public int width;
   public int height;
   public short bitsPerPixel;
   public int compression;
   public int imageSize;
   public int xPixelsPerMeter;
   public int yPixelsPerMeter;
   public int colorsUsed;
   public int colorsImportant;
   public int redMask;
   public int greenMask;
   public int blueMask;
   public int alphaMask;
   public int colorSpace;
   public double redX;
   public double redY;
   public double redZ;
   public double greenX;
   public double greenY;
   public double greenZ;
   public double blueX;
   public double blueY;
   public double blueZ;
   public int gammaRed;
   public int gammaGreen;
   public int gammaBlue;
   public int intent;
   public byte[] palette = null;
   public int paletteSize;
   public int red;
   public int green;
   public int blue;
   public List comments = null;

   public BMPMetadata() {
      super(true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", (String[])null, (String[])null);
   }

   public boolean isReadOnly() {
      return true;
   }

   public Node getAsTree(String var1) {
      if (var1.equals("javax_imageio_bmp_1.0")) {
         return this.getNativeTree();
      } else if (var1.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException(I18N.getString("BMPMetadata0"));
      }
   }

   private String toISO8859(byte[] var1) {
      try {
         return new String(var1, "ISO-8859-1");
      } catch (UnsupportedEncodingException var3) {
         return "";
      }
   }

   private Node getNativeTree() {
      IIOMetadataNode var1 = new IIOMetadataNode("javax_imageio_bmp_1.0");
      this.addChildNode(var1, "BMPVersion", this.bmpVersion);
      this.addChildNode(var1, "Width", new Integer(this.width));
      this.addChildNode(var1, "Height", new Integer(this.height));
      this.addChildNode(var1, "BitsPerPixel", new Short(this.bitsPerPixel));
      this.addChildNode(var1, "Compression", new Integer(this.compression));
      this.addChildNode(var1, "ImageSize", new Integer(this.imageSize));
      IIOMetadataNode var2 = this.addChildNode(var1, "PixelsPerMeter", (Object)null);
      this.addChildNode(var2, "X", new Integer(this.xPixelsPerMeter));
      this.addChildNode(var2, "Y", new Integer(this.yPixelsPerMeter));
      this.addChildNode(var1, "ColorsUsed", new Integer(this.colorsUsed));
      this.addChildNode(var1, "ColorsImportant", new Integer(this.colorsImportant));
      int var3 = 0;

      int var4;
      for(var4 = 0; var4 < this.bmpVersion.length(); ++var4) {
         if (Character.isDigit(this.bmpVersion.charAt(var4))) {
            var3 = this.bmpVersion.charAt(var4) - 48;
         }
      }

      if (var3 >= 4) {
         var2 = this.addChildNode(var1, "Mask", (Object)null);
         this.addChildNode(var2, "Red", new Integer(this.redMask));
         this.addChildNode(var2, "Green", new Integer(this.greenMask));
         this.addChildNode(var2, "Blue", new Integer(this.blueMask));
         this.addChildNode(var2, "Alpha", new Integer(this.alphaMask));
         this.addChildNode(var1, "ColorSpaceType", new Integer(this.colorSpace));
         var2 = this.addChildNode(var1, "CIEXYZEndPoints", (Object)null);
         this.addXYZPoints(var2, "Red", this.redX, this.redY, this.redZ);
         this.addXYZPoints(var2, "Green", this.greenX, this.greenY, this.greenZ);
         this.addXYZPoints(var2, "Blue", this.blueX, this.blueY, this.blueZ);
         this.addChildNode(var1, "Intent", new Integer(this.intent));
      }

      if (this.palette != null && this.paletteSize > 0) {
         var2 = this.addChildNode(var1, "Palette", (Object)null);
         var4 = this.palette.length / this.paletteSize;
         int var5 = 0;

         for(int var6 = 0; var5 < this.paletteSize; ++var5) {
            IIOMetadataNode var7 = this.addChildNode(var2, "PaletteEntry", (Object)null);
            this.red = this.palette[var6++] & 255;
            this.green = this.palette[var6++] & 255;
            this.blue = this.palette[var6++] & 255;
            this.addChildNode(var7, "Red", new Byte((byte)this.red));
            this.addChildNode(var7, "Green", new Byte((byte)this.green));
            this.addChildNode(var7, "Blue", new Byte((byte)this.blue));
            if (var4 == 4) {
               this.addChildNode(var7, "Alpha", new Byte((byte)(this.palette[var6++] & 255)));
            }
         }
      }

      return var1;
   }

   protected IIOMetadataNode getStandardChromaNode() {
      if (this.palette != null && this.paletteSize > 0) {
         IIOMetadataNode var1 = new IIOMetadataNode("Chroma");
         IIOMetadataNode var2 = new IIOMetadataNode("Palette");
         int var3 = this.palette.length / this.paletteSize;
         var2.setAttribute("value", "" + var3);
         int var4 = 0;

         for(int var5 = 0; var4 < this.paletteSize; ++var4) {
            IIOMetadataNode var6 = new IIOMetadataNode("PaletteEntry");
            var6.setAttribute("index", "" + var4);
            var6.setAttribute("red", "" + this.palette[var5++]);
            var6.setAttribute("green", "" + this.palette[var5++]);
            var6.setAttribute("blue", "" + this.palette[var5++]);
            if (var3 == 4 && this.palette[var5] != 0) {
               var6.setAttribute("alpha", "" + this.palette[var5++]);
            }

            var2.appendChild(var6);
         }

         var1.appendChild(var2);
         return var1;
      } else {
         return null;
      }
   }

   protected IIOMetadataNode getStandardCompressionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Compression");
      IIOMetadataNode var2 = new IIOMetadataNode("CompressionTypeName");
      var2.setAttribute("value", BMPCompressionTypes.getName(this.compression));
      var1.appendChild(var2);
      return var1;
   }

   protected IIOMetadataNode getStandardDataNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Data");
      String var2 = "";
      if (this.bitsPerPixel == 24) {
         var2 = "8 8 8 ";
      } else if (this.bitsPerPixel == 16 || this.bitsPerPixel == 32) {
         var2 = "" + this.countBits(this.redMask) + " " + this.countBits(this.greenMask) + this.countBits(this.blueMask) + "" + this.countBits(this.alphaMask);
      }

      IIOMetadataNode var3 = new IIOMetadataNode("BitsPerSample");
      var3.setAttribute("value", var2);
      var1.appendChild(var3);
      return var1;
   }

   protected IIOMetadataNode getStandardDimensionNode() {
      if ((float)this.yPixelsPerMeter > 0.0F && (float)this.xPixelsPerMeter > 0.0F) {
         IIOMetadataNode var1 = new IIOMetadataNode("Dimension");
         float var2 = (float)(this.yPixelsPerMeter / this.xPixelsPerMeter);
         IIOMetadataNode var3 = new IIOMetadataNode("PixelAspectRatio");
         var3.setAttribute("value", "" + var2);
         var1.appendChild(var3);
         var3 = new IIOMetadataNode("HorizontalPhysicalPixelSpacing");
         var3.setAttribute("value", "" + 1 / this.xPixelsPerMeter * 1000);
         var1.appendChild(var3);
         var3 = new IIOMetadataNode("VerticalPhysicalPixelSpacing");
         var3.setAttribute("value", "" + 1 / this.yPixelsPerMeter * 1000);
         var1.appendChild(var3);
         return var1;
      } else {
         return null;
      }
   }

   public void setFromTree(String var1, Node var2) {
      throw new IllegalStateException(I18N.getString("BMPMetadata1"));
   }

   public void mergeTree(String var1, Node var2) {
      throw new IllegalStateException(I18N.getString("BMPMetadata1"));
   }

   public void reset() {
      throw new IllegalStateException(I18N.getString("BMPMetadata1"));
   }

   private String countBits(int var1) {
      int var2;
      for(var2 = 0; var1 > 0; var1 >>>= 1) {
         if ((var1 & 1) == 1) {
            ++var2;
         }
      }

      return var2 == 0 ? "" : "" + var2;
   }

   private void addXYZPoints(IIOMetadataNode var1, String var2, double var3, double var5, double var7) {
      IIOMetadataNode var9 = this.addChildNode(var1, var2, (Object)null);
      this.addChildNode(var9, "X", new Double(var3));
      this.addChildNode(var9, "Y", new Double(var5));
      this.addChildNode(var9, "Z", new Double(var7));
   }

   private IIOMetadataNode addChildNode(IIOMetadataNode var1, String var2, Object var3) {
      IIOMetadataNode var4 = new IIOMetadataNode(var2);
      if (var3 != null) {
         var4.setUserObject(var3);
         var4.setNodeValue(ImageUtil.convertObjectToString(var3));
      }

      var1.appendChild(var4);
      return var4;
   }
}
