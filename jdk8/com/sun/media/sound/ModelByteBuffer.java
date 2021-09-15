package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Iterator;

public final class ModelByteBuffer {
   private ModelByteBuffer root = this;
   private File file;
   private long fileoffset;
   private byte[] buffer;
   private long offset;
   private final long len;

   private ModelByteBuffer(ModelByteBuffer var1, long var2, long var4, boolean var6) {
      this.root = var1.root;
      this.offset = 0L;
      long var7 = var1.len;
      if (var2 < 0L) {
         var2 = 0L;
      }

      if (var2 > var7) {
         var2 = var7;
      }

      if (var4 < 0L) {
         var4 = 0L;
      }

      if (var4 > var7) {
         var4 = var7;
      }

      if (var2 > var4) {
         var2 = var4;
      }

      this.offset = var2;
      this.len = var4 - var2;
      if (var6) {
         this.buffer = this.root.buffer;
         if (this.root.file != null) {
            this.file = this.root.file;
            this.fileoffset = this.root.fileoffset + this.arrayOffset();
            this.offset = 0L;
         } else {
            this.offset = this.arrayOffset();
         }

         this.root = this;
      }

   }

   public ModelByteBuffer(byte[] var1) {
      this.buffer = var1;
      this.offset = 0L;
      this.len = (long)var1.length;
   }

   public ModelByteBuffer(byte[] var1, int var2, int var3) {
      this.buffer = var1;
      this.offset = (long)var2;
      this.len = (long)var3;
   }

   public ModelByteBuffer(File var1) {
      this.file = var1;
      this.fileoffset = 0L;
      this.len = var1.length();
   }

   public ModelByteBuffer(File var1, long var2, long var4) {
      this.file = var1;
      this.fileoffset = var2;
      this.len = var4;
   }

   public void writeTo(OutputStream var1) throws IOException {
      if (this.root.file != null && this.root.buffer == null) {
         InputStream var2 = this.getInputStream();
         byte[] var3 = new byte[1024];

         int var4;
         while((var4 = var2.read(var3)) != -1) {
            var1.write(var3, 0, var4);
         }
      } else {
         var1.write(this.array(), (int)this.arrayOffset(), (int)this.capacity());
      }

   }

   public InputStream getInputStream() {
      if (this.root.file != null && this.root.buffer == null) {
         try {
            return new ModelByteBuffer.RandomFileInputStream();
         } catch (IOException var2) {
            return null;
         }
      } else {
         return new ByteArrayInputStream(this.array(), (int)this.arrayOffset(), (int)this.capacity());
      }
   }

   public ModelByteBuffer subbuffer(long var1) {
      return this.subbuffer(var1, this.capacity());
   }

   public ModelByteBuffer subbuffer(long var1, long var3) {
      return this.subbuffer(var1, var3, false);
   }

   public ModelByteBuffer subbuffer(long var1, long var3, boolean var5) {
      return new ModelByteBuffer(this, var1, var3, var5);
   }

   public byte[] array() {
      return this.root.buffer;
   }

   public long arrayOffset() {
      return this.root != this ? this.root.arrayOffset() + this.offset : this.offset;
   }

   public long capacity() {
      return this.len;
   }

   public ModelByteBuffer getRoot() {
      return this.root;
   }

   public File getFile() {
      return this.file;
   }

   public long getFilePointer() {
      return this.fileoffset;
   }

   public static void loadAll(Collection<ModelByteBuffer> var0) throws IOException {
      File var1 = null;
      RandomAccessFile var2 = null;

      try {
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            ModelByteBuffer var4 = (ModelByteBuffer)var3.next();
            var4 = var4.root;
            if (var4.file != null && var4.buffer == null) {
               if (var1 == null || !var1.equals(var4.file)) {
                  if (var2 != null) {
                     var2.close();
                     var2 = null;
                  }

                  var1 = var4.file;
                  var2 = new RandomAccessFile(var4.file, "r");
               }

               var2.seek(var4.fileoffset);
               byte[] var5 = new byte[(int)var4.capacity()];
               int var6 = 0;
               int var7 = var5.length;

               while(var6 != var7) {
                  if (var7 - var6 > 65536) {
                     var2.readFully(var5, var6, 65536);
                     var6 += 65536;
                  } else {
                     var2.readFully(var5, var6, var7 - var6);
                     var6 = var7;
                  }
               }

               var4.buffer = var5;
               var4.offset = 0L;
            }
         }
      } finally {
         if (var2 != null) {
            var2.close();
         }

      }

   }

   public void load() throws IOException {
      if (this.root != this) {
         this.root.load();
      } else if (this.buffer == null) {
         if (this.file == null) {
            throw new IllegalStateException("No file associated with this ByteBuffer!");
         } else {
            DataInputStream var1 = new DataInputStream(this.getInputStream());
            this.buffer = new byte[(int)this.capacity()];
            this.offset = 0L;
            var1.readFully(this.buffer);
            var1.close();
         }
      }
   }

   public void unload() {
      if (this.root != this) {
         this.root.unload();
      } else if (this.file == null) {
         throw new IllegalStateException("No file associated with this ByteBuffer!");
      } else {
         this.root.buffer = null;
      }
   }

   private class RandomFileInputStream extends InputStream {
      private final RandomAccessFile raf;
      private long left;
      private long mark = 0L;
      private long markleft = 0L;

      RandomFileInputStream() throws IOException {
         this.raf = new RandomAccessFile(ModelByteBuffer.this.root.file, "r");
         this.raf.seek(ModelByteBuffer.this.root.fileoffset + ModelByteBuffer.this.arrayOffset());
         this.left = ModelByteBuffer.this.capacity();
      }

      public int available() throws IOException {
         return this.left > 2147483647L ? Integer.MAX_VALUE : (int)this.left;
      }

      public synchronized void mark(int var1) {
         try {
            this.mark = this.raf.getFilePointer();
            this.markleft = this.left;
         } catch (IOException var3) {
         }

      }

      public boolean markSupported() {
         return true;
      }

      public synchronized void reset() throws IOException {
         this.raf.seek(this.mark);
         this.left = this.markleft;
      }

      public long skip(long var1) throws IOException {
         if (var1 < 0L) {
            return 0L;
         } else {
            if (var1 > this.left) {
               var1 = this.left;
            }

            long var3 = this.raf.getFilePointer();
            this.raf.seek(var3 + var1);
            this.left -= var1;
            return var1;
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if ((long)var3 > this.left) {
            var3 = (int)this.left;
         }

         if (this.left == 0L) {
            return -1;
         } else {
            var3 = this.raf.read(var1, var2, var3);
            if (var3 == -1) {
               return -1;
            } else {
               this.left -= (long)var3;
               return var3;
            }
         }
      }

      public int read(byte[] var1) throws IOException {
         int var2 = var1.length;
         if ((long)var2 > this.left) {
            var2 = (int)this.left;
         }

         if (this.left == 0L) {
            return -1;
         } else {
            var2 = this.raf.read(var1, 0, var2);
            if (var2 == -1) {
               return -1;
            } else {
               this.left -= (long)var2;
               return var2;
            }
         }
      }

      public int read() throws IOException {
         if (this.left == 0L) {
            return -1;
         } else {
            int var1 = this.raf.read();
            if (var1 == -1) {
               return -1;
            } else {
               --this.left;
               return var1;
            }
         }
      }

      public void close() throws IOException {
         this.raf.close();
      }
   }
}
