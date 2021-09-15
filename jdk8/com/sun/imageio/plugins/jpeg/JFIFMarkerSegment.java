package com.sun.imageio.plugins.jpeg;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class JFIFMarkerSegment extends MarkerSegment {
   int majorVersion;
   int minorVersion;
   int resUnits;
   int Xdensity;
   int Ydensity;
   int thumbWidth;
   int thumbHeight;
   JFIFMarkerSegment.JFIFThumbRGB thumb;
   ArrayList extSegments;
   JFIFMarkerSegment.ICCMarkerSegment iccSegment;
   private static final int THUMB_JPEG = 16;
   private static final int THUMB_PALETTE = 17;
   private static final int THUMB_UNASSIGNED = 18;
   private static final int THUMB_RGB = 19;
   private static final int DATA_SIZE = 14;
   private static final int ID_SIZE = 5;
   private final int MAX_THUMB_WIDTH;
   private final int MAX_THUMB_HEIGHT;
   private final boolean debug;
   private boolean inICC;
   private JFIFMarkerSegment.ICCMarkerSegment tempICCSegment;

   JFIFMarkerSegment() {
      super(224);
      this.thumb = null;
      this.extSegments = new ArrayList();
      this.iccSegment = null;
      this.MAX_THUMB_WIDTH = 255;
      this.MAX_THUMB_HEIGHT = 255;
      this.debug = false;
      this.inICC = false;
      this.tempICCSegment = null;
      this.majorVersion = 1;
      this.minorVersion = 2;
      this.resUnits = 0;
      this.Xdensity = 1;
      this.Ydensity = 1;
      this.thumbWidth = 0;
      this.thumbHeight = 0;
   }

   JFIFMarkerSegment(JPEGBuffer var1) throws IOException {
      super(var1);
      this.thumb = null;
      this.extSegments = new ArrayList();
      this.iccSegment = null;
      this.MAX_THUMB_WIDTH = 255;
      this.MAX_THUMB_HEIGHT = 255;
      this.debug = false;
      this.inICC = false;
      this.tempICCSegment = null;
      var1.bufPtr += 5;
      this.majorVersion = var1.buf[var1.bufPtr++];
      this.minorVersion = var1.buf[var1.bufPtr++];
      this.resUnits = var1.buf[var1.bufPtr++];
      this.Xdensity = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.Xdensity |= var1.buf[var1.bufPtr++] & 255;
      this.Ydensity = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.Ydensity |= var1.buf[var1.bufPtr++] & 255;
      this.thumbWidth = var1.buf[var1.bufPtr++] & 255;
      this.thumbHeight = var1.buf[var1.bufPtr++] & 255;
      var1.bufAvail -= 14;
      if (this.thumbWidth > 0) {
         this.thumb = new JFIFMarkerSegment.JFIFThumbRGB(var1, this.thumbWidth, this.thumbHeight);
      }

   }

   JFIFMarkerSegment(Node var1) throws IIOInvalidTreeException {
      this();
      this.updateFromNativeNode(var1, true);
   }

   protected Object clone() {
      JFIFMarkerSegment var1 = (JFIFMarkerSegment)super.clone();
      if (!this.extSegments.isEmpty()) {
         var1.extSegments = new ArrayList();
         Iterator var2 = this.extSegments.iterator();

         while(var2.hasNext()) {
            JFIFMarkerSegment.JFIFExtensionMarkerSegment var3 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)var2.next();
            var1.extSegments.add(var3.clone());
         }
      }

      if (this.iccSegment != null) {
         var1.iccSegment = (JFIFMarkerSegment.ICCMarkerSegment)this.iccSegment.clone();
      }

      return var1;
   }

   void addJFXX(JPEGBuffer var1, JPEGImageReader var2) throws IOException {
      this.extSegments.add(new JFIFMarkerSegment.JFIFExtensionMarkerSegment(var1, var2));
   }

   void addICC(JPEGBuffer var1) throws IOException {
      if (!this.inICC) {
         if (this.iccSegment != null) {
            throw new IIOException("> 1 ICC APP2 Marker Segment not supported");
         }

         this.tempICCSegment = new JFIFMarkerSegment.ICCMarkerSegment(var1);
         if (!this.inICC) {
            this.iccSegment = this.tempICCSegment;
            this.tempICCSegment = null;
         }
      } else if (this.tempICCSegment.addData(var1)) {
         this.iccSegment = this.tempICCSegment;
         this.tempICCSegment = null;
      }

   }

   void addICC(ICC_ColorSpace var1) throws IOException {
      if (this.iccSegment != null) {
         throw new IIOException("> 1 ICC APP2 Marker Segment not supported");
      } else {
         this.iccSegment = new JFIFMarkerSegment.ICCMarkerSegment(var1);
      }
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("app0JFIF");
      var1.setAttribute("majorVersion", Integer.toString(this.majorVersion));
      var1.setAttribute("minorVersion", Integer.toString(this.minorVersion));
      var1.setAttribute("resUnits", Integer.toString(this.resUnits));
      var1.setAttribute("Xdensity", Integer.toString(this.Xdensity));
      var1.setAttribute("Ydensity", Integer.toString(this.Ydensity));
      var1.setAttribute("thumbWidth", Integer.toString(this.thumbWidth));
      var1.setAttribute("thumbHeight", Integer.toString(this.thumbHeight));
      if (!this.extSegments.isEmpty()) {
         IIOMetadataNode var2 = new IIOMetadataNode("JFXX");
         var1.appendChild(var2);
         Iterator var3 = this.extSegments.iterator();

         while(var3.hasNext()) {
            JFIFMarkerSegment.JFIFExtensionMarkerSegment var4 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)var3.next();
            var2.appendChild(var4.getNativeNode());
         }
      }

      if (this.iccSegment != null) {
         var1.appendChild(this.iccSegment.getNativeNode());
      }

      return var1;
   }

   void updateFromNativeNode(Node var1, boolean var2) throws IIOInvalidTreeException {
      NamedNodeMap var3 = var1.getAttributes();
      if (var3.getLength() > 0) {
         int var4 = getAttributeValue(var1, var3, "majorVersion", 0, 255, false);
         this.majorVersion = var4 != -1 ? var4 : this.majorVersion;
         var4 = getAttributeValue(var1, var3, "minorVersion", 0, 255, false);
         this.minorVersion = var4 != -1 ? var4 : this.minorVersion;
         var4 = getAttributeValue(var1, var3, "resUnits", 0, 2, false);
         this.resUnits = var4 != -1 ? var4 : this.resUnits;
         var4 = getAttributeValue(var1, var3, "Xdensity", 1, 65535, false);
         this.Xdensity = var4 != -1 ? var4 : this.Xdensity;
         var4 = getAttributeValue(var1, var3, "Ydensity", 1, 65535, false);
         this.Ydensity = var4 != -1 ? var4 : this.Ydensity;
         var4 = getAttributeValue(var1, var3, "thumbWidth", 0, 255, false);
         this.thumbWidth = var4 != -1 ? var4 : this.thumbWidth;
         var4 = getAttributeValue(var1, var3, "thumbHeight", 0, 255, false);
         this.thumbHeight = var4 != -1 ? var4 : this.thumbHeight;
      }

      if (var1.hasChildNodes()) {
         NodeList var13 = var1.getChildNodes();
         int var5 = var13.getLength();
         if (var5 > 2) {
            throw new IIOInvalidTreeException("app0JFIF node cannot have > 2 children", var1);
         }

         for(int var6 = 0; var6 < var5; ++var6) {
            Node var7 = var13.item(var6);
            String var8 = var7.getNodeName();
            if (var8.equals("JFXX")) {
               if (!this.extSegments.isEmpty() && var2) {
                  throw new IIOInvalidTreeException("app0JFIF node cannot have > 1 JFXX node", var1);
               }

               NodeList var9 = var7.getChildNodes();
               int var10 = var9.getLength();

               for(int var11 = 0; var11 < var10; ++var11) {
                  Node var12 = var9.item(var11);
                  this.extSegments.add(new JFIFMarkerSegment.JFIFExtensionMarkerSegment(var12));
               }
            }

            if (var8.equals("app2ICC")) {
               if (this.iccSegment != null && var2) {
                  throw new IIOInvalidTreeException("> 1 ICC APP2 Marker Segment not supported", var1);
               }

               this.iccSegment = new JFIFMarkerSegment.ICCMarkerSegment(var7);
            }
         }
      }

   }

   int getThumbnailWidth(int var1) {
      if (this.thumb != null) {
         if (var1 == 0) {
            return this.thumb.getWidth();
         }

         --var1;
      }

      JFIFMarkerSegment.JFIFExtensionMarkerSegment var2 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)this.extSegments.get(var1);
      return var2.thumb.getWidth();
   }

   int getThumbnailHeight(int var1) {
      if (this.thumb != null) {
         if (var1 == 0) {
            return this.thumb.getHeight();
         }

         --var1;
      }

      JFIFMarkerSegment.JFIFExtensionMarkerSegment var2 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)this.extSegments.get(var1);
      return var2.thumb.getHeight();
   }

   BufferedImage getThumbnail(ImageInputStream var1, int var2, JPEGImageReader var3) throws IOException {
      var3.thumbnailStarted(var2);
      BufferedImage var4 = null;
      if (this.thumb != null && var2 == 0) {
         var4 = this.thumb.getThumbnail(var1, var3);
      } else {
         if (this.thumb != null) {
            --var2;
         }

         JFIFMarkerSegment.JFIFExtensionMarkerSegment var5 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)this.extSegments.get(var2);
         var4 = var5.thumb.getThumbnail(var1, var3);
      }

      var3.thumbnailComplete();
      return var4;
   }

   void write(ImageOutputStream var1, JPEGImageWriter var2) throws IOException {
      this.write(var1, (BufferedImage)null, var2);
   }

   void write(ImageOutputStream var1, BufferedImage var2, JPEGImageWriter var3) throws IOException {
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int[] var7 = null;
      if (var2 != null) {
         var4 = var2.getWidth();
         var5 = var2.getHeight();
         if (var4 > 255 || var5 > 255) {
            var3.warningOccurred(12);
         }

         var4 = Math.min(var4, 255);
         var5 = Math.min(var5, 255);
         var7 = var2.getRaster().getPixels(0, 0, var4, var5, (int[])null);
         var6 = var7.length;
      }

      this.length = 16 + var6;
      this.writeTag(var1);
      byte[] var8 = new byte[]{74, 70, 73, 70, 0};
      var1.write(var8);
      var1.write(this.majorVersion);
      var1.write(this.minorVersion);
      var1.write(this.resUnits);
      write2bytes(var1, this.Xdensity);
      write2bytes(var1, this.Ydensity);
      var1.write(var4);
      var1.write(var5);
      if (var7 != null) {
         var3.thumbnailStarted(0);
         this.writeThumbnailData(var1, var7, var3);
         var3.thumbnailComplete();
      }

   }

   void writeThumbnailData(ImageOutputStream var1, int[] var2, JPEGImageWriter var3) throws IOException {
      int var4 = var2.length / 20;
      if (var4 == 0) {
         var4 = 1;
      }

      for(int var5 = 0; var5 < var2.length; ++var5) {
         var1.write(var2[var5]);
         if (var5 > var4 && var5 % var4 == 0) {
            var3.thumbnailProgress((float)var5 * 100.0F / (float)var2.length);
         }
      }

   }

   void writeWithThumbs(ImageOutputStream var1, List var2, JPEGImageWriter var3) throws IOException {
      if (var2 != null) {
         JFIFMarkerSegment.JFIFExtensionMarkerSegment var4 = null;
         if (var2.size() == 1) {
            if (!this.extSegments.isEmpty()) {
               var4 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)this.extSegments.get(0);
            }

            this.writeThumb(var1, (BufferedImage)var2.get(0), var4, 0, true, var3);
         } else {
            this.write(var1, var3);

            for(int var5 = 0; var5 < var2.size(); ++var5) {
               var4 = null;
               if (var5 < this.extSegments.size()) {
                  var4 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)this.extSegments.get(var5);
               }

               this.writeThumb(var1, (BufferedImage)var2.get(var5), var4, var5, false, var3);
            }
         }
      } else {
         this.write(var1, var3);
      }

   }

   private void writeThumb(ImageOutputStream var1, BufferedImage var2, JFIFMarkerSegment.JFIFExtensionMarkerSegment var3, int var4, boolean var5, JPEGImageWriter var6) throws IOException {
      ColorModel var7 = var2.getColorModel();
      ColorSpace var8 = var7.getColorSpace();
      BufferedImage var9;
      if (var7 instanceof IndexColorModel) {
         if (var5) {
            this.write(var1, var6);
         }

         if (var3 != null && var3.code != 17) {
            var9 = ((IndexColorModel)var7).convertToIntDiscrete(var2.getRaster(), false);
            var3.setThumbnail(var9);
            var6.thumbnailStarted(var4);
            var3.write(var1, var6);
            var6.thumbnailComplete();
         } else {
            this.writeJFXXSegment(var4, var2, var1, var6);
         }
      } else if (var8.getType() == 5) {
         if (var3 == null) {
            if (var5) {
               this.write(var1, var2, var6);
            } else {
               this.writeJFXXSegment(var4, var2, var1, var6);
            }
         } else {
            if (var5) {
               this.write(var1, var6);
            }

            if (var3.code == 17) {
               this.writeJFXXSegment(var4, var2, var1, var6);
               var6.warningOccurred(14);
            } else {
               var3.setThumbnail(var2);
               var6.thumbnailStarted(var4);
               var3.write(var1, var6);
               var6.thumbnailComplete();
            }
         }
      } else if (var8.getType() == 6) {
         if (var3 == null) {
            if (var5) {
               var9 = expandGrayThumb(var2);
               this.write(var1, var9, var6);
            } else {
               this.writeJFXXSegment(var4, var2, var1, var6);
            }
         } else {
            if (var5) {
               this.write(var1, var6);
            }

            if (var3.code == 19) {
               var9 = expandGrayThumb(var2);
               this.writeJFXXSegment(var4, var9, var1, var6);
            } else if (var3.code == 16) {
               var3.setThumbnail(var2);
               var6.thumbnailStarted(var4);
               var3.write(var1, var6);
               var6.thumbnailComplete();
            } else if (var3.code == 17) {
               this.writeJFXXSegment(var4, var2, var1, var6);
               var6.warningOccurred(15);
            }
         }
      } else {
         var6.warningOccurred(9);
      }

   }

   private void writeJFXXSegment(int var1, BufferedImage var2, ImageOutputStream var3, JPEGImageWriter var4) throws IOException {
      JFIFMarkerSegment.JFIFExtensionMarkerSegment var5 = null;

      try {
         var5 = new JFIFMarkerSegment.JFIFExtensionMarkerSegment(var2);
      } catch (JFIFMarkerSegment.IllegalThumbException var7) {
         var4.warningOccurred(9);
         return;
      }

      var4.thumbnailStarted(var1);
      var5.write(var3, var4);
      var4.thumbnailComplete();
   }

   private static BufferedImage expandGrayThumb(BufferedImage var0) {
      BufferedImage var1 = new BufferedImage(var0.getWidth(), var0.getHeight(), 1);
      Graphics var2 = var1.getGraphics();
      var2.drawImage(var0, 0, 0, (ImageObserver)null);
      return var1;
   }

   static void writeDefaultJFIF(ImageOutputStream var0, List var1, ICC_Profile var2, JPEGImageWriter var3) throws IOException {
      JFIFMarkerSegment var4 = new JFIFMarkerSegment();
      var4.writeWithThumbs(var0, var1, var3);
      if (var2 != null) {
         writeICC(var2, var0);
      }

   }

   void print() {
      this.printTag("JFIF");
      System.out.print("Version ");
      System.out.print(this.majorVersion);
      System.out.println(".0" + Integer.toString(this.minorVersion));
      System.out.print("Resolution units: ");
      System.out.println(this.resUnits);
      System.out.print("X density: ");
      System.out.println(this.Xdensity);
      System.out.print("Y density: ");
      System.out.println(this.Ydensity);
      System.out.print("Thumbnail Width: ");
      System.out.println(this.thumbWidth);
      System.out.print("Thumbnail Height: ");
      System.out.println(this.thumbHeight);
      if (!this.extSegments.isEmpty()) {
         Iterator var1 = this.extSegments.iterator();

         while(var1.hasNext()) {
            JFIFMarkerSegment.JFIFExtensionMarkerSegment var2 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)var1.next();
            var2.print();
         }
      }

      if (this.iccSegment != null) {
         this.iccSegment.print();
      }

   }

   static void writeICC(ICC_Profile var0, ImageOutputStream var1) throws IOException {
      byte var2 = 2;
      int var4 = "ICC_PROFILE".length() + 1;
      byte var5 = 2;
      int var6 = '\uffff' - var2 - var4 - var5;
      byte[] var7 = var0.getData();
      int var8 = var7.length / var6;
      if (var7.length % var6 != 0) {
         ++var8;
      }

      int var9 = 1;
      int var10 = 0;

      for(int var11 = 0; var11 < var8; ++var11) {
         int var12 = Math.min(var7.length - var10, var6);
         int var13 = var12 + var5 + var4 + var2;
         var1.write(255);
         var1.write(226);
         MarkerSegment.write2bytes(var1, var13);
         byte[] var14 = "ICC_PROFILE".getBytes("US-ASCII");
         var1.write(var14);
         var1.write(0);
         var1.write(var9++);
         var1.write(var8);
         var1.write(var7, var10, var12);
         var10 += var12;
      }

   }

   class ICCMarkerSegment extends MarkerSegment {
      ArrayList chunks = null;
      byte[] profile = null;
      private static final int ID_SIZE = 12;
      int chunksRead;
      int numChunks;

      ICCMarkerSegment(ICC_ColorSpace var2) {
         super(226);
         this.chunks = null;
         this.chunksRead = 0;
         this.numChunks = 0;
         this.profile = var2.getProfile().getData();
      }

      ICCMarkerSegment(JPEGBuffer var2) throws IOException {
         super(var2);
         var2.bufPtr += 12;
         var2.bufAvail -= 12;
         this.length -= 12;
         int var3 = var2.buf[var2.bufPtr] & 255;
         this.numChunks = var2.buf[var2.bufPtr + 1] & 255;
         if (var3 > this.numChunks) {
            throw new IIOException("Image format Error; chunk num > num chunks");
         } else {
            if (this.numChunks == 1) {
               this.length -= 2;
               this.profile = new byte[this.length];
               var2.bufPtr += 2;
               var2.bufAvail -= 2;
               var2.readData(this.profile);
               JFIFMarkerSegment.this.inICC = false;
            } else {
               byte[] var4 = new byte[this.length];
               this.length -= 2;
               var2.readData(var4);
               this.chunks = new ArrayList();
               this.chunks.add(var4);
               this.chunksRead = 1;
               JFIFMarkerSegment.this.inICC = true;
            }

         }
      }

      ICCMarkerSegment(Node var2) throws IIOInvalidTreeException {
         super(226);
         if (var2 instanceof IIOMetadataNode) {
            IIOMetadataNode var3 = (IIOMetadataNode)var2;
            ICC_Profile var4 = (ICC_Profile)var3.getUserObject();
            if (var4 != null) {
               this.profile = var4.getData();
            }
         }

      }

      protected Object clone() {
         JFIFMarkerSegment.ICCMarkerSegment var1 = (JFIFMarkerSegment.ICCMarkerSegment)super.clone();
         if (this.profile != null) {
            var1.profile = (byte[])((byte[])this.profile.clone());
         }

         return var1;
      }

      boolean addData(JPEGBuffer var1) throws IOException {
         ++var1.bufPtr;
         --var1.bufAvail;
         int var2 = (var1.buf[var1.bufPtr++] & 255) << 8;
         var2 |= var1.buf[var1.bufPtr++] & 255;
         var1.bufAvail -= 2;
         var2 -= 2;
         var1.bufPtr += 12;
         var1.bufAvail -= 12;
         var2 -= 12;
         int var3 = var1.buf[var1.bufPtr] & 255;
         if (var3 > this.numChunks) {
            throw new IIOException("Image format Error; chunk num > num chunks");
         } else {
            int var4 = var1.buf[var1.bufPtr + 1] & 255;
            if (this.numChunks != var4) {
               throw new IIOException("Image format Error; icc num chunks mismatch");
            } else {
               var2 -= 2;
               boolean var5 = false;
               byte[] var6 = new byte[var2];
               var1.readData(var6);
               this.chunks.add(var6);
               this.length += var2;
               ++this.chunksRead;
               if (this.chunksRead < this.numChunks) {
                  JFIFMarkerSegment.this.inICC = true;
               } else {
                  this.profile = new byte[this.length];
                  int var7 = 0;

                  for(int var8 = 1; var8 <= this.numChunks; ++var8) {
                     boolean var9 = false;

                     for(int var10 = 0; var10 < this.chunks.size(); ++var10) {
                        byte[] var11 = (byte[])((byte[])this.chunks.get(var10));
                        if (var11[0] == var8) {
                           System.arraycopy(var11, 2, this.profile, var7, var11.length - 2);
                           var7 += var11.length - 2;
                           var9 = true;
                        }
                     }

                     if (!var9) {
                        throw new IIOException("Image Format Error: Missing ICC chunk num " + var8);
                     }
                  }

                  this.chunks = null;
                  this.chunksRead = 0;
                  this.numChunks = 0;
                  JFIFMarkerSegment.this.inICC = false;
                  var5 = true;
               }

               return var5;
            }
         }
      }

      IIOMetadataNode getNativeNode() {
         IIOMetadataNode var1 = new IIOMetadataNode("app2ICC");
         if (this.profile != null) {
            var1.setUserObject(ICC_Profile.getInstance(this.profile));
         }

         return var1;
      }

      void write(ImageOutputStream var1) throws IOException {
      }

      void print() {
         this.printTag("ICC Profile APP2");
      }
   }

   class JFIFThumbJPEG extends JFIFMarkerSegment.JFIFThumb {
      JPEGMetadata thumbMetadata = null;
      byte[] data = null;
      private static final int PREAMBLE_SIZE = 6;

      JFIFThumbJPEG(JPEGBuffer var2, int var3, JPEGImageReader var4) throws IOException {
         super(var2);
         long var5 = this.streamPos + (long)(var3 - 6);
         var2.iis.seek(this.streamPos);
         this.thumbMetadata = new JPEGMetadata(false, true, var2.iis, var4);
         var2.iis.seek(var5);
         var2.bufAvail = 0;
         var2.bufPtr = 0;
      }

      JFIFThumbJPEG(Node var2) throws IIOInvalidTreeException {
         super();
         if (var2.getChildNodes().getLength() > 1) {
            throw new IIOInvalidTreeException("JFIFThumbJPEG node must have 0 or 1 child", var2);
         } else {
            Node var3 = var2.getFirstChild();
            if (var3 != null) {
               String var4 = var3.getNodeName();
               if (!var4.equals("markerSequence")) {
                  throw new IIOInvalidTreeException("JFIFThumbJPEG child must be a markerSequence node", var2);
               }

               this.thumbMetadata = new JPEGMetadata(false, true);
               this.thumbMetadata.setFromMarkerSequenceNode(var3);
            }

         }
      }

      JFIFThumbJPEG(BufferedImage var2) throws JFIFMarkerSegment.IllegalThumbException {
         super();
         short var3 = 4096;
         char var4 = '\ufff7';

         try {
            ByteArrayOutputStream var5 = new ByteArrayOutputStream(var3);
            MemoryCacheImageOutputStream var6 = new MemoryCacheImageOutputStream(var5);
            JPEGImageWriter var7 = new JPEGImageWriter((ImageWriterSpi)null);
            var7.setOutput(var6);
            JPEGMetadata var8 = (JPEGMetadata)var7.getDefaultImageMetadata(new ImageTypeSpecifier(var2), (ImageWriteParam)null);
            MarkerSegment var9 = var8.findMarkerSegment(JFIFMarkerSegment.class, true);
            if (var9 == null) {
               throw JFIFMarkerSegment.this.new IllegalThumbException();
            } else {
               var8.markerSequence.remove(var9);
               var7.write(new IIOImage(var2, (List)null, var8));
               var7.dispose();
               if (var5.size() > var4) {
                  throw JFIFMarkerSegment.this.new IllegalThumbException();
               } else {
                  this.data = var5.toByteArray();
               }
            }
         } catch (IOException var10) {
            throw JFIFMarkerSegment.this.new IllegalThumbException();
         }
      }

      int getWidth() {
         int var1 = 0;
         SOFMarkerSegment var2 = (SOFMarkerSegment)this.thumbMetadata.findMarkerSegment(SOFMarkerSegment.class, true);
         if (var2 != null) {
            var1 = var2.samplesPerLine;
         }

         return var1;
      }

      int getHeight() {
         int var1 = 0;
         SOFMarkerSegment var2 = (SOFMarkerSegment)this.thumbMetadata.findMarkerSegment(SOFMarkerSegment.class, true);
         if (var2 != null) {
            var1 = var2.numLines;
         }

         return var1;
      }

      BufferedImage getThumbnail(ImageInputStream var1, JPEGImageReader var2) throws IOException {
         var1.mark();
         var1.seek(this.streamPos);
         JPEGImageReader var3 = new JPEGImageReader((ImageReaderSpi)null);
         var3.setInput(var1);
         var3.addIIOReadProgressListener(new JFIFMarkerSegment.JFIFThumbJPEG.ThumbnailReadListener(var2));
         BufferedImage var4 = var3.read(0, (ImageReadParam)null);
         var3.dispose();
         var1.reset();
         return var4;
      }

      protected Object clone() {
         JFIFMarkerSegment.JFIFThumbJPEG var1 = (JFIFMarkerSegment.JFIFThumbJPEG)super.clone();
         if (this.thumbMetadata != null) {
            var1.thumbMetadata = (JPEGMetadata)this.thumbMetadata.clone();
         }

         return var1;
      }

      IIOMetadataNode getNativeNode() {
         IIOMetadataNode var1 = new IIOMetadataNode("JFIFthumbJPEG");
         if (this.thumbMetadata != null) {
            var1.appendChild(this.thumbMetadata.getNativeTree());
         }

         return var1;
      }

      int getLength() {
         return this.data == null ? 0 : this.data.length;
      }

      void write(ImageOutputStream var1, JPEGImageWriter var2) throws IOException {
         int var3 = this.data.length / 20;
         if (var3 == 0) {
            var3 = 1;
         }

         float var6;
         for(int var4 = 0; var4 < this.data.length; var2.thumbnailProgress(var6)) {
            int var5 = Math.min(var3, this.data.length - var4);
            var1.write(this.data, var4, var5);
            var4 += var3;
            var6 = (float)var4 * 100.0F / (float)this.data.length;
            if (var6 > 100.0F) {
               var6 = 100.0F;
            }
         }

      }

      void print() {
         System.out.println("JFIF thumbnail stored as JPEG");
      }

      private class ThumbnailReadListener implements IIOReadProgressListener {
         JPEGImageReader reader = null;

         ThumbnailReadListener(JPEGImageReader var2) {
            this.reader = var2;
         }

         public void sequenceStarted(ImageReader var1, int var2) {
         }

         public void sequenceComplete(ImageReader var1) {
         }

         public void imageStarted(ImageReader var1, int var2) {
         }

         public void imageProgress(ImageReader var1, float var2) {
            this.reader.thumbnailProgress(var2);
         }

         public void imageComplete(ImageReader var1) {
         }

         public void thumbnailStarted(ImageReader var1, int var2, int var3) {
         }

         public void thumbnailProgress(ImageReader var1, float var2) {
         }

         public void thumbnailComplete(ImageReader var1) {
         }

         public void readAborted(ImageReader var1) {
         }
      }
   }

   class JFIFThumbPalette extends JFIFMarkerSegment.JFIFThumbUncompressed {
      private static final int PALETTE_SIZE = 768;

      JFIFThumbPalette(JPEGBuffer var2, int var3, int var4) throws IOException {
         super(var2, var3, var4, 768 + var3 * var4, "JFIFThumbPalette");
      }

      JFIFThumbPalette(Node var2) throws IIOInvalidTreeException {
         super(var2, "JFIFThumbPalette");
      }

      JFIFThumbPalette(BufferedImage var2) throws JFIFMarkerSegment.IllegalThumbException {
         super(var2);
         IndexColorModel var3 = (IndexColorModel)this.thumbnail.getColorModel();
         if (var3.getMapSize() > 256) {
            throw JFIFMarkerSegment.this.new IllegalThumbException();
         }
      }

      int getLength() {
         return this.thumbWidth * this.thumbHeight + 768;
      }

      BufferedImage getThumbnail(ImageInputStream var1, JPEGImageReader var2) throws IOException {
         var1.mark();
         var1.seek(this.streamPos);
         byte[] var3 = new byte[768];
         float var4 = 768.0F / (float)this.getLength();
         this.readByteBuffer(var1, var3, var2, var4, 0.0F);
         DataBufferByte var5 = new DataBufferByte(this.thumbWidth * this.thumbHeight);
         this.readByteBuffer(var1, var5.getData(), var2, 1.0F - var4, var4);
         var1.read();
         var1.reset();
         IndexColorModel var6 = new IndexColorModel(8, 256, var3, 0, false);
         SampleModel var7 = var6.createCompatibleSampleModel(this.thumbWidth, this.thumbHeight);
         WritableRaster var8 = Raster.createWritableRaster(var7, var5, (Point)null);
         return new BufferedImage(var6, var8, false, (Hashtable)null);
      }

      void write(ImageOutputStream var1, JPEGImageWriter var2) throws IOException {
         super.write(var1, var2);
         byte[] var3 = new byte[768];
         IndexColorModel var4 = (IndexColorModel)this.thumbnail.getColorModel();
         byte[] var5 = new byte[256];
         byte[] var6 = new byte[256];
         byte[] var7 = new byte[256];
         var4.getReds(var5);
         var4.getGreens(var6);
         var4.getBlues(var7);

         for(int var8 = 0; var8 < 256; ++var8) {
            var3[var8 * 3] = var5[var8];
            var3[var8 * 3 + 1] = var6[var8];
            var3[var8 * 3 + 2] = var7[var8];
         }

         var1.write(var3);
         this.writePixels(var1, var2);
      }
   }

   class JFIFThumbRGB extends JFIFMarkerSegment.JFIFThumbUncompressed {
      JFIFThumbRGB(JPEGBuffer var2, int var3, int var4) throws IOException {
         super(var2, var3, var4, var3 * var4 * 3, "JFIFthumbRGB");
      }

      JFIFThumbRGB(Node var2) throws IIOInvalidTreeException {
         super(var2, "JFIFthumbRGB");
      }

      JFIFThumbRGB(BufferedImage var2) throws JFIFMarkerSegment.IllegalThumbException {
         super(var2);
      }

      int getLength() {
         return this.thumbWidth * this.thumbHeight * 3;
      }

      BufferedImage getThumbnail(ImageInputStream var1, JPEGImageReader var2) throws IOException {
         var1.mark();
         var1.seek(this.streamPos);
         DataBufferByte var3 = new DataBufferByte(this.getLength());
         this.readByteBuffer(var1, var3.getData(), var2, 1.0F, 0.0F);
         var1.reset();
         WritableRaster var4 = Raster.createInterleavedRaster(var3, this.thumbWidth, this.thumbHeight, this.thumbWidth * 3, 3, new int[]{0, 1, 2}, (Point)null);
         ComponentColorModel var5 = new ComponentColorModel(JPEG.JCS.sRGB, false, false, 1, 0);
         return new BufferedImage(var5, var4, false, (Hashtable)null);
      }

      void write(ImageOutputStream var1, JPEGImageWriter var2) throws IOException {
         super.write(var1, var2);
         this.writePixels(var1, var2);
      }
   }

   abstract class JFIFThumbUncompressed extends JFIFMarkerSegment.JFIFThumb {
      BufferedImage thumbnail = null;
      int thumbWidth;
      int thumbHeight;
      String name;

      JFIFThumbUncompressed(JPEGBuffer var2, int var3, int var4, int var5, String var6) throws IOException {
         super(var2);
         this.thumbWidth = var3;
         this.thumbHeight = var4;
         var2.skipData(var5);
         this.name = var6;
      }

      JFIFThumbUncompressed(Node var2, String var3) throws IIOInvalidTreeException {
         super();
         this.thumbWidth = 0;
         this.thumbHeight = 0;
         this.name = var3;
         NamedNodeMap var4 = var2.getAttributes();
         int var5 = var4.getLength();
         if (var5 > 2) {
            throw new IIOInvalidTreeException(var3 + " node cannot have > 2 attributes", var2);
         } else {
            if (var5 != 0) {
               int var6 = MarkerSegment.getAttributeValue(var2, var4, "thumbWidth", 0, 255, false);
               this.thumbWidth = var6 != -1 ? var6 : this.thumbWidth;
               var6 = MarkerSegment.getAttributeValue(var2, var4, "thumbHeight", 0, 255, false);
               this.thumbHeight = var6 != -1 ? var6 : this.thumbHeight;
            }

         }
      }

      JFIFThumbUncompressed(BufferedImage var2) {
         super();
         this.thumbnail = var2;
         this.thumbWidth = var2.getWidth();
         this.thumbHeight = var2.getHeight();
         this.name = null;
      }

      void readByteBuffer(ImageInputStream var1, byte[] var2, JPEGImageReader var3, float var4, float var5) throws IOException {
         int var6 = Math.max((int)((float)(var2.length / 20) / var4), 1);

         float var9;
         for(int var7 = 0; var7 < var2.length; var3.thumbnailProgress(var9)) {
            int var8 = Math.min(var6, var2.length - var7);
            var1.read(var2, var7, var8);
            var7 += var6;
            var9 = (float)var7 * 100.0F / (float)var2.length * var4 + var5;
            if (var9 > 100.0F) {
               var9 = 100.0F;
            }
         }

      }

      int getWidth() {
         return this.thumbWidth;
      }

      int getHeight() {
         return this.thumbHeight;
      }

      IIOMetadataNode getNativeNode() {
         IIOMetadataNode var1 = new IIOMetadataNode(this.name);
         var1.setAttribute("thumbWidth", Integer.toString(this.thumbWidth));
         var1.setAttribute("thumbHeight", Integer.toString(this.thumbHeight));
         return var1;
      }

      void write(ImageOutputStream var1, JPEGImageWriter var2) throws IOException {
         if (this.thumbWidth > 255 || this.thumbHeight > 255) {
            var2.warningOccurred(12);
         }

         this.thumbWidth = Math.min(this.thumbWidth, 255);
         this.thumbHeight = Math.min(this.thumbHeight, 255);
         var1.write(this.thumbWidth);
         var1.write(this.thumbHeight);
      }

      void writePixels(ImageOutputStream var1, JPEGImageWriter var2) throws IOException {
         if (this.thumbWidth > 255 || this.thumbHeight > 255) {
            var2.warningOccurred(12);
         }

         this.thumbWidth = Math.min(this.thumbWidth, 255);
         this.thumbHeight = Math.min(this.thumbHeight, 255);
         int[] var3 = this.thumbnail.getRaster().getPixels(0, 0, this.thumbWidth, this.thumbHeight, (int[])null);
         JFIFMarkerSegment.this.writeThumbnailData(var1, var3, var2);
      }

      void print() {
         System.out.print(this.name + " width: ");
         System.out.println(this.thumbWidth);
         System.out.print(this.name + " height: ");
         System.out.println(this.thumbHeight);
      }
   }

   abstract class JFIFThumb implements Cloneable {
      long streamPos = -1L;

      abstract int getLength();

      abstract int getWidth();

      abstract int getHeight();

      abstract BufferedImage getThumbnail(ImageInputStream var1, JPEGImageReader var2) throws IOException;

      protected JFIFThumb() {
      }

      protected JFIFThumb(JPEGBuffer var2) throws IOException {
         this.streamPos = var2.getStreamPosition();
      }

      abstract void print();

      abstract IIOMetadataNode getNativeNode();

      abstract void write(ImageOutputStream var1, JPEGImageWriter var2) throws IOException;

      protected Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            return null;
         }
      }
   }

   class JFIFExtensionMarkerSegment extends MarkerSegment {
      int code;
      JFIFMarkerSegment.JFIFThumb thumb;
      private static final int DATA_SIZE = 6;
      private static final int ID_SIZE = 5;

      JFIFExtensionMarkerSegment(JPEGBuffer var2, JPEGImageReader var3) throws IOException {
         super(var2);
         var2.bufPtr += 5;
         this.code = var2.buf[var2.bufPtr++] & 255;
         var2.bufAvail -= 6;
         if (this.code == 16) {
            this.thumb = JFIFMarkerSegment.this.new JFIFThumbJPEG(var2, this.length, var3);
         } else {
            var2.loadBuf(2);
            int var4 = var2.buf[var2.bufPtr++] & 255;
            int var5 = var2.buf[var2.bufPtr++] & 255;
            var2.bufAvail -= 2;
            if (this.code == 17) {
               this.thumb = JFIFMarkerSegment.this.new JFIFThumbPalette(var2, var4, var5);
            } else {
               this.thumb = JFIFMarkerSegment.this.new JFIFThumbRGB(var2, var4, var5);
            }
         }

      }

      JFIFExtensionMarkerSegment(Node var2) throws IIOInvalidTreeException {
         super(224);
         NamedNodeMap var3 = var2.getAttributes();
         if (var3.getLength() > 0) {
            this.code = getAttributeValue(var2, var3, "extensionCode", 16, 19, false);
            if (this.code == 18) {
               throw new IIOInvalidTreeException("invalid extensionCode attribute value", var2);
            }
         } else {
            this.code = 18;
         }

         if (var2.getChildNodes().getLength() != 1) {
            throw new IIOInvalidTreeException("app0JFXX node must have exactly 1 child", var2);
         } else {
            Node var4 = var2.getFirstChild();
            String var5 = var4.getNodeName();
            if (var5.equals("JFIFthumbJPEG")) {
               if (this.code == 18) {
                  this.code = 16;
               }

               this.thumb = JFIFMarkerSegment.this.new JFIFThumbJPEG(var4);
            } else if (var5.equals("JFIFthumbPalette")) {
               if (this.code == 18) {
                  this.code = 17;
               }

               this.thumb = JFIFMarkerSegment.this.new JFIFThumbPalette(var4);
            } else {
               if (!var5.equals("JFIFthumbRGB")) {
                  throw new IIOInvalidTreeException("unrecognized app0JFXX child node", var2);
               }

               if (this.code == 18) {
                  this.code = 19;
               }

               this.thumb = JFIFMarkerSegment.this.new JFIFThumbRGB(var4);
            }

         }
      }

      JFIFExtensionMarkerSegment(BufferedImage var2) throws JFIFMarkerSegment.IllegalThumbException {
         super(224);
         ColorModel var3 = var2.getColorModel();
         int var4 = var3.getColorSpace().getType();
         if (var3.hasAlpha()) {
            throw JFIFMarkerSegment.this.new IllegalThumbException();
         } else {
            if (var3 instanceof IndexColorModel) {
               this.code = 17;
               this.thumb = JFIFMarkerSegment.this.new JFIFThumbPalette(var2);
            } else if (var4 == 5) {
               this.code = 19;
               this.thumb = JFIFMarkerSegment.this.new JFIFThumbRGB(var2);
            } else {
               if (var4 != 6) {
                  throw JFIFMarkerSegment.this.new IllegalThumbException();
               }

               this.code = 16;
               this.thumb = JFIFMarkerSegment.this.new JFIFThumbJPEG(var2);
            }

         }
      }

      void setThumbnail(BufferedImage var1) {
         try {
            switch(this.code) {
            case 16:
               this.thumb = JFIFMarkerSegment.this.new JFIFThumbJPEG(var1);
               break;
            case 17:
               this.thumb = JFIFMarkerSegment.this.new JFIFThumbPalette(var1);
            case 18:
            default:
               break;
            case 19:
               this.thumb = JFIFMarkerSegment.this.new JFIFThumbRGB(var1);
            }

         } catch (JFIFMarkerSegment.IllegalThumbException var3) {
            throw new InternalError("Illegal thumb in setThumbnail!", var3);
         }
      }

      protected Object clone() {
         JFIFMarkerSegment.JFIFExtensionMarkerSegment var1 = (JFIFMarkerSegment.JFIFExtensionMarkerSegment)super.clone();
         if (this.thumb != null) {
            var1.thumb = (JFIFMarkerSegment.JFIFThumb)this.thumb.clone();
         }

         return var1;
      }

      IIOMetadataNode getNativeNode() {
         IIOMetadataNode var1 = new IIOMetadataNode("app0JFXX");
         var1.setAttribute("extensionCode", Integer.toString(this.code));
         var1.appendChild(this.thumb.getNativeNode());
         return var1;
      }

      void write(ImageOutputStream var1, JPEGImageWriter var2) throws IOException {
         this.length = 8 + this.thumb.getLength();
         this.writeTag(var1);
         byte[] var3 = new byte[]{74, 70, 88, 88, 0};
         var1.write(var3);
         var1.write(this.code);
         this.thumb.write(var1, var2);
      }

      void print() {
         this.printTag("JFXX");
         this.thumb.print();
      }
   }

   private class IllegalThumbException extends Exception {
      private IllegalThumbException() {
      }

      // $FF: synthetic method
      IllegalThumbException(Object var2) {
         this();
      }
   }
}
