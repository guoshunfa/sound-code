package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

class NativeUnpack {
   private long unpackerPtr;
   private BufferedInputStream in;
   private int _verbose;
   private long _byteCount;
   private int _segCount;
   private int _fileCount;
   private long _estByteLimit;
   private int _estSegLimit;
   private int _estFileLimit;
   private int _prevPercent = -1;
   private final CRC32 _crc32 = new CRC32();
   private byte[] _buf = new byte[16384];
   private UnpackerImpl _p200;
   private PropMap _props;

   private static synchronized native void initIDs();

   private synchronized native long start(ByteBuffer var1, long var2);

   private synchronized native boolean getNextFile(Object[] var1);

   private synchronized native ByteBuffer getUnusedInput();

   private synchronized native long finish();

   protected synchronized native boolean setOption(String var1, String var2);

   protected synchronized native String getOption(String var1);

   NativeUnpack(UnpackerImpl var1) {
      this._p200 = var1;
      this._props = var1.props;
      var1._nunp = this;
   }

   private static Object currentInstance() {
      UnpackerImpl var0 = (UnpackerImpl)Utils.getTLGlobals();
      return var0 == null ? null : var0._nunp;
   }

   private synchronized long getUnpackerPtr() {
      return this.unpackerPtr;
   }

   private long readInputFn(ByteBuffer var1, long var2) throws IOException {
      if (this.in == null) {
         return 0L;
      } else {
         long var4 = (long)(var1.capacity() - var1.position());

         assert var2 <= var4;

         long var6 = 0L;
         int var8 = 0;

         while(var6 < var2) {
            ++var8;
            int var9 = this._buf.length;
            if ((long)var9 > var4 - var6) {
               var9 = (int)(var4 - var6);
            }

            int var10 = this.in.read(this._buf, 0, var9);
            if (var10 <= 0) {
               break;
            }

            var6 += (long)var10;

            assert var6 <= var4;

            var1.put(this._buf, 0, var10);
         }

         if (this._verbose > 1) {
            Utils.log.fine("readInputFn(" + var2 + "," + var4 + ") => " + var6 + " steps=" + var8);
         }

         if (var4 > 100L) {
            this._estByteLimit = this._byteCount + var4;
         } else {
            this._estByteLimit = (this._byteCount + var6) * 20L;
         }

         this._byteCount += var6;
         this.updateProgress();
         return var6;
      }
   }

   private void updateProgress() {
      double var5 = (double)this._segCount;
      if (this._estByteLimit > 0L && this._byteCount > 0L) {
         var5 += (double)this._byteCount / (double)this._estByteLimit;
      }

      double var7 = (double)this._fileCount;
      double var9 = 0.33D * var5 / (double)Math.max(this._estSegLimit, 1) + 0.67D * var7 / (double)Math.max(this._estFileLimit, 1);
      int var11 = (int)Math.round(100.0D * var9);
      if (var11 > 100) {
         var11 = 100;
      }

      if (var11 > this._prevPercent) {
         this._prevPercent = var11;
         this._props.setInteger("unpack.progress", var11);
         if (this._verbose > 0) {
            Utils.log.info("progress = " + var11);
         }
      }

   }

   private void copyInOption(String var1) {
      String var2 = this._props.getProperty(var1);
      if (this._verbose > 0) {
         Utils.log.info("set " + var1 + "=" + var2);
      }

      if (var2 != null) {
         boolean var3 = this.setOption(var1, var2);
         if (!var3) {
            Utils.log.warning("Invalid option " + var1 + "=" + var2);
         }
      }

   }

   void run(InputStream var1, JarOutputStream var2, ByteBuffer var3) throws IOException {
      BufferedInputStream var4 = new BufferedInputStream(var1);
      this.in = var4;
      this._verbose = this._props.getInteger("com.sun.java.util.jar.pack.verbose");
      int var5 = "keep".equals(this._props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "0")) ? 0 : this._props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
      this.copyInOption("com.sun.java.util.jar.pack.verbose");
      this.copyInOption("unpack.deflate.hint");
      if (var5 == 0) {
         this.copyInOption("com.sun.java.util.jar.pack.unpack.modification.time");
      }

      this.updateProgress();

      while(true) {
         long var6 = this.start(var3, 0L);
         this._byteCount = this._estByteLimit = 0L;
         ++this._segCount;
         int var8 = (int)(var6 >>> 32);
         int var9 = (int)(var6 >>> 0);
         this._estSegLimit = this._segCount + var8;
         double var10 = (double)(this._fileCount + var9);
         this._estFileLimit = (int)(var10 * (double)this._estSegLimit / (double)this._segCount);
         int[] var12 = new int[]{0, 0, 0, 0};
         Object[] var13 = new Object[]{var12, null, null, null};

         while(this.getNextFile(var13)) {
            String var14 = (String)var13[1];
            long var15 = ((long)var12[0] << 32) + ((long)var12[1] << 32 >>> 32);
            long var17 = var5 != 0 ? (long)var5 : (long)var12[2];
            boolean var19 = var12[3] != 0;
            ByteBuffer var20 = (ByteBuffer)var13[2];
            ByteBuffer var21 = (ByteBuffer)var13[3];
            this.writeEntry(var2, var14, var17, var15, var19, var20, var21);
            ++this._fileCount;
            this.updateProgress();
         }

         var3 = this.getUnusedInput();
         long var22 = this.finish();
         if (this._verbose > 0) {
            Utils.log.info("bytes consumed = " + var22);
         }

         if (var3 == null && !Utils.isPackMagic(Utils.readMagic(var4))) {
            return;
         }

         if (this._verbose > 0 && var3 != null) {
            Utils.log.info("unused input = " + var3);
         }
      }
   }

   void run(InputStream var1, JarOutputStream var2) throws IOException {
      this.run(var1, var2, (ByteBuffer)null);
   }

   void run(File var1, JarOutputStream var2) throws IOException {
      Object var3 = null;
      FileInputStream var4 = new FileInputStream(var1);
      Throwable var5 = null;

      try {
         this.run(var4, var2, (ByteBuffer)var3);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               var4.close();
            }
         }

      }

   }

   private void writeEntry(JarOutputStream var1, String var2, long var3, long var5, boolean var7, ByteBuffer var8, ByteBuffer var9) throws IOException {
      int var10 = (int)var5;
      if ((long)var10 != var5) {
         throw new IOException("file too large: " + var5);
      } else {
         CRC32 var11 = this._crc32;
         if (this._verbose > 1) {
            Utils.log.fine("Writing entry: " + var2 + " size=" + var10 + (var7 ? " deflated" : ""));
         }

         int var12;
         if (this._buf.length < var10) {
            var12 = var10;

            while(var12 < this._buf.length) {
               var12 <<= 1;
               if (var12 <= 0) {
                  var12 = var10;
                  break;
               }
            }

            this._buf = new byte[var12];
         }

         assert this._buf.length >= var10;

         var12 = 0;
         int var13;
         if (var8 != null) {
            var13 = var8.capacity();
            var8.get(this._buf, var12, var13);
            var12 += var13;
         }

         if (var9 != null) {
            var13 = var9.capacity();
            var9.get(this._buf, var12, var13);
            var12 += var13;
         }

         while(var12 < var10) {
            var13 = this.in.read(this._buf, var12, var10 - var12);
            if (var13 <= 0) {
               throw new IOException("EOF at end of archive");
            }

            var12 += var13;
         }

         ZipEntry var14 = new ZipEntry(var2);
         var14.setTime(var3 * 1000L);
         if (var10 == 0) {
            var14.setMethod(0);
            var14.setSize(0L);
            var14.setCrc(0L);
            var14.setCompressedSize(0L);
         } else if (!var7) {
            var14.setMethod(0);
            var14.setSize((long)var10);
            var14.setCompressedSize((long)var10);
            var11.reset();
            var11.update(this._buf, 0, var10);
            var14.setCrc(var11.getValue());
         } else {
            var14.setMethod(8);
            var14.setSize((long)var10);
         }

         var1.putNextEntry(var14);
         if (var10 > 0) {
            var1.write(this._buf, 0, var10);
         }

         var1.closeEntry();
         if (this._verbose > 0) {
            Utils.log.info("Writing " + Utils.zeString(var14));
         }

      }
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("unpack");
            return null;
         }
      });
      initIDs();
   }
}
