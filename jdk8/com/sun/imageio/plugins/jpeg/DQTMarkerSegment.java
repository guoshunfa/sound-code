package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DQTMarkerSegment extends MarkerSegment {
   List tables = new ArrayList();

   DQTMarkerSegment(float var1, boolean var2) {
      super(219);
      this.tables.add(new DQTMarkerSegment.Qtable(true, var1));
      if (var2) {
         this.tables.add(new DQTMarkerSegment.Qtable(false, var1));
      }

   }

   DQTMarkerSegment(JPEGBuffer var1) throws IOException {
      super(var1);

      DQTMarkerSegment.Qtable var3;
      for(int var2 = this.length; var2 > 0; var2 -= var3.data.length + 1) {
         var3 = new DQTMarkerSegment.Qtable(var1);
         this.tables.add(var3);
      }

      var1.bufAvail -= this.length;
   }

   DQTMarkerSegment(JPEGQTable[] var1) {
      super(219);

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.tables.add(new DQTMarkerSegment.Qtable(var1[var2], var2));
      }

   }

   DQTMarkerSegment(Node var1) throws IIOInvalidTreeException {
      super(219);
      NodeList var2 = var1.getChildNodes();
      int var3 = var2.getLength();
      if (var3 >= 1 && var3 <= 4) {
         for(int var4 = 0; var4 < var3; ++var4) {
            this.tables.add(new DQTMarkerSegment.Qtable(var2.item(var4)));
         }

      } else {
         throw new IIOInvalidTreeException("Invalid DQT node", var1);
      }
   }

   protected Object clone() {
      DQTMarkerSegment var1 = (DQTMarkerSegment)super.clone();
      var1.tables = new ArrayList(this.tables.size());
      Iterator var2 = this.tables.iterator();

      while(var2.hasNext()) {
         DQTMarkerSegment.Qtable var3 = (DQTMarkerSegment.Qtable)var2.next();
         var1.tables.add(var3.clone());
      }

      return var1;
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("dqt");

      for(int var2 = 0; var2 < this.tables.size(); ++var2) {
         DQTMarkerSegment.Qtable var3 = (DQTMarkerSegment.Qtable)this.tables.get(var2);
         var1.appendChild(var3.getNativeNode());
      }

      return var1;
   }

   void write(ImageOutputStream var1) throws IOException {
   }

   void print() {
      this.printTag("DQT");
      System.out.println("Num tables: " + Integer.toString(this.tables.size()));

      for(int var1 = 0; var1 < this.tables.size(); ++var1) {
         DQTMarkerSegment.Qtable var2 = (DQTMarkerSegment.Qtable)this.tables.get(var1);
         var2.print();
      }

      System.out.println();
   }

   DQTMarkerSegment.Qtable getChromaForLuma(DQTMarkerSegment.Qtable var1) {
      DQTMarkerSegment.Qtable var2 = null;
      boolean var3 = true;
      int var4 = 1;

      while(true) {
         var1.getClass();
         if (var4 >= 64) {
            break;
         }

         if (var1.data[var4] != var1.data[var4 - 1]) {
            var3 = false;
            break;
         }

         ++var4;
      }

      if (var3) {
         var2 = (DQTMarkerSegment.Qtable)var1.clone();
         var2.tableID = 1;
      } else {
         var4 = 0;
         int var5 = 1;

         while(true) {
            var1.getClass();
            if (var5 >= 64) {
               float var7 = (float)var1.data[var4] / (float)JPEGQTable.K1Div2Luminance.getTable()[var4];
               JPEGQTable var6 = JPEGQTable.K2Div2Chrominance.getScaledInstance(var7, true);
               var2 = new DQTMarkerSegment.Qtable(var6, 1);
               break;
            }

            if (var1.data[var5] > var1.data[var4]) {
               var4 = var5;
            }

            ++var5;
         }
      }

      return var2;
   }

   DQTMarkerSegment.Qtable getQtableFromNode(Node var1) throws IIOInvalidTreeException {
      return new DQTMarkerSegment.Qtable(var1);
   }

   class Qtable implements Cloneable {
      int elementPrecision;
      int tableID;
      final int QTABLE_SIZE = 64;
      int[] data;
      private final int[] zigzag = new int[]{0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63};

      Qtable(boolean var2, float var3) {
         this.elementPrecision = 0;
         JPEGQTable var4 = null;
         if (var2) {
            this.tableID = 0;
            var4 = JPEGQTable.K1Div2Luminance;
         } else {
            this.tableID = 1;
            var4 = JPEGQTable.K2Div2Chrominance;
         }

         if (var3 != 0.75F) {
            var3 = JPEG.convertToLinearQuality(var3);
            if (var2) {
               var4 = JPEGQTable.K1Luminance.getScaledInstance(var3, true);
            } else {
               var4 = JPEGQTable.K2Div2Chrominance.getScaledInstance(var3, true);
            }
         }

         this.data = var4.getTable();
      }

      Qtable(JPEGBuffer var2) throws IIOException {
         this.elementPrecision = var2.buf[var2.bufPtr] >>> 4;
         this.tableID = var2.buf[var2.bufPtr++] & 15;
         if (this.elementPrecision != 0) {
            throw new IIOException("Unsupported element precision");
         } else {
            this.data = new int[64];

            for(int var3 = 0; var3 < 64; ++var3) {
               this.data[var3] = var2.buf[var2.bufPtr + this.zigzag[var3]] & 255;
            }

            var2.bufPtr += 64;
         }
      }

      Qtable(JPEGQTable var2, int var3) {
         this.elementPrecision = 0;
         this.tableID = var3;
         this.data = var2.getTable();
      }

      Qtable(Node var2) throws IIOInvalidTreeException {
         if (var2.getNodeName().equals("dqtable")) {
            NamedNodeMap var3 = var2.getAttributes();
            int var4 = var3.getLength();
            if (var4 >= 1 && var4 <= 2) {
               this.elementPrecision = 0;
               this.tableID = MarkerSegment.getAttributeValue(var2, var3, "qtableId", 0, 3, true);
               if (var2 instanceof IIOMetadataNode) {
                  IIOMetadataNode var5 = (IIOMetadataNode)var2;
                  JPEGQTable var6 = (JPEGQTable)var5.getUserObject();
                  if (var6 == null) {
                     throw new IIOInvalidTreeException("dqtable node must have user object", var2);
                  } else {
                     this.data = var6.getTable();
                  }
               } else {
                  throw new IIOInvalidTreeException("dqtable node must have user object", var2);
               }
            } else {
               throw new IIOInvalidTreeException("dqtable node must have 1 or 2 attributes", var2);
            }
         } else {
            throw new IIOInvalidTreeException("Invalid node, expected dqtable", var2);
         }
      }

      protected Object clone() {
         DQTMarkerSegment.Qtable var1 = null;

         try {
            var1 = (DQTMarkerSegment.Qtable)super.clone();
         } catch (CloneNotSupportedException var3) {
         }

         if (this.data != null) {
            var1.data = (int[])((int[])this.data.clone());
         }

         return var1;
      }

      IIOMetadataNode getNativeNode() {
         IIOMetadataNode var1 = new IIOMetadataNode("dqtable");
         var1.setAttribute("elementPrecision", Integer.toString(this.elementPrecision));
         var1.setAttribute("qtableId", Integer.toString(this.tableID));
         var1.setUserObject(new JPEGQTable(this.data));
         return var1;
      }

      void print() {
         System.out.println("Table id: " + Integer.toString(this.tableID));
         System.out.println("Element precision: " + Integer.toString(this.elementPrecision));
         (new JPEGQTable(this.data)).toString();
      }
   }
}
