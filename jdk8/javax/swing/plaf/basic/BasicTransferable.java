package javax.swing.plaf.basic;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.plaf.UIResource;
import sun.awt.datatransfer.DataTransferer;

class BasicTransferable implements Transferable, UIResource {
   protected String plainData;
   protected String htmlData;
   private static DataFlavor[] htmlFlavors;
   private static DataFlavor[] stringFlavors;
   private static DataFlavor[] plainFlavors;

   public BasicTransferable(String var1, String var2) {
      this.plainData = var1;
      this.htmlData = var2;
   }

   public DataFlavor[] getTransferDataFlavors() {
      DataFlavor[] var1 = this.getRicherFlavors();
      int var2 = var1 != null ? var1.length : 0;
      int var3 = this.isHTMLSupported() ? htmlFlavors.length : 0;
      int var4 = this.isPlainSupported() ? plainFlavors.length : 0;
      int var5 = this.isPlainSupported() ? stringFlavors.length : 0;
      int var6 = var2 + var3 + var4 + var5;
      DataFlavor[] var7 = new DataFlavor[var6];
      int var8 = 0;
      if (var2 > 0) {
         System.arraycopy(var1, 0, var7, var8, var2);
         var8 += var2;
      }

      if (var3 > 0) {
         System.arraycopy(htmlFlavors, 0, var7, var8, var3);
         var8 += var3;
      }

      if (var4 > 0) {
         System.arraycopy(plainFlavors, 0, var7, var8, var4);
         var8 += var4;
      }

      if (var5 > 0) {
         System.arraycopy(stringFlavors, 0, var7, var8, var5);
         int var10000 = var8 + var5;
      }

      return var7;
   }

   public boolean isDataFlavorSupported(DataFlavor var1) {
      DataFlavor[] var2 = this.getTransferDataFlavors();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3].equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException, IOException {
      DataFlavor[] var2 = this.getRicherFlavors();
      if (this.isRicherFlavor(var1)) {
         return this.getRicherData(var1);
      } else {
         String var3;
         if (this.isHTMLFlavor(var1)) {
            var3 = this.getHTMLData();
            var3 = var3 == null ? "" : var3;
            if (String.class.equals(var1.getRepresentationClass())) {
               return var3;
            }

            if (Reader.class.equals(var1.getRepresentationClass())) {
               return new StringReader(var3);
            }

            if (InputStream.class.equals(var1.getRepresentationClass())) {
               return this.createInputStream(var1, var3);
            }
         } else if (this.isPlainFlavor(var1)) {
            var3 = this.getPlainData();
            var3 = var3 == null ? "" : var3;
            if (String.class.equals(var1.getRepresentationClass())) {
               return var3;
            }

            if (Reader.class.equals(var1.getRepresentationClass())) {
               return new StringReader(var3);
            }

            if (InputStream.class.equals(var1.getRepresentationClass())) {
               return this.createInputStream(var1, var3);
            }
         } else if (this.isStringFlavor(var1)) {
            var3 = this.getPlainData();
            var3 = var3 == null ? "" : var3;
            return var3;
         }

         throw new UnsupportedFlavorException(var1);
      }
   }

   private InputStream createInputStream(DataFlavor var1, String var2) throws IOException, UnsupportedFlavorException {
      String var3 = DataTransferer.getTextCharset(var1);
      if (var3 == null) {
         throw new UnsupportedFlavorException(var1);
      } else {
         return new ByteArrayInputStream(var2.getBytes(var3));
      }
   }

   protected boolean isRicherFlavor(DataFlavor var1) {
      DataFlavor[] var2 = this.getRicherFlavors();
      int var3 = var2 != null ? var2.length : 0;

      for(int var4 = 0; var4 < var3; ++var4) {
         if (var2[var4].equals(var1)) {
            return true;
         }
      }

      return false;
   }

   protected DataFlavor[] getRicherFlavors() {
      return null;
   }

   protected Object getRicherData(DataFlavor var1) throws UnsupportedFlavorException {
      return null;
   }

   protected boolean isHTMLFlavor(DataFlavor var1) {
      DataFlavor[] var2 = htmlFlavors;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3].equals(var1)) {
            return true;
         }
      }

      return false;
   }

   protected boolean isHTMLSupported() {
      return this.htmlData != null;
   }

   protected String getHTMLData() {
      return this.htmlData;
   }

   protected boolean isPlainFlavor(DataFlavor var1) {
      DataFlavor[] var2 = plainFlavors;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3].equals(var1)) {
            return true;
         }
      }

      return false;
   }

   protected boolean isPlainSupported() {
      return this.plainData != null;
   }

   protected String getPlainData() {
      return this.plainData;
   }

   protected boolean isStringFlavor(DataFlavor var1) {
      DataFlavor[] var2 = stringFlavors;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3].equals(var1)) {
            return true;
         }
      }

      return false;
   }

   static {
      try {
         htmlFlavors = new DataFlavor[3];
         htmlFlavors[0] = new DataFlavor("text/html;class=java.lang.String");
         htmlFlavors[1] = new DataFlavor("text/html;class=java.io.Reader");
         htmlFlavors[2] = new DataFlavor("text/html;charset=unicode;class=java.io.InputStream");
         plainFlavors = new DataFlavor[3];
         plainFlavors[0] = new DataFlavor("text/plain;class=java.lang.String");
         plainFlavors[1] = new DataFlavor("text/plain;class=java.io.Reader");
         plainFlavors[2] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream");
         stringFlavors = new DataFlavor[2];
         stringFlavors[0] = new DataFlavor("application/x-java-jvm-local-objectref;class=java.lang.String");
         stringFlavors[1] = DataFlavor.stringFlavor;
      } catch (ClassNotFoundException var1) {
         System.err.println("error initializing javax.swing.plaf.basic.BasicTranserable");
      }

   }
}
