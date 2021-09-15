package com.sun.imageio.plugins.gif;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class GIFImageMetadata extends GIFMetadata {
   static final String nativeMetadataFormatName = "javax_imageio_gif_image_1.0";
   static final String[] disposalMethodNames = new String[]{"none", "doNotDispose", "restoreToBackgroundColor", "restoreToPrevious", "undefinedDisposalMethod4", "undefinedDisposalMethod5", "undefinedDisposalMethod6", "undefinedDisposalMethod7"};
   public int imageLeftPosition;
   public int imageTopPosition;
   public int imageWidth;
   public int imageHeight;
   public boolean interlaceFlag;
   public boolean sortFlag;
   public byte[] localColorTable;
   public int disposalMethod;
   public boolean userInputFlag;
   public boolean transparentColorFlag;
   public int delayTime;
   public int transparentColorIndex;
   public boolean hasPlainTextExtension;
   public int textGridLeft;
   public int textGridTop;
   public int textGridWidth;
   public int textGridHeight;
   public int characterCellWidth;
   public int characterCellHeight;
   public int textForegroundColor;
   public int textBackgroundColor;
   public byte[] text;
   public List applicationIDs;
   public List authenticationCodes;
   public List applicationData;
   public List comments;

   protected GIFImageMetadata(boolean var1, String var2, String var3, String[] var4, String[] var5) {
      super(var1, var2, var3, var4, var5);
      this.interlaceFlag = false;
      this.sortFlag = false;
      this.localColorTable = null;
      this.disposalMethod = 0;
      this.userInputFlag = false;
      this.transparentColorFlag = false;
      this.delayTime = 0;
      this.transparentColorIndex = 0;
      this.hasPlainTextExtension = false;
      this.applicationIDs = null;
      this.authenticationCodes = null;
      this.applicationData = null;
      this.comments = null;
   }

   public GIFImageMetadata() {
      this(true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", (String[])null, (String[])null);
   }

   public boolean isReadOnly() {
      return true;
   }

   public Node getAsTree(String var1) {
      if (var1.equals("javax_imageio_gif_image_1.0")) {
         return this.getNativeTree();
      } else if (var1.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException("Not a recognized format!");
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
      IIOMetadataNode var2 = new IIOMetadataNode("javax_imageio_gif_image_1.0");
      IIOMetadataNode var1 = new IIOMetadataNode("ImageDescriptor");
      var1.setAttribute("imageLeftPosition", Integer.toString(this.imageLeftPosition));
      var1.setAttribute("imageTopPosition", Integer.toString(this.imageTopPosition));
      var1.setAttribute("imageWidth", Integer.toString(this.imageWidth));
      var1.setAttribute("imageHeight", Integer.toString(this.imageHeight));
      var1.setAttribute("interlaceFlag", this.interlaceFlag ? "TRUE" : "FALSE");
      var2.appendChild(var1);
      int var3;
      int var4;
      IIOMetadataNode var5;
      if (this.localColorTable != null) {
         var1 = new IIOMetadataNode("LocalColorTable");
         var3 = this.localColorTable.length / 3;
         var1.setAttribute("sizeOfLocalColorTable", Integer.toString(var3));
         var1.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");

         for(var4 = 0; var4 < var3; ++var4) {
            var5 = new IIOMetadataNode("ColorTableEntry");
            var5.setAttribute("index", Integer.toString(var4));
            int var6 = this.localColorTable[3 * var4] & 255;
            int var7 = this.localColorTable[3 * var4 + 1] & 255;
            int var8 = this.localColorTable[3 * var4 + 2] & 255;
            var5.setAttribute("red", Integer.toString(var6));
            var5.setAttribute("green", Integer.toString(var7));
            var5.setAttribute("blue", Integer.toString(var8));
            var1.appendChild(var5);
         }

         var2.appendChild(var1);
      }

      var1 = new IIOMetadataNode("GraphicControlExtension");
      var1.setAttribute("disposalMethod", disposalMethodNames[this.disposalMethod]);
      var1.setAttribute("userInputFlag", this.userInputFlag ? "TRUE" : "FALSE");
      var1.setAttribute("transparentColorFlag", this.transparentColorFlag ? "TRUE" : "FALSE");
      var1.setAttribute("delayTime", Integer.toString(this.delayTime));
      var1.setAttribute("transparentColorIndex", Integer.toString(this.transparentColorIndex));
      var2.appendChild(var1);
      if (this.hasPlainTextExtension) {
         var1 = new IIOMetadataNode("PlainTextExtension");
         var1.setAttribute("textGridLeft", Integer.toString(this.textGridLeft));
         var1.setAttribute("textGridTop", Integer.toString(this.textGridTop));
         var1.setAttribute("textGridWidth", Integer.toString(this.textGridWidth));
         var1.setAttribute("textGridHeight", Integer.toString(this.textGridHeight));
         var1.setAttribute("characterCellWidth", Integer.toString(this.characterCellWidth));
         var1.setAttribute("characterCellHeight", Integer.toString(this.characterCellHeight));
         var1.setAttribute("textForegroundColor", Integer.toString(this.textForegroundColor));
         var1.setAttribute("textBackgroundColor", Integer.toString(this.textBackgroundColor));
         var1.setAttribute("text", this.toISO8859(this.text));
         var2.appendChild(var1);
      }

      var3 = this.applicationIDs == null ? 0 : this.applicationIDs.size();
      byte[] var12;
      if (var3 > 0) {
         var1 = new IIOMetadataNode("ApplicationExtensions");

         for(var4 = 0; var4 < var3; ++var4) {
            var5 = new IIOMetadataNode("ApplicationExtension");
            byte[] var10 = (byte[])((byte[])this.applicationIDs.get(var4));
            var5.setAttribute("applicationID", this.toISO8859(var10));
            var12 = (byte[])((byte[])this.authenticationCodes.get(var4));
            var5.setAttribute("authenticationCode", this.toISO8859(var12));
            byte[] var13 = (byte[])((byte[])this.applicationData.get(var4));
            var5.setUserObject((byte[])((byte[])var13.clone()));
            var1.appendChild(var5);
         }

         var2.appendChild(var1);
      }

      var4 = this.comments == null ? 0 : this.comments.size();
      if (var4 > 0) {
         var1 = new IIOMetadataNode("CommentExtensions");

         for(int var9 = 0; var9 < var4; ++var9) {
            IIOMetadataNode var11 = new IIOMetadataNode("CommentExtension");
            var12 = (byte[])((byte[])this.comments.get(var9));
            var11.setAttribute("value", this.toISO8859(var12));
            var1.appendChild(var11);
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
      var2 = new IIOMetadataNode("NumChannels");
      var2.setAttribute("value", this.transparentColorFlag ? "4" : "3");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("BlackIsZero");
      var2.setAttribute("value", "TRUE");
      var1.appendChild(var2);
      if (this.localColorTable != null) {
         var2 = new IIOMetadataNode("Palette");
         int var3 = this.localColorTable.length / 3;

         for(int var4 = 0; var4 < var3; ++var4) {
            IIOMetadataNode var5 = new IIOMetadataNode("PaletteEntry");
            var5.setAttribute("index", Integer.toString(var4));
            var5.setAttribute("red", Integer.toString(this.localColorTable[3 * var4] & 255));
            var5.setAttribute("green", Integer.toString(this.localColorTable[3 * var4 + 1] & 255));
            var5.setAttribute("blue", Integer.toString(this.localColorTable[3 * var4 + 2] & 255));
            var2.appendChild(var5);
         }

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
      var2 = new IIOMetadataNode("NumProgressiveScans");
      var2.setAttribute("value", this.interlaceFlag ? "4" : "1");
      var1.appendChild(var2);
      return var1;
   }

   public IIOMetadataNode getStandardDataNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Data");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("SampleFormat");
      var2.setAttribute("value", "Index");
      var1.appendChild(var2);
      return var1;
   }

   public IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Dimension");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("ImageOrientation");
      var2.setAttribute("value", "Normal");
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("HorizontalPixelOffset");
      var2.setAttribute("value", Integer.toString(this.imageLeftPosition));
      var1.appendChild(var2);
      var2 = new IIOMetadataNode("VerticalPixelOffset");
      var2.setAttribute("value", Integer.toString(this.imageTopPosition));
      var1.appendChild(var2);
      return var1;
   }

   public IIOMetadataNode getStandardTextNode() {
      if (this.comments == null) {
         return null;
      } else {
         Iterator var1 = this.comments.iterator();
         if (!var1.hasNext()) {
            return null;
         } else {
            IIOMetadataNode var2 = new IIOMetadataNode("Text");
            IIOMetadataNode var3 = null;

            while(var1.hasNext()) {
               byte[] var4 = (byte[])((byte[])var1.next());
               String var5 = null;

               try {
                  var5 = new String(var4, "ISO-8859-1");
               } catch (UnsupportedEncodingException var7) {
                  throw new RuntimeException("Encoding ISO-8859-1 unknown!");
               }

               var3 = new IIOMetadataNode("TextEntry");
               var3.setAttribute("value", var5);
               var3.setAttribute("encoding", "ISO-8859-1");
               var3.setAttribute("compression", "none");
               var2.appendChild(var3);
            }

            return var2;
         }
      }
   }

   public IIOMetadataNode getStandardTransparencyNode() {
      if (!this.transparentColorFlag) {
         return null;
      } else {
         IIOMetadataNode var1 = new IIOMetadataNode("Transparency");
         IIOMetadataNode var2 = null;
         var2 = new IIOMetadataNode("TransparentIndex");
         var2.setAttribute("value", Integer.toString(this.transparentColorIndex));
         var1.appendChild(var2);
         return var1;
      }
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
