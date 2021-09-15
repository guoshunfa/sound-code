package sun.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TelnetOutputStream extends BufferedOutputStream {
   boolean stickyCRLF = false;
   boolean seenCR = false;
   public boolean binaryMode = false;

   public TelnetOutputStream(OutputStream var1, boolean var2) {
      super(var1);
      this.binaryMode = var2;
   }

   public void setStickyCRLF(boolean var1) {
      this.stickyCRLF = var1;
   }

   public void write(int var1) throws IOException {
      if (this.binaryMode) {
         super.write(var1);
      } else {
         if (this.seenCR) {
            if (var1 != 10) {
               super.write(0);
            }

            super.write(var1);
            if (var1 != 13) {
               this.seenCR = false;
            }
         } else {
            if (var1 == 10) {
               super.write(13);
               super.write(10);
               return;
            }

            if (var1 == 13) {
               if (this.stickyCRLF) {
                  this.seenCR = true;
               } else {
                  super.write(13);
                  var1 = 0;
               }
            }

            super.write(var1);
         }

      }
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.binaryMode) {
         super.write(var1, var2, var3);
      } else {
         while(true) {
            --var3;
            if (var3 < 0) {
               return;
            }

            this.write(var1[var2++]);
         }
      }
   }
}
