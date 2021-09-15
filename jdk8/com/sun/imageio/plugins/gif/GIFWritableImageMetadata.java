package com.sun.imageio.plugins.gif;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

class GIFWritableImageMetadata extends GIFImageMetadata {
   static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_image_1.0";

   GIFWritableImageMetadata() {
      super(true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", (String[])null, (String[])null);
   }

   public boolean isReadOnly() {
      return false;
   }

   public void reset() {
      this.imageLeftPosition = 0;
      this.imageTopPosition = 0;
      this.imageWidth = 0;
      this.imageHeight = 0;
      this.interlaceFlag = false;
      this.sortFlag = false;
      this.localColorTable = null;
      this.disposalMethod = 0;
      this.userInputFlag = false;
      this.transparentColorFlag = false;
      this.delayTime = 0;
      this.transparentColorIndex = 0;
      this.hasPlainTextExtension = false;
      this.textGridLeft = 0;
      this.textGridTop = 0;
      this.textGridWidth = 0;
      this.textGridHeight = 0;
      this.characterCellWidth = 0;
      this.characterCellHeight = 0;
      this.textForegroundColor = 0;
      this.textBackgroundColor = 0;
      this.text = null;
      this.applicationIDs = null;
      this.authenticationCodes = null;
      this.applicationData = null;
      this.comments = null;
   }

   private byte[] fromISO8859(String var1) {
      try {
         return var1.getBytes("ISO-8859-1");
      } catch (UnsupportedEncodingException var3) {
         return "".getBytes();
      }
   }

   protected void mergeNativeTree(Node var1) throws IIOInvalidTreeException {
      if (!var1.getNodeName().equals("javax_imageio_gif_image_1.0")) {
         fatal(var1, "Root must be javax_imageio_gif_image_1.0");
      }

      for(Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         String var3 = var2.getNodeName();
         if (var3.equals("ImageDescriptor")) {
            this.imageLeftPosition = getIntAttribute(var2, "imageLeftPosition", -1, true, true, 0, 65535);
            this.imageTopPosition = getIntAttribute(var2, "imageTopPosition", -1, true, true, 0, 65535);
            this.imageWidth = getIntAttribute(var2, "imageWidth", -1, true, true, 1, 65535);
            this.imageHeight = getIntAttribute(var2, "imageHeight", -1, true, true, 1, 65535);
            this.interlaceFlag = getBooleanAttribute(var2, "interlaceFlag", false, true);
         } else if (var3.equals("LocalColorTable")) {
            int var10 = getIntAttribute(var2, "sizeOfLocalColorTable", true, 2, 256);
            if (var10 != 2 && var10 != 4 && var10 != 8 && var10 != 16 && var10 != 32 && var10 != 64 && var10 != 128 && var10 != 256) {
               fatal(var2, "Bad value for LocalColorTable attribute sizeOfLocalColorTable!");
            }

            this.sortFlag = getBooleanAttribute(var2, "sortFlag", false, true);
            this.localColorTable = this.getColorTable(var2, "ColorTableEntry", true, var10);
         } else {
            String var9;
            if (var3.equals("GraphicControlExtension")) {
               var9 = getStringAttribute(var2, "disposalMethod", (String)null, true, disposalMethodNames);

               for(this.disposalMethod = 0; !var9.equals(disposalMethodNames[this.disposalMethod]); ++this.disposalMethod) {
               }

               this.userInputFlag = getBooleanAttribute(var2, "userInputFlag", false, true);
               this.transparentColorFlag = getBooleanAttribute(var2, "transparentColorFlag", false, true);
               this.delayTime = getIntAttribute(var2, "delayTime", -1, true, true, 0, 65535);
               this.transparentColorIndex = getIntAttribute(var2, "transparentColorIndex", -1, true, true, 0, 65535);
            } else if (var3.equals("PlainTextExtension")) {
               this.hasPlainTextExtension = true;
               this.textGridLeft = getIntAttribute(var2, "textGridLeft", -1, true, true, 0, 65535);
               this.textGridTop = getIntAttribute(var2, "textGridTop", -1, true, true, 0, 65535);
               this.textGridWidth = getIntAttribute(var2, "textGridWidth", -1, true, true, 1, 65535);
               this.textGridHeight = getIntAttribute(var2, "textGridHeight", -1, true, true, 1, 65535);
               this.characterCellWidth = getIntAttribute(var2, "characterCellWidth", -1, true, true, 1, 65535);
               this.characterCellHeight = getIntAttribute(var2, "characterCellHeight", -1, true, true, 1, 65535);
               this.textForegroundColor = getIntAttribute(var2, "textForegroundColor", -1, true, true, 0, 255);
               this.textBackgroundColor = getIntAttribute(var2, "textBackgroundColor", -1, true, true, 0, 255);
               var9 = getStringAttribute(var2, "text", "", false, (String[])null);
               this.text = this.fromISO8859(var9);
            } else {
               String var5;
               if (var3.equals("ApplicationExtensions")) {
                  IIOMetadataNode var8 = (IIOMetadataNode)var2.getFirstChild();
                  if (!var8.getNodeName().equals("ApplicationExtension")) {
                     fatal(var2, "Only a ApplicationExtension may be a child of a ApplicationExtensions!");
                  }

                  var5 = getStringAttribute(var8, "applicationID", (String)null, true, (String[])null);
                  String var6 = getStringAttribute(var8, "authenticationCode", (String)null, true, (String[])null);
                  Object var7 = var8.getUserObject();
                  if (var7 == null || !(var7 instanceof byte[])) {
                     fatal(var8, "Bad user object in ApplicationExtension!");
                  }

                  if (this.applicationIDs == null) {
                     this.applicationIDs = new ArrayList();
                     this.authenticationCodes = new ArrayList();
                     this.applicationData = new ArrayList();
                  }

                  this.applicationIDs.add(this.fromISO8859(var5));
                  this.authenticationCodes.add(this.fromISO8859(var6));
                  this.applicationData.add(var7);
               } else if (var3.equals("CommentExtensions")) {
                  Node var4 = var2.getFirstChild();
                  if (var4 != null) {
                     while(var4 != null) {
                        if (!var4.getNodeName().equals("CommentExtension")) {
                           fatal(var2, "Only a CommentExtension may be a child of a CommentExtensions!");
                        }

                        if (this.comments == null) {
                           this.comments = new ArrayList();
                        }

                        var5 = getStringAttribute(var4, "value", (String)null, true, (String[])null);
                        this.comments.add(this.fromISO8859(var5));
                        var4 = var4.getNextSibling();
                     }
                  }
               } else {
                  fatal(var2, "Unknown child of root node!");
               }
            }
         }
      }

   }

   protected void mergeStandardTree(Node var1) throws IIOInvalidTreeException {
      if (!var1.getNodeName().equals("javax_imageio_1.0")) {
         fatal(var1, "Root must be javax_imageio_1.0");
      }

      for(Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         String var3 = var2.getNodeName();
         Node var4;
         String var5;
         if (var3.equals("Chroma")) {
            for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
               var5 = var4.getNodeName();
               if (var5.equals("Palette")) {
                  this.localColorTable = this.getColorTable(var4, "PaletteEntry", false, -1);
                  break;
               }
            }
         } else if (var3.equals("Compression")) {
            for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
               var5 = var4.getNodeName();
               if (var5.equals("NumProgressiveScans")) {
                  int var8 = getIntAttribute(var4, "value", 4, false, true, 1, Integer.MAX_VALUE);
                  if (var8 > 1) {
                     this.interlaceFlag = true;
                  }
                  break;
               }
            }
         } else if (var3.equals("Dimension")) {
            for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
               var5 = var4.getNodeName();
               if (var5.equals("HorizontalPixelOffset")) {
                  this.imageLeftPosition = getIntAttribute(var4, "value", -1, true, true, 0, 65535);
               } else if (var5.equals("VerticalPixelOffset")) {
                  this.imageTopPosition = getIntAttribute(var4, "value", -1, true, true, 0, 65535);
               }
            }
         } else if (var3.equals("Text")) {
            for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
               var5 = var4.getNodeName();
               if (var5.equals("TextEntry") && getAttribute(var4, "compression", "none", false).equals("none") && Charset.isSupported(getAttribute(var4, "encoding", "ISO-8859-1", false))) {
                  String var6 = getAttribute(var4, "value");
                  byte[] var7 = this.fromISO8859(var6);
                  if (this.comments == null) {
                     this.comments = new ArrayList();
                  }

                  this.comments.add(var7);
               }
            }
         } else if (var3.equals("Transparency")) {
            for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
               var5 = var4.getNodeName();
               if (var5.equals("TransparentIndex")) {
                  this.transparentColorIndex = getIntAttribute(var4, "value", -1, true, true, 0, 255);
                  this.transparentColorFlag = true;
                  break;
               }
            }
         }
      }

   }

   public void setFromTree(String var1, Node var2) throws IIOInvalidTreeException {
      this.reset();
      this.mergeTree(var1, var2);
   }
}
