package sun.awt;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class XSettings {
   private long serial = -1L;

   public Map update(byte[] var1) {
      return (new XSettings.Update(var1)).update();
   }

   class Update {
      private static final int LITTLE_ENDIAN = 0;
      private static final int BIG_ENDIAN = 1;
      private static final int TYPE_INTEGER = 0;
      private static final int TYPE_STRING = 1;
      private static final int TYPE_COLOR = 2;
      private byte[] data;
      private int dlen;
      private int idx;
      private boolean isLittle;
      private long serial = -1L;
      private int nsettings = 0;
      private boolean isValid;
      private HashMap updatedSettings;

      Update(byte[] var2) {
         this.data = var2;
         this.dlen = var2.length;
         if (this.dlen >= 12) {
            this.idx = 0;
            this.isLittle = this.getCARD8() == 0;
            this.idx = 4;
            this.serial = this.getCARD32();
            this.idx = 8;
            this.nsettings = this.getINT32();
            this.updatedSettings = new HashMap();
            this.isValid = true;
         }
      }

      private void needBytes(int var1) throws IndexOutOfBoundsException {
         if (this.idx + var1 > this.dlen) {
            throw new IndexOutOfBoundsException("at " + this.idx + " need " + var1 + " length " + this.dlen);
         }
      }

      private int getCARD8() throws IndexOutOfBoundsException {
         this.needBytes(1);
         int var1 = this.data[this.idx] & 255;
         ++this.idx;
         return var1;
      }

      private int getCARD16() throws IndexOutOfBoundsException {
         this.needBytes(2);
         int var1;
         if (this.isLittle) {
            var1 = this.data[this.idx + 0] & 255 | (this.data[this.idx + 1] & 255) << 8;
         } else {
            var1 = (this.data[this.idx + 0] & 255) << 8 | this.data[this.idx + 1] & 255;
         }

         this.idx += 2;
         return var1;
      }

      private int getINT32() throws IndexOutOfBoundsException {
         this.needBytes(4);
         int var1;
         if (this.isLittle) {
            var1 = this.data[this.idx + 0] & 255 | (this.data[this.idx + 1] & 255) << 8 | (this.data[this.idx + 2] & 255) << 16 | (this.data[this.idx + 3] & 255) << 24;
         } else {
            var1 = (this.data[this.idx + 0] & 255) << 24 | (this.data[this.idx + 1] & 255) << 16 | (this.data[this.idx + 2] & 255) << 8 | (this.data[this.idx + 3] & 255) << 0;
         }

         this.idx += 4;
         return var1;
      }

      private long getCARD32() throws IndexOutOfBoundsException {
         return (long)this.getINT32() & 4294967295L;
      }

      private String getString(int var1) throws IndexOutOfBoundsException {
         this.needBytes(var1);
         String var2 = null;

         try {
            var2 = new String(this.data, this.idx, var1, "UTF-8");
         } catch (UnsupportedEncodingException var4) {
         }

         this.idx = this.idx + var1 + 3 & -4;
         return var2;
      }

      public Map update() {
         if (!this.isValid) {
            return null;
         } else {
            synchronized(XSettings.this) {
               long var2 = XSettings.this.serial;
               if (this.serial <= var2) {
                  return null;
               } else {
                  for(int var4 = 0; var4 < this.nsettings && this.idx < this.dlen; ++var4) {
                     this.updateOne(var2);
                  }

                  XSettings.this.serial = this.serial;
                  return this.updatedSettings;
               }
            }
         }
      }

      private void updateOne(long var1) throws IndexOutOfBoundsException, IllegalArgumentException {
         int var3 = this.getCARD8();
         ++this.idx;
         int var4 = this.getCARD16();
         int var5 = this.idx;
         this.idx = this.idx + var4 + 3 & -4;
         long var6 = this.getCARD32();
         if (var6 <= var1) {
            if (var3 == 0) {
               this.idx += 4;
            } else if (var3 == 1) {
               int var14 = this.getINT32();
               this.idx = this.idx + var14 + 3 & -4;
            } else {
               if (var3 != 2) {
                  throw new IllegalArgumentException("Unknown type: " + var3);
               }

               this.idx += 8;
            }

         } else {
            this.idx = var5;
            String var8 = this.getString(var4);
            this.idx += 4;
            Object var9 = null;
            if (var3 == 0) {
               var9 = this.getINT32();
            } else if (var3 == 1) {
               var9 = this.getString(this.getINT32());
            } else {
               if (var3 != 2) {
                  throw new IllegalArgumentException("Unknown type: " + var3);
               }

               int var10 = this.getCARD16();
               int var11 = this.getCARD16();
               int var12 = this.getCARD16();
               int var13 = this.getCARD16();
               var9 = new Color((float)var10 / 65535.0F, (float)var11 / 65535.0F, (float)var12 / 65535.0F, (float)var13 / 65535.0F);
            }

            if (var8 != null) {
               this.updatedSettings.put(var8, var9);
            }
         }
      }
   }
}
