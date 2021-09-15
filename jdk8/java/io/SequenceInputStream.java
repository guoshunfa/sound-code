package java.io;

import java.util.Enumeration;
import java.util.Vector;

public class SequenceInputStream extends InputStream {
   Enumeration<? extends InputStream> e;
   InputStream in;

   public SequenceInputStream(Enumeration<? extends InputStream> var1) {
      this.e = var1;

      try {
         this.nextStream();
      } catch (IOException var3) {
         throw new Error("panic");
      }
   }

   public SequenceInputStream(InputStream var1, InputStream var2) {
      Vector var3 = new Vector(2);
      var3.addElement(var1);
      var3.addElement(var2);
      this.e = var3.elements();

      try {
         this.nextStream();
      } catch (IOException var5) {
         throw new Error("panic");
      }
   }

   final void nextStream() throws IOException {
      if (this.in != null) {
         this.in.close();
      }

      if (this.e.hasMoreElements()) {
         this.in = (InputStream)this.e.nextElement();
         if (this.in == null) {
            throw new NullPointerException();
         }
      } else {
         this.in = null;
      }

   }

   public int available() throws IOException {
      return this.in == null ? 0 : this.in.available();
   }

   public int read() throws IOException {
      while(this.in != null) {
         int var1 = this.in.read();
         if (var1 != -1) {
            return var1;
         }

         this.nextStream();
      }

      return -1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.in == null) {
         return -1;
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         if (var3 == 0) {
            return 0;
         } else {
            do {
               int var4 = this.in.read(var1, var2, var3);
               if (var4 > 0) {
                  return var4;
               }

               this.nextStream();
            } while(this.in != null);

            return -1;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void close() throws IOException {
      do {
         this.nextStream();
      } while(this.in != null);

   }
}
