package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class DRIMarkerSegment extends MarkerSegment {
   int restartInterval = 0;

   DRIMarkerSegment(JPEGBuffer var1) throws IOException {
      super(var1);
      this.restartInterval = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.restartInterval |= var1.buf[var1.bufPtr++] & 255;
      var1.bufAvail -= this.length;
   }

   DRIMarkerSegment(Node var1) throws IIOInvalidTreeException {
      super(221);
      this.updateFromNativeNode(var1, true);
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("dri");
      var1.setAttribute("interval", Integer.toString(this.restartInterval));
      return var1;
   }

   void updateFromNativeNode(Node var1, boolean var2) throws IIOInvalidTreeException {
      this.restartInterval = getAttributeValue(var1, (NamedNodeMap)null, "interval", 0, 65535, true);
   }

   void write(ImageOutputStream var1) throws IOException {
   }

   void print() {
      this.printTag("DRI");
      System.out.println("Interval: " + Integer.toString(this.restartInterval));
   }
}
