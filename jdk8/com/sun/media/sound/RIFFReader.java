package com.sun.media.sound;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class RIFFReader extends InputStream {
   private final RIFFReader root;
   private long filepointer = 0L;
   private final String fourcc;
   private String riff_type = null;
   private long ckSize = 2147483647L;
   private InputStream stream;
   private long avail = 2147483647L;
   private RIFFReader lastiterator = null;

   public RIFFReader(InputStream var1) throws IOException {
      if (var1 instanceof RIFFReader) {
         this.root = ((RIFFReader)var1).root;
      } else {
         this.root = this;
      }

      this.stream = var1;

      int var2;
      do {
         var2 = this.read();
         if (var2 == -1) {
            this.fourcc = "";
            this.riff_type = null;
            this.avail = 0L;
            return;
         }
      } while(var2 == 0);

      byte[] var3 = new byte[4];
      var3[0] = (byte)var2;
      this.readFully(var3, 1, 3);
      this.fourcc = new String(var3, "ascii");
      this.ckSize = this.readUnsignedInt();
      this.avail = this.ckSize;
      if (this.getFormat().equals("RIFF") || this.getFormat().equals("LIST")) {
         if (this.avail > 2147483647L) {
            throw new RIFFInvalidDataException("Chunk size too big");
         }

         byte[] var4 = new byte[4];
         this.readFully(var4);
         this.riff_type = new String(var4, "ascii");
      }

   }

   public long getFilePointer() throws IOException {
      return this.root.filepointer;
   }

   public boolean hasNextChunk() throws IOException {
      if (this.lastiterator != null) {
         this.lastiterator.finish();
      }

      return this.avail != 0L;
   }

   public RIFFReader nextChunk() throws IOException {
      if (this.lastiterator != null) {
         this.lastiterator.finish();
      }

      if (this.avail == 0L) {
         return null;
      } else {
         this.lastiterator = new RIFFReader(this);
         return this.lastiterator;
      }
   }

   public String getFormat() {
      return this.fourcc;
   }

   public String getType() {
      return this.riff_type;
   }

   public long getSize() {
      return this.ckSize;
   }

   public int read() throws IOException {
      if (this.avail == 0L) {
         return -1;
      } else {
         int var1 = this.stream.read();
         if (var1 == -1) {
            this.avail = 0L;
            return -1;
         } else {
            --this.avail;
            ++this.filepointer;
            return var1;
         }
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.avail == 0L) {
         return -1;
      } else {
         int var4;
         if ((long)var3 > this.avail) {
            var4 = this.stream.read(var1, var2, (int)this.avail);
            if (var4 != -1) {
               this.filepointer += (long)var4;
            }

            this.avail = 0L;
            return var4;
         } else {
            var4 = this.stream.read(var1, var2, var3);
            if (var4 == -1) {
               this.avail = 0L;
               return -1;
            } else {
               this.avail -= (long)var4;
               this.filepointer += (long)var4;
               return var4;
            }
         }
      }
   }

   public final void readFully(byte[] var1) throws IOException {
      this.readFully(var1, 0, var1.length);
   }

   public final void readFully(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         while(var3 > 0) {
            int var4 = this.read(var1, var2, var3);
            if (var4 < 0) {
               throw new EOFException();
            }

            if (var4 == 0) {
               Thread.yield();
            }

            var2 += var4;
            var3 -= var4;
         }

      }
   }

   public final long skipBytes(long var1) throws IOException {
      if (var1 < 0L) {
         return 0L;
      } else {
         long var3;
         long var5;
         for(var3 = 0L; var3 != var1; var3 += var5) {
            var5 = this.skip(var1 - var3);
            if (var5 < 0L) {
               break;
            }

            if (var5 == 0L) {
               Thread.yield();
            }
         }

         return var3;
      }
   }

   public long skip(long var1) throws IOException {
      if (this.avail == 0L) {
         return -1L;
      } else {
         long var3;
         if (var1 > this.avail) {
            var3 = this.stream.skip(this.avail);
            if (var3 != -1L) {
               this.filepointer += var3;
            }

            this.avail = 0L;
            return var3;
         } else {
            var3 = this.stream.skip(var1);
            if (var3 == -1L) {
               this.avail = 0L;
               return -1L;
            } else {
               this.avail -= var3;
               this.filepointer += var3;
               return var3;
            }
         }
      }
   }

   public int available() {
      return (int)this.avail;
   }

   public void finish() throws IOException {
      if (this.avail != 0L) {
         this.skipBytes(this.avail);
      }

   }

   public String readString(int var1) throws IOException {
      byte[] var2;
      try {
         var2 = new byte[var1];
      } catch (OutOfMemoryError var4) {
         throw new IOException("Length too big", var4);
      }

      this.readFully(var2);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] == 0) {
            return new String(var2, 0, var3, "ascii");
         }
      }

      return new String(var2, "ascii");
   }

   public byte readByte() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return (byte)var1;
      }
   }

   public short readShort() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else if (var2 < 0) {
         throw new EOFException();
      } else {
         return (short)(var1 | var2 << 8);
      }
   }

   public int readInt() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      int var3 = this.read();
      int var4 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else if (var2 < 0) {
         throw new EOFException();
      } else if (var3 < 0) {
         throw new EOFException();
      } else if (var4 < 0) {
         throw new EOFException();
      } else {
         return var1 + (var2 << 8) | var3 << 16 | var4 << 24;
      }
   }

   public long readLong() throws IOException {
      long var1 = (long)this.read();
      long var3 = (long)this.read();
      long var5 = (long)this.read();
      long var7 = (long)this.read();
      long var9 = (long)this.read();
      long var11 = (long)this.read();
      long var13 = (long)this.read();
      long var15 = (long)this.read();
      if (var1 < 0L) {
         throw new EOFException();
      } else if (var3 < 0L) {
         throw new EOFException();
      } else if (var5 < 0L) {
         throw new EOFException();
      } else if (var7 < 0L) {
         throw new EOFException();
      } else if (var9 < 0L) {
         throw new EOFException();
      } else if (var11 < 0L) {
         throw new EOFException();
      } else if (var13 < 0L) {
         throw new EOFException();
      } else if (var15 < 0L) {
         throw new EOFException();
      } else {
         return var1 | var3 << 8 | var5 << 16 | var7 << 24 | var9 << 32 | var11 << 40 | var13 << 48 | var15 << 56;
      }
   }

   public int readUnsignedByte() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1;
      }
   }

   public int readUnsignedShort() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else if (var2 < 0) {
         throw new EOFException();
      } else {
         return var1 | var2 << 8;
      }
   }

   public long readUnsignedInt() throws IOException {
      long var1 = (long)this.read();
      long var3 = (long)this.read();
      long var5 = (long)this.read();
      long var7 = (long)this.read();
      if (var1 < 0L) {
         throw new EOFException();
      } else if (var3 < 0L) {
         throw new EOFException();
      } else if (var5 < 0L) {
         throw new EOFException();
      } else if (var7 < 0L) {
         throw new EOFException();
      } else {
         return var1 + (var3 << 8) | var5 << 16 | var7 << 24;
      }
   }

   public void close() throws IOException {
      this.finish();
      if (this == this.root) {
         this.stream.close();
      }

      this.stream = null;
   }
}
