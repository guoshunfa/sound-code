package java.util.zip;

public class Deflater {
   private final ZStreamRef zsRef;
   private byte[] buf;
   private int off;
   private int len;
   private int level;
   private int strategy;
   private boolean setParams;
   private boolean finish;
   private boolean finished;
   private long bytesRead;
   private long bytesWritten;
   public static final int DEFLATED = 8;
   public static final int NO_COMPRESSION = 0;
   public static final int BEST_SPEED = 1;
   public static final int BEST_COMPRESSION = 9;
   public static final int DEFAULT_COMPRESSION = -1;
   public static final int FILTERED = 1;
   public static final int HUFFMAN_ONLY = 2;
   public static final int DEFAULT_STRATEGY = 0;
   public static final int NO_FLUSH = 0;
   public static final int SYNC_FLUSH = 2;
   public static final int FULL_FLUSH = 3;

   public Deflater(int var1, boolean var2) {
      this.buf = new byte[0];
      this.level = var1;
      this.strategy = 0;
      this.zsRef = new ZStreamRef(init(var1, 0, var2));
   }

   public Deflater(int var1) {
      this(var1, false);
   }

   public Deflater() {
      this(-1, false);
   }

   public void setInput(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         synchronized(this.zsRef) {
            this.buf = var1;
            this.off = var2;
            this.len = var3;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void setInput(byte[] var1) {
      this.setInput(var1, 0, var1.length);
   }

   public void setDictionary(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         synchronized(this.zsRef) {
            this.ensureOpen();
            setDictionary(this.zsRef.address(), var1, var2, var3);
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void setDictionary(byte[] var1) {
      this.setDictionary(var1, 0, var1.length);
   }

   public void setStrategy(int var1) {
      switch(var1) {
      case 0:
      case 1:
      case 2:
         synchronized(this.zsRef) {
            if (this.strategy != var1) {
               this.strategy = var1;
               this.setParams = true;
            }

            return;
         }
      default:
         throw new IllegalArgumentException();
      }
   }

   public void setLevel(int var1) {
      if ((var1 < 0 || var1 > 9) && var1 != -1) {
         throw new IllegalArgumentException("invalid compression level");
      } else {
         synchronized(this.zsRef) {
            if (this.level != var1) {
               this.level = var1;
               this.setParams = true;
            }

         }
      }
   }

   public boolean needsInput() {
      synchronized(this.zsRef) {
         return this.len <= 0;
      }
   }

   public void finish() {
      synchronized(this.zsRef) {
         this.finish = true;
      }
   }

   public boolean finished() {
      synchronized(this.zsRef) {
         return this.finished;
      }
   }

   public int deflate(byte[] var1, int var2, int var3) {
      return this.deflate(var1, var2, var3, 0);
   }

   public int deflate(byte[] var1) {
      return this.deflate(var1, 0, var1.length, 0);
   }

   public int deflate(byte[] var1, int var2, int var3, int var4) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         synchronized(this.zsRef) {
            this.ensureOpen();
            if (var4 != 0 && var4 != 2 && var4 != 3) {
               throw new IllegalArgumentException();
            } else {
               int var6 = this.len;
               int var7 = this.deflateBytes(this.zsRef.address(), var1, var2, var3, var4);
               this.bytesWritten += (long)var7;
               this.bytesRead += (long)(var6 - this.len);
               return var7;
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public int getAdler() {
      synchronized(this.zsRef) {
         this.ensureOpen();
         return getAdler(this.zsRef.address());
      }
   }

   public int getTotalIn() {
      return (int)this.getBytesRead();
   }

   public long getBytesRead() {
      synchronized(this.zsRef) {
         this.ensureOpen();
         return this.bytesRead;
      }
   }

   public int getTotalOut() {
      return (int)this.getBytesWritten();
   }

   public long getBytesWritten() {
      synchronized(this.zsRef) {
         this.ensureOpen();
         return this.bytesWritten;
      }
   }

   public void reset() {
      synchronized(this.zsRef) {
         this.ensureOpen();
         reset(this.zsRef.address());
         this.finish = false;
         this.finished = false;
         this.off = this.len = 0;
         this.bytesRead = this.bytesWritten = 0L;
      }
   }

   public void end() {
      synchronized(this.zsRef) {
         long var2 = this.zsRef.address();
         this.zsRef.clear();
         if (var2 != 0L) {
            end(var2);
            this.buf = null;
         }

      }
   }

   protected void finalize() {
      this.end();
   }

   private void ensureOpen() {
      assert Thread.holdsLock(this.zsRef);

      if (this.zsRef.address() == 0L) {
         throw new NullPointerException("Deflater has been closed");
      }
   }

   private static native void initIDs();

   private static native long init(int var0, int var1, boolean var2);

   private static native void setDictionary(long var0, byte[] var2, int var3, int var4);

   private native int deflateBytes(long var1, byte[] var3, int var4, int var5, int var6);

   private static native int getAdler(long var0);

   private static native void reset(long var0);

   private static native void end(long var0);

   static {
      initIDs();
   }
}
