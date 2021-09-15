package com.sun.media.sound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public final class RIFFWriter extends OutputStream {
   private int chunktype;
   private RIFFWriter.RandomAccessWriter raf;
   private final long chunksizepointer;
   private final long startpointer;
   private RIFFWriter childchunk;
   private boolean open;
   private boolean writeoverride;

   public RIFFWriter(String var1, String var2) throws IOException {
      this(new RIFFWriter.RandomAccessFileWriter(var1), var2, 0);
   }

   public RIFFWriter(File var1, String var2) throws IOException {
      this(new RIFFWriter.RandomAccessFileWriter(var1), var2, 0);
   }

   public RIFFWriter(OutputStream var1, String var2) throws IOException {
      this(new RIFFWriter.RandomAccessByteWriter(var1), var2, 0);
   }

   private RIFFWriter(RIFFWriter.RandomAccessWriter var1, String var2, int var3) throws IOException {
      this.chunktype = 0;
      this.childchunk = null;
      this.open = true;
      this.writeoverride = false;
      if (var3 == 0 && var1.length() != 0L) {
         var1.setLength(0L);
      }

      this.raf = var1;
      if (var1.getPointer() % 2L != 0L) {
         var1.write(0);
      }

      if (var3 == 0) {
         var1.write("RIFF".getBytes("ascii"));
      } else if (var3 == 1) {
         var1.write("LIST".getBytes("ascii"));
      } else {
         var1.write((var2 + "    ").substring(0, 4).getBytes("ascii"));
      }

      this.chunksizepointer = var1.getPointer();
      this.chunktype = 2;
      this.writeUnsignedInt(0L);
      this.chunktype = var3;
      this.startpointer = var1.getPointer();
      if (var3 != 2) {
         var1.write((var2 + "    ").substring(0, 4).getBytes("ascii"));
      }

   }

   public void seek(long var1) throws IOException {
      this.raf.seek(var1);
   }

   public long getFilePointer() throws IOException {
      return this.raf.getPointer();
   }

   public void setWriteOverride(boolean var1) {
      this.writeoverride = var1;
   }

   public boolean getWriteOverride() {
      return this.writeoverride;
   }

   public void close() throws IOException {
      if (this.open) {
         if (this.childchunk != null) {
            this.childchunk.close();
            this.childchunk = null;
         }

         int var1 = this.chunktype;
         long var2 = this.raf.getPointer();
         this.raf.seek(this.chunksizepointer);
         this.chunktype = 2;
         this.writeUnsignedInt(var2 - this.startpointer);
         if (var1 == 0) {
            this.raf.close();
         } else {
            this.raf.seek(var2);
         }

         this.open = false;
         this.raf = null;
      }
   }

   public void write(int var1) throws IOException {
      if (!this.writeoverride) {
         if (this.chunktype != 2) {
            throw new IllegalArgumentException("Only chunks can write bytes!");
         }

         if (this.childchunk != null) {
            this.childchunk.close();
            this.childchunk = null;
         }
      }

      this.raf.write(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (!this.writeoverride) {
         if (this.chunktype != 2) {
            throw new IllegalArgumentException("Only chunks can write bytes!");
         }

         if (this.childchunk != null) {
            this.childchunk.close();
            this.childchunk = null;
         }
      }

      this.raf.write(var1, var2, var3);
   }

   public RIFFWriter writeList(String var1) throws IOException {
      if (this.chunktype == 2) {
         throw new IllegalArgumentException("Only LIST and RIFF can write lists!");
      } else {
         if (this.childchunk != null) {
            this.childchunk.close();
            this.childchunk = null;
         }

         this.childchunk = new RIFFWriter(this.raf, var1, 1);
         return this.childchunk;
      }
   }

   public RIFFWriter writeChunk(String var1) throws IOException {
      if (this.chunktype == 2) {
         throw new IllegalArgumentException("Only LIST and RIFF can write chunks!");
      } else {
         if (this.childchunk != null) {
            this.childchunk.close();
            this.childchunk = null;
         }

         this.childchunk = new RIFFWriter(this.raf, var1, 2);
         return this.childchunk;
      }
   }

   public void writeString(String var1) throws IOException {
      byte[] var2 = var1.getBytes();
      this.write(var2);
   }

   public void writeString(String var1, int var2) throws IOException {
      byte[] var3 = var1.getBytes();
      if (var3.length > var2) {
         this.write(var3, 0, var2);
      } else {
         this.write(var3);

         for(int var4 = var3.length; var4 < var2; ++var4) {
            this.write(0);
         }
      }

   }

   public void writeByte(int var1) throws IOException {
      this.write(var1);
   }

   public void writeShort(short var1) throws IOException {
      this.write(var1 >>> 0 & 255);
      this.write(var1 >>> 8 & 255);
   }

   public void writeInt(int var1) throws IOException {
      this.write(var1 >>> 0 & 255);
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 16 & 255);
      this.write(var1 >>> 24 & 255);
   }

   public void writeLong(long var1) throws IOException {
      this.write((int)(var1 >>> 0) & 255);
      this.write((int)(var1 >>> 8) & 255);
      this.write((int)(var1 >>> 16) & 255);
      this.write((int)(var1 >>> 24) & 255);
      this.write((int)(var1 >>> 32) & 255);
      this.write((int)(var1 >>> 40) & 255);
      this.write((int)(var1 >>> 48) & 255);
      this.write((int)(var1 >>> 56) & 255);
   }

   public void writeUnsignedByte(int var1) throws IOException {
      this.writeByte((byte)var1);
   }

   public void writeUnsignedShort(int var1) throws IOException {
      this.writeShort((short)var1);
   }

   public void writeUnsignedInt(long var1) throws IOException {
      this.writeInt((int)var1);
   }

   private static class RandomAccessByteWriter implements RIFFWriter.RandomAccessWriter {
      byte[] buff = new byte[32];
      int length = 0;
      int pos = 0;
      byte[] s;
      final OutputStream stream;

      RandomAccessByteWriter(OutputStream var1) {
         this.stream = var1;
      }

      public void seek(long var1) throws IOException {
         this.pos = (int)var1;
      }

      public long getPointer() throws IOException {
         return (long)this.pos;
      }

      public void close() throws IOException {
         this.stream.write(this.buff, 0, this.length);
         this.stream.close();
      }

      public void write(int var1) throws IOException {
         if (this.s == null) {
            this.s = new byte[1];
         }

         this.s[0] = (byte)var1;
         this.write(this.s, 0, 1);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         int var4 = this.pos + var3;
         if (var4 > this.length) {
            this.setLength((long)var4);
         }

         int var5 = var2 + var3;

         for(int var6 = var2; var6 < var5; ++var6) {
            this.buff[this.pos++] = var1[var6];
         }

      }

      public void write(byte[] var1) throws IOException {
         this.write(var1, 0, var1.length);
      }

      public long length() throws IOException {
         return (long)this.length;
      }

      public void setLength(long var1) throws IOException {
         this.length = (int)var1;
         if (this.length > this.buff.length) {
            int var3 = Math.max(this.buff.length << 1, this.length);
            byte[] var4 = new byte[var3];
            System.arraycopy(this.buff, 0, var4, 0, this.buff.length);
            this.buff = var4;
         }

      }
   }

   private static class RandomAccessFileWriter implements RIFFWriter.RandomAccessWriter {
      RandomAccessFile raf;

      RandomAccessFileWriter(File var1) throws FileNotFoundException {
         this.raf = new RandomAccessFile(var1, "rw");
      }

      RandomAccessFileWriter(String var1) throws FileNotFoundException {
         this.raf = new RandomAccessFile(var1, "rw");
      }

      public void seek(long var1) throws IOException {
         this.raf.seek(var1);
      }

      public long getPointer() throws IOException {
         return this.raf.getFilePointer();
      }

      public void close() throws IOException {
         this.raf.close();
      }

      public void write(int var1) throws IOException {
         this.raf.write(var1);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.raf.write(var1, var2, var3);
      }

      public void write(byte[] var1) throws IOException {
         this.raf.write(var1);
      }

      public long length() throws IOException {
         return this.raf.length();
      }

      public void setLength(long var1) throws IOException {
         this.raf.setLength(var1);
      }
   }

   private interface RandomAccessWriter {
      void seek(long var1) throws IOException;

      long getPointer() throws IOException;

      void close() throws IOException;

      void write(int var1) throws IOException;

      void write(byte[] var1, int var2, int var3) throws IOException;

      void write(byte[] var1) throws IOException;

      long length() throws IOException;

      void setLength(long var1) throws IOException;
   }
}
