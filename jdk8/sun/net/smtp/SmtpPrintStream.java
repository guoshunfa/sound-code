package sun.net.smtp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

class SmtpPrintStream extends PrintStream {
   private SmtpClient target;
   private int lastc = 10;

   SmtpPrintStream(OutputStream var1, SmtpClient var2) throws UnsupportedEncodingException {
      super(var1, false, var2.getEncoding());
      this.target = var2;
   }

   public void close() {
      if (this.target != null) {
         if (this.lastc != 10) {
            this.write(10);
         }

         try {
            this.target.issueCommand(".\r\n", 250);
            this.target.message = null;
            this.out = null;
            this.target = null;
         } catch (IOException var2) {
         }

      }
   }

   public void write(int var1) {
      try {
         if (this.lastc == 10 && var1 == 46) {
            this.out.write(46);
         }

         if (var1 == 10 && this.lastc != 13) {
            this.out.write(13);
         }

         this.out.write(var1);
         this.lastc = var1;
      } catch (IOException var3) {
      }

   }

   public void write(byte[] var1, int var2, int var3) {
      try {
         int var4 = this.lastc;

         while(true) {
            --var3;
            if (var3 < 0) {
               this.lastc = var4;
               break;
            }

            byte var5 = var1[var2++];
            if (var4 == 10 && var5 == 46) {
               this.out.write(46);
            }

            if (var5 == 10 && var4 != 13) {
               this.out.write(13);
            }

            this.out.write(var5);
            var4 = var5;
         }
      } catch (IOException var6) {
      }

   }

   public void print(String var1) {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.write(var1.charAt(var3));
      }

   }
}
