package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SOSMarkerSegment extends MarkerSegment {
   int startSpectralSelection;
   int endSpectralSelection;
   int approxHigh;
   int approxLow;
   SOSMarkerSegment.ScanComponentSpec[] componentSpecs;

   SOSMarkerSegment(boolean var1, byte[] var2, int var3) {
      super(218);
      this.startSpectralSelection = 0;
      this.endSpectralSelection = 63;
      this.approxHigh = 0;
      this.approxLow = 0;
      this.componentSpecs = new SOSMarkerSegment.ScanComponentSpec[var3];

      for(int var4 = 0; var4 < var3; ++var4) {
         byte var5 = 0;
         if (var1 && (var4 == 1 || var4 == 2)) {
            var5 = 1;
         }

         this.componentSpecs[var4] = new SOSMarkerSegment.ScanComponentSpec(var2[var4], var5);
      }

   }

   SOSMarkerSegment(JPEGBuffer var1) throws IOException {
      super(var1);
      byte var2 = var1.buf[var1.bufPtr++];
      this.componentSpecs = new SOSMarkerSegment.ScanComponentSpec[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.componentSpecs[var3] = new SOSMarkerSegment.ScanComponentSpec(var1);
      }

      this.startSpectralSelection = var1.buf[var1.bufPtr++];
      this.endSpectralSelection = var1.buf[var1.bufPtr++];
      this.approxHigh = var1.buf[var1.bufPtr] >> 4;
      this.approxLow = var1.buf[var1.bufPtr++] & 15;
      var1.bufAvail -= this.length;
   }

   SOSMarkerSegment(Node var1) throws IIOInvalidTreeException {
      super(218);
      this.startSpectralSelection = 0;
      this.endSpectralSelection = 63;
      this.approxHigh = 0;
      this.approxLow = 0;
      this.updateFromNativeNode(var1, true);
   }

   protected Object clone() {
      SOSMarkerSegment var1 = (SOSMarkerSegment)super.clone();
      if (this.componentSpecs != null) {
         var1.componentSpecs = (SOSMarkerSegment.ScanComponentSpec[])((SOSMarkerSegment.ScanComponentSpec[])this.componentSpecs.clone());

         for(int var2 = 0; var2 < this.componentSpecs.length; ++var2) {
            var1.componentSpecs[var2] = (SOSMarkerSegment.ScanComponentSpec)this.componentSpecs[var2].clone();
         }
      }

      return var1;
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("sos");
      var1.setAttribute("numScanComponents", Integer.toString(this.componentSpecs.length));
      var1.setAttribute("startSpectralSelection", Integer.toString(this.startSpectralSelection));
      var1.setAttribute("endSpectralSelection", Integer.toString(this.endSpectralSelection));
      var1.setAttribute("approxHigh", Integer.toString(this.approxHigh));
      var1.setAttribute("approxLow", Integer.toString(this.approxLow));

      for(int var2 = 0; var2 < this.componentSpecs.length; ++var2) {
         var1.appendChild(this.componentSpecs[var2].getNativeNode());
      }

      return var1;
   }

   void updateFromNativeNode(Node var1, boolean var2) throws IIOInvalidTreeException {
      NamedNodeMap var3 = var1.getAttributes();
      int var4 = getAttributeValue(var1, var3, "numScanComponents", 1, 4, true);
      int var5 = getAttributeValue(var1, var3, "startSpectralSelection", 0, 63, false);
      this.startSpectralSelection = var5 != -1 ? var5 : this.startSpectralSelection;
      var5 = getAttributeValue(var1, var3, "endSpectralSelection", 0, 63, false);
      this.endSpectralSelection = var5 != -1 ? var5 : this.endSpectralSelection;
      var5 = getAttributeValue(var1, var3, "approxHigh", 0, 15, false);
      this.approxHigh = var5 != -1 ? var5 : this.approxHigh;
      var5 = getAttributeValue(var1, var3, "approxLow", 0, 15, false);
      this.approxLow = var5 != -1 ? var5 : this.approxLow;
      NodeList var6 = var1.getChildNodes();
      if (var6.getLength() != var4) {
         throw new IIOInvalidTreeException("numScanComponents must match the number of children", var1);
      } else {
         this.componentSpecs = new SOSMarkerSegment.ScanComponentSpec[var4];

         for(int var7 = 0; var7 < var4; ++var7) {
            this.componentSpecs[var7] = new SOSMarkerSegment.ScanComponentSpec(var6.item(var7));
         }

      }
   }

   void write(ImageOutputStream var1) throws IOException {
   }

   void print() {
      this.printTag("SOS");
      System.out.print("Start spectral selection: ");
      System.out.println(this.startSpectralSelection);
      System.out.print("End spectral selection: ");
      System.out.println(this.endSpectralSelection);
      System.out.print("Approx high: ");
      System.out.println(this.approxHigh);
      System.out.print("Approx low: ");
      System.out.println(this.approxLow);
      System.out.print("Num scan components: ");
      System.out.println(this.componentSpecs.length);

      for(int var1 = 0; var1 < this.componentSpecs.length; ++var1) {
         this.componentSpecs[var1].print();
      }

   }

   SOSMarkerSegment.ScanComponentSpec getScanComponentSpec(byte var1, int var2) {
      return new SOSMarkerSegment.ScanComponentSpec(var1, var2);
   }

   class ScanComponentSpec implements Cloneable {
      int componentSelector;
      int dcHuffTable;
      int acHuffTable;

      ScanComponentSpec(byte var2, int var3) {
         this.componentSelector = var2;
         this.dcHuffTable = var3;
         this.acHuffTable = var3;
      }

      ScanComponentSpec(JPEGBuffer var2) {
         this.componentSelector = var2.buf[var2.bufPtr++];
         this.dcHuffTable = var2.buf[var2.bufPtr] >> 4;
         this.acHuffTable = var2.buf[var2.bufPtr++] & 15;
      }

      ScanComponentSpec(Node var2) throws IIOInvalidTreeException {
         NamedNodeMap var3 = var2.getAttributes();
         this.componentSelector = MarkerSegment.getAttributeValue(var2, var3, "componentSelector", 0, 255, true);
         this.dcHuffTable = MarkerSegment.getAttributeValue(var2, var3, "dcHuffTable", 0, 3, true);
         this.acHuffTable = MarkerSegment.getAttributeValue(var2, var3, "acHuffTable", 0, 3, true);
      }

      protected Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            return null;
         }
      }

      IIOMetadataNode getNativeNode() {
         IIOMetadataNode var1 = new IIOMetadataNode("scanComponentSpec");
         var1.setAttribute("componentSelector", Integer.toString(this.componentSelector));
         var1.setAttribute("dcHuffTable", Integer.toString(this.dcHuffTable));
         var1.setAttribute("acHuffTable", Integer.toString(this.acHuffTable));
         return var1;
      }

      void print() {
         System.out.print("Component Selector: ");
         System.out.println(this.componentSelector);
         System.out.print("DC huffman table: ");
         System.out.println(this.dcHuffTable);
         System.out.print("AC huffman table: ");
         System.out.println(this.acHuffTable);
      }
   }
}
