package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class AdobeMarkerSegment extends MarkerSegment {
   int version;
   int flags0;
   int flags1;
   int transform;
   private static final int ID_SIZE = 5;

   AdobeMarkerSegment(int var1) {
      super(238);
      this.version = 101;
      this.flags0 = 0;
      this.flags1 = 0;
      this.transform = var1;
   }

   AdobeMarkerSegment(JPEGBuffer var1) throws IOException {
      super(var1);
      var1.bufPtr += 5;
      this.version = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.version |= var1.buf[var1.bufPtr++] & 255;
      this.flags0 = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.flags0 |= var1.buf[var1.bufPtr++] & 255;
      this.flags1 = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.flags1 |= var1.buf[var1.bufPtr++] & 255;
      this.transform = var1.buf[var1.bufPtr++] & 255;
      var1.bufAvail -= this.length;
   }

   AdobeMarkerSegment(Node var1) throws IIOInvalidTreeException {
      this(0);
      this.updateFromNativeNode(var1, true);
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("app14Adobe");
      var1.setAttribute("version", Integer.toString(this.version));
      var1.setAttribute("flags0", Integer.toString(this.flags0));
      var1.setAttribute("flags1", Integer.toString(this.flags1));
      var1.setAttribute("transform", Integer.toString(this.transform));
      return var1;
   }

   void updateFromNativeNode(Node var1, boolean var2) throws IIOInvalidTreeException {
      NamedNodeMap var3 = var1.getAttributes();
      this.transform = getAttributeValue(var1, var3, "transform", 0, 2, true);
      int var4 = var3.getLength();
      if (var4 > 4) {
         throw new IIOInvalidTreeException("Adobe APP14 node cannot have > 4 attributes", var1);
      } else {
         if (var4 > 1) {
            int var5 = getAttributeValue(var1, var3, "version", 100, 255, false);
            this.version = var5 != -1 ? var5 : this.version;
            var5 = getAttributeValue(var1, var3, "flags0", 0, 65535, false);
            this.flags0 = var5 != -1 ? var5 : this.flags0;
            var5 = getAttributeValue(var1, var3, "flags1", 0, 65535, false);
            this.flags1 = var5 != -1 ? var5 : this.flags1;
         }

      }
   }

   void write(ImageOutputStream var1) throws IOException {
      this.length = 14;
      this.writeTag(var1);
      byte[] var2 = new byte[]{65, 100, 111, 98, 101};
      var1.write(var2);
      write2bytes(var1, this.version);
      write2bytes(var1, this.flags0);
      write2bytes(var1, this.flags1);
      var1.write(this.transform);
   }

   static void writeAdobeSegment(ImageOutputStream var0, int var1) throws IOException {
      (new AdobeMarkerSegment(var1)).write(var0);
   }

   void print() {
      this.printTag("Adobe APP14");
      System.out.print("Version: ");
      System.out.println(this.version);
      System.out.print("Flags0: 0x");
      System.out.println(Integer.toHexString(this.flags0));
      System.out.print("Flags1: 0x");
      System.out.println(Integer.toHexString(this.flags1));
      System.out.print("Transform: ");
      System.out.println(this.transform);
   }
}
