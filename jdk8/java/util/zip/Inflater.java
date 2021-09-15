package java.util.zip;

public class Inflater {
   private final ZStreamRef zsRef;
   private byte[] buf;
   private int off;
   private int len;
   private boolean finished;
   private boolean needDict;
   private long bytesRead;
   private long bytesWritten;
   private static final byte[] defaultBuf = new byte[0];

   public Inflater(boolean var1) {
      this.buf = defaultBuf;
      this.zsRef = new ZStreamRef(init(var1));
   }

   public Inflater() {
      this(false);
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
            this.needDict = false;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void setDictionary(byte[] var1) {
      this.setDictionary(var1, 0, var1.length);
   }

   public int getRemaining() {
      synchronized(this.zsRef) {
         return this.len;
      }
   }

   public boolean needsInput() {
      synchronized(this.zsRef) {
         return this.len <= 0;
      }
   }

   public boolean needsDictionary() {
      synchronized(this.zsRef) {
         return this.needDict;
      }
   }

   public boolean finished() {
      synchronized(this.zsRef) {
         return this.finished;
      }
   }

   public int inflate(byte[] var1, int var2, int var3) throws DataFormatException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         synchronized(this.zsRef) {
            this.ensureOpen();
            int var5 = this.len;
            int var6 = this.inflateBytes(this.zsRef.address(), var1, var2, var3);
            this.bytesWritten += (long)var6;
            this.bytesRead += (long)(var5 - this.len);
            return var6;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public int inflate(byte[] var1) throws DataFormatException {
      return this.inflate(var1, 0, var1.length);
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
         this.buf = defaultBuf;
         this.finished = false;
         this.needDict = false;
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
         throw new NullPointerException("Inflater has been closed");
      }
   }

   boolean ended() {
      synchronized(this.zsRef) {
         return this.zsRef.address() == 0L;
      }
   }

   private static native void initIDs();

   private static native long init(boolean var0);

   private static native void setDictionary(long var0, byte[] var2, int var3, int var4);

   private native int inflateBytes(long var1, byte[] var3, int var4, int var5) throws DataFormatException;

   private static native int getAdler(long var0);

   private static native void reset(long var0);

   private static native void end(long var0);

   static {
      initIDs();
   }
}
