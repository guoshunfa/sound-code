package sun.net;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TelnetInputStream extends FilterInputStream {
   boolean stickyCRLF = false;
   boolean seenCR = false;
   public boolean binaryMode = false;

   public TelnetInputStream(InputStream var1, boolean var2) {
      super(var1);
      this.binaryMode = var2;
   }

   public void setStickyCRLF(boolean var1) {
      this.stickyCRLF = var1;
   }

   public int read() throws IOException {
      if (this.binaryMode) {
         return super.read();
      } else if (this.seenCR) {
         this.seenCR = false;
         return 10;
      } else {
         int var1;
         if ((var1 = super.read()) == 13) {
            switch(super.read()) {
            case -1:
            default:
               throw new TelnetProtocolException("misplaced CR in input");
            case 0:
               return 13;
            case 10:
               if (this.stickyCRLF) {
                  this.seenCR = true;
                  return 13;
               } else {
                  return 10;
               }
            }
         } else {
            return var1;
         }
      }
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.binaryMode) {
         return super.read(var1, var2, var3);
      } else {
         int var5 = var2;

         while(true) {
            --var3;
            if (var3 < 0) {
               break;
            }

            int var4 = this.read();
            if (var4 == -1) {
               break;
            }

            var1[var2++] = (byte)var4;
         }

         return var2 > var5 ? var2 - var5 : -1;
      }
   }
}
