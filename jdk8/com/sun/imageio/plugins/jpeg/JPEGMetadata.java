package com.sun.imageio.plugins.jpeg;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JPEGMetadata extends IIOMetadata implements Cloneable {
   private static final boolean debug = false;
   private List resetSequence;
   private boolean inThumb;
   private boolean hasAlpha;
   List markerSequence;
   final boolean isStream;
   private boolean transparencyDone;

   JPEGMetadata(boolean var1, boolean var2) {
      super(true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", (String[])null, (String[])null);
      this.resetSequence = null;
      this.inThumb = false;
      this.markerSequence = new ArrayList();
      this.inThumb = var2;
      this.isStream = var1;
      if (var1) {
         this.nativeMetadataFormatName = "javax_imageio_jpeg_stream_1.0";
         this.nativeMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat";
      }

   }

   JPEGMetadata(boolean var1, boolean var2, ImageInputStream var3, JPEGImageReader var4) throws IOException {
      this(var1, var2);
      JPEGBuffer var5 = new JPEGBuffer(var3);
      var5.loadBuf(0);
      if ((var5.buf[0] & 255) == 255 && (var5.buf[1] & 255) == 216 && (var5.buf[2] & 255) == 255) {
         boolean var6 = false;
         var5.bufAvail -= 2;
         var5.bufPtr = 2;
         Object var7 = null;

         while(!var6) {
            var5.loadBuf(1);
            var5.scanForFF(var4);
            JFIFMarkerSegment var10;
            switch(var5.buf[var5.bufPtr] & 255) {
            case 0:
               --var5.bufAvail;
               ++var5.bufPtr;
               break;
            case 192:
            case 193:
            case 194:
               if (var1) {
                  throw new IIOException("SOF not permitted in stream metadata");
               }

               var7 = new SOFMarkerSegment(var5);
               break;
            case 196:
               var7 = new DHTMarkerSegment(var5);
               break;
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
               ++var5.bufPtr;
               --var5.bufAvail;
               break;
            case 217:
               var6 = true;
               ++var5.bufPtr;
               --var5.bufAvail;
               break;
            case 218:
               if (var1) {
                  throw new IIOException("SOS not permitted in stream metadata");
               }

               var7 = new SOSMarkerSegment(var5);
               break;
            case 219:
               var7 = new DQTMarkerSegment(var5);
               break;
            case 221:
               var7 = new DRIMarkerSegment(var5);
               break;
            case 224:
               var5.loadBuf(8);
               byte[] var8 = var5.buf;
               int var9 = var5.bufPtr;
               if (var8[var9 + 3] == 74 && var8[var9 + 4] == 70 && var8[var9 + 5] == 73 && var8[var9 + 6] == 70 && var8[var9 + 7] == 0) {
                  if (this.inThumb) {
                     var4.warningOccurred(1);
                     new JFIFMarkerSegment(var5);
                  } else {
                     if (var1) {
                        throw new IIOException("JFIF not permitted in stream metadata");
                     }

                     if (!this.markerSequence.isEmpty()) {
                        throw new IIOException("JFIF APP0 must be first marker after SOI");
                     }

                     var7 = new JFIFMarkerSegment(var5);
                  }
               } else {
                  if (var8[var9 + 3] == 74 && var8[var9 + 4] == 70 && var8[var9 + 5] == 88 && var8[var9 + 6] == 88 && var8[var9 + 7] == 0) {
                     if (var1) {
                        throw new IIOException("JFXX not permitted in stream metadata");
                     }

                     if (this.inThumb) {
                        throw new IIOException("JFXX markers not allowed in JFIF JPEG thumbnail");
                     }

                     var10 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
                     if (var10 == null) {
                        throw new IIOException("JFXX encountered without prior JFIF!");
                     }

                     var10.addJFXX(var5, var4);
                     break;
                  }

                  var7 = new MarkerSegment(var5);
                  ((MarkerSegment)var7).loadData(var5);
               }
               break;
            case 226:
               var5.loadBuf(15);
               if (var5.buf[var5.bufPtr + 3] == 73 && var5.buf[var5.bufPtr + 4] == 67 && var5.buf[var5.bufPtr + 5] == 67 && var5.buf[var5.bufPtr + 6] == 95 && var5.buf[var5.bufPtr + 7] == 80 && var5.buf[var5.bufPtr + 8] == 82 && var5.buf[var5.bufPtr + 9] == 79 && var5.buf[var5.bufPtr + 10] == 70 && var5.buf[var5.bufPtr + 11] == 73 && var5.buf[var5.bufPtr + 12] == 76 && var5.buf[var5.bufPtr + 13] == 69 && var5.buf[var5.bufPtr + 14] == 0) {
                  if (var1) {
                     throw new IIOException("ICC profiles not permitted in stream metadata");
                  }

                  var10 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
                  if (var10 == null) {
                     var7 = new MarkerSegment(var5);
                     ((MarkerSegment)var7).loadData(var5);
                  } else {
                     var10.addICC(var5);
                  }
                  break;
               }

               var7 = new MarkerSegment(var5);
               ((MarkerSegment)var7).loadData(var5);
               break;
            case 238:
               var5.loadBuf(8);
               if (var5.buf[var5.bufPtr + 3] == 65 && var5.buf[var5.bufPtr + 4] == 100 && var5.buf[var5.bufPtr + 5] == 111 && var5.buf[var5.bufPtr + 6] == 98 && var5.buf[var5.bufPtr + 7] == 101) {
                  if (var1) {
                     throw new IIOException("Adobe APP14 markers not permitted in stream metadata");
                  }

                  var7 = new AdobeMarkerSegment(var5);
                  break;
               }

               var7 = new MarkerSegment(var5);
               ((MarkerSegment)var7).loadData(var5);
               break;
            case 254:
               var7 = new COMMarkerSegment(var5);
               break;
            default:
               var7 = new MarkerSegment(var5);
               ((MarkerSegment)var7).loadData(var5);
               ((MarkerSegment)var7).unknown = true;
            }

            if (var7 != null) {
               this.markerSequence.add(var7);
               var7 = null;
            }
         }

         var5.pushBack();
         if (!this.isConsistent()) {
            throw new IIOException("Inconsistent metadata read from stream");
         }
      } else {
         throw new IIOException("Image format error");
      }
   }

   JPEGMetadata(ImageWriteParam var1, JPEGImageWriter var2) {
      this(true, false);
      JPEGImageWriteParam var3 = null;
      if (var1 != null && var1 instanceof JPEGImageWriteParam) {
         var3 = (JPEGImageWriteParam)var1;
         if (!var3.areTablesSet()) {
            var3 = null;
         }
      }

      if (var3 != null) {
         this.markerSequence.add(new DQTMarkerSegment(var3.getQTables()));
         this.markerSequence.add(new DHTMarkerSegment(var3.getDCHuffmanTables(), var3.getACHuffmanTables()));
      } else {
         this.markerSequence.add(new DQTMarkerSegment(JPEG.getDefaultQTables()));
         this.markerSequence.add(new DHTMarkerSegment(JPEG.getDefaultHuffmanTables(true), JPEG.getDefaultHuffmanTables(false)));
      }

      if (!this.isConsistent()) {
         throw new InternalError("Default stream metadata is inconsistent");
      }
   }

   JPEGMetadata(ImageTypeSpecifier var1, ImageWriteParam var2, JPEGImageWriter var3) {
      this(false, false);
      boolean var4 = true;
      boolean var5 = false;
      byte var6 = 0;
      boolean var7 = true;
      boolean var8 = false;
      boolean var9 = false;
      boolean var10 = false;
      boolean var11 = false;
      boolean var12 = true;
      boolean var13 = true;
      float var14 = 0.75F;
      byte[] var15 = new byte[]{1, 2, 3, 4};
      int var16 = 0;
      ImageTypeSpecifier var17 = null;
      if (var2 != null) {
         var17 = var2.getDestinationType();
         if (var17 != null && var1 != null) {
            var3.warningOccurred(0);
            var17 = null;
         }

         if (var2.canWriteProgressive() && var2.getProgressiveMode() == 1) {
            var9 = true;
            var10 = true;
            var13 = false;
         }

         if (var2 instanceof JPEGImageWriteParam) {
            JPEGImageWriteParam var18 = (JPEGImageWriteParam)var2;
            if (var18.areTablesSet()) {
               var12 = false;
               var13 = false;
               if (var18.getDCHuffmanTables().length > 2 || var18.getACHuffmanTables().length > 2) {
                  var11 = true;
               }
            }

            if (!var9) {
               var10 = var18.getOptimizeHuffmanTables();
               if (var10) {
                  var13 = false;
               }
            }
         }

         if (var2.canWriteCompressed() && var2.getCompressionMode() == 2) {
            var14 = var2.getCompressionQuality();
         }
      }

      ColorSpace var24 = null;
      ColorModel var19;
      boolean var20;
      boolean var21;
      int var22;
      if (var17 != null) {
         var19 = var17.getColorModel();
         var16 = var19.getNumComponents();
         var20 = var19.getNumColorComponents() != var16;
         var21 = var19.hasAlpha();
         var24 = var19.getColorSpace();
         var22 = var24.getType();
         switch(var22) {
         case 3:
            if (var20) {
               var4 = false;
               if (!var21) {
                  var5 = true;
                  var6 = 2;
               }
            }
            break;
         case 5:
            var4 = false;
            var5 = true;
            var7 = false;
            var15[0] = 82;
            var15[1] = 71;
            var15[2] = 66;
            if (var21) {
               var15[3] = 65;
            }
            break;
         case 6:
            var7 = false;
            if (var20) {
               var4 = false;
            }
            break;
         case 13:
            if (var24 == JPEG.JCS.getYCC()) {
               var4 = false;
               var15[0] = 89;
               var15[1] = 67;
               var15[2] = 99;
               if (var21) {
                  var15[3] = 65;
               }
            }
            break;
         default:
            var4 = false;
            var7 = false;
         }
      } else if (var1 != null) {
         var19 = var1.getColorModel();
         var16 = var19.getNumComponents();
         var20 = var19.getNumColorComponents() != var16;
         var21 = var19.hasAlpha();
         var24 = var19.getColorSpace();
         var22 = var24.getType();
         switch(var22) {
         case 3:
            if (var20) {
               var4 = false;
               if (!var21) {
                  var5 = true;
                  var6 = 2;
               }
            }
            break;
         case 4:
         case 7:
         case 8:
         case 10:
         case 11:
         case 12:
         default:
            var4 = false;
            var7 = false;
            break;
         case 5:
            if (var21) {
               var4 = false;
            }
            break;
         case 6:
            var7 = false;
            if (var20) {
               var4 = false;
            }
            break;
         case 9:
            var4 = false;
            var5 = true;
            var6 = 2;
            break;
         case 13:
            var4 = false;
            var7 = false;
            if (var24.equals(ColorSpace.getInstance(1002))) {
               var7 = true;
               var5 = true;
               var15[0] = 89;
               var15[1] = 67;
               var15[2] = 99;
               if (var21) {
                  var15[3] = 65;
               }
            }
         }
      }

      if (var4 && JPEG.isNonStandardICC(var24)) {
         var8 = true;
      }

      if (var4) {
         JFIFMarkerSegment var25 = new JFIFMarkerSegment();
         this.markerSequence.add(var25);
         if (var8) {
            try {
               var25.addICC((ICC_ColorSpace)var24);
            } catch (IOException var23) {
            }
         }
      }

      if (var5) {
         this.markerSequence.add(new AdobeMarkerSegment(var6));
      }

      if (var12) {
         this.markerSequence.add(new DQTMarkerSegment(var14, var7));
      }

      if (var13) {
         this.markerSequence.add(new DHTMarkerSegment(var7));
      }

      this.markerSequence.add(new SOFMarkerSegment(var9, var11, var7, var15, var16));
      if (!var9) {
         this.markerSequence.add(new SOSMarkerSegment(var7, var15, var16));
      }

      if (!this.isConsistent()) {
         throw new InternalError("Default image metadata is inconsistent");
      }
   }

   MarkerSegment findMarkerSegment(int var1) {
      Iterator var2 = this.markerSequence.iterator();

      MarkerSegment var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (MarkerSegment)var2.next();
      } while(var3.tag != var1);

      return var3;
   }

   MarkerSegment findMarkerSegment(Class var1, boolean var2) {
      MarkerSegment var4;
      if (var2) {
         Iterator var3 = this.markerSequence.iterator();

         while(var3.hasNext()) {
            var4 = (MarkerSegment)var3.next();
            if (var1.isInstance(var4)) {
               return var4;
            }
         }
      } else {
         ListIterator var5 = this.markerSequence.listIterator(this.markerSequence.size());

         while(var5.hasPrevious()) {
            var4 = (MarkerSegment)var5.previous();
            if (var1.isInstance(var4)) {
               return var4;
            }
         }
      }

      return null;
   }

   private int findMarkerSegmentPosition(Class var1, boolean var2) {
      ListIterator var3;
      int var4;
      MarkerSegment var5;
      if (var2) {
         var3 = this.markerSequence.listIterator();

         for(var4 = 0; var3.hasNext(); ++var4) {
            var5 = (MarkerSegment)var3.next();
            if (var1.isInstance(var5)) {
               return var4;
            }
         }
      } else {
         var3 = this.markerSequence.listIterator(this.markerSequence.size());

         for(var4 = this.markerSequence.size() - 1; var3.hasPrevious(); --var4) {
            var5 = (MarkerSegment)var3.previous();
            if (var1.isInstance(var5)) {
               return var4;
            }
         }
      }

      return -1;
   }

   private int findLastUnknownMarkerSegmentPosition() {
      ListIterator var1 = this.markerSequence.listIterator(this.markerSequence.size());

      for(int var2 = this.markerSequence.size() - 1; var1.hasPrevious(); --var2) {
         MarkerSegment var3 = (MarkerSegment)var1.previous();
         if (var3.unknown) {
            return var2;
         }
      }

      return -1;
   }

   protected Object clone() {
      JPEGMetadata var1 = null;

      try {
         var1 = (JPEGMetadata)super.clone();
      } catch (CloneNotSupportedException var3) {
      }

      if (this.markerSequence != null) {
         var1.markerSequence = this.cloneSequence();
      }

      var1.resetSequence = null;
      return var1;
   }

   private List cloneSequence() {
      if (this.markerSequence == null) {
         return null;
      } else {
         ArrayList var1 = new ArrayList(this.markerSequence.size());
         Iterator var2 = this.markerSequence.iterator();

         while(var2.hasNext()) {
            MarkerSegment var3 = (MarkerSegment)var2.next();
            var1.add(var3.clone());
         }

         return var1;
      }
   }

   public Node getAsTree(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null formatName!");
      } else {
         if (this.isStream) {
            if (var1.equals("javax_imageio_jpeg_stream_1.0")) {
               return this.getNativeTree();
            }
         } else {
            if (var1.equals("javax_imageio_jpeg_image_1.0")) {
               return this.getNativeTree();
            }

            if (var1.equals("javax_imageio_1.0")) {
               return this.getStandardTree();
            }
         }

         throw new IllegalArgumentException("Unsupported format name: " + var1);
      }
   }

   IIOMetadataNode getNativeTree() {
      Iterator var3 = this.markerSequence.iterator();
      IIOMetadataNode var1;
      IIOMetadataNode var2;
      if (this.isStream) {
         var1 = new IIOMetadataNode("javax_imageio_jpeg_stream_1.0");
         var2 = var1;
      } else {
         IIOMetadataNode var4 = new IIOMetadataNode("markerSequence");
         if (!this.inThumb) {
            var1 = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
            IIOMetadataNode var5 = new IIOMetadataNode("JPEGvariety");
            var1.appendChild(var5);
            JFIFMarkerSegment var6 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
            if (var6 != null) {
               var3.next();
               var5.appendChild(var6.getNativeNode());
            }

            var1.appendChild(var4);
         } else {
            var1 = var4;
         }

         var2 = var4;
      }

      while(var3.hasNext()) {
         MarkerSegment var7 = (MarkerSegment)var3.next();
         var2.appendChild(var7.getNativeNode());
      }

      return var1;
   }

   protected IIOMetadataNode getStandardChromaNode() {
      this.hasAlpha = false;
      SOFMarkerSegment var1 = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
      if (var1 == null) {
         return null;
      } else {
         IIOMetadataNode var2 = new IIOMetadataNode("Chroma");
         IIOMetadataNode var3 = new IIOMetadataNode("ColorSpaceType");
         var2.appendChild(var3);
         int var4 = var1.componentSpecs.length;
         IIOMetadataNode var5 = new IIOMetadataNode("NumChannels");
         var2.appendChild(var5);
         var5.setAttribute("value", Integer.toString(var4));
         if (this.findMarkerSegment(JFIFMarkerSegment.class, true) != null) {
            if (var4 == 1) {
               var3.setAttribute("name", "GRAY");
            } else {
               var3.setAttribute("name", "YCbCr");
            }

            return var2;
         } else {
            AdobeMarkerSegment var6 = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
            if (var6 != null) {
               switch(var6.transform) {
               case 0:
                  if (var4 == 3) {
                     var3.setAttribute("name", "RGB");
                  } else if (var4 == 4) {
                     var3.setAttribute("name", "CMYK");
                  }
                  break;
               case 1:
                  var3.setAttribute("name", "YCbCr");
                  break;
               case 2:
                  var3.setAttribute("name", "YCCK");
               }

               return var2;
            } else if (var4 < 3) {
               var3.setAttribute("name", "GRAY");
               if (var4 == 2) {
                  this.hasAlpha = true;
               }

               return var2;
            } else {
               boolean var7 = true;

               int var9;
               for(int var8 = 0; var8 < var1.componentSpecs.length; ++var8) {
                  var9 = var1.componentSpecs[var8].componentId;
                  if (var9 < 1 || var9 >= var1.componentSpecs.length) {
                     var7 = false;
                  }
               }

               if (var7) {
                  var3.setAttribute("name", "YCbCr");
                  if (var4 == 4) {
                     this.hasAlpha = true;
                  }

                  return var2;
               } else if (var1.componentSpecs[0].componentId == 82 && var1.componentSpecs[1].componentId == 71 && var1.componentSpecs[2].componentId == 66) {
                  var3.setAttribute("name", "RGB");
                  if (var4 == 4 && var1.componentSpecs[3].componentId == 65) {
                     this.hasAlpha = true;
                  }

                  return var2;
               } else if (var1.componentSpecs[0].componentId == 89 && var1.componentSpecs[1].componentId == 67 && var1.componentSpecs[2].componentId == 99) {
                  var3.setAttribute("name", "PhotoYCC");
                  if (var4 == 4 && var1.componentSpecs[3].componentId == 65) {
                     this.hasAlpha = true;
                  }

                  return var2;
               } else {
                  boolean var12 = false;
                  var9 = var1.componentSpecs[0].HsamplingFactor;
                  int var10 = var1.componentSpecs[0].VsamplingFactor;

                  for(int var11 = 1; var11 < var1.componentSpecs.length; ++var11) {
                     if (var1.componentSpecs[var11].HsamplingFactor != var9 || var1.componentSpecs[var11].VsamplingFactor != var10) {
                        var12 = true;
                        break;
                     }
                  }

                  if (var12) {
                     var3.setAttribute("name", "YCbCr");
                     if (var4 == 4) {
                        this.hasAlpha = true;
                     }

                     return var2;
                  } else {
                     if (var4 == 3) {
                        var3.setAttribute("name", "RGB");
                     } else {
                        var3.setAttribute("name", "CMYK");
                     }

                     return var2;
                  }
               }
            }
         }
      }
   }

   protected IIOMetadataNode getStandardCompressionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Compression");
      IIOMetadataNode var2 = new IIOMetadataNode("CompressionTypeName");
      var2.setAttribute("value", "JPEG");
      var1.appendChild(var2);
      IIOMetadataNode var3 = new IIOMetadataNode("Lossless");
      var3.setAttribute("value", "FALSE");
      var1.appendChild(var3);
      int var4 = 0;
      Iterator var5 = this.markerSequence.iterator();

      while(var5.hasNext()) {
         MarkerSegment var6 = (MarkerSegment)var5.next();
         if (var6.tag == 218) {
            ++var4;
         }
      }

      if (var4 != 0) {
         IIOMetadataNode var7 = new IIOMetadataNode("NumProgressiveScans");
         var7.setAttribute("value", Integer.toString(var4));
         var1.appendChild(var7);
      }

      return var1;
   }

   protected IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Dimension");
      IIOMetadataNode var2 = new IIOMetadataNode("ImageOrientation");
      var2.setAttribute("value", "normal");
      var1.appendChild(var2);
      JFIFMarkerSegment var3 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
      if (var3 != null) {
         float var4;
         if (var3.resUnits == 0) {
            var4 = (float)var3.Xdensity / (float)var3.Ydensity;
         } else {
            var4 = (float)var3.Ydensity / (float)var3.Xdensity;
         }

         IIOMetadataNode var5 = new IIOMetadataNode("PixelAspectRatio");
         var5.setAttribute("value", Float.toString(var4));
         var1.insertBefore(var5, var2);
         if (var3.resUnits != 0) {
            float var6 = var3.resUnits == 1 ? 25.4F : 10.0F;
            IIOMetadataNode var7 = new IIOMetadataNode("HorizontalPixelSize");
            var7.setAttribute("value", Float.toString(var6 / (float)var3.Xdensity));
            var1.appendChild(var7);
            IIOMetadataNode var8 = new IIOMetadataNode("VerticalPixelSize");
            var8.setAttribute("value", Float.toString(var6 / (float)var3.Ydensity));
            var1.appendChild(var8);
         }
      }

      return var1;
   }

   protected IIOMetadataNode getStandardTextNode() {
      IIOMetadataNode var1 = null;
      if (this.findMarkerSegment(254) != null) {
         var1 = new IIOMetadataNode("Text");
         Iterator var2 = this.markerSequence.iterator();

         while(var2.hasNext()) {
            MarkerSegment var3 = (MarkerSegment)var2.next();
            if (var3.tag == 254) {
               COMMarkerSegment var4 = (COMMarkerSegment)var3;
               IIOMetadataNode var5 = new IIOMetadataNode("TextEntry");
               var5.setAttribute("keyword", "comment");
               var5.setAttribute("value", var4.getComment());
               var1.appendChild(var5);
            }
         }
      }

      return var1;
   }

   protected IIOMetadataNode getStandardTransparencyNode() {
      IIOMetadataNode var1 = null;
      if (this.hasAlpha) {
         var1 = new IIOMetadataNode("Transparency");
         IIOMetadataNode var2 = new IIOMetadataNode("Alpha");
         var2.setAttribute("value", "nonpremultiplied");
         var1.appendChild(var2);
      }

      return var1;
   }

   public boolean isReadOnly() {
      return false;
   }

   public void mergeTree(String var1, Node var2) throws IIOInvalidTreeException {
      if (var1 == null) {
         throw new IllegalArgumentException("null formatName!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("null root!");
      } else {
         List var3 = null;
         if (this.resetSequence == null) {
            this.resetSequence = this.cloneSequence();
            var3 = this.resetSequence;
         } else {
            var3 = this.cloneSequence();
         }

         if (this.isStream && var1.equals("javax_imageio_jpeg_stream_1.0")) {
            this.mergeNativeTree(var2);
         } else if (!this.isStream && var1.equals("javax_imageio_jpeg_image_1.0")) {
            this.mergeNativeTree(var2);
         } else {
            if (this.isStream || !var1.equals("javax_imageio_1.0")) {
               throw new IllegalArgumentException("Unsupported format name: " + var1);
            }

            this.mergeStandardTree(var2);
         }

         if (!this.isConsistent()) {
            this.markerSequence = var3;
            throw new IIOInvalidTreeException("Merged tree is invalid; original restored", var2);
         }
      }
   }

   private void mergeNativeTree(Node var1) throws IIOInvalidTreeException {
      String var2 = var1.getNodeName();
      if (var2 != (this.isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0")) {
         throw new IIOInvalidTreeException("Invalid root node name: " + var2, var1);
      } else if (var1.getChildNodes().getLength() != 2) {
         throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", var1);
      } else {
         this.mergeJFIFsubtree(var1.getFirstChild());
         this.mergeSequenceSubtree(var1.getLastChild());
      }
   }

   private void mergeJFIFsubtree(Node var1) throws IIOInvalidTreeException {
      if (var1.getChildNodes().getLength() != 0) {
         Node var2 = var1.getFirstChild();
         JFIFMarkerSegment var3 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
         if (var3 != null) {
            var3.updateFromNativeNode(var2, false);
         } else {
            this.markerSequence.add(0, new JFIFMarkerSegment(var2));
         }
      }

   }

   private void mergeSequenceSubtree(Node var1) throws IIOInvalidTreeException {
      NodeList var2 = var1.getChildNodes();

      for(int var3 = 0; var3 < var2.getLength(); ++var3) {
         Node var4 = var2.item(var3);
         String var5 = var4.getNodeName();
         if (var5.equals("dqt")) {
            this.mergeDQTNode(var4);
         } else if (var5.equals("dht")) {
            this.mergeDHTNode(var4);
         } else if (var5.equals("dri")) {
            this.mergeDRINode(var4);
         } else if (var5.equals("com")) {
            this.mergeCOMNode(var4);
         } else if (var5.equals("app14Adobe")) {
            this.mergeAdobeNode(var4);
         } else if (var5.equals("unknown")) {
            this.mergeUnknownNode(var4);
         } else if (var5.equals("sof")) {
            this.mergeSOFNode(var4);
         } else {
            if (!var5.equals("sos")) {
               throw new IIOInvalidTreeException("Invalid node: " + var5, var4);
            }

            this.mergeSOSNode(var4);
         }
      }

   }

   private void mergeDQTNode(Node var1) throws IIOInvalidTreeException {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.markerSequence.iterator();

      while(var3.hasNext()) {
         MarkerSegment var4 = (MarkerSegment)var3.next();
         if (var4 instanceof DQTMarkerSegment) {
            var2.add(var4);
         }
      }

      int var5;
      int var7;
      if (!var2.isEmpty()) {
         NodeList var14 = var1.getChildNodes();

         for(var5 = 0; var5 < var14.getLength(); ++var5) {
            Node var6 = var14.item(var5);
            var7 = MarkerSegment.getAttributeValue(var6, (NamedNodeMap)null, "qtableId", 0, 3, true);
            DQTMarkerSegment var8 = null;
            int var9 = -1;

            for(int var10 = 0; var10 < var2.size(); ++var10) {
               DQTMarkerSegment var11 = (DQTMarkerSegment)var2.get(var10);

               for(int var12 = 0; var12 < var11.tables.size(); ++var12) {
                  DQTMarkerSegment.Qtable var13 = (DQTMarkerSegment.Qtable)var11.tables.get(var12);
                  if (var7 == var13.tableID) {
                     var8 = var11;
                     var9 = var12;
                     break;
                  }
               }

               if (var8 != null) {
                  break;
               }
            }

            if (var8 != null) {
               var8.tables.set(var9, var8.getQtableFromNode(var6));
            } else {
               var8 = (DQTMarkerSegment)var2.get(var2.size() - 1);
               var8.tables.add(var8.getQtableFromNode(var6));
            }
         }
      } else {
         DQTMarkerSegment var15 = new DQTMarkerSegment(var1);
         var5 = this.findMarkerSegmentPosition(DHTMarkerSegment.class, true);
         int var16 = this.findMarkerSegmentPosition(SOFMarkerSegment.class, true);
         var7 = this.findMarkerSegmentPosition(SOSMarkerSegment.class, true);
         if (var5 != -1) {
            this.markerSequence.add(var5, var15);
         } else if (var16 != -1) {
            this.markerSequence.add(var16, var15);
         } else if (var7 != -1) {
            this.markerSequence.add(var7, var15);
         } else {
            this.markerSequence.add(var15);
         }
      }

   }

   private void mergeDHTNode(Node var1) throws IIOInvalidTreeException {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.markerSequence.iterator();

      while(var3.hasNext()) {
         MarkerSegment var4 = (MarkerSegment)var3.next();
         if (var4 instanceof DHTMarkerSegment) {
            var2.add(var4);
         }
      }

      int var5;
      if (!var2.isEmpty()) {
         NodeList var16 = var1.getChildNodes();

         for(var5 = 0; var5 < var16.getLength(); ++var5) {
            Node var6 = var16.item(var5);
            NamedNodeMap var7 = var6.getAttributes();
            int var8 = MarkerSegment.getAttributeValue(var6, var7, "htableId", 0, 3, true);
            int var9 = MarkerSegment.getAttributeValue(var6, var7, "class", 0, 1, true);
            DHTMarkerSegment var10 = null;
            int var11 = -1;

            for(int var12 = 0; var12 < var2.size(); ++var12) {
               DHTMarkerSegment var13 = (DHTMarkerSegment)var2.get(var12);

               for(int var14 = 0; var14 < var13.tables.size(); ++var14) {
                  DHTMarkerSegment.Htable var15 = (DHTMarkerSegment.Htable)var13.tables.get(var14);
                  if (var8 == var15.tableID && var9 == var15.tableClass) {
                     var10 = var13;
                     var11 = var14;
                     break;
                  }
               }

               if (var10 != null) {
                  break;
               }
            }

            if (var10 != null) {
               var10.tables.set(var11, var10.getHtableFromNode(var6));
            } else {
               var10 = (DHTMarkerSegment)var2.get(var2.size() - 1);
               var10.tables.add(var10.getHtableFromNode(var6));
            }
         }
      } else {
         DHTMarkerSegment var17 = new DHTMarkerSegment(var1);
         var5 = this.findMarkerSegmentPosition(DQTMarkerSegment.class, false);
         int var18 = this.findMarkerSegmentPosition(SOFMarkerSegment.class, true);
         int var19 = this.findMarkerSegmentPosition(SOSMarkerSegment.class, true);
         if (var5 != -1) {
            this.markerSequence.add(var5 + 1, var17);
         } else if (var18 != -1) {
            this.markerSequence.add(var18, var17);
         } else if (var19 != -1) {
            this.markerSequence.add(var19, var17);
         } else {
            this.markerSequence.add(var17);
         }
      }

   }

   private void mergeDRINode(Node var1) throws IIOInvalidTreeException {
      DRIMarkerSegment var2 = (DRIMarkerSegment)this.findMarkerSegment(DRIMarkerSegment.class, true);
      if (var2 != null) {
         var2.updateFromNativeNode(var1, false);
      } else {
         DRIMarkerSegment var3 = new DRIMarkerSegment(var1);
         int var4 = this.findMarkerSegmentPosition(SOFMarkerSegment.class, true);
         int var5 = this.findMarkerSegmentPosition(SOSMarkerSegment.class, true);
         if (var4 != -1) {
            this.markerSequence.add(var4, var3);
         } else if (var5 != -1) {
            this.markerSequence.add(var5, var3);
         } else {
            this.markerSequence.add(var3);
         }
      }

   }

   private void mergeCOMNode(Node var1) throws IIOInvalidTreeException {
      COMMarkerSegment var2 = new COMMarkerSegment(var1);
      this.insertCOMMarkerSegment(var2);
   }

   private void insertCOMMarkerSegment(COMMarkerSegment var1) {
      int var2 = this.findMarkerSegmentPosition(COMMarkerSegment.class, false);
      boolean var3 = this.findMarkerSegment(JFIFMarkerSegment.class, true) != null;
      int var4 = this.findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
      if (var2 != -1) {
         this.markerSequence.add(var2 + 1, var1);
      } else if (var3) {
         this.markerSequence.add(1, var1);
      } else if (var4 != -1) {
         this.markerSequence.add(var4 + 1, var1);
      } else {
         this.markerSequence.add(0, var1);
      }

   }

   private void mergeAdobeNode(Node var1) throws IIOInvalidTreeException {
      AdobeMarkerSegment var2 = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
      if (var2 != null) {
         var2.updateFromNativeNode(var1, false);
      } else {
         AdobeMarkerSegment var3 = new AdobeMarkerSegment(var1);
         this.insertAdobeMarkerSegment(var3);
      }

   }

   private void insertAdobeMarkerSegment(AdobeMarkerSegment var1) {
      boolean var2 = this.findMarkerSegment(JFIFMarkerSegment.class, true) != null;
      int var3 = this.findLastUnknownMarkerSegmentPosition();
      if (var2) {
         this.markerSequence.add(1, var1);
      } else if (var3 != -1) {
         this.markerSequence.add(var3 + 1, var1);
      } else {
         this.markerSequence.add(0, var1);
      }

   }

   private void mergeUnknownNode(Node var1) throws IIOInvalidTreeException {
      MarkerSegment var2 = new MarkerSegment(var1);
      int var3 = this.findLastUnknownMarkerSegmentPosition();
      boolean var4 = this.findMarkerSegment(JFIFMarkerSegment.class, true) != null;
      int var5 = this.findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
      if (var3 != -1) {
         this.markerSequence.add(var3 + 1, var2);
      } else if (var4) {
         this.markerSequence.add(1, var2);
      }

      if (var5 != -1) {
         this.markerSequence.add(var5, var2);
      } else {
         this.markerSequence.add(0, var2);
      }

   }

   private void mergeSOFNode(Node var1) throws IIOInvalidTreeException {
      SOFMarkerSegment var2 = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
      if (var2 != null) {
         var2.updateFromNativeNode(var1, false);
      } else {
         SOFMarkerSegment var3 = new SOFMarkerSegment(var1);
         int var4 = this.findMarkerSegmentPosition(SOSMarkerSegment.class, true);
         if (var4 != -1) {
            this.markerSequence.add(var4, var3);
         } else {
            this.markerSequence.add(var3);
         }
      }

   }

   private void mergeSOSNode(Node var1) throws IIOInvalidTreeException {
      SOSMarkerSegment var2 = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, true);
      SOSMarkerSegment var3 = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, false);
      if (var2 != null) {
         if (var2 != var3) {
            throw new IIOInvalidTreeException("Can't merge SOS node into a tree with > 1 SOS node", var1);
         }

         var2.updateFromNativeNode(var1, false);
      } else {
         this.markerSequence.add(new SOSMarkerSegment(var1));
      }

   }

   private void mergeStandardTree(Node var1) throws IIOInvalidTreeException {
      this.transparencyDone = false;
      NodeList var2 = var1.getChildNodes();

      for(int var3 = 0; var3 < var2.getLength(); ++var3) {
         Node var4 = var2.item(var3);
         String var5 = var4.getNodeName();
         if (var5.equals("Chroma")) {
            this.mergeStandardChromaNode(var4, var2);
         } else if (var5.equals("Compression")) {
            this.mergeStandardCompressionNode(var4);
         } else if (var5.equals("Data")) {
            this.mergeStandardDataNode(var4);
         } else if (var5.equals("Dimension")) {
            this.mergeStandardDimensionNode(var4);
         } else if (var5.equals("Document")) {
            this.mergeStandardDocumentNode(var4);
         } else if (var5.equals("Text")) {
            this.mergeStandardTextNode(var4);
         } else {
            if (!var5.equals("Transparency")) {
               throw new IIOInvalidTreeException("Invalid node: " + var5, var4);
            }

            this.mergeStandardTransparencyNode(var4);
         }
      }

   }

   private void mergeStandardChromaNode(Node var1, NodeList var2) throws IIOInvalidTreeException {
      if (this.transparencyDone) {
         throw new IIOInvalidTreeException("Transparency node must follow Chroma node", var1);
      } else {
         Node var3 = var1.getFirstChild();
         if (var3 != null && var3.getNodeName().equals("ColorSpaceType")) {
            String var4 = var3.getAttributes().getNamedItem("name").getNodeValue();
            boolean var5 = false;
            boolean var6 = false;
            boolean var7 = false;
            byte var8 = 0;
            boolean var9 = false;
            byte[] var10 = new byte[]{1, 2, 3, 4};
            int var30;
            if (var4.equals("GRAY")) {
               var30 = 1;
               var6 = true;
            } else if (var4.equals("YCbCr")) {
               var30 = 3;
               var6 = true;
               var9 = true;
            } else if (var4.equals("PhotoYCC")) {
               var30 = 3;
               var7 = true;
               var8 = 1;
               var10[0] = 89;
               var10[1] = 67;
               var10[2] = 99;
            } else if (var4.equals("RGB")) {
               var30 = 3;
               var7 = true;
               var8 = 0;
               var10[0] = 82;
               var10[1] = 71;
               var10[2] = 66;
            } else if (!var4.equals("XYZ") && !var4.equals("Lab") && !var4.equals("Luv") && !var4.equals("YxY") && !var4.equals("HSV") && !var4.equals("HLS") && !var4.equals("CMY") && !var4.equals("3CLR")) {
               if (var4.equals("YCCK")) {
                  var30 = 4;
                  var7 = true;
                  var8 = 2;
                  var9 = true;
               } else if (var4.equals("CMYK")) {
                  var30 = 4;
                  var7 = true;
                  var8 = 0;
               } else {
                  if (!var4.equals("4CLR")) {
                     return;
                  }

                  var30 = 4;
               }
            } else {
               var30 = 3;
            }

            boolean var11 = false;

            for(int var12 = 0; var12 < var2.getLength(); ++var12) {
               Node var13 = var2.item(var12);
               if (var13.getNodeName().equals("Transparency")) {
                  var11 = this.wantAlpha(var13);
                  break;
               }
            }

            if (var11) {
               ++var30;
               var6 = false;
               if (var10[0] == 82) {
                  var10[3] = 65;
                  var7 = false;
               }
            }

            JFIFMarkerSegment var31 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
            AdobeMarkerSegment var32 = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
            SOFMarkerSegment var14 = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
            SOSMarkerSegment var15 = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, true);
            if (var14 == null || var14.tag != 194 || var14.componentSpecs.length == var30 || var15 == null) {
               if (!var6 && var31 != null) {
                  this.markerSequence.remove(var31);
               }

               if (var6 && !this.isStream) {
                  this.markerSequence.add(0, new JFIFMarkerSegment());
               }

               if (var7) {
                  if (var32 == null && !this.isStream) {
                     var32 = new AdobeMarkerSegment(var8);
                     this.insertAdobeMarkerSegment(var32);
                  } else {
                     var32.transform = var8;
                  }
               } else if (var32 != null) {
                  this.markerSequence.remove(var32);
               }

               boolean var16 = false;
               boolean var17 = false;
               boolean var18 = false;
               int[] var19 = new int[]{0, 1, 1, 0};
               int[] var20 = new int[]{0, 0, 0, 0};
               int[] var21 = var9 ? var19 : var20;
               SOFMarkerSegment.ComponentSpec[] var22 = null;
               MarkerSegment var25;
               Iterator var34;
               if (var14 != null) {
                  var22 = var14.componentSpecs;
                  var18 = var14.tag == 194;
                  this.markerSequence.set(this.markerSequence.indexOf(var14), new SOFMarkerSegment(var18, false, var9, var10, var30));

                  int var23;
                  for(var23 = 0; var23 < var22.length; ++var23) {
                     if (var22[var23].QtableSelector != var21[var23]) {
                        var16 = true;
                     }
                  }

                  if (var18) {
                     boolean var33 = false;

                     for(int var24 = 0; var24 < var22.length; ++var24) {
                        if (var10[var24] != var22[var24].componentId) {
                           var33 = true;
                        }
                     }

                     if (var33) {
                        var34 = this.markerSequence.iterator();

                        label260:
                        while(true) {
                           do {
                              if (!var34.hasNext()) {
                                 break label260;
                              }

                              var25 = (MarkerSegment)var34.next();
                           } while(!(var25 instanceof SOSMarkerSegment));

                           SOSMarkerSegment var26 = (SOSMarkerSegment)var25;

                           for(int var27 = 0; var27 < var26.componentSpecs.length; ++var27) {
                              int var28 = var26.componentSpecs[var27].componentSelector;

                              for(int var29 = 0; var29 < var22.length; ++var29) {
                                 if (var22[var29].componentId == var28) {
                                    var26.componentSpecs[var27].componentSelector = var10[var29];
                                 }
                              }
                           }
                        }
                     }
                  } else if (var15 != null) {
                     for(var23 = 0; var23 < var15.componentSpecs.length; ++var23) {
                        if (var15.componentSpecs[var23].dcHuffTable != var21[var23] || var15.componentSpecs[var23].acHuffTable != var21[var23]) {
                           var17 = true;
                        }
                     }

                     this.markerSequence.set(this.markerSequence.indexOf(var15), new SOSMarkerSegment(var9, var10, var30));
                  }
               } else if (this.isStream) {
                  var16 = true;
                  var17 = true;
               }

               ArrayList var35;
               boolean var36;
               Iterator var37;
               Iterator var43;
               if (var16) {
                  var35 = new ArrayList();
                  var34 = this.markerSequence.iterator();

                  while(var34.hasNext()) {
                     var25 = (MarkerSegment)var34.next();
                     if (var25 instanceof DQTMarkerSegment) {
                        var35.add(var25);
                     }
                  }

                  if (!var35.isEmpty() && var9) {
                     var36 = false;
                     var37 = var35.iterator();

                     DQTMarkerSegment var39;
                     while(var37.hasNext()) {
                        var39 = (DQTMarkerSegment)var37.next();
                        var43 = var39.tables.iterator();

                        while(var43.hasNext()) {
                           DQTMarkerSegment.Qtable var45 = (DQTMarkerSegment.Qtable)var43.next();
                           if (var45.tableID == 1) {
                              var36 = true;
                           }
                        }
                     }

                     if (!var36) {
                        DQTMarkerSegment.Qtable var38 = null;
                        Iterator var40 = var35.iterator();

                        while(var40.hasNext()) {
                           DQTMarkerSegment var44 = (DQTMarkerSegment)var40.next();
                           Iterator var46 = var44.tables.iterator();

                           while(var46.hasNext()) {
                              DQTMarkerSegment.Qtable var48 = (DQTMarkerSegment.Qtable)var46.next();
                              if (var48.tableID == 0) {
                                 var38 = var48;
                              }
                           }
                        }

                        var39 = (DQTMarkerSegment)var35.get(var35.size() - 1);
                        var39.tables.add(var39.getChromaForLuma(var38));
                     }
                  }
               }

               if (var17) {
                  var35 = new ArrayList();
                  var34 = this.markerSequence.iterator();

                  while(var34.hasNext()) {
                     var25 = (MarkerSegment)var34.next();
                     if (var25 instanceof DHTMarkerSegment) {
                        var35.add(var25);
                     }
                  }

                  if (!var35.isEmpty() && var9) {
                     var36 = false;
                     var37 = var35.iterator();

                     while(var37.hasNext()) {
                        DHTMarkerSegment var41 = (DHTMarkerSegment)var37.next();
                        var43 = var41.tables.iterator();

                        while(var43.hasNext()) {
                           DHTMarkerSegment.Htable var47 = (DHTMarkerSegment.Htable)var43.next();
                           if (var47.tableID == 1) {
                              var36 = true;
                           }
                        }
                     }

                     if (!var36) {
                        DHTMarkerSegment var42 = (DHTMarkerSegment)var35.get(var35.size() - 1);
                        var42.addHtable(JPEGHuffmanTable.StdDCLuminance, true, 1);
                        var42.addHtable(JPEGHuffmanTable.StdACLuminance, true, 1);
                     }
                  }
               }

            }
         }
      }
   }

   private boolean wantAlpha(Node var1) {
      boolean var2 = false;
      Node var3 = var1.getFirstChild();
      if (var3.getNodeName().equals("Alpha") && var3.hasAttributes()) {
         String var4 = var3.getAttributes().getNamedItem("value").getNodeValue();
         if (!var4.equals("none")) {
            var2 = true;
         }
      }

      this.transparencyDone = true;
      return var2;
   }

   private void mergeStandardCompressionNode(Node var1) throws IIOInvalidTreeException {
   }

   private void mergeStandardDataNode(Node var1) throws IIOInvalidTreeException {
   }

   private void mergeStandardDimensionNode(Node var1) throws IIOInvalidTreeException {
      JFIFMarkerSegment var2 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
      if (var2 == null) {
         boolean var3 = false;
         SOFMarkerSegment var4 = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
         if (var4 != null) {
            int var5 = var4.componentSpecs.length;
            if (var5 == 1 || var5 == 3) {
               var3 = true;

               for(int var6 = 0; var6 < var4.componentSpecs.length; ++var6) {
                  if (var4.componentSpecs[var6].componentId != var6 + 1) {
                     var3 = false;
                  }
               }

               AdobeMarkerSegment var14 = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
               if (var14 != null && var14.transform != (var5 == 1 ? 0 : 1)) {
                  var3 = false;
               }
            }
         }

         if (var3) {
            var2 = new JFIFMarkerSegment();
            this.markerSequence.add(0, var2);
         }
      }

      if (var2 != null) {
         NodeList var12 = var1.getChildNodes();

         for(int var11 = 0; var11 < var12.getLength(); ++var11) {
            Node var13 = var12.item(var11);
            NamedNodeMap var15 = var13.getAttributes();
            String var7 = var13.getNodeName();
            String var8;
            float var9;
            if (var7.equals("PixelAspectRatio")) {
               var8 = var15.getNamedItem("value").getNodeValue();
               var9 = Float.parseFloat(var8);
               Point var10 = findIntegerRatio(var9);
               var2.resUnits = 0;
               var2.Xdensity = var10.x;
               var2.Xdensity = var10.y;
            } else {
               int var16;
               if (var7.equals("HorizontalPixelSize")) {
                  var8 = var15.getNamedItem("value").getNodeValue();
                  var9 = Float.parseFloat(var8);
                  var16 = (int)Math.round(1.0D / ((double)var9 * 10.0D));
                  var2.resUnits = 2;
                  var2.Xdensity = var16;
               } else if (var7.equals("VerticalPixelSize")) {
                  var8 = var15.getNamedItem("value").getNodeValue();
                  var9 = Float.parseFloat(var8);
                  var16 = (int)Math.round(1.0D / ((double)var9 * 10.0D));
                  var2.resUnits = 2;
                  var2.Ydensity = var16;
               }
            }
         }
      }

   }

   private static Point findIntegerRatio(float var0) {
      float var1 = 0.005F;
      var0 = Math.abs(var0);
      if (var0 <= var1) {
         return new Point(1, 255);
      } else if (var0 >= 255.0F) {
         return new Point(255, 1);
      } else {
         boolean var2 = false;
         if ((double)var0 < 1.0D) {
            var0 = 1.0F / var0;
            var2 = true;
         }

         int var3 = 1;
         int var4 = Math.round(var0);
         float var5 = (float)var4;

         for(float var6 = Math.abs(var0 - var5); var6 > var1; var6 = Math.abs(var0 - var5)) {
            ++var3;
            var4 = Math.round((float)var3 * var0);
            var5 = (float)var4 / (float)var3;
         }

         return var2 ? new Point(var3, var4) : new Point(var4, var3);
      }
   }

   private void mergeStandardDocumentNode(Node var1) throws IIOInvalidTreeException {
   }

   private void mergeStandardTextNode(Node var1) throws IIOInvalidTreeException {
      NodeList var2 = var1.getChildNodes();

      for(int var3 = 0; var3 < var2.getLength(); ++var3) {
         Node var4 = var2.item(var3);
         NamedNodeMap var5 = var4.getAttributes();
         Node var6 = var5.getNamedItem("compression");
         boolean var7 = true;
         String var8;
         if (var6 != null) {
            var8 = var6.getNodeValue();
            if (!var8.equals("none")) {
               var7 = false;
            }
         }

         if (var7) {
            var8 = var5.getNamedItem("value").getNodeValue();
            COMMarkerSegment var9 = new COMMarkerSegment(var8);
            this.insertCOMMarkerSegment(var9);
         }
      }

   }

   private void mergeStandardTransparencyNode(Node var1) throws IIOInvalidTreeException {
      if (!this.transparencyDone && !this.isStream) {
         boolean var2 = this.wantAlpha(var1);
         JFIFMarkerSegment var3 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
         AdobeMarkerSegment var4 = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
         SOFMarkerSegment var5 = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
         SOSMarkerSegment var6 = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, true);
         if (var5 != null && var5.tag == 194) {
            return;
         }

         if (var5 != null) {
            int var7 = var5.componentSpecs.length;
            boolean var8 = var7 == 2 || var7 == 4;
            if (var8 != var2) {
               SOFMarkerSegment.ComponentSpec[] var9;
               int var10;
               if (var2) {
                  ++var7;
                  if (var3 != null) {
                     this.markerSequence.remove(var3);
                  }

                  if (var4 != null) {
                     var4.transform = 0;
                  }

                  var9 = new SOFMarkerSegment.ComponentSpec[var7];

                  for(var10 = 0; var10 < var5.componentSpecs.length; ++var10) {
                     var9[var10] = var5.componentSpecs[var10];
                  }

                  byte var14 = (byte)var5.componentSpecs[0].componentId;
                  byte var11 = (byte)(var14 > 1 ? 65 : 4);
                  var9[var7 - 1] = var5.getComponentSpec(var11, var5.componentSpecs[0].HsamplingFactor, var5.componentSpecs[0].QtableSelector);
                  var5.componentSpecs = var9;
                  SOSMarkerSegment.ScanComponentSpec[] var12 = new SOSMarkerSegment.ScanComponentSpec[var7];

                  for(int var13 = 0; var13 < var6.componentSpecs.length; ++var13) {
                     var12[var13] = var6.componentSpecs[var13];
                  }

                  var12[var7 - 1] = var6.getScanComponentSpec(var11, 0);
                  var6.componentSpecs = var12;
               } else {
                  --var7;
                  var9 = new SOFMarkerSegment.ComponentSpec[var7];

                  for(var10 = 0; var10 < var7; ++var10) {
                     var9[var10] = var5.componentSpecs[var10];
                  }

                  var5.componentSpecs = var9;
                  SOSMarkerSegment.ScanComponentSpec[] var16 = new SOSMarkerSegment.ScanComponentSpec[var7];

                  for(int var15 = 0; var15 < var7; ++var15) {
                     var16[var15] = var6.componentSpecs[var15];
                  }

                  var6.componentSpecs = var16;
               }
            }
         }
      }

   }

   public void setFromTree(String var1, Node var2) throws IIOInvalidTreeException {
      if (var1 == null) {
         throw new IllegalArgumentException("null formatName!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("null root!");
      } else {
         if (this.isStream && var1.equals("javax_imageio_jpeg_stream_1.0")) {
            this.setFromNativeTree(var2);
         } else if (!this.isStream && var1.equals("javax_imageio_jpeg_image_1.0")) {
            this.setFromNativeTree(var2);
         } else {
            if (this.isStream || !var1.equals("javax_imageio_1.0")) {
               throw new IllegalArgumentException("Unsupported format name: " + var1);
            }

            super.setFromTree(var1, var2);
         }

      }
   }

   private void setFromNativeTree(Node var1) throws IIOInvalidTreeException {
      if (this.resetSequence == null) {
         this.resetSequence = this.markerSequence;
      }

      this.markerSequence = new ArrayList();
      String var2 = var1.getNodeName();
      if (var2 != (this.isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0")) {
         throw new IIOInvalidTreeException("Invalid root node name: " + var2, var1);
      } else {
         Node var3;
         if (!this.isStream) {
            if (var1.getChildNodes().getLength() != 2) {
               throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", var1);
            }

            var3 = var1.getFirstChild();
            if (var3.getChildNodes().getLength() != 0) {
               this.markerSequence.add(new JFIFMarkerSegment(var3.getFirstChild()));
            }
         }

         var3 = this.isStream ? var1 : var1.getLastChild();
         this.setFromMarkerSequenceNode(var3);
      }
   }

   void setFromMarkerSequenceNode(Node var1) throws IIOInvalidTreeException {
      NodeList var2 = var1.getChildNodes();

      for(int var3 = 0; var3 < var2.getLength(); ++var3) {
         Node var4 = var2.item(var3);
         String var5 = var4.getNodeName();
         if (var5.equals("dqt")) {
            this.markerSequence.add(new DQTMarkerSegment(var4));
         } else if (var5.equals("dht")) {
            this.markerSequence.add(new DHTMarkerSegment(var4));
         } else if (var5.equals("dri")) {
            this.markerSequence.add(new DRIMarkerSegment(var4));
         } else if (var5.equals("com")) {
            this.markerSequence.add(new COMMarkerSegment(var4));
         } else if (var5.equals("app14Adobe")) {
            this.markerSequence.add(new AdobeMarkerSegment(var4));
         } else if (var5.equals("unknown")) {
            this.markerSequence.add(new MarkerSegment(var4));
         } else if (var5.equals("sof")) {
            this.markerSequence.add(new SOFMarkerSegment(var4));
         } else {
            if (!var5.equals("sos")) {
               throw new IIOInvalidTreeException("Invalid " + (this.isStream ? "stream " : "image ") + "child: " + var5, var4);
            }

            this.markerSequence.add(new SOSMarkerSegment(var4));
         }
      }

   }

   private boolean isConsistent() {
      SOFMarkerSegment var1 = (SOFMarkerSegment)this.findMarkerSegment(SOFMarkerSegment.class, true);
      JFIFMarkerSegment var2 = (JFIFMarkerSegment)this.findMarkerSegment(JFIFMarkerSegment.class, true);
      AdobeMarkerSegment var3 = (AdobeMarkerSegment)this.findMarkerSegment(AdobeMarkerSegment.class, true);
      boolean var4 = true;
      if (!this.isStream) {
         if (var1 != null) {
            int var5 = var1.componentSpecs.length;
            int var6 = this.countScanBands();
            if (var6 != 0 && var6 != var5) {
               var4 = false;
            }

            if (var2 != null) {
               if (var5 != 1 && var5 != 3) {
                  var4 = false;
               }

               for(int var7 = 0; var7 < var5; ++var7) {
                  if (var1.componentSpecs[var7].componentId != var7 + 1) {
                     var4 = false;
                  }
               }

               if (var3 != null && (var5 == 1 && var3.transform != 0 || var5 == 3 && var3.transform != 1)) {
                  var4 = false;
               }
            }
         } else {
            SOSMarkerSegment var8 = (SOSMarkerSegment)this.findMarkerSegment(SOSMarkerSegment.class, true);
            if (var2 != null || var3 != null || var1 != null || var8 != null) {
               var4 = false;
            }
         }
      }

      return var4;
   }

   private int countScanBands() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.markerSequence.iterator();

      while(true) {
         MarkerSegment var3;
         do {
            if (!var2.hasNext()) {
               return var1.size();
            }

            var3 = (MarkerSegment)var2.next();
         } while(!(var3 instanceof SOSMarkerSegment));

         SOSMarkerSegment var4 = (SOSMarkerSegment)var3;
         SOSMarkerSegment.ScanComponentSpec[] var5 = var4.componentSpecs;

         for(int var6 = 0; var6 < var5.length; ++var6) {
            Integer var7 = new Integer(var5[var6].componentSelector);
            if (!var1.contains(var7)) {
               var1.add(var7);
            }
         }
      }
   }

   void writeToStream(ImageOutputStream var1, boolean var2, boolean var3, List var4, ICC_Profile var5, boolean var6, int var7, JPEGImageWriter var8) throws IOException {
      if (var3) {
         JFIFMarkerSegment.writeDefaultJFIF(var1, var4, var5, var8);
         if (!var6 && var7 != -1 && var7 != 0 && var7 != 1) {
            var6 = true;
            var8.warningOccurred(13);
         }
      }

      Iterator var9 = this.markerSequence.iterator();

      while(true) {
         while(var9.hasNext()) {
            MarkerSegment var10 = (MarkerSegment)var9.next();
            if (var10 instanceof JFIFMarkerSegment) {
               if (!var2) {
                  JFIFMarkerSegment var12 = (JFIFMarkerSegment)var10;
                  var12.writeWithThumbs(var1, var4, var8);
                  if (var5 != null) {
                     JFIFMarkerSegment.writeICC(var5, var1);
                  }
               }
            } else if (var10 instanceof AdobeMarkerSegment) {
               if (!var6) {
                  AdobeMarkerSegment var11;
                  if (var7 != -1) {
                     var11 = (AdobeMarkerSegment)var10.clone();
                     var11.transform = var7;
                     var11.write(var1);
                  } else if (var3) {
                     var11 = (AdobeMarkerSegment)var10;
                     if (var11.transform != 0 && var11.transform != 1) {
                        var8.warningOccurred(13);
                     } else {
                        var11.write(var1);
                     }
                  } else {
                     var10.write(var1);
                  }
               }
            } else {
               var10.write(var1);
            }
         }

         return;
      }
   }

   public void reset() {
      if (this.resetSequence != null) {
         this.markerSequence = this.resetSequence;
         this.resetSequence = null;
      }

   }

   public void print() {
      for(int var1 = 0; var1 < this.markerSequence.size(); ++var1) {
         MarkerSegment var2 = (MarkerSegment)this.markerSequence.get(var1);
         var2.print();
      }

   }
}
