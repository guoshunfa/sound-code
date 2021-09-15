package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class GIFStreamMetadata extends GIFMetadata {
   static final String nativeMetadataFormatName = "javax_imageio_gif_stream_1.0";
   static final String[] versionStrings = new String[]{"87a", "89a"};
   public String version;
   public int logicalScreenWidth;
   public int logicalScreenHeight;
   public int colorResolution;
   public int pixelAspectRatio;
   public int backgroundColorIndex;
   public boolean sortFlag;
   static final String[] colorTableSizes = new String[]{"2", "4", "8", "16", "32", "64", "128", "256"};
   public byte[] globalColorTable;

   protected GIFStreamMetadata(boolean var1, String var2, String var3, String[] var4, String[] var5) {
      super(var1, var2, var3, var4, var5);
      this.globalColorTable = null;
   }

   public GIFStreamMetadata() {
      this(true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", (String[])null, (String[])null);
   }

   public boolean isReadOnly() {
      return true;
   }

   public Node getAsTree(String var1) {
      if (var1.equals("javax_imageio_gif_stream_1.0")) {
         return this.getNativeTree();
      } else if (var1.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
      }
   }

   private Node getNativeTree() {
      IIOMetadataNode var2 = new IIOMetadataNode("javax_imageio_gif_stream_1.0");
      IIOMetadataNode var1 = new IIOMetadataNode("Version");
      var1.setAttribute("value", this.version);
      var2.appendChild(var1);
      var1 = new IIOMetadataNode("LogicalScreenDescriptor");
      var1.setAttribute("logicalScreenWidth", this.logicalScreenWidth == -1 ? "" : Integer.toString(this.logicalScreenWidth));
      var1.setAttribute("logicalScreenHeight", this.logicalScreenHeight == -1 ? "" : Integer.toString(this.logicalScreenHeight));
      var1.setAttribute("colorResolution", this.colorResolution == -1 ? "" : Integer.toString(this.colorResolution));
      var1.setAttribute("pixelAspectRatio", Integer.toString(this.pixelAspectRatio));
      var2.appendChild(var1);
      if (this.globalColorTable != null) {
         var1 = new IIOMetadataNode("GlobalColorTable");
         int var3 = this.globalColorTable.length / 3;
         var1.setAttribute("sizeOfGlobalColorTable", Integer.toString(var3));
         var1.setAttribute("backgroundColorIndex", Integer.toString(this.backgroundColorIndex));
         var1.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");

         for(int var4 = 0; var4 < var3; ++var4) {
            IIOMetadataNode var5 = new IIOMetadataNode("ColorTableEntry");
            var5.setAttribute("index", Integer.toString(var4));
            int var6 = this.globalColorTable[3 * var4] & 255;
            int var7 = this.globalColorTable[3 * var4 + 1] & 255;
            int var8 = this.globalColorTable[3 * var4 + 2] & 255;
            var5.setAttribute("red", Integer.toString(var6));
            var5.setAttribute("green", Integer.toString(var7));
            var5.setAttribute("blue", Integer.toString(var8));
            var1.appendChild(var5);
         }

         var2.appendChild(var1);
      }

      return var2;
   }

   public IIOMetadataNode getStandardChromaNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Chroma");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("ColorSpaceType");
      var2.setAttribute("name", "RGB");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("BlackIsZero");
      var2.setAttribute("value", "TRUE");
      var1.appendChild(var2);
      if (this.globalColorTable != null) {
         var2 = new IIOMetadataNode("Palette");
         int var3 = this.globalColorTable.length / 3;

         for(int var4 = 0; var4 < var3; ++var4) {
            IIOMetadataNode var5 = new IIOMetadataNode("PaletteEntry");
            var5.setAttribute("index", Integer.toString(var4));
            var5.setAttribute("red", Integer.toString(this.globalColorTable[3 * var4] & 255));
            var5.setAttribute("green", Integer.toString(this.globalColorTable[3 * var4 + 1] & 255));
            var5.setAttribute("blue", Integer.toString(this.globalColorTable[3 * var4 + 2] & 255));
            var2.appendChild(var5);
         }

         var1.appendChild(var2);
         var2 = new IIOMetadataNode("BackgroundIndex");
         var2.setAttribute("value", Integer.toString(this.backgroundColorIndex));
         var1.appendChild(var2);
      }

      return var1;
   }

   public IIOMetadataNode getStandardCompressionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Compression");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("CompressionTypeName");
      var2.setAttribute("value", "lzw");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("Lossless");
      var2.setAttribute("value", "TRUE");
      var1.appendChild(var2);
      return var1;
   }

   public IIOMetadataNode getStandardDataNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Data");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("SampleFormat");
      var2.setAttribute("value", "Index");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("BitsPerSample");
      var2.setAttribute("value", this.colorResolution == -1 ? "" : Integer.toString(this.colorResolution));
      var1.appendChild(var2);
      return var1;
   }

   public IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Dimension");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("PixelAspectRatio");
      float var3 = 1.0F;
      if (this.pixelAspectRatio != 0) {
         var3 = (float)(this.pixelAspectRatio + 15) / 64.0F;
      }

      var2.setAttribute("value", Float.toString(var3));
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("ImageOrientation");
      var2.setAttribute("value", "Normal");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("HorizontalScreenSize");
      var2.setAttribute("value", this.logicalScreenWidth == -1 ? "" : Integer.toString(this.logicalScreenWidth));
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("VerticalScreenSize");
      var2.setAttribute("value", this.logicalScreenHeight == -1 ? "" : Integer.toString(this.logicalScreenHeight));
      var1.appendChild(var2);
      return var1;
   }

   public IIOMetadataNode getStandardDocumentNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Document");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("FormatVersion");
      var2.setAttribute("value", this.version);
      var1.appendChild(var2);
      return var1;
   }

   public IIOMetadataNode getStandardTextNode() {
      return null;
   }

   public IIOMetadataNode getStandardTransparencyNode() {
      return null;
   }

   public void setFromTree(String var1, Node var2) throws IIOInvalidTreeException {
      throw new IllegalStateException("Metadata is read-only!");
   }

   protected void mergeNativeTree(Node var1) throws IIOInvalidTreeException {
      throw new IllegalStateException("Metadata is read-only!");
   }

   protected void mergeStandardTree(Node var1) throws IIOInvalidTreeException {
      throw new IllegalStateException("Metadata is read-only!");
   }

   public void reset() {
      throw new IllegalStateException("Metadata is read-only!");
   }
}
