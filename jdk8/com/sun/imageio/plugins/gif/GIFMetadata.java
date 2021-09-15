package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.Node;

abstract class GIFMetadata extends IIOMetadata {
   static final int UNDEFINED_INTEGER_VALUE = -1;

   protected static void fatal(Node var0, String var1) throws IIOInvalidTreeException {
      throw new IIOInvalidTreeException(var1, var0);
   }

   protected static String getStringAttribute(Node var0, String var1, String var2, boolean var3, String[] var4) throws IIOInvalidTreeException {
      Node var5 = var0.getAttributes().getNamedItem(var1);
      if (var5 == null) {
         if (!var3) {
            return var2;
         }

         fatal(var0, "Required attribute " + var1 + " not present!");
      }

      String var6 = var5.getNodeValue();
      if (var4 != null) {
         if (var6 == null) {
            fatal(var0, "Null value for " + var0.getNodeName() + " attribute " + var1 + "!");
         }

         boolean var7 = false;
         int var8 = var4.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            if (var6.equals(var4[var9])) {
               var7 = true;
               break;
            }
         }

         if (!var7) {
            fatal(var0, "Bad value for " + var0.getNodeName() + " attribute " + var1 + "!");
         }
      }

      return var6;
   }

   protected static int getIntAttribute(Node var0, String var1, int var2, boolean var3, boolean var4, int var5, int var6) throws IIOInvalidTreeException {
      String var7 = getStringAttribute(var0, var1, (String)null, var3, (String[])null);
      if (var7 != null && !"".equals(var7)) {
         int var8 = var2;

         try {
            var8 = Integer.parseInt(var7);
         } catch (NumberFormatException var10) {
            fatal(var0, "Bad value for " + var0.getNodeName() + " attribute " + var1 + "!");
         }

         if (var4 && (var8 < var5 || var8 > var6)) {
            fatal(var0, "Bad value for " + var0.getNodeName() + " attribute " + var1 + "!");
         }

         return var8;
      } else {
         return var2;
      }
   }

   protected static float getFloatAttribute(Node var0, String var1, float var2, boolean var3) throws IIOInvalidTreeException {
      String var4 = getStringAttribute(var0, var1, (String)null, var3, (String[])null);
      return var4 == null ? var2 : Float.parseFloat(var4);
   }

   protected static int getIntAttribute(Node var0, String var1, boolean var2, int var3, int var4) throws IIOInvalidTreeException {
      return getIntAttribute(var0, var1, -1, true, var2, var3, var4);
   }

   protected static float getFloatAttribute(Node var0, String var1) throws IIOInvalidTreeException {
      return getFloatAttribute(var0, var1, -1.0F, true);
   }

   protected static boolean getBooleanAttribute(Node var0, String var1, boolean var2, boolean var3) throws IIOInvalidTreeException {
      Node var4 = var0.getAttributes().getNamedItem(var1);
      if (var4 == null) {
         if (!var3) {
            return var2;
         }

         fatal(var0, "Required attribute " + var1 + " not present!");
      }

      String var5 = var4.getNodeValue();
      if (!var5.equals("TRUE") && !var5.equals("true")) {
         if (!var5.equals("FALSE") && !var5.equals("false")) {
            fatal(var0, "Attribute " + var1 + " must be 'TRUE' or 'FALSE'!");
            return false;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   protected static boolean getBooleanAttribute(Node var0, String var1) throws IIOInvalidTreeException {
      return getBooleanAttribute(var0, var1, false, true);
   }

   protected static int getEnumeratedAttribute(Node var0, String var1, String[] var2, int var3, boolean var4) throws IIOInvalidTreeException {
      Node var5 = var0.getAttributes().getNamedItem(var1);
      if (var5 == null) {
         if (!var4) {
            return var3;
         }

         fatal(var0, "Required attribute " + var1 + " not present!");
      }

      String var6 = var5.getNodeValue();

      for(int var7 = 0; var7 < var2.length; ++var7) {
         if (var6.equals(var2[var7])) {
            return var7;
         }
      }

      fatal(var0, "Illegal value for attribute " + var1 + "!");
      return -1;
   }

   protected static int getEnumeratedAttribute(Node var0, String var1, String[] var2) throws IIOInvalidTreeException {
      return getEnumeratedAttribute(var0, var1, var2, -1, true);
   }

   protected static String getAttribute(Node var0, String var1, String var2, boolean var3) throws IIOInvalidTreeException {
      Node var4 = var0.getAttributes().getNamedItem(var1);
      if (var4 == null) {
         if (!var3) {
            return var2;
         }

         fatal(var0, "Required attribute " + var1 + " not present!");
      }

      return var4.getNodeValue();
   }

   protected static String getAttribute(Node var0, String var1) throws IIOInvalidTreeException {
      return getAttribute(var0, var1, (String)null, true);
   }

   protected GIFMetadata(boolean var1, String var2, String var3, String[] var4, String[] var5) {
      super(var1, var2, var3, var4, var5);
   }

   public void mergeTree(String var1, Node var2) throws IIOInvalidTreeException {
      if (var1.equals(this.nativeMetadataFormatName)) {
         if (var2 == null) {
            throw new IllegalArgumentException("root == null!");
         }

         this.mergeNativeTree(var2);
      } else {
         if (!var1.equals("javax_imageio_1.0")) {
            throw new IllegalArgumentException("Not a recognized format!");
         }

         if (var2 == null) {
            throw new IllegalArgumentException("root == null!");
         }

         this.mergeStandardTree(var2);
      }

   }

   protected byte[] getColorTable(Node var1, String var2, boolean var3, int var4) throws IIOInvalidTreeException {
      byte[] var5 = new byte[256];
      byte[] var6 = new byte[256];
      byte[] var7 = new byte[256];
      int var8 = -1;
      Node var9 = var1.getFirstChild();
      if (var9 == null) {
         fatal(var1, "Palette has no entries!");
      }

      int var10;
      while(var9 != null) {
         if (!var9.getNodeName().equals(var2)) {
            fatal(var1, "Only a " + var2 + " may be a child of a " + var9.getNodeName() + "!");
         }

         var10 = getIntAttribute(var9, "index", true, 0, 255);
         if (var10 > var8) {
            var8 = var10;
         }

         var5[var10] = (byte)getIntAttribute(var9, "red", true, 0, 255);
         var6[var10] = (byte)getIntAttribute(var9, "green", true, 0, 255);
         var7[var10] = (byte)getIntAttribute(var9, "blue", true, 0, 255);
         var9 = var9.getNextSibling();
      }

      var10 = var8 + 1;
      if (var3 && var10 != var4) {
         fatal(var1, "Unexpected length for palette!");
      }

      byte[] var11 = new byte[3 * var10];
      int var12 = 0;

      for(int var13 = 0; var12 < var10; ++var12) {
         var11[var13++] = var5[var12];
         var11[var13++] = var6[var12];
         var11[var13++] = var7[var12];
      }

      return var11;
   }

   protected abstract void mergeNativeTree(Node var1) throws IIOInvalidTreeException;

   protected abstract void mergeStandardTree(Node var1) throws IIOInvalidTreeException;
}
