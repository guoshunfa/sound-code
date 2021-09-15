package sun.misc;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.jar.Manifest;
import sun.nio.ByteBuffered;

public abstract class Resource {
   private InputStream cis;

   public abstract String getName();

   public abstract URL getURL();

   public abstract URL getCodeSourceURL();

   public abstract InputStream getInputStream() throws IOException;

   public abstract int getContentLength() throws IOException;

   private synchronized InputStream cachedInputStream() throws IOException {
      if (this.cis == null) {
         this.cis = this.getInputStream();
      }

      return this.cis;
   }

   public byte[] getBytes() throws IOException {
      InputStream var2 = this.cachedInputStream();
      boolean var3 = Thread.interrupted();

      int var4;
      while(true) {
         try {
            var4 = this.getContentLength();
            break;
         } catch (InterruptedIOException var22) {
            Thread.interrupted();
            var3 = true;
         }
      }

      byte[] var1;
      try {
         var1 = new byte[0];
         if (var4 == -1) {
            var4 = Integer.MAX_VALUE;
         }

         int var7;
         for(int var5 = 0; var5 < var4; var5 += var7) {
            int var6;
            if (var5 >= var1.length) {
               var6 = Math.min(var4 - var5, var1.length + 1024);
               if (var1.length < var5 + var6) {
                  var1 = Arrays.copyOf(var1, var5 + var6);
               }
            } else {
               var6 = var1.length - var5;
            }

            var7 = 0;

            try {
               var7 = var2.read(var1, var5, var6);
            } catch (InterruptedIOException var20) {
               Thread.interrupted();
               var3 = true;
            }

            if (var7 < 0) {
               if (var4 != Integer.MAX_VALUE) {
                  throw new EOFException("Detect premature EOF");
               }

               if (var1.length != var5) {
                  var1 = Arrays.copyOf(var1, var5);
               }
               break;
            }
         }
      } finally {
         try {
            var2.close();
         } catch (InterruptedIOException var18) {
            var3 = true;
         } catch (IOException var19) {
         }

         if (var3) {
            Thread.currentThread().interrupt();
         }

      }

      return var1;
   }

   public ByteBuffer getByteBuffer() throws IOException {
      InputStream var1 = this.cachedInputStream();
      return var1 instanceof ByteBuffered ? ((ByteBuffered)var1).getByteBuffer() : null;
   }

   public Manifest getManifest() throws IOException {
      return null;
   }

   public Certificate[] getCertificates() {
      return null;
   }

   public CodeSigner[] getCodeSigners() {
      return null;
   }
}
