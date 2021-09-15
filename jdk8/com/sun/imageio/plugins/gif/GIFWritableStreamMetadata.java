package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;

class GIFWritableStreamMetadata extends GIFStreamMetadata {
   static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_stream_1.0";

   public GIFWritableStreamMetadata() {
      super(true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", (String[])null, (String[])null);
      this.reset();
   }

   public boolean isReadOnly() {
      return false;
   }

   public void mergeTree(String var1, Node var2) throws IIOInvalidTreeException {
      if (var1.equals("javax_imageio_gif_stream_1.0")) {
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

   public void reset() {
      this.version = null;
      this.logicalScreenWidth = -1;
      this.logicalScreenHeight = -1;
      this.colorResolution = -1;
      this.pixelAspectRatio = 0;
      this.backgroundColorIndex = 0;
      this.sortFlag = false;
      this.globalColorTable = null;
   }

   protected void mergeNativeTree(Node var1) throws IIOInvalidTreeException {
      if (!var1.getNodeName().equals("javax_imageio_gif_stream_1.0")) {
         fatal(var1, "Root must be javax_imageio_gif_stream_1.0");
      }

      for(Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         String var3 = var2.getNodeName();
         if (var3.equals("Version")) {
            this.version = getStringAttribute(var2, "value", (String)null, true, versionStrings);
         } else if (var3.equals("LogicalScreenDescriptor")) {
            this.logicalScreenWidth = getIntAttribute(var2, "logicalScreenWidth", -1, true, true, 1, 65535);
            this.logicalScreenHeight = getIntAttribute(var2, "logicalScreenHeight", -1, true, true, 1, 65535);
            this.colorResolution = getIntAttribute(var2, "colorResolution", -1, true, true, 1, 8);
            this.pixelAspectRatio = getIntAttribute(var2, "pixelAspectRatio", 0, true, true, 0, 255);
         } else if (var3.equals("GlobalColorTable")) {
            int var4 = getIntAttribute(var2, "sizeOfGlobalColorTable", true, 2, 256);
            if (var4 != 2 && var4 != 4 && var4 != 8 && var4 != 16 && var4 != 32 && var4 != 64 && var4 != 128 && var4 != 256) {
               fatal(var2, "Bad value for GlobalColorTable attribute sizeOfGlobalColorTable!");
            }

            this.backgroundColorIndex = getIntAttribute(var2, "backgroundColorIndex", 0, true, true, 0, 255);
            this.sortFlag = getBooleanAttribute(var2, "sortFlag", false, true);
            this.globalColorTable = this.getColorTable(var2, "ColorTableEntry", true, var4);
         } else {
            fatal(var2, "Unknown child of root node!");
         }
      }

   }

   protected void mergeStandardTree(Node var1) throws IIOInvalidTreeException {
      if (!var1.getNodeName().equals("javax_imageio_1.0")) {
         fatal(var1, "Root must be javax_imageio_1.0");
      }

      label99:
      for(Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         String var3 = var2.getNodeName();
         Node var4;
         String var5;
         if (var3.equals("Chroma")) {
            for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
               var5 = var4.getNodeName();
               if (var5.equals("Palette")) {
                  this.globalColorTable = this.getColorTable(var4, "PaletteEntry", false, -1);
               } else if (var5.equals("BackgroundIndex")) {
                  this.backgroundColorIndex = getIntAttribute(var4, "value", -1, true, true, 0, 255);
               }
            }
         } else if (var3.equals("Data")) {
            for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
               var5 = var4.getNodeName();
               if (var5.equals("BitsPerSample")) {
                  this.colorResolution = getIntAttribute(var4, "value", -1, true, true, 1, 8);
                  break;
               }
            }
         } else {
            int var7;
            if (var3.equals("Dimension")) {
               for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
                  var5 = var4.getNodeName();
                  if (var5.equals("PixelAspectRatio")) {
                     float var8 = getFloatAttribute(var4, "value");
                     if (var8 == 1.0F) {
                        this.pixelAspectRatio = 0;
                     } else {
                        var7 = (int)(var8 * 64.0F - 15.0F);
                        this.pixelAspectRatio = Math.max(Math.min(var7, 255), 0);
                     }
                  } else if (var5.equals("HorizontalScreenSize")) {
                     this.logicalScreenWidth = getIntAttribute(var4, "value", -1, true, true, 1, 65535);
                  } else if (var5.equals("VerticalScreenSize")) {
                     this.logicalScreenHeight = getIntAttribute(var4, "value", -1, true, true, 1, 65535);
                  }
               }
            } else if (var3.equals("Document")) {
               for(var4 = var2.getFirstChild(); var4 != null; var4 = var4.getNextSibling()) {
                  var5 = var4.getNodeName();
                  if (var5.equals("FormatVersion")) {
                     String var6 = getStringAttribute(var4, "value", (String)null, true, (String[])null);
                     var7 = 0;

                     while(true) {
                        if (var7 >= versionStrings.length) {
                           continue label99;
                        }

                        if (var6.equals(versionStrings[var7])) {
                           this.version = var6;
                           continue label99;
                        }

                        ++var7;
                     }
                  }
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
