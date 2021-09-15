package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SOFMarkerSegment extends MarkerSegment {
   int samplePrecision;
   int numLines;
   int samplesPerLine;
   SOFMarkerSegment.ComponentSpec[] componentSpecs;

   SOFMarkerSegment(boolean var1, boolean var2, boolean var3, byte[] var4, int var5) {
      super(var1 ? 194 : (var2 ? 193 : 192));
      this.samplePrecision = 8;
      this.numLines = 0;
      this.samplesPerLine = 0;
      this.componentSpecs = new SOFMarkerSegment.ComponentSpec[var5];

      for(int var6 = 0; var6 < var5; ++var6) {
         byte var7 = 1;
         byte var8 = 0;
         if (var3) {
            var7 = 2;
            if (var6 == 1 || var6 == 2) {
               var7 = 1;
               var8 = 1;
            }
         }

         this.componentSpecs[var6] = new SOFMarkerSegment.ComponentSpec(var4[var6], var7, var8);
      }

   }

   SOFMarkerSegment(JPEGBuffer var1) throws IOException {
      super(var1);
      this.samplePrecision = var1.buf[var1.bufPtr++];
      this.numLines = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.numLines |= var1.buf[var1.bufPtr++] & 255;
      this.samplesPerLine = (var1.buf[var1.bufPtr++] & 255) << 8;
      this.samplesPerLine |= var1.buf[var1.bufPtr++] & 255;
      int var2 = var1.buf[var1.bufPtr++] & 255;
      this.componentSpecs = new SOFMarkerSegment.ComponentSpec[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.componentSpecs[var3] = new SOFMarkerSegment.ComponentSpec(var1);
      }

      var1.bufAvail -= this.length;
   }

   SOFMarkerSegment(Node var1) throws IIOInvalidTreeException {
      super(192);
      this.samplePrecision = 8;
      this.numLines = 0;
      this.samplesPerLine = 0;
      this.updateFromNativeNode(var1, true);
   }

   protected Object clone() {
      SOFMarkerSegment var1 = (SOFMarkerSegment)super.clone();
      if (this.componentSpecs != null) {
         var1.componentSpecs = (SOFMarkerSegment.ComponentSpec[])((SOFMarkerSegment.ComponentSpec[])this.componentSpecs.clone());

         for(int var2 = 0; var2 < this.componentSpecs.length; ++var2) {
            var1.componentSpecs[var2] = (SOFMarkerSegment.ComponentSpec)this.componentSpecs[var2].clone();
         }
      }

      return var1;
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("sof");
      var1.setAttribute("process", Integer.toString(this.tag - 192));
      var1.setAttribute("samplePrecision", Integer.toString(this.samplePrecision));
      var1.setAttribute("numLines", Integer.toString(this.numLines));
      var1.setAttribute("samplesPerLine", Integer.toString(this.samplesPerLine));
      var1.setAttribute("numFrameComponents", Integer.toString(this.componentSpecs.length));

      for(int var2 = 0; var2 < this.componentSpecs.length; ++var2) {
         var1.appendChild(this.componentSpecs[var2].getNativeNode());
      }

      return var1;
   }

   void updateFromNativeNode(Node var1, boolean var2) throws IIOInvalidTreeException {
      NamedNodeMap var3 = var1.getAttributes();
      int var4 = getAttributeValue(var1, var3, "process", 0, 2, false);
      this.tag = var4 != -1 ? var4 + 192 : this.tag;
      getAttributeValue(var1, var3, "samplePrecision", 8, 8, false);
      var4 = getAttributeValue(var1, var3, "numLines", 0, 65535, false);
      this.numLines = var4 != -1 ? var4 : this.numLines;
      var4 = getAttributeValue(var1, var3, "samplesPerLine", 0, 65535, false);
      this.samplesPerLine = var4 != -1 ? var4 : this.samplesPerLine;
      int var5 = getAttributeValue(var1, var3, "numFrameComponents", 1, 4, false);
      NodeList var6 = var1.getChildNodes();
      if (var6.getLength() != var5) {
         throw new IIOInvalidTreeException("numFrameComponents must match number of children", var1);
      } else {
         this.componentSpecs = new SOFMarkerSegment.ComponentSpec[var5];

         for(int var7 = 0; var7 < var5; ++var7) {
            this.componentSpecs[var7] = new SOFMarkerSegment.ComponentSpec(var6.item(var7));
         }

      }
   }

   void write(ImageOutputStream var1) throws IOException {
   }

   void print() {
      this.printTag("SOF");
      System.out.print("Sample precision: ");
      System.out.println(this.samplePrecision);
      System.out.print("Number of lines: ");
      System.out.println(this.numLines);
      System.out.print("Samples per line: ");
      System.out.println(this.samplesPerLine);
      System.out.print("Number of components: ");
      System.out.println(this.componentSpecs.length);

      for(int var1 = 0; var1 < this.componentSpecs.length; ++var1) {
         this.componentSpecs[var1].print();
      }

   }

   int getIDencodedCSType() {
      for(int var1 = 0; var1 < this.componentSpecs.length; ++var1) {
         if (this.componentSpecs[var1].componentId < 65) {
            return 0;
         }
      }

      switch(this.componentSpecs.length) {
      case 3:
         if (this.componentSpecs[0].componentId == 82 && this.componentSpecs[0].componentId == 71 && this.componentSpecs[0].componentId == 66) {
            return 2;
         }

         if (this.componentSpecs[0].componentId == 89 && this.componentSpecs[0].componentId == 67 && this.componentSpecs[0].componentId == 99) {
            return 5;
         }
         break;
      case 4:
         if (this.componentSpecs[0].componentId == 82 && this.componentSpecs[0].componentId == 71 && this.componentSpecs[0].componentId == 66 && this.componentSpecs[0].componentId == 65) {
            return 6;
         }

         if (this.componentSpecs[0].componentId == 89 && this.componentSpecs[0].componentId == 67 && this.componentSpecs[0].componentId == 99 && this.componentSpecs[0].componentId == 65) {
            return 10;
         }
      }

      return 0;
   }

   SOFMarkerSegment.ComponentSpec getComponentSpec(byte var1, int var2, int var3) {
      return new SOFMarkerSegment.ComponentSpec(var1, var2, var3);
   }

   class ComponentSpec implements Cloneable {
      int componentId;
      int HsamplingFactor;
      int VsamplingFactor;
      int QtableSelector;

      ComponentSpec(byte var2, int var3, int var4) {
         this.componentId = var2;
         this.HsamplingFactor = var3;
         this.VsamplingFactor = var3;
         this.QtableSelector = var4;
      }

      ComponentSpec(JPEGBuffer var2) {
         this.componentId = var2.buf[var2.bufPtr++];
         this.HsamplingFactor = var2.buf[var2.bufPtr] >>> 4;
         this.VsamplingFactor = var2.buf[var2.bufPtr++] & 15;
         this.QtableSelector = var2.buf[var2.bufPtr++];
      }

      ComponentSpec(Node var2) throws IIOInvalidTreeException {
         NamedNodeMap var3 = var2.getAttributes();
         this.componentId = MarkerSegment.getAttributeValue(var2, var3, "componentId", 0, 255, true);
         this.HsamplingFactor = MarkerSegment.getAttributeValue(var2, var3, "HsamplingFactor", 1, 255, true);
         this.VsamplingFactor = MarkerSegment.getAttributeValue(var2, var3, "VsamplingFactor", 1, 255, true);
         this.QtableSelector = MarkerSegment.getAttributeValue(var2, var3, "QtableSelector", 0, 3, true);
      }

      protected Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            return null;
         }
      }

      IIOMetadataNode getNativeNode() {
         IIOMetadataNode var1 = new IIOMetadataNode("componentSpec");
         var1.setAttribute("componentId", Integer.toString(this.componentId));
         var1.setAttribute("HsamplingFactor", Integer.toString(this.HsamplingFactor));
         var1.setAttribute("VsamplingFactor", Integer.toString(this.VsamplingFactor));
         var1.setAttribute("QtableSelector", Integer.toString(this.QtableSelector));
         return var1;
      }

      void print() {
         System.out.print("Component ID: ");
         System.out.println(this.componentId);
         System.out.print("H sampling factor: ");
         System.out.println(this.HsamplingFactor);
         System.out.print("V sampling factor: ");
         System.out.println(this.VsamplingFactor);
         System.out.print("Q table selector: ");
         System.out.println(this.QtableSelector);
      }
   }
}
