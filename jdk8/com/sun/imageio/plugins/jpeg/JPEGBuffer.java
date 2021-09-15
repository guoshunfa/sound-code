package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

class JPEGBuffer {
   private boolean debug = false;
   final int BUFFER_SIZE = 4096;
   byte[] buf = new byte[4096];
   int bufAvail = 0;
   int bufPtr = 0;
   ImageInputStream iis;

   JPEGBuffer(ImageInputStream var1) {
      this.iis = var1;
   }

   void loadBuf(int var1) throws IOException {
      if (this.debug) {
         System.out.print("loadbuf called with ");
         System.out.print("count " + var1 + ", ");
         System.out.println("bufAvail " + this.bufAvail + ", ");
      }

      if (var1 != 0) {
         if (this.bufAvail >= var1) {
            return;
         }
      } else if (this.bufAvail == 4096) {
         return;
      }

      if (this.bufAvail > 0 && this.bufAvail < 4096) {
         System.arraycopy(this.buf, this.bufPtr, this.buf, 0, this.bufAvail);
      }

      int var2 = this.iis.read(this.buf, this.bufAvail, this.buf.length - this.bufAvail);
      if (this.debug) {
         System.out.println("iis.read returned " + var2);
      }

      if (var2 != -1) {
         this.bufAvail += var2;
      }

      this.bufPtr = 0;
      int var3 = Math.min(4096, var1);
      if (this.bufAvail < var3) {
         throw new IIOException("Image Format Error");
      }
   }

   void readData(byte[] var1) throws IOException {
      int var2 = var1.length;
      if (this.bufAvail >= var2) {
         System.arraycopy(this.buf, this.bufPtr, var1, 0, var2);
         this.bufAvail -= var2;
         this.bufPtr += var2;
      } else {
         int var3 = 0;
         if (this.bufAvail > 0) {
            System.arraycopy(this.buf, this.bufPtr, var1, 0, this.bufAvail);
            var3 = this.bufAvail;
            var2 -= this.bufAvail;
            this.bufAvail = 0;
            this.bufPtr = 0;
         }

         if (this.iis.read(var1, var3, var2) != var2) {
            throw new IIOException("Image format Error");
         }
      }
   }

   void skipData(int var1) throws IOException {
      if (this.bufAvail >= var1) {
         this.bufAvail -= var1;
         this.bufPtr += var1;
      } else {
         if (this.bufAvail > 0) {
            var1 -= this.bufAvail;
            this.bufAvail = 0;
            this.bufPtr = 0;
         }

         if (this.iis.skipBytes(var1) != var1) {
            throw new IIOException("Image format Error");
         }
      }
   }

   void pushBack() throws IOException {
      this.iis.seek(this.iis.getStreamPosition() - (long)this.bufAvail);
      this.bufAvail = 0;
      this.bufPtr = 0;
   }

   long getStreamPosition() throws IOException {
      return this.iis.getStreamPosition() - (long)this.bufAvail;
   }

   boolean scanForFF(JPEGImageReader var1) throws IOException {
      boolean var2 = false;
      boolean var3 = false;

      while(!var3) {
         while(this.bufAvail > 0) {
            if ((this.buf[this.bufPtr++] & 255) == 255) {
               --this.bufAvail;
               var3 = true;
               break;
            }

            --this.bufAvail;
         }

         this.loadBuf(0);
         if (var3) {
            while(this.bufAvail > 0 && (this.buf[this.bufPtr] & 255) == 255) {
               ++this.bufPtr;
               --this.bufAvail;
            }
         }

         if (this.bufAvail == 0) {
            var2 = true;
            this.buf[0] = -39;
            this.bufAvail = 1;
            this.bufPtr = 0;
            var3 = true;
         }
      }

      return var2;
   }

   void print(int var1) {
      System.out.print("buffer has ");
      System.out.print(this.bufAvail);
      System.out.println(" bytes available");
      if (this.bufAvail < var1) {
         var1 = this.bufAvail;
      }

      for(int var2 = this.bufPtr; var1 > 0; --var1) {
         int var3 = this.buf[var2++] & 255;
         System.out.print(" " + Integer.toHexString(var3));
      }

      System.out.println();
   }
}
