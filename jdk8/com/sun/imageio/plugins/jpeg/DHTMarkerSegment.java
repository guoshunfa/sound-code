package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DHTMarkerSegment extends MarkerSegment {
   List tables = new ArrayList();

   DHTMarkerSegment(boolean var1) {
      super(196);
      this.tables.add(new DHTMarkerSegment.Htable(JPEGHuffmanTable.StdDCLuminance, true, 0));
      if (var1) {
         this.tables.add(new DHTMarkerSegment.Htable(JPEGHuffmanTable.StdDCChrominance, true, 1));
      }

      this.tables.add(new DHTMarkerSegment.Htable(JPEGHuffmanTable.StdACLuminance, false, 0));
      if (var1) {
         this.tables.add(new DHTMarkerSegment.Htable(JPEGHuffmanTable.StdACChrominance, false, 1));
      }

   }

   DHTMarkerSegment(JPEGBuffer var1) throws IOException {
      super(var1);

      DHTMarkerSegment.Htable var3;
      for(int var2 = this.length; var2 > 0; var2 -= 17 + var3.values.length) {
         var3 = new DHTMarkerSegment.Htable(var1);
         this.tables.add(var3);
      }

      var1.bufAvail -= this.length;
   }

   DHTMarkerSegment(JPEGHuffmanTable[] var1, JPEGHuffmanTable[] var2) {
      super(196);

      int var3;
      for(var3 = 0; var3 < var1.length; ++var3) {
         this.tables.add(new DHTMarkerSegment.Htable(var1[var3], true, var3));
      }

      for(var3 = 0; var3 < var2.length; ++var3) {
         this.tables.add(new DHTMarkerSegment.Htable(var2[var3], false, var3));
      }

   }

   DHTMarkerSegment(Node var1) throws IIOInvalidTreeException {
      super(196);
      NodeList var2 = var1.getChildNodes();
      int var3 = var2.getLength();
      if (var3 >= 1 && var3 <= 4) {
         for(int var4 = 0; var4 < var3; ++var4) {
            this.tables.add(new DHTMarkerSegment.Htable(var2.item(var4)));
         }

      } else {
         throw new IIOInvalidTreeException("Invalid DHT node", var1);
      }
   }

   protected Object clone() {
      DHTMarkerSegment var1 = (DHTMarkerSegment)super.clone();
      var1.tables = new ArrayList(this.tables.size());
      Iterator var2 = this.tables.iterator();

      while(var2.hasNext()) {
         DHTMarkerSegment.Htable var3 = (DHTMarkerSegment.Htable)var2.next();
         var1.tables.add(var3.clone());
      }

      return var1;
   }

   IIOMetadataNode getNativeNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("dht");

      for(int var2 = 0; var2 < this.tables.size(); ++var2) {
         DHTMarkerSegment.Htable var3 = (DHTMarkerSegment.Htable)this.tables.get(var2);
         var1.appendChild(var3.getNativeNode());
      }

      return var1;
   }

   void write(ImageOutputStream var1) throws IOException {
   }

   void print() {
      this.printTag("DHT");
      System.out.println("Num tables: " + Integer.toString(this.tables.size()));

      for(int var1 = 0; var1 < this.tables.size(); ++var1) {
         DHTMarkerSegment.Htable var2 = (DHTMarkerSegment.Htable)this.tables.get(var1);
         var2.print();
      }

      System.out.println();
   }

   DHTMarkerSegment.Htable getHtableFromNode(Node var1) throws IIOInvalidTreeException {
      return new DHTMarkerSegment.Htable(var1);
   }

   void addHtable(JPEGHuffmanTable var1, boolean var2, int var3) {
      this.tables.add(new DHTMarkerSegment.Htable(var1, var2, var3));
   }

   class Htable implements Cloneable {
      int tableClass;
      int tableID;
      private static final int NUM_LENGTHS = 16;
      short[] numCodes = new short[16];
      short[] values;

      Htable(JPEGBuffer var2) {
         this.tableClass = var2.buf[var2.bufPtr] >>> 4;
         this.tableID = var2.buf[var2.bufPtr++] & 15;

         int var3;
         for(var3 = 0; var3 < 16; ++var3) {
            this.numCodes[var3] = (short)(var2.buf[var2.bufPtr++] & 255);
         }

         var3 = 0;

         int var4;
         for(var4 = 0; var4 < 16; ++var4) {
            var3 += this.numCodes[var4];
         }

         this.values = new short[var3];

         for(var4 = 0; var4 < var3; ++var4) {
            this.values[var4] = (short)(var2.buf[var2.bufPtr++] & 255);
         }

      }

      Htable(JPEGHuffmanTable var2, boolean var3, int var4) {
         this.tableClass = var3 ? 0 : 1;
         this.tableID = var4;
         this.numCodes = var2.getLengths();
         this.values = var2.getValues();
      }

      Htable(Node var2) throws IIOInvalidTreeException {
         if (var2.getNodeName().equals("dhtable")) {
            NamedNodeMap var3 = var2.getAttributes();
            int var4 = var3.getLength();
            if (var4 != 2) {
               throw new IIOInvalidTreeException("dhtable node must have 2 attributes", var2);
            } else {
               this.tableClass = MarkerSegment.getAttributeValue(var2, var3, "class", 0, 1, true);
               this.tableID = MarkerSegment.getAttributeValue(var2, var3, "htableId", 0, 3, true);
               if (var2 instanceof IIOMetadataNode) {
                  IIOMetadataNode var5 = (IIOMetadataNode)var2;
                  JPEGHuffmanTable var6 = (JPEGHuffmanTable)var5.getUserObject();
                  if (var6 == null) {
                     throw new IIOInvalidTreeException("dhtable node must have user object", var2);
                  } else {
                     this.numCodes = var6.getLengths();
                     this.values = var6.getValues();
                  }
               } else {
                  throw new IIOInvalidTreeException("dhtable node must have user object", var2);
               }
            }
         } else {
            throw new IIOInvalidTreeException("Invalid node, expected dqtable", var2);
         }
      }

      protected Object clone() {
         DHTMarkerSegment.Htable var1 = null;

         try {
            var1 = (DHTMarkerSegment.Htable)super.clone();
         } catch (CloneNotSupportedException var3) {
         }

         if (this.numCodes != null) {
            var1.numCodes = (short[])((short[])this.numCodes.clone());
         }

         if (this.values != null) {
            var1.values = (short[])((short[])this.values.clone());
         }

         return var1;
      }

      IIOMetadataNode getNativeNode() {
         IIOMetadataNode var1 = new IIOMetadataNode("dhtable");
         var1.setAttribute("class", Integer.toString(this.tableClass));
         var1.setAttribute("htableId", Integer.toString(this.tableID));
         var1.setUserObject(new JPEGHuffmanTable(this.numCodes, this.values));
         return var1;
      }

      void print() {
         System.out.println("Huffman Table");
         System.out.println("table class: " + (this.tableClass == 0 ? "DC" : "AC"));
         System.out.println("table id: " + Integer.toString(this.tableID));
         (new JPEGHuffmanTable(this.numCodes, this.values)).toString();
      }
   }
}
