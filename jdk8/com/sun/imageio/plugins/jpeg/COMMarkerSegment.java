package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;

class COMMarkerSegment extends MarkerSegment {
   private static final String ENCODING = "ISO-8859-1";

   COMMarkerSegment(JPEGBuffer var1) throws IOException {
      super(var1);
      this.loadData(var1);
   }

   COMMarkerSegment(String var1) {
      super(254);
      this.data = var1.getBytes();
   }

   COMMarkerSegment(Node var1) throws IIOInvalidTreeException {
      super(254);
      if (var1 instanceof IIOMetadataNode) {
         IIOMetadataNode var2 = (IIOMetadataNode)var1;
         this.data = (byte[])((byte[])var2.getUserObject());
      }

      if (this.data == null) {
         String var3 = var1.getAttributes().getNamedItem("comment").getNodeValue();
         if (var3 == null) {
            throw new IIOInvalidTreeException("Empty comment node!", var1);
         }

         this.data = var3.getBytes();
      }

   }

   String getComment() {
      try {
         return new String(this.data, "ISO-8859-1");
      } catch (UnsupportedEncodingException var2) {
         return null;
      }
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("com");
      var1.setAttribute("comment", this.getComment());
      if (this.data != null) {
         var1.setUserObject(this.data.clone());
      }

      return var1;
   }

   void write(ImageOutputStream var1) throws IOException {
      this.length = 2 + this.data.length;
      this.writeTag(var1);
      var1.write(this.data);
   }

   void print() {
      this.printTag("COM");
      System.out.println("<" + this.getComment() + ">");
   }
}
