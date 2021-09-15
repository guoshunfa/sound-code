package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class MarkerSegment implements Cloneable {
   protected static final int LENGTH_SIZE = 2;
   int tag;
   int length;
   byte[] data = null;
   boolean unknown = false;

   MarkerSegment(JPEGBuffer var1) throws IOException {
      var1.loadBuf(3);
      this.tag = var1.buf[var1.bufPtr++] & 255;
      this.length = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.length |= var1.buf[var1.bufPtr++] & 255;
      this.length -= 2;
      if (this.length < 0) {
         throw new IIOException("Invalid segment length: " + this.length);
      } else {
         var1.bufAvail -= 3;
         var1.loadBuf(this.length);
      }
   }

   MarkerSegment(int var1) {
      this.tag = var1;
      this.length = 0;
   }

   MarkerSegment(Node var1) throws IIOInvalidTreeException {
      this.tag = getAttributeValue(var1, (NamedNodeMap)null, "MarkerTag", 0, 255, true);
      this.length = 0;
      if (var1 instanceof IIOMetadataNode) {
         IIOMetadataNode var2 = (IIOMetadataNode)var1;

         try {
            this.data = (byte[])((byte[])var2.getUserObject());
         } catch (Exception var5) {
            IIOInvalidTreeException var4 = new IIOInvalidTreeException("Can't get User Object", var1);
            var4.initCause(var5);
            throw var4;
         }
      } else {
         throw new IIOInvalidTreeException("Node must have User Object", var1);
      }
   }

   protected Object clone() {
      MarkerSegment var1 = null;

      try {
         var1 = (MarkerSegment)super.clone();
      } catch (CloneNotSupportedException var3) {
      }

      if (this.data != null) {
         var1.data = (byte[])((byte[])this.data.clone());
      }

      return var1;
   }

   void loadData(JPEGBuffer var1) throws IOException {
      this.data = new byte[this.length];
      var1.readData(this.data);
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("unknown");
      var1.setAttribute("MarkerTag", Integer.toString(this.tag));
      var1.setUserObject(this.data);
      return var1;
   }

   static int getAttributeValue(Node var0, NamedNodeMap var1, String var2, int var3, int var4, boolean var5) throws IIOInvalidTreeException {
      if (var1 == null) {
         var1 = var0.getAttributes();
      }

      String var6 = var1.getNamedItem(var2).getNodeValue();
      int var7 = -1;
      if (var6 == null) {
         if (var5) {
            throw new IIOInvalidTreeException(var2 + " attribute not found", var0);
         }
      } else {
         var7 = Integer.parseInt(var6);
         if (var7 < var3 || var7 > var4) {
            throw new IIOInvalidTreeException(var2 + " attribute out of range", var0);
         }
      }

      return var7;
   }

   void writeTag(ImageOutputStream var1) throws IOException {
      var1.write(255);
      var1.write(this.tag);
      write2bytes(var1, this.length);
   }

   void write(ImageOutputStream var1) throws IOException {
      this.length = 2 + (this.data != null ? this.data.length : 0);
      this.writeTag(var1);
      if (this.data != null) {
         var1.write(this.data);
      }

   }

   static void write2bytes(ImageOutputStream var0, int var1) throws IOException {
      var0.write(var1 >> 8 & 255);
      var0.write(var1 & 255);
   }

   void printTag(String var1) {
      System.out.println(var1 + " marker segment - marker = 0x" + Integer.toHexString(this.tag));
      System.out.println("length: " + this.length);
   }

   void print() {
      this.printTag("Unknown");
      int var1;
      if (this.length > 10) {
         System.out.print("First 5 bytes:");

         for(var1 = 0; var1 < 5; ++var1) {
            System.out.print(" Ox" + Integer.toHexString(this.data[var1]));
         }

         System.out.print("\nLast 5 bytes:");

         for(var1 = this.data.length - 5; var1 < this.data.length; ++var1) {
            System.out.print(" Ox" + Integer.toHexString(this.data[var1]));
         }
      } else {
         System.out.print("Data:");

         for(var1 = 0; var1 < this.data.length; ++var1) {
            System.out.print(" Ox" + Integer.toHexString(this.data[var1]));
         }
      }

      System.out.println();
   }
}
