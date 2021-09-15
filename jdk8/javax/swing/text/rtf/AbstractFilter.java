package javax.swing.text.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

abstract class AbstractFilter extends OutputStream {
   protected char[] translationTable;
   protected boolean[] specialsTable;
   static final char[] latin1TranslationTable;
   static final boolean[] noSpecialsTable = new boolean[256];
   static final boolean[] allSpecialsTable;

   public void readFromStream(InputStream var1) throws IOException {
      byte[] var2 = new byte[16384];

      while(true) {
         int var3 = var1.read(var2);
         if (var3 < 0) {
            return;
         }

         this.write(var2, 0, var3);
      }
   }

   public void readFromReader(Reader var1) throws IOException {
      char[] var2 = new char[2048];

      while(true) {
         int var3 = var1.read(var2);
         if (var3 < 0) {
            return;
         }

         for(int var4 = 0; var4 < var3; ++var4) {
            this.write(var2[var4]);
         }
      }
   }

   public AbstractFilter() {
      this.translationTable = latin1TranslationTable;
      this.specialsTable = noSpecialsTable;
   }

   public void write(int var1) throws IOException {
      if (var1 < 0) {
         var1 += 256;
      }

      if (this.specialsTable[var1]) {
         this.writeSpecial(var1);
      } else {
         char var2 = this.translationTable[var1];
         if (var2 != 0) {
            this.write(var2);
         }
      }

   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      StringBuilder var4;
      for(var4 = null; var3 > 0; ++var2) {
         short var5 = (short)var1[var2];
         if (var5 < 0) {
            var5 = (short)(var5 + 256);
         }

         if (this.specialsTable[var5]) {
            if (var4 != null) {
               this.write(var4.toString());
               var4 = null;
            }

            this.writeSpecial(var5);
         } else {
            char var6 = this.translationTable[var5];
            if (var6 != 0) {
               if (var4 == null) {
                  var4 = new StringBuilder();
               }

               var4.append(var6);
            }
         }

         --var3;
      }

      if (var4 != null) {
         this.write(var4.toString());
      }

   }

   public void write(String var1) throws IOException {
      int var3 = var1.length();

      for(int var2 = 0; var2 < var3; ++var2) {
         this.write(var1.charAt(var2));
      }

   }

   protected abstract void write(char var1) throws IOException;

   protected abstract void writeSpecial(int var1) throws IOException;

   static {
      int var0;
      for(var0 = 0; var0 < 256; ++var0) {
         noSpecialsTable[var0] = false;
      }

      allSpecialsTable = new boolean[256];

      for(var0 = 0; var0 < 256; ++var0) {
         allSpecialsTable[var0] = true;
      }

      latin1TranslationTable = new char[256];

      for(var0 = 0; var0 < 256; ++var0) {
         latin1TranslationTable[var0] = (char)var0;
      }

   }
}
